package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.DetalleAbastecimiento;
import util.Conexion;

public class DetalleAbastecimientoDAO {

    public boolean guardar(DetalleAbastecimiento detalle) {

        String sql = "INSERT INTO DETALLE_ABASTECIMIENTO "
                + "(ID_DETALLE, ID_ABASTECIMIENTO, ID_PRODUCTO, CANTIDAD, COSTO_UNITARIO) "
                + "VALUES (SEQ_DETALLE_ABAST.NEXTVAL, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdAbastecimiento());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getCostoUnitario());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar detalle de abastecimiento: " + e.getMessage());
            return false;
        }
    }
}
