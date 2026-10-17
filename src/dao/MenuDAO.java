package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Menu;
import util.Conexion;

public class MenuDAO {

    public boolean guardar(String nombre, double precio, Map<Integer, Integer> componentes) {
        if (nombre == null || nombre.trim().isEmpty() || precio <= 0
                || componentes == null || componentes.size() < 3) {
            return false;
        }
        try (java.sql.Connection cn = util.Conexion.conectar()) {
            boolean autoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                java.util.Set<String> categorias = new java.util.HashSet<>();
                for (Map.Entry<Integer, Integer> componente : componentes.entrySet()) {
                    if (componente.getKey() <= 0 || componente.getValue() <= 0) {
                        cn.rollback();
                        return false;
                    }
                    try (PreparedStatement ps = cn.prepareStatement(
                            "SELECT C.TIPO FROM PRODUCTOS P JOIN CATEGORIAS C ON C.NOMBRE = P.CATEGORIA "
                            + "WHERE P.ID_PRODUCTO = ? AND P.ACTIVO = 1")) {
                        ps.setInt(1, componente.getKey());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                cn.rollback();
                                return false;
                            }
                            String categoria = rs.getString("TIPO");
                            if (java.util.Arrays.asList("COMIDA", "BEBIDA", "ACOMPANAMIENTO").contains(categoria)) {
                                categorias.add(categoria);
                            }
                        }
                    }
                }
                if (!categorias.containsAll(java.util.Arrays.asList("COMIDA", "BEBIDA", "ACOMPANAMIENTO"))) {
                    cn.rollback();
                    return false;
                }
                int idMenu;
                try (PreparedStatement ps = cn.prepareStatement("SELECT SEQ_MENUS.NEXTVAL FROM DUAL");
                     ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        cn.rollback();
                        return false;
                    }
                    idMenu = rs.getInt(1);
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO MENUS (ID_MENU, NOMBRE, PRECIO, ACTIVO) VALUES (?, ?, ?, 1)")) {
                    ps.setInt(1, idMenu);
                    ps.setString(2, nombre.trim());
                    ps.setDouble(3, precio);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO DETALLE_MENU (ID_DETALLE_MENU, ID_MENU, ID_PRODUCTO, CANTIDAD) "
                        + "VALUES (SEQ_DETALLE_MENU.NEXTVAL, ?, ?, ?)")) {
                    for (Map.Entry<Integer, Integer> componente : componentes.entrySet()) {
                        ps.setInt(1, idMenu);
                        ps.setInt(2, componente.getKey());
                        ps.setInt(3, componente.getValue());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                cn.commit();
                return true;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(autoCommit);
            }
        } catch (Exception e) {
            System.out.println("Error al guardar menú: " + e.getMessage());
            return false;
        }
    }

    public List<Menu> listarActivos() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT M.ID_MENU, M.NOMBRE, M.PRECIO FROM MENUS M "
                + "WHERE M.ACTIVO = 1 AND EXISTS ("
                + "SELECT 1 FROM DETALLE_MENU DM JOIN PRODUCTOS P "
                + "ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                + "JOIN CATEGORIAS C ON C.NOMBRE = P.CATEGORIA "
                + "WHERE DM.ID_MENU = M.ID_MENU "
                + "GROUP BY DM.ID_MENU HAVING COUNT(DISTINCT CASE "
                + "WHEN C.TIPO IN ('COMIDA','BEBIDA','ACOMPANAMIENTO') "
                + "THEN C.TIPO END) = 3) ORDER BY M.NOMBRE";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                menus.add(new Menu(
                        rs.getInt("ID_MENU"),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar menús: " + e.getMessage());
        }
        return menus;
    }
}
