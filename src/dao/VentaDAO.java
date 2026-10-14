package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ItemVenta;
import model.MenuVenta;
import model.ProductoVenta;
import util.Conexion;

public class VentaDAO {

    public int crearPedido(int idCliente, List<ItemVenta> items) {
        if (idCliente <= 0 || items == null || items.isEmpty()) {
            return -1;
        }

        try (Connection cn = Conexion.conectar()) {
            boolean autoCommitOriginal = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT ID_CLIENTE FROM CLIENTES WHERE ID_CLIENTE = ? AND ACTIVO = 1 FOR UPDATE")) {
                    ps.setInt(1, idCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return -1;
                        }
                    }
                }

                Map<Integer, Integer> demanda = new LinkedHashMap<>();
                double total = 0;
                for (ItemVenta item : items) {
                    if (item == null || item.getCantidad() <= 0) {
                        cn.rollback();
                        return -1;
                    }
                    if (item instanceof ProductoVenta) {
                        double precio;
                        String categoria;
                        int existencia;
                        try (PreparedStatement ps = cn.prepareStatement(
                                "SELECT PRECIO, CATEGORIA, EXISTENCIA FROM PRODUCTOS "
                                + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1 FOR UPDATE")) {
                            ps.setInt(1, item.getId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    cn.rollback();
                                    return -1;
                                }
                                precio = rs.getDouble("PRECIO");
                                categoria = rs.getString("CATEGORIA");
                                existencia = rs.getInt("EXISTENCIA");
                            }
                        }
                        if (existencia < item.getCantidad()) {
                            cn.rollback();
                            return -1;
                        }
                        total += precio * item.getCantidad();
                        demanda.merge(item.getId(), item.getCantidad(), Integer::sum);
                    } else if (item instanceof MenuVenta) {
                        double precio;
                        try (PreparedStatement ps = cn.prepareStatement(
                                "SELECT PRECIO FROM MENUS WHERE ID_MENU = ? AND ACTIVO = 1 FOR UPDATE")) {
                            ps.setInt(1, item.getId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    cn.rollback();
                                    return -1;
                                }
                                precio = rs.getDouble("PRECIO");
                            }
                        }
                        total += precio * item.getCantidad();
                        boolean completo = false;
                        try (PreparedStatement ps = cn.prepareStatement(
                                "SELECT P.ID_PRODUCTO, P.EXISTENCIA, P.ACTIVO, DM.CANTIDAD "
                                + "FROM DETALLE_MENU DM JOIN PRODUCTOS P ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                                + "WHERE DM.ID_MENU = ?")) {
                            ps.setInt(1, item.getId());
                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    completo = true;
                                    int idProducto = rs.getInt("ID_PRODUCTO");
                                    int necesario = rs.getInt("CANTIDAD") * item.getCantidad();
                                    if (rs.getInt("ACTIVO") != 1 || necesario <= 0
                                            || rs.getInt("EXISTENCIA") < necesario) {
                                        cn.rollback();
                                        return -1;
                                    }
                                    demanda.merge(idProducto, necesario, Integer::sum);
                                }
                            }
                        }
                        if (!completo) {
                            cn.rollback();
                            return -1;
                        }
                    } else {
                        cn.rollback();
                        return -1;
                    }
                }

                for (Map.Entry<Integer, Integer> entry : demanda.entrySet()) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "SELECT EXISTENCIA FROM PRODUCTOS WHERE ID_PRODUCTO = ? AND ACTIVO = 1")) {
                        ps.setInt(1, entry.getKey());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || rs.getInt("EXISTENCIA") < entry.getValue()) {
                                cn.rollback();
                                return -1;
                            }
                        }
                    }
                }

                int idPedido;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT SEQ_PEDIDOS.NEXTVAL FROM DUAL");
                     ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        cn.rollback();
                        return -1;
                    }
                    idPedido = rs.getInt(1);
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO PEDIDOS (ID_PEDIDO, ID_CLIENTE, TOTAL, ESTADO) VALUES (?, ?, ?, 'PENDIENTE')")) {
                    ps.setInt(1, idPedido);
                    ps.setInt(2, idCliente);
                    ps.setDouble(3, total);
                    ps.executeUpdate();
                }

                for (ItemVenta item : items) {
                    if (item instanceof ProductoVenta) {
                        double precio;
                        try (PreparedStatement ps = cn.prepareStatement(
                                "SELECT PRECIO FROM PRODUCTOS WHERE ID_PRODUCTO = ? AND ACTIVO = 1")) {
                            ps.setInt(1, item.getId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    cn.rollback();
                                    return -1;
                                }
                                precio = rs.getDouble("PRECIO");
                            }
                        }
                        try (PreparedStatement ps = cn.prepareStatement(
                                "INSERT INTO DETALLE_PEDIDO (ID_DETALLE, ID_PEDIDO, ID_PRODUCTO, CANTIDAD, PRECIO_UNITARIO) "
                                + "VALUES (SEQ_DETALLE_PEDIDO.NEXTVAL, ?, ?, ?, ?)")) {
                            ps.setInt(1, idPedido);
                            ps.setInt(2, item.getId());
                            ps.setInt(3, item.getCantidad());
                            ps.setDouble(4, precio);
                            ps.executeUpdate();
                        }
                    } else {
                        double precio;
                        try (PreparedStatement ps = cn.prepareStatement(
                                "SELECT PRECIO FROM MENUS WHERE ID_MENU = ? AND ACTIVO = 1")) {
                            ps.setInt(1, item.getId());
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    cn.rollback();
                                    return -1;
                                }
                                precio = rs.getDouble("PRECIO");
                            }
                        }
                        try (PreparedStatement ps = cn.prepareStatement(
                                "INSERT INTO DETALLE_PEDIDO_MENU (ID_DETALLE_PEDIDO_MENU, ID_PEDIDO, ID_MENU, CANTIDAD, PRECIO_UNITARIO) "
                                + "VALUES (SEQ_DETALLE_PEDIDO_MENU.NEXTVAL, ?, ?, ?, ?)")) {
                            ps.setInt(1, idPedido);
                            ps.setInt(2, item.getId());
                            ps.setInt(3, item.getCantidad());
                            ps.setDouble(4, precio);
                            ps.executeUpdate();
                        }
                    }
                }
                cn.commit();
                return idPedido;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(autoCommitOriginal);
            }
        } catch (Exception e) {
            System.out.println("Error al crear pedido: " + e.getMessage());
            return -1;
        }
    }
}
