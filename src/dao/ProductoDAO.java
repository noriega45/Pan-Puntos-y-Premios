package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Producto;
import util.Conexion;

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
}