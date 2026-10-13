package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Producto;
import util.Conexion;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean guardar(Producto producto) {

        String sql = "INSERT INTO PRODUCTOS "
                + "(ID_PRODUCTO, CODIGO, CATEGORIA, NOMBRE, PRECIO, EXISTENCIA, ACTIVO) "
                + "VALUES (SEQ_PRODUCTOS.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getCategoria());
            ps.setString(3, producto.getNombre());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getExistencia());
            ps.setInt(6, producto.isActivo() ? 1 : 0);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }
    
public List<Producto> listar() {
    List<Producto> productos = new ArrayList<>();

    String sql = "SELECT ID_PRODUCTO, CODIGO, CATEGORIA, "
               + "NOMBRE, PRECIO, EXISTENCIA "
               + "FROM PRODUCTOS "
               + "WHERE ACTIVO = 1 "
               + "ORDER BY ID_PRODUCTO";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Producto producto = new Producto(
                rs.getInt("ID_PRODUCTO"),
                rs.getString("CODIGO"),
                rs.getString("CATEGORIA"),
                rs.getString("NOMBRE"),
                rs.getDouble("PRECIO"),
                rs.getInt("EXISTENCIA")
            );

            productos.add(producto);
        }

    } catch (Exception e) {
        System.out.println(
            "Error al listar productos: " + e.getMessage()
        );
    }

    return productos;
}

public boolean actualizar(Producto producto) {
    String sql = "UPDATE PRODUCTOS "
               + "SET CODIGO = ?, CATEGORIA = ?, NOMBRE = ?, "
               + "PRECIO = ?, EXISTENCIA = ? "
               + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, producto.getCodigo());
        ps.setString(2, producto.getCategoria());
        ps.setString(3, producto.getNombre());
        ps.setDouble(4, producto.getPrecio());
        ps.setInt(5, producto.getExistencia());
        ps.setInt(6, producto.getIdProducto());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println("Error al actualizar producto: " + e.getMessage());
        return false;
    }
}


public boolean desactivar(int idProducto) {
    String sql = "UPDATE PRODUCTOS SET ACTIVO = 0 "
               + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idProducto);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println(
            "Error al desactivar producto: " + e.getMessage()
        );
        return false;
    }
   }
        
public boolean aumentarExistencia(int idProducto, int cantidad) {

    if (idProducto <= 0 || cantidad <= 0) {
        return false;
    }

    String sql = "UPDATE PRODUCTOS "
               + "SET EXISTENCIA = EXISTENCIA + ? "
               + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, cantidad);
        ps.setInt(2, idProducto);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println(
            "Error al aumentar existencia: " + e.getMessage()
        );
        return false;
    }
}

}