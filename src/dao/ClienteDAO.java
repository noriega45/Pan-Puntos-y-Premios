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
                + "(ID_CLIENTE, NOMBRE, NIT, TELEFONO, CORREO) "
                + "VALUES (SEQ_CLIENTES.NEXTVAL, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getNit());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }
    public List<Cliente> listar() {

    List<Cliente> clientes = new ArrayList<>();

    String sql = "SELECT ID_CLIENTE, NOMBRE, NIT, TELEFONO, CORREO "
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
                    rs.getString("NIT"),
                    rs.getString("TELEFONO"),
                    rs.getString("CORREO")
            );

            clientes.add(cliente);
        }

    } catch (Exception e) {
        System.out.println("Error al listar clientes: " + e.getMessage());
    }

    return clientes;
}
    public boolean actualizar(Cliente cliente) {

    String sql = "UPDATE CLIENTES "
               + "SET NOMBRE = ?, NIT = ?, TELEFONO = ?, CORREO = ? "
               + "WHERE ID_CLIENTE = ?";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getNit());
        ps.setString(3, cliente.getTelefono());
        ps.setString(4, cliente.getCorreo());
        ps.setInt(5, cliente.getIdCliente());

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
    public boolean existe(int idCliente) {
    String sql = "SELECT 1 FROM CLIENTES "
               + "WHERE ID_CLIENTE = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idCliente);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }

    } catch (Exception e) {
        System.out.println("Error al verificar cliente: " + e.getMessage());
        return false;
    }
}
    
}