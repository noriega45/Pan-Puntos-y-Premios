
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Pago;
import util.Conexion;

public class PagoDAO {

public boolean guardar(Pago pago) {
    if (pago == null
            || pago.getIdPedido() <= 0
            || pago.getMetodoPago() == null
            || pago.getMonto() <= 0
            || pago.getEstado() == null) {
        return false;
    }

    String metodo = pago.getMetodoPago().trim().toUpperCase();
    String estado = pago.getEstado().trim().toUpperCase();

    if (!metodo.equals("EFECTIVO") && !metodo.equals("TARJETA")) {
        return false;
    }

    if (!estado.equals("APROBADO") && !estado.equals("RECHAZADO")) {
        return false;
    }
    if (metodo.equals("TARJETA")
            && (pago.getReferencia() == null || pago.getReferencia().trim().isEmpty())) {
        return false;
    }

    String sqlPedido =
            "SELECT P.TOTAL FROM PEDIDOS P "
            + "WHERE P.ID_PEDIDO = ? AND P.ESTADO = 'PENDIENTE' "
            + "AND EXISTS (SELECT 1 FROM CLIENTES C "
            + "WHERE C.ID_CLIENTE = P.ID_CLIENTE AND C.ACTIVO = 1) "
            + "FOR UPDATE";

    String sqlCantidadDetalles =
            "SELECT (SELECT COUNT(*) FROM DETALLE_PEDIDO "
            + "WHERE ID_PEDIDO = ?) + "
            + "(SELECT COUNT(*) FROM DETALLE_PEDIDO_MENU "
            + "WHERE ID_PEDIDO = ?) AS CANTIDAD FROM DUAL";

    String sqlProductos =
            "SELECT ID_PRODUCTO, SUM(CANTIDAD) AS CANTIDAD "
            + "FROM DETALLE_PEDIDO WHERE ID_PEDIDO = ? "
            + "GROUP BY ID_PRODUCTO";

    String sqlComponentesMenu =
            "SELECT DM.ID_PRODUCTO, "
            + "SUM(DPM.CANTIDAD * DM.CANTIDAD) AS CANTIDAD "
            + "FROM DETALLE_PEDIDO_MENU DPM "
            + "JOIN DETALLE_MENU DM ON DM.ID_MENU = DPM.ID_MENU "
            + "WHERE DPM.ID_PEDIDO = ? "
            + "GROUP BY DM.ID_PRODUCTO";

    String sqlInsertPago =
            "INSERT INTO PAGOS "
            + "(ID_PAGO, ID_PEDIDO, METODO_PAGO, MONTO, ESTADO, REFERENCIA) "
            + "VALUES (SEQ_PAGOS.NEXTVAL, ?, ?, ?, ?, ?)";

    String sqlActualizarPedido =
            "UPDATE PEDIDOS SET ESTADO = 'PAGADO' "
            + "WHERE ID_PEDIDO = ? AND ESTADO = 'PENDIENTE'";

    try (Connection cn = Conexion.conectar()) {
        boolean autoCommitOriginal = cn.getAutoCommit();
        cn.setAutoCommit(false);

        try {
            double totalPedido;
            try (PreparedStatement ps = cn.prepareStatement(sqlPedido)) {
                ps.setInt(1, pago.getIdPedido());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        cn.rollback();
                        return false;
                    }
                    totalPedido = rs.getDouble("TOTAL");
                }
            }

            if (totalPedido <= 0
                    || Math.abs(totalPedido - pago.getMonto()) > 0.01) {
                cn.rollback();
                return false;
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlCantidadDetalles)) {
                ps.setInt(1, pago.getIdPedido());
                ps.setInt(2, pago.getIdPedido());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("CANTIDAD") == 0) {
                        cn.rollback();
                        return false;
                    }
                }
            }

            if (estado.equals("APROBADO")) {
                if (!descontarExistencias(
                        cn, sqlProductos, pago.getIdPedido())
                        || !descontarExistencias(
                        cn, sqlComponentesMenu, pago.getIdPedido())) {
                    cn.rollback();
                    return false;
                }

                if (!acreditarPuntos(cn, pago.getIdPedido())) {
                    cn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlInsertPago)) {
                ps.setInt(1, pago.getIdPedido());
                ps.setString(2, metodo);
                ps.setDouble(3, pago.getMonto());
                ps.setString(4, estado);
                ps.setString(5, pago.getReferencia());

                if (ps.executeUpdate() == 0) {
                    cn.rollback();
                    return false;
                }
            }

            if (estado.equals("APROBADO")) {
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlActualizarPedido)) {
                    ps.setInt(1, pago.getIdPedido());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }
            }

            cn.commit();
            return true;

        } catch (Exception e) {
            cn.rollback();
            throw e;
        } finally {
            cn.setAutoCommit(autoCommitOriginal);
        }

    } catch (Exception e) {
        System.out.println("Error al guardar pago: " + e.getMessage());
        return false;
    }
}

private boolean descontarExistencias(
        Connection cn, String sql, int idPedido) throws Exception {

    String sqlDescontar =
            "UPDATE PRODUCTOS "
            + "SET EXISTENCIA = EXISTENCIA - ? "
            + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1 AND EXISTENCIA >= ?";

    try (PreparedStatement psConsulta = cn.prepareStatement(sql)) {
        psConsulta.setInt(1, idPedido);

        try (ResultSet rs = psConsulta.executeQuery()) {
            while (rs.next()) {
                int idProducto = rs.getInt("ID_PRODUCTO");
                int cantidad = rs.getInt("CANTIDAD");

                try (PreparedStatement psDescontar =
                        cn.prepareStatement(sqlDescontar)) {
                    psDescontar.setInt(1, cantidad);
                    psDescontar.setInt(2, idProducto);
                    psDescontar.setInt(3, cantidad);

                    if (psDescontar.executeUpdate() == 0) {
                        return false;
                    }
                }

                try (PreparedStatement psMovimiento = cn.prepareStatement(
                        "INSERT INTO MOVIMIENTOS_INVENTARIO "
                        + "(ID_MOVIMIENTO, ID_PRODUCTO, TIPO, CANTIDAD, REFERENCIA) "
                        + "VALUES (SEQ_MOVIMIENTOS_INVENTARIO.NEXTVAL, ?, 'VENTA', ?, ?)")) {
                    psMovimiento.setInt(1, idProducto);
                    psMovimiento.setInt(2, -cantidad);
                    psMovimiento.setString(3, "Pedido #" + idPedido);
                    psMovimiento.executeUpdate();
                }
            }
        }
    }

    return true;
}

