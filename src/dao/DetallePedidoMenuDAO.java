package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.DetallePedidoMenu;
import util.Conexion;
import java.util.ArrayList;
import java.util.List;
import model.DetallePedidoMenu;

public class DetallePedidoMenuDAO {

    public boolean guardar(DetallePedidoMenu detalle) {
        if (detalle == null
                || detalle.getIdPedido() <= 0
                || detalle.getIdMenu() <= 0
                || detalle.getCantidad() <= 0) {
            return false;
        }

        String sqlPedido =
                "SELECT ID_PEDIDO FROM PEDIDOS "
                + "WHERE ID_PEDIDO = ? AND ESTADO = 'PENDIENTE' "
                + "FOR UPDATE";

        String sqlMenu =
                "SELECT PRECIO FROM MENUS "
                + "WHERE ID_MENU = ? AND ACTIVO = 1";

        String sqlComponentes =
                "SELECT P.EXISTENCIA, P.ACTIVO, DM.CANTIDAD "
                + "FROM DETALLE_MENU DM "
                + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                + "WHERE DM.ID_MENU = ?";

        String sqlInsert =
                "INSERT INTO DETALLE_PEDIDO_MENU "
                + "(ID_DETALLE_PEDIDO_MENU, ID_PEDIDO, ID_MENU, "
                + "CANTIDAD, PRECIO_UNITARIO) "
                + "VALUES (SEQ_DETALLE_PEDIDO_MENU.NEXTVAL, ?, ?, ?, ?)";

        String sqlTotal =
                "UPDATE PEDIDOS SET TOTAL = "
                + "(SELECT NVL(SUM(CANTIDAD * PRECIO_UNITARIO), 0) "
                + "FROM DETALLE_PEDIDO WHERE ID_PEDIDO = ?) + "
                + "(SELECT NVL(SUM(CANTIDAD * PRECIO_UNITARIO), 0) "
                + "FROM DETALLE_PEDIDO_MENU WHERE ID_PEDIDO = ?) "
                + "WHERE ID_PEDIDO = ?";

        try (Connection cn = Conexion.conectar()) {
            boolean autoCommitOriginal = cn.getAutoCommit();
            cn.setAutoCommit(false);

            try {
                try (PreparedStatement ps = cn.prepareStatement(sqlPedido)) {
                    ps.setInt(1, detalle.getIdPedido());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                double precioMenu;
                try (PreparedStatement ps = cn.prepareStatement(sqlMenu)) {
                    ps.setInt(1, detalle.getIdMenu());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                        precioMenu = rs.getDouble("PRECIO");
                    }
                }

                boolean tieneComponentes = false;
                try (PreparedStatement ps =
                        cn.prepareStatement(sqlComponentes)) {
                    ps.setInt(1, detalle.getIdMenu());

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            tieneComponentes = true;
                            int existencia = rs.getInt("EXISTENCIA");
                            int activo = rs.getInt("ACTIVO");
                            int cantidadNecesaria =
                                    rs.getInt("CANTIDAD") * detalle.getCantidad();

                            if (activo != 1 || existencia < cantidadNecesaria) {
                                cn.rollback();
                                return false;
                            }
                        }
                    }
                }

                if (!tieneComponentes) {
                    cn.rollback();
                    return false;
                }

                try (PreparedStatement ps = cn.prepareStatement(sqlInsert)) {
                    ps.setInt(1, detalle.getIdPedido());
                    ps.setInt(2, detalle.getIdMenu());
                    ps.setInt(3, detalle.getCantidad());
                    ps.setDouble(4, precioMenu);

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(sqlTotal)) {
                    ps.setInt(1, detalle.getIdPedido());
                    ps.setInt(2, detalle.getIdPedido());
                    ps.setInt(3, detalle.getIdPedido());

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
                cn.setAutoCommit(autoCommitOriginal);
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al guardar menú en pedido: " + e.getMessage()
            );
            return false;
        }
    }
    public List<DetallePedidoMenu> listarPorPedido(int idPedido) {
    List<DetallePedidoMenu> detalles = new ArrayList<>();

    if (idPedido <= 0) {
        return detalles;
    }

    String sql = "SELECT ID_DETALLE_PEDIDO_MENU, ID_PEDIDO, ID_MENU, "
            + "CANTIDAD, PRECIO_UNITARIO "
            + "FROM DETALLE_PEDIDO_MENU "
            + "WHERE ID_PEDIDO = ? "
            + "ORDER BY ID_DETALLE_PEDIDO_MENU";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idPedido);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                detalles.add(new DetallePedidoMenu(
                        rs.getInt("ID_DETALLE_PEDIDO_MENU"),
                        rs.getInt("ID_PEDIDO"),
                        rs.getInt("ID_MENU"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("PRECIO_UNITARIO")
                ));
            }
        }

    } catch (Exception e) {
        System.out.println("Error al listar menús del pedido: "
                + e.getMessage());
    }

    return detalles;
}
}