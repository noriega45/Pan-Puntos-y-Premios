
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

        String sql =
                "INSERT INTO PAGOS "
                + "(ID_PAGO, ID_PEDIDO, METODO_PAGO, MONTO, "
                + "ESTADO, REFERENCIA) "
                + "VALUES (SEQ_PAGOS.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, pago.getIdPedido());
            ps.setString(2, pago.getMetodoPago().toUpperCase());
            ps.setDouble(3, pago.getMonto());
            ps.setString(4, pago.getEstado().toUpperCase());
            ps.setString(5, pago.getReferencia());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println(
                    "Error al guardar pago: " + e.getMessage()
            );
            return false;
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
                "SELECT COUNT(*) AS CANTIDAD "
                + "FROM DETALLE_PEDIDO "
                + "WHERE ID_PEDIDO = ?";

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
