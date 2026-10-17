
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

        String sqlId = "SELECT SEQ_ABASTECIMIENTOS.NEXTVAL FROM DUAL";
        String sql = "INSERT INTO ABASTECIMIENTOS "
                + "(ID_ABASTECIMIENTO, ID_PROVEEDOR, FECHA, TOTAL) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar()) {
            int idAbastecimiento;
            try (PreparedStatement psId = cn.prepareStatement(sqlId);
                 ResultSet rs = psId.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                idAbastecimiento = rs.getInt(1);
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idAbastecimiento);
                ps.setInt(2, abastecimiento.getIdProveedor());
                ps.setDate(3, abastecimiento.getFecha());
                ps.setDouble(4, abastecimiento.getTotal());
                if (ps.executeUpdate() > 0) {
                    abastecimiento.setIdAbastecimiento(idAbastecimiento);
                    return true;
                }
                return false;
            }

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
