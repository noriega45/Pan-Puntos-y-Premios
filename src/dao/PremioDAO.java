package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Premio;
import util.Conexion;

public class PremioDAO {

    public List<Premio> listarActivos() {
        List<Premio> premios = new ArrayList<>();
        String sql = "SELECT ID_PREMIO, NOMBRE, DESCRIPCION, PUNTOS_REQUERIDOS, "
                + "EXISTENCIA, ID_PRODUCTO, ID_MENU FROM PREMIOS "
                + "WHERE ACTIVO = 1 AND EXISTENCIA > 0 "
                + "AND (ID_PRODUCTO IS NOT NULL OR ID_MENU IS NOT NULL) "
                + "ORDER BY PUNTOS_REQUERIDOS";
        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Premio premio = new Premio(rs.getInt("ID_PREMIO"),
                        rs.getString("NOMBRE"), rs.getString("DESCRIPCION"),
                        rs.getInt("PUNTOS_REQUERIDOS"), rs.getInt("EXISTENCIA"));
                premio.setIdProducto(rs.getInt("ID_PRODUCTO"));
                premio.setIdMenu(rs.getInt("ID_MENU"));
                premios.add(premio);
            }
        } catch (Exception e) {
            System.out.println("Error al listar premios: " + e.getMessage());
        }
        return premios;
    }

    public boolean guardar(Premio premio) {

        if (premio == null || premio.getNombre() == null
                || premio.getNombre().trim().isEmpty()
                || premio.getPuntosRequeridos() <= 0 || premio.getExistencia() < 0
                || (premio.getIdProducto() > 0) == (premio.getIdMenu() > 0)) {
            return false;
        }

        String sql = "INSERT INTO PREMIOS "
                + "(ID_PREMIO, NOMBRE, DESCRIPCION, PUNTOS_REQUERIDOS, EXISTENCIA, ACTIVO, ID_PRODUCTO, ID_MENU) "
                + "VALUES (SEQ_PREMIOS.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, premio.getNombre());
            ps.setString(2, premio.getDescripcion());
            ps.setInt(3, premio.getPuntosRequeridos());
            ps.setInt(4, premio.getExistencia());
            ps.setInt(5, premio.isActivo() ? 1 : 0);
            if (premio.getIdProducto() > 0) {
                ps.setInt(6, premio.getIdProducto());
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setInt(7, premio.getIdMenu());
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar premio: " + e.getMessage());
            return false;
        }
    }
}
