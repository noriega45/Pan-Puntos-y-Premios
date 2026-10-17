package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;
import util.Conexion;

public class CategoriaDAO {

    public boolean guardar(Categoria categoria) {
        String sql = "INSERT INTO CATEGORIAS "
                + "(ID_CATEGORIA, NOMBRE, DESCRIPCION, TIPO) "
                + "VALUES (SEQ_CATEGORIAS.NEXTVAL, ?, ?, ?)";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setString(3, categoria.getTipo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al guardar categoria: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT ID_CATEGORIA, NOMBRE, DESCRIPCION, TIPO "
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
                categoria.setTipo(rs.getString("TIPO"));
                categorias.add(categoria);
            }
        } catch (Exception e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }
        return categorias;
    }

    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE CATEGORIAS "
                + "SET NOMBRE = ?, DESCRIPCION = ?, TIPO = ? "
                + "WHERE ID_CATEGORIA = ? AND ACTIVO = 1";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setString(3, categoria.getTipo());
            ps.setInt(4, categoria.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
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
            System.out.println("Error al desactivar categoría: " + e.getMessage());
            return false;
        }
    }

    public int contarProductosActivos(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS P "
                + "JOIN CATEGORIAS C ON C.NOMBRE = P.CATEGORIA "
                + "WHERE C.ID_CATEGORIA = ? AND P.ACTIVO = 1";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al contar productos de la categoría: " + e.getMessage());
        }
        return 0;
    }

    public int contarProductos(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM PRODUCTOS P "
                + "JOIN CATEGORIAS C ON C.NOMBRE = P.CATEGORIA "
                + "WHERE C.ID_CATEGORIA = ?";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al contar productos de la categoría: " + e.getMessage());
        }
        return 0;
    }
}
