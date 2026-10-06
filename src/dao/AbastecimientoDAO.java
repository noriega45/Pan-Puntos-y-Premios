package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Abastecimiento;
import util.Conexion;

public class AbastecimientoDAO {

    public boolean guardar(Abastecimiento abastecimiento) {

        String sql = "INSERT INTO ABASTECIMIENTOS "
                + "(ID_ABASTECIMIENTO, ID_PROVEEDOR, TOTAL) "
                + "VALUES (SEQ_ABASTECIMIENTOS.NEXTVAL, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, abastecimiento.getIdProveedor());
            ps.setDouble(2, abastecimiento.getTotal());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar abastecimiento: " + e.getMessage());
            return false;
        }
    }
}
