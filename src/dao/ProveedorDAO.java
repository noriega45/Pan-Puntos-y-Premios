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
        
public java.util.List<Proveedor> listar() {
    java.util.List<Proveedor> proveedores = new java.util.ArrayList<>();

    String sql = "SELECT ID_PROVEEDOR, NOMBRE, NIT, TELEFONO, "
               + "CORREO, DIRECCION, ACTIVO "
               + "FROM PROVEEDORES "
               + "ORDER BY ID_PROVEEDOR";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
        Proveedor proveedor = new Proveedor(
            rs.getInt("ID_PROVEEDOR"),
            rs.getString("NOMBRE"),
            rs.getString("NIT"),
            rs.getString("TELEFONO"),
            rs.getString("CORREO"),
            rs.getString("DIRECCION")
        );

        proveedor.setActivo(rs.getInt("ACTIVO") == 1);
        proveedores.add(proveedor);
        }

    } catch (Exception e) {
        System.out.println("Error al listar proveedores: " + e.getMessage());
    }

    return proveedores;
}
    public boolean actualizar(Proveedor proveedor) {
    String sql = "UPDATE PROVEEDORES "
               + "SET NOMBRE = ?, NIT = ?, TELEFONO = ?, "
               + "CORREO = ?, DIRECCION = ? "
               + "WHERE ID_PROVEEDOR = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, proveedor.getNombre());
        ps.setString(2, proveedor.getNit());
        ps.setString(3, proveedor.getTelefono());
        ps.setString(4, proveedor.getCorreo());
        ps.setString(5, proveedor.getDireccion());
        ps.setInt(6, proveedor.getIdProveedor());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println("Error al actualizar proveedor: " + e.getMessage());
        return false;
    }
}
    public boolean desactivar(int idProveedor) {
    String sql = "UPDATE PROVEEDORES "
               + "SET ACTIVO = 0 "
               + "WHERE ID_PROVEEDOR = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idProveedor);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println("Error al desactivar proveedor: " + e.getMessage());
        return false;
    }
}


}
