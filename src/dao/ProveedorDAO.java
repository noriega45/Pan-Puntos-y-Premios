package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Proveedor;
import util.Conexion;

public class ProveedorDAO {

    public boolean guardar(Proveedor proveedor) {

        String sql = "INSERT INTO PROVEEDORES "
                + "(ID_PROVEEDOR, NOMBRE, NIT, TELEFONO, CORREO, DIRECCION, ACTIVO) "
                + "VALUES (SEQ_PROVEEDORES.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getNit());
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getCorreo());
            ps.setString(5, proveedor.getDireccion());
            ps.setInt(6, proveedor.isActivo() ? 1 : 0);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }
}
