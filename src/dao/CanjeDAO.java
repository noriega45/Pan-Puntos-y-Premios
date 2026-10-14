package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Canje;
import util.Conexion;

public class CanjeDAO {

    public boolean canjear(int idCliente, int idPremio) {
        if (idCliente <= 0 || idPremio <= 0) {
            return false;
        }

        String sqlCliente = "SELECT PUNTOS FROM CLIENTES "
                + "WHERE ID_CLIENTE = ? AND ACTIVO = 1 FOR UPDATE";
        String sqlPremio = "SELECT NOMBRE, PUNTOS_REQUERIDOS, ID_PRODUCTO, ID_MENU "
                + "FROM PREMIOS WHERE ID_PREMIO = ? AND ACTIVO = 1 "
                + "AND EXISTENCIA > 0 AND (ID_PRODUCTO IS NOT NULL OR ID_MENU IS NOT NULL) FOR UPDATE";

        try (Connection cn = Conexion.conectar()) {
            boolean autoCommitOriginal = cn.getAutoCommit();
            cn.setAutoCommit(false);
            try {
                int saldo;
                try (PreparedStatement ps = cn.prepareStatement(sqlCliente)) {
                    ps.setInt(1, idCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                        saldo = rs.getInt("PUNTOS");
                    }
                }

                String nombrePremio;
                int costo;
                int idProducto;
                int idMenu;
                try (PreparedStatement ps = cn.prepareStatement(sqlPremio)) {
                    ps.setInt(1, idPremio);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                        nombrePremio = rs.getString("NOMBRE");
                        costo = rs.getInt("PUNTOS_REQUERIDOS");
                        idProducto = rs.getInt("ID_PRODUCTO");
                        if (rs.wasNull()) {
                            idProducto = 0;
                        }
                        idMenu = rs.getInt("ID_MENU");
                        if (rs.wasNull()) {
                            idMenu = 0;
                        }
                    }
                }

                if (costo <= 0 || saldo < costo || (idProducto == 0) == (idMenu == 0)) {
                    cn.rollback();
                    return false;
                }

                if (idProducto > 0) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "UPDATE PRODUCTOS SET EXISTENCIA = EXISTENCIA - 1 "
                            + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1 AND EXISTENCIA >= 1")) {
                        ps.setInt(1, idProducto);
                        if (ps.executeUpdate() != 1) {
                            cn.rollback();
                            return false;
                        }
                    }
                    registrarMovimientoInventario(cn, idProducto, -1, "Canje de premio #" + idPremio);
                } else if (!descontarComponentesMenu(cn, idMenu, idPremio)) {
                    cn.rollback();
                    return false;
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE CLIENTES SET PUNTOS = PUNTOS - ? "
                        + "WHERE ID_CLIENTE = ? AND ACTIVO = 1 AND PUNTOS >= ?")) {
                    ps.setInt(1, costo);
                    ps.setInt(2, idCliente);
                    ps.setInt(3, costo);
                    if (ps.executeUpdate() != 1) {
                        cn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE PREMIOS SET EXISTENCIA = EXISTENCIA - 1 "
                        + "WHERE ID_PREMIO = ? AND ACTIVO = 1 AND EXISTENCIA > 0")) {
                    ps.setInt(1, idPremio);
                    if (ps.executeUpdate() != 1) {
                        cn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO CANJES (ID_CANJE, ID_CLIENTE, ID_PREMIO, PUNTOS_UTILIZADOS) "
                        + "VALUES (SEQ_CANJES.NEXTVAL, ?, ?, ?)")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idPremio);
                    ps.setInt(3, costo);
                    if (ps.executeUpdate() != 1) {
                        cn.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO MOVIMIENTOS_PUNTOS "
                        + "(ID_MOVIMIENTO, ID_CLIENTE, TIPO, PUNTOS, DESCRIPCION) "
                        + "VALUES (SEQ_MOVIMIENTOS_PUNTOS.NEXTVAL, ?, 'CANJE', ?, ?)")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, costo);
                    ps.setString(3, "Canje: " + nombrePremio);
                    if (ps.executeUpdate() != 1) {
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
            System.out.println("Error al registrar canje: " + e.getMessage());
            return false;
        }
    }

    private boolean descontarComponentesMenu(Connection cn, int idMenu, int idPremio) throws Exception {
        boolean tieneComponentes = false;
        String sqlComponentes = "SELECT P.ID_PRODUCTO, P.ACTIVO, DM.CANTIDAD "
                + "FROM DETALLE_MENU DM JOIN PRODUCTOS P ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                + "WHERE DM.ID_MENU = ?";
        try (PreparedStatement ps = cn.prepareStatement(sqlComponentes)) {
            ps.setInt(1, idMenu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tieneComponentes = true;
                    int idProducto = rs.getInt("ID_PRODUCTO");
                    int cantidad = rs.getInt("CANTIDAD");
                    if (rs.getInt("ACTIVO") != 1 || cantidad <= 0) {
                        return false;
                    }
                    try (PreparedStatement descontar = cn.prepareStatement(
                            "UPDATE PRODUCTOS SET EXISTENCIA = EXISTENCIA - ? "
                            + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1 AND EXISTENCIA >= ?")) {
                        descontar.setInt(1, cantidad);
                        descontar.setInt(2, idProducto);
                        descontar.setInt(3, cantidad);
                        if (descontar.executeUpdate() != 1) {
                            return false;
                        }
                    }
                    registrarMovimientoInventario(cn, idProducto, -cantidad,
                            "Canje de premio #" + idPremio + ", menú #" + idMenu);
                }
            }
        }
        return tieneComponentes;
    }

    private void registrarMovimientoInventario(Connection cn, int idProducto,
            int cantidad, String referencia) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO MOVIMIENTOS_INVENTARIO "
                + "(ID_MOVIMIENTO, ID_PRODUCTO, TIPO, CANTIDAD, REFERENCIA) "
                + "VALUES (SEQ_MOVIMIENTOS_INVENTARIO.NEXTVAL, ?, 'CANJE', ?, ?)")) {
            ps.setInt(1, idProducto);
            ps.setInt(2, cantidad);
            ps.setString(3, referencia);
            ps.executeUpdate();
        }
    }

    public List<Canje> listarPorCliente(int idCliente) {
        List<Canje> canjes = new ArrayList<>();
        if (idCliente <= 0) {
            return canjes;
        }
        String sql = "SELECT ID_CANJE, ID_CLIENTE, ID_PREMIO, FECHA, PUNTOS_UTILIZADOS "
                + "FROM CANJES WHERE ID_CLIENTE = ? ORDER BY FECHA DESC, ID_CANJE DESC";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    canjes.add(new Canje(rs.getInt("ID_CANJE"), rs.getInt("ID_CLIENTE"),
                            rs.getInt("ID_PREMIO"), rs.getDate("FECHA"),
                            rs.getInt("PUNTOS_UTILIZADOS")));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar canjes: " + e.getMessage());
        }
        return canjes;
    }
}
