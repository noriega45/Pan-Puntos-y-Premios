package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.Conexion;

public class CanjeDAO {

    public String canjear(int idCliente, int idPremio) {
        if (idCliente <= 0) {
            return "Selecciona un cliente.";
        }
        if (idPremio <= 0) {
            return "Selecciona un premio.";
        }
        String sqlCliente = "SELECT PUNTOS FROM CLIENTES "
                + "WHERE ID_CLIENTE = ? AND ACTIVO = 1 FOR UPDATE";
        String sqlPremio = "SELECT NOMBRE, PUNTOS_REQUERIDOS, EXISTENCIA, ID_PRODUCTO, ID_MENU "
                + "FROM PREMIOS WHERE ID_PREMIO = ? AND ACTIVO = 1 FOR UPDATE";
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
                            return "El cliente no existe o está inactivo.";
                        }
                        saldo = rs.getInt("PUNTOS");
                    }
                }
                String nombrePremio;
                int costo;
                int existenciaPremio;
                int idProducto;
                int idMenu;
                try (PreparedStatement ps = cn.prepareStatement(sqlPremio)) {
                    ps.setInt(1, idPremio);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return "El premio no existe o está inactivo.";
                        }
                        nombrePremio = rs.getString("NOMBRE");
                        costo = rs.getInt("PUNTOS_REQUERIDOS");
                        existenciaPremio = rs.getInt("EXISTENCIA");
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
                if (existenciaPremio <= 0) {
                    cn.rollback();
                    return "El premio " + nombrePremio + " ya no tiene unidades disponibles.";
                }
                if ((idProducto == 0) == (idMenu == 0)) {
                    cn.rollback();
                    return "El premio " + nombrePremio + " no está asociado a un producto o menú.";
                }
                if (saldo < costo) {
                    cn.rollback();
                    return "Puntos insuficientes: el cliente tiene " + saldo
                            + " y el premio requiere " + costo + ".";
                }
                if (idProducto > 0) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "UPDATE PRODUCTOS SET EXISTENCIA = EXISTENCIA - 1 "
                            + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1 AND EXISTENCIA >= 1")) {
                        ps.setInt(1, idProducto);
                        if (ps.executeUpdate() != 1) {
                            cn.rollback();
                            return "No hay existencias en inventario del producto de este premio.";
                        }
                    }
                    registrarMovimientoInventario(cn, idProducto, -1, "Canje de premio #" + idPremio);
                } else if (!descontarComponentesMenu(cn, idMenu, idPremio)) {
                    cn.rollback();
                    return "No hay existencias suficientes de los productos que forman el menú de este premio.";
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE CLIENTES SET PUNTOS = PUNTOS - ? "
                        + "WHERE ID_CLIENTE = ? AND ACTIVO = 1 AND PUNTOS >= ?")) {
                    ps.setInt(1, costo);
                    ps.setInt(2, idCliente);
                    ps.setInt(3, costo);
                    if (ps.executeUpdate() != 1) {
                        cn.rollback();
                        return "No se pudieron descontar los puntos del cliente.";
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE PREMIOS SET EXISTENCIA = EXISTENCIA - 1 "
                        + "WHERE ID_PREMIO = ? AND ACTIVO = 1 AND EXISTENCIA > 0")) {
                    ps.setInt(1, idPremio);
                    if (ps.executeUpdate() != 1) {
                        cn.rollback();
                        return "El premio ya no tiene unidades disponibles.";
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO CANJES (ID_CANJE, ID_CLIENTE, ID_PREMIO, PUNTOS_UTILIZADOS) "
                        + "VALUES (SEQ_CANJES.NEXTVAL, ?, ?, ?)")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idPremio);
                    ps.setInt(3, costo);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO MOVIMIENTOS_PUNTOS "
                        + "(ID_MOVIMIENTO, ID_CLIENTE, TIPO, PUNTOS, DESCRIPCION) "
                        + "VALUES (SEQ_MOVIMIENTOS_PUNTOS.NEXTVAL, ?, 'CANJE', ?, ?)")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, costo);
                    ps.setString(3, "Canje: " + nombrePremio);
                    ps.executeUpdate();
                }
                cn.commit();
                return null;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(autoCommitOriginal);
            }
        } catch (Exception e) {
            System.out.println("Error al registrar canje: " + e.getMessage());
            return "Error de base de datos al registrar el canje: " + e.getMessage();
        }
    }

    public List<Object[]> listarHistorialCliente(int idCliente) {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT C.FECHA, P.NOMBRE, C.PUNTOS_UTILIZADOS "
                + "FROM CANJES C JOIN PREMIOS P ON P.ID_PREMIO = C.ID_PREMIO "
                + "WHERE C.ID_CLIENTE = ? ORDER BY C.FECHA DESC, C.ID_CANJE DESC";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getTimestamp("FECHA"), rs.getString("NOMBRE"),
                        rs.getInt("PUNTOS_UTILIZADOS")});
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar historial de canjes: " + e.getMessage());
        }
        return filas;
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

}
