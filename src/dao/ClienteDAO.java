package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Cliente;
import util.Conexion;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean guardar(Cliente cliente) {

        String sql = "INSERT INTO CLIENTES "
                + "(ID_CLIENTE, NOMBRE, TELEFONO, CORREO) "
                + "SELECT NVL(MAX(ID_CLIENTE), 0) + 1, ?, ?, ? FROM CLIENTES";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }
    public List<Cliente> listar() {

    List<Cliente> clientes = new ArrayList<>();

    String sql = "SELECT ID_CLIENTE, NOMBRE, TELEFONO, CORREO, PUNTOS "
               + "FROM CLIENTES "
               + "WHERE ACTIVO = 1 "
               + "ORDER BY ID_CLIENTE";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Cliente cliente = new Cliente(
                    rs.getInt("ID_CLIENTE"),
                    rs.getString("NOMBRE"),
                    null,
                    rs.getString("TELEFONO"),
                    rs.getString("CORREO")
            );
            cliente.setPuntos(rs.getInt("PUNTOS"));

            clientes.add(cliente);
        }

    } catch (Exception e) {
        System.out.println("Error al listar clientes: " + e.getMessage());
    }

    return clientes;
}

public Cliente buscarPorId(int idCliente) {
    String sql = "SELECT ID_CLIENTE, NOMBRE, TELEFONO, CORREO, PUNTOS "
            + "FROM CLIENTES WHERE ID_CLIENTE = ? AND ACTIVO = 1";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Cliente cliente = new Cliente(rs.getInt("ID_CLIENTE"),
                        rs.getString("NOMBRE"), null,
                        rs.getString("TELEFONO"), rs.getString("CORREO"));
                cliente.setPuntos(rs.getInt("PUNTOS"));
                return cliente;
            }
        }
    } catch (Exception e) {
        System.out.println("Error al buscar cliente: " + e.getMessage());
    }
    return null;
}

public List<Object[]> listarPedidosCliente(int idCliente) {
    List<Object[]> filas = new ArrayList<>();
    String sql = "SELECT ID_PEDIDO, FECHA, TOTAL, ESTADO FROM PEDIDOS "
            + "WHERE ID_CLIENTE = ? ORDER BY FECHA DESC, ID_PEDIDO DESC";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt("ID_PEDIDO"), rs.getDate("FECHA"),
                    rs.getDouble("TOTAL"), rs.getString("ESTADO")});
            }
        }
    } catch (Exception e) {
        System.out.println("Error al listar pedidos del cliente: " + e.getMessage());
    }
    return filas;
}

public List<Object[]> listarMovimientosPuntos(int idCliente) {
    List<Object[]> filas = new ArrayList<>();
    String sql = "SELECT FECHA, TIPO, PUNTOS, DESCRIPCION FROM MOVIMIENTOS_PUNTOS "
            + "WHERE ID_CLIENTE = ? ORDER BY FECHA DESC, ID_MOVIMIENTO DESC";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getTimestamp("FECHA"), rs.getString("TIPO"),
                    rs.getInt("PUNTOS"), rs.getString("DESCRIPCION")});
            }
        }
    } catch (Exception e) {
        System.out.println("Error al listar movimientos de puntos: " + e.getMessage());
    }
    return filas;
}
    public boolean actualizar(Cliente cliente) {

    String sql = "UPDATE CLIENTES "
               + "SET NOMBRE = ?, TELEFONO = ?, CORREO = ? "
               + "WHERE ID_CLIENTE = ?";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getTelefono());
        ps.setString(3, cliente.getCorreo());
        ps.setInt(4, cliente.getIdCliente());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println(
            "Error al actualizar cliente: " + e.getMessage()
        );
        return false;
    }
}
    public boolean desactivar(int idCliente) {
    String sql = "UPDATE CLIENTES SET ACTIVO = 0 "
               + "WHERE ID_CLIENTE = ?";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println(
            "Error al desactivar cliente: " + e.getMessage()
        );
        return false;
    }
}
    public int buscarIdDuplicado(String telefono, String correo, int idExcluir) {
        if (telefono == null || telefono.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()) {
            return 0;
        }
        String sql = "SELECT MIN(ID_CLIENTE) FROM CLIENTES "
                + "WHERE TRIM(TELEFONO) = ? AND LOWER(TRIM(CORREO)) = LOWER(?) AND ID_CLIENTE <> ?";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, telefono.trim());
            ps.setString(2, correo.trim());
            ps.setInt(3, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar cliente duplicado: " + e.getMessage());
        }
        return 0;
    }

}
