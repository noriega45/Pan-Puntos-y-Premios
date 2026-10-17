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

}
