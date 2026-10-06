package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Cliente;
import util.Conexion;

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
}