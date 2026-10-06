package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Categoria;
import util.Conexion;

public class CategoriaDAO {

    public boolean guardar(Categoria categoria) {

        String sql = "INSERT INTO CATEGORIAS "
                + "(ID_CATEGORIA, NOMBRE, DESCRIPCION) "
                + "VALUES (SEQ_CATEGORIAS.NEXTVAL, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar categoria: " + e.getMessage());
            return false;
        }
    }
}
