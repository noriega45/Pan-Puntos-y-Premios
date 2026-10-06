package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Premio;
import util.Conexion;

public class PremioDAO {

    public boolean guardar(Premio premio) {

        String sql = "INSERT INTO PREMIOS "
                + "(ID_PREMIO, NOMBRE, DESCRIPCION, PUNTOS_REQUERIDOS, EXISTENCIA, ACTIVO) "
                + "VALUES (SEQ_PREMIOS.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, premio.getNombre());
            ps.setString(2, premio.getDescripcion());
            ps.setInt(3, premio.getPuntosRequeridos());
            ps.setInt(4, premio.getExistencia());
            ps.setInt(5, premio.isActivo() ? 1 : 0);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar premio: " + e.getMessage());
            return false;
        }
    }
}