private boolean acreditarPuntos(Connection cn, int idPedido) throws Exception {
    String sqlPuntos =
            "SELECT PE.ID_CLIENTE, "
            + "NVL((SELECT SUM(CASE "
            + "WHEN UPPER(PR.CATEGORIA) = 'SANDWICH' "
            + "THEN DP.CANTIDAD * 2 ELSE 0 END) "
            + "FROM DETALLE_PEDIDO DP "
            + "JOIN PRODUCTOS PR ON PR.ID_PRODUCTO = DP.ID_PRODUCTO "
            + "WHERE DP.ID_PEDIDO = PE.ID_PEDIDO), 0) "
            + "+ NVL((SELECT SUM(DPM.CANTIDAD * 8) "
            + "FROM DETALLE_PEDIDO_MENU DPM "
            + "WHERE DPM.ID_PEDIDO = PE.ID_PEDIDO), 0) AS PUNTOS "
            + "FROM PEDIDOS PE WHERE PE.ID_PEDIDO = ?";

    int idCliente;
    int puntos;
    try (PreparedStatement ps = cn.prepareStatement(sqlPuntos)) {
        ps.setInt(1, idPedido);
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return false;
            }
            idCliente = rs.getInt("ID_CLIENTE");
            puntos = rs.getInt("PUNTOS");
        }
    }

    if (puntos == 0) {
        return true;
    }

    String sqlActualizarCliente =
            "UPDATE CLIENTES SET PUNTOS = PUNTOS + ? "
            + "WHERE ID_CLIENTE = ? AND ACTIVO = 1";
    try (PreparedStatement ps = cn.prepareStatement(sqlActualizarCliente)) {
        ps.setInt(1, puntos);
        ps.setInt(2, idCliente);
        if (ps.executeUpdate() == 0) {
            return false;
        }
    }

    String sqlMovimiento =
            "INSERT INTO MOVIMIENTOS_PUNTOS "
            + "(ID_MOVIMIENTO, ID_CLIENTE, TIPO, PUNTOS, DESCRIPCION) "
            + "VALUES (SEQ_MOVIMIENTOS_PUNTOS.NEXTVAL, ?, ?, ?, ?)";
    try (PreparedStatement ps = cn.prepareStatement(sqlMovimiento)) {
        ps.setInt(1, idCliente);
        ps.setString(2, "ACUMULACION");
        ps.setInt(3, puntos);
        ps.setString(4, "Puntos obtenidos por pedido " + idPedido);
        return ps.executeUpdate() > 0;
    }
}

    public List<Pago> listarPorPedido(int idPedido) {

        List<Pago> pagos = new ArrayList<>();

        if (idPedido <= 0) {
            return pagos;
        }

        String sql =
                "SELECT ID_PAGO, ID_PEDIDO, FECHA_PAGO, "
                + "METODO_PAGO, MONTO, ESTADO, REFERENCIA "
                + "FROM PAGOS WHERE ID_PEDIDO = ? "
                + "ORDER BY FECHA_PAGO DESC";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(new Pago(
                            rs.getInt("ID_PAGO"),
                            rs.getInt("ID_PEDIDO"),
                            rs.getTimestamp("FECHA_PAGO"),
                            rs.getString("METODO_PAGO"),
                            rs.getDouble("MONTO"),
                            rs.getString("ESTADO"),
                            rs.getString("REFERENCIA")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al listar pagos: " + e.getMessage()
            );
        }

        return pagos;
    }
    
    public boolean verificarPedidoParaCobro(int idPedido) {

        if (idPedido <= 0) {
            return false;
        }

        String sqlPedido =
                "SELECT ID_PEDIDO, TOTAL "
                + "FROM PEDIDOS "
                + "WHERE ID_PEDIDO = ? "
                + "AND ESTADO = 'PENDIENTE' "
                + "FOR UPDATE";

        String sqlDetalles =
        "SELECT (SELECT COUNT(*) FROM DETALLE_PEDIDO "
        + "WHERE ID_PEDIDO = ?) + "
        +"(SELECT COUNT(*) FROM DETALLE_PEDIDO_MENU "
        + "WHERE ID_PEDIDO = ?) AS CANTIDAD FROM DUAL";

        try (Connection cn = Conexion.conectar()) {

            cn.setAutoCommit(false);

            try {
                double total;

                // Verificar que el pedido esté pendiente.
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlPedido)) {

                    ps.setInt(1, idPedido);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }

                        total = rs.getDouble("TOTAL");

                        if (total <= 0) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                // Verificar que el pedido tenga detalles.
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlDetalles)) {

                    ps.setInt(1, idPedido);
                    ps.setInt(2, idPedido);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()
                                || rs.getInt("CANTIDAD") == 0) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                cn.commit();
                return true;

            } catch (Exception e) {
                cn.rollback();
                throw e;

            } finally {
                cn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al verificar pedido para cobro: "
                    + e.getMessage()
            );
            return false;
        }
    }

}
