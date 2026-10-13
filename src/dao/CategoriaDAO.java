package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Categoria;
import util.Conexion;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        public List<Categoria> listar() {
    List<Categoria> categorias = new ArrayList<>();

    String sql = "SELECT ID_CATEGORIA, NOMBRE, DESCRIPCION "
               + "FROM CATEGORIAS "
               + "WHERE ACTIVO = 1 "
               + "ORDER BY ID_CATEGORIA";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Categoria categoria = new Categoria(
                rs.getInt("ID_CATEGORIA"),
                rs.getString("NOMBRE"),
                rs.getString("DESCRIPCION")
            );

            categorias.add(categoria);
        }

    } catch (Exception e) {
        System.out.println(
            "Error al listar categorías: " + e.getMessage()
        );
    }

    return categorias;
}

public boolean actualizar(Categoria categoria) {
    String sql = "UPDATE CATEGORIAS "
               + "SET NOMBRE = ?, DESCRIPCION = ? "
               + "WHERE ID_CATEGORIA = ? AND ACTIVO = 1";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, categoria.getNombre());
        ps.setString(2, categoria.getDescripcion());
        ps.setInt(3, categoria.getIdCategoria());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.out.println(
            "Error al actualizar categoría: " + e.getMessage()
        );
        return false;
    }
    }

    public boolean desactivar(int idCategoria) {
        String sql = "UPDATE CATEGORIAS SET ACTIVO = 0 "
                   + "WHERE ID_CATEGORIA = ? AND ACTIVO = 1";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println(
                "Error al desactivar categoría: " + e.getMessage()
            );
            return false;
        }
}
}
