
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Abastecimiento;
import util.Conexion;

public class AbastecimientoDAO {

    public boolean guardar(Abastecimiento abastecimiento) {

        String sql = "INSERT INTO ABASTECIMIENTOS "
                + "(ID_ABASTECIMIENTO, ID_PROVEEDOR, FECHA, TOTAL) "
                + "VALUES (SEQ_ABASTECIMIENTOS.NEXTVAL, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, abastecimiento.getIdProveedor());
            ps.setDate(2, abastecimiento.getFecha());
            ps.setDouble(3, abastecimiento.getTotal());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar abastecimiento: "
                    + e.getMessage());
            return false;
        }
    }

    public List<Abastecimiento> listar() {

        List<Abastecimiento> abastecimientos = new ArrayList<>();

        String sql = "SELECT ID_ABASTECIMIENTO, ID_PROVEEDOR, FECHA, TOTAL "
                + "FROM ABASTECIMIENTOS "
                + "ORDER BY ID_ABASTECIMIENTO DESC";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Abastecimiento abastecimiento = new Abastecimiento(
                        rs.getInt("ID_ABASTECIMIENTO"),
                        rs.getInt("ID_PROVEEDOR"),
                        rs.getDate("FECHA"),
                        rs.getDouble("TOTAL")
                );

                abastecimientos.add(abastecimiento);
            }

        } catch (Exception e) {
            System.out.println("Error al listar abastecimientos: "
                    + e.getMessage());
        }

        return abastecimientos;
    }
}
