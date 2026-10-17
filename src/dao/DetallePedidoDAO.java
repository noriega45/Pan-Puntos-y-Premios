
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.DetallePedido;
import util.Conexion;

public class DetallePedidoDAO {

    public boolean guardar(DetallePedido detalle) {

        if (detalle == null
                || detalle.getIdPedido() <= 0
                || detalle.getIdProducto() <= 0
                || detalle.getCantidad() <= 0
                || detalle.getPrecioUnitario() < 0) {
            return false;
        }

        String sqlPedido =
                "SELECT ID_PEDIDO FROM PEDIDOS "
                + "WHERE ID_PEDIDO = ? AND ESTADO = 'PENDIENTE' "
                + "FOR UPDATE";

        String sqlProducto =
                "SELECT EXISTENCIA FROM PRODUCTOS "
                + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1";

        String sqlDetalle =
                "INSERT INTO DETALLE_PEDIDO "
                + "(ID_DETALLE, ID_PEDIDO, ID_PRODUCTO, "
                + "CANTIDAD, PRECIO_UNITARIO) "
                + "VALUES (SEQ_DETALLE_PEDIDO.NEXTVAL, ?, ?, ?, ?)";

        String sqlTotal =
                "UPDATE PEDIDOS SET TOTAL = ("
                + "SELECT NVL(SUM(CANTIDAD * PRECIO_UNITARIO), 0) "
                + "FROM DETALLE_PEDIDO WHERE ID_PEDIDO = ?) "
                + "WHERE ID_PEDIDO = ?";

        try (Connection cn = Conexion.conectar()) {

            cn.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlPedido)) {

                    ps.setInt(1, detalle.getIdPedido());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlProducto)) {

                    ps.setInt(1, detalle.getIdProducto());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()
                                || rs.getInt("EXISTENCIA")
                                < detalle.getCantidad()) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlDetalle)) {

                    ps.setInt(1, detalle.getIdPedido());
                    ps.setInt(2, detalle.getIdProducto());
                    ps.setInt(3, detalle.getCantidad());
                    ps.setDouble(4, detalle.getPrecioUnitario());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                        cn.prepareStatement(sqlTotal)) {

                    ps.setInt(1, detalle.getIdPedido());
                    ps.setInt(2, detalle.getIdPedido());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
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
                    "Error al guardar detalle de pedido: "
                    + e.getMessage()
            );
            return false;
        }
    }

    public List<DetallePedido> listarPorPedido(int idPedido) {

        List<DetallePedido> detalles = new ArrayList<>();

        if (idPedido <= 0) {
            return detalles;
        }

        String sql =
                "SELECT ID_DETALLE, ID_PEDIDO, ID_PRODUCTO, "
                + "CANTIDAD, PRECIO_UNITARIO "
                + "FROM DETALLE_PEDIDO "
                + "WHERE ID_PEDIDO = ? ORDER BY ID_DETALLE";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetallePedido(
                            rs.getInt("ID_DETALLE"),
                            rs.getInt("ID_PEDIDO"),
                            rs.getInt("ID_PRODUCTO"),
                            rs.getInt("CANTIDAD"),
                            rs.getDouble("PRECIO_UNITARIO")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al listar detalles del pedido: "
                    + e.getMessage()
            );
        }

        return detalles;
    }

    public boolean actualizarTotalPedido(int idPedido) {

        if (idPedido <= 0) {
            return false;
        }

        String sql =
                "UPDATE PEDIDOS SET TOTAL = ("
                + "SELECT NVL(SUM(CANTIDAD * PRECIO_UNITARIO), 0) "
                + "FROM DETALLE_PEDIDO WHERE ID_PEDIDO = ?) "
                + "WHERE ID_PEDIDO = ?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            ps.setInt(2, idPedido);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println(
                    "Error al actualizar total del pedido: "
                    + e.getMessage()
            );
            return false;
        }
    }
}
