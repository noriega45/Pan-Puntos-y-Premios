package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Pedido;
import util.Conexion;

public class PedidoDAO {

     public java.util.List<Pedido> listarPendientesConDetalles() {
    java.util.List<Pedido> pedidos = new java.util.ArrayList<>();

    String sql = "SELECT P.ID_PEDIDO, P.ID_CLIENTE, P.FECHA, P.TOTAL, P.ESTADO "
            + "FROM PEDIDOS P "
            + "WHERE P.ESTADO = 'PENDIENTE' "
            + "AND (EXISTS (SELECT 1 FROM DETALLE_PEDIDO D "
            + "WHERE D.ID_PEDIDO = P.ID_PEDIDO) "
            + "OR EXISTS (SELECT 1 FROM DETALLE_PEDIDO_MENU DM "
            + "WHERE DM.ID_PEDIDO = P.ID_PEDIDO)) "
            + "ORDER BY P.ID_PEDIDO DESC";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            pedidos.add(new Pedido(
                rs.getInt("ID_PEDIDO"),
                rs.getInt("ID_CLIENTE"),
                rs.getDate("FECHA"),
                rs.getDouble("TOTAL"),
                rs.getString("ESTADO")
            ));
        }

    } catch (Exception e) {
        System.out.println("Error al listar pedidos pendientes: " + e.getMessage());
    }

    return pedidos;
}

public boolean marcarEntregado(int idPedido) {
    String sql = "UPDATE PEDIDOS SET ESTADO = 'ENTREGADO' "
            + "WHERE ID_PEDIDO = ? AND ESTADO = 'PAGADO'";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idPedido);
        return ps.executeUpdate() == 1;
    } catch (Exception e) {
        System.out.println("Error al marcar pedido como entregado: " + e.getMessage());
        return false;
    }
}

public boolean anularPendiente(int idPedido) {
    String sql = "UPDATE PEDIDOS SET ESTADO = 'ANULADO' "
            + "WHERE ID_PEDIDO = ? AND ESTADO = 'PENDIENTE' "
            + "AND NOT EXISTS (SELECT 1 FROM PAGOS P WHERE P.ID_PEDIDO = PEDIDOS.ID_PEDIDO "
            + "AND UPPER(P.ESTADO) = 'APROBADO')";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idPedido);
        return ps.executeUpdate() == 1;
    } catch (Exception e) {
        System.out.println("Error al anular pedido: " + e.getMessage());
        return false;
    }
}

    public java.util.List<Object[]> listarDetalleConNombres(int idPedido) {
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        String sql = "SELECT 'Producto' TIPO, PR.NOMBRE, D.CANTIDAD, D.PRECIO_UNITARIO "
                + "FROM DETALLE_PEDIDO D JOIN PRODUCTOS PR ON PR.ID_PRODUCTO = D.ID_PRODUCTO "
                + "WHERE D.ID_PEDIDO = ? "
                + "UNION ALL "
                + "SELECT 'Menú' TIPO, M.NOMBRE, DM.CANTIDAD, DM.PRECIO_UNITARIO "
                + "FROM DETALLE_PEDIDO_MENU DM JOIN MENUS M ON M.ID_MENU = DM.ID_MENU "
                + "WHERE DM.ID_PEDIDO = ? "
                + "ORDER BY 1 DESC, 2";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idPedido);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cantidad = rs.getInt("CANTIDAD");
                    double precio = rs.getDouble("PRECIO_UNITARIO");
                    filas.add(new Object[]{
                        rs.getString("TIPO").trim(),
                        rs.getString("NOMBRE"),
                        cantidad,
                        String.format("%.2f", precio),
                        String.format("%.2f", cantidad * precio)
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar detalle del pedido: " + e.getMessage());
        }
        return filas;
    }

    public java.util.List<Object[]> listarHistorial(String estado) {
        java.util.List<Object[]> filas = new java.util.ArrayList<>();
        boolean conRechazo = "CON PAGO RECHAZADO".equals(estado);
        boolean filtrar = !conRechazo && estado != null && !estado.isEmpty() && !"TODOS".equals(estado);
        String sql = "SELECT P.ID_PEDIDO, P.ID_CLIENTE, C.NOMBRE, P.FECHA, P.TOTAL, P.ESTADO, "
                + "(SELECT MAX(PG.ESTADO) KEEP (DENSE_RANK LAST ORDER BY PG.ID_PAGO) "
                + " FROM PAGOS PG WHERE PG.ID_PEDIDO = P.ID_PEDIDO) ULTIMO_PAGO, "
                + "(SELECT COUNT(*) FROM PAGOS PG WHERE PG.ID_PEDIDO = P.ID_PEDIDO "
                + " AND PG.ESTADO = 'RECHAZADO') RECHAZOS "
                + "FROM PEDIDOS P JOIN CLIENTES C ON C.ID_CLIENTE = P.ID_CLIENTE "
                + (filtrar ? "WHERE P.ESTADO = ? " : "")
                + (conRechazo ? "WHERE EXISTS (SELECT 1 FROM PAGOS PG WHERE PG.ID_PEDIDO = P.ID_PEDIDO "
                        + "AND PG.ESTADO = 'RECHAZADO') " : "")
                + "ORDER BY P.ID_PEDIDO DESC";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (filtrar) {
                ps.setString(1, estado);
            }
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getInt("ID_PEDIDO"),
                        rs.getInt("ID_CLIENTE") + " - " + rs.getString("NOMBRE"),
                        rs.getTimestamp("FECHA"),
                        String.format("%.2f", rs.getDouble("TOTAL")),
                        rs.getString("ESTADO"),
                        rs.getString("ULTIMO_PAGO") == null ? "Sin pagos" : rs.getString("ULTIMO_PAGO"),
                        rs.getInt("RECHAZOS")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar historial de pedidos: " + e.getMessage());
        }
        return filas;
    }
}
