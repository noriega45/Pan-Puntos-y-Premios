
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.DetalleAbastecimiento;
import util.Conexion;

public class DetalleAbastecimientoDAO {

    public boolean guardar(DetalleAbastecimiento detalle) {

        if (detalle == null
                || detalle.getIdAbastecimiento() <= 0
                || detalle.getIdProducto() <= 0
                || detalle.getCantidad() <= 0
                || detalle.getCostoUnitario() < 0) {
            return false;
        }

        String sqlDetalle = "INSERT INTO DETALLE_ABASTECIMIENTO "
                + "(ID_DETALLE, ID_ABASTECIMIENTO, ID_PRODUCTO, "
                + "CANTIDAD, COSTO_UNITARIO) "
                + "VALUES (SEQ_DETALLE_ABAST.NEXTVAL, ?, ?, ?, ?)";

        String sqlInventario = "UPDATE PRODUCTOS "
                + "SET EXISTENCIA = EXISTENCIA + ? "
                + "WHERE ID_PRODUCTO = ? AND ACTIVO = 1";

        String sqlTotal = "UPDATE ABASTECIMIENTOS "
                + "SET TOTAL = ("
                + "SELECT NVL(SUM(CANTIDAD * COSTO_UNITARIO), 0) "
                + "FROM DETALLE_ABASTECIMIENTO "
                + "WHERE ID_ABASTECIMIENTO = ?) "
                + "WHERE ID_ABASTECIMIENTO = ?";

        try (Connection cn = Conexion.conectar()) {

            cn.setAutoCommit(false);

            try {
                // 1. Verificar que exista el abastecimiento.
                String sqlExiste = "SELECT ID_ABASTECIMIENTO "
                        + "FROM ABASTECIMIENTOS "
                        + "WHERE ID_ABASTECIMIENTO = ? "
                        + "FOR UPDATE";

                try (PreparedStatement ps = cn.prepareStatement(sqlExiste)) {
                    ps.setInt(1, detalle.getIdAbastecimiento());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                    }
                }

                // 2. Registrar el detalle.
                try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                    ps.setInt(1, detalle.getIdAbastecimiento());
                    ps.setInt(2, detalle.getIdProducto());
                    ps.setInt(3, detalle.getCantidad());
                    ps.setDouble(4, detalle.getCostoUnitario());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }

                // 3. Aumentar las existencias del producto activo.
                try (PreparedStatement ps = cn.prepareStatement(sqlInventario)) {
                    ps.setInt(1, detalle.getCantidad());
                    ps.setInt(2, detalle.getIdProducto());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }

                // 4. Recalcular el total del abastecimiento.
                try (PreparedStatement ps = cn.prepareStatement(sqlTotal)) {
                    ps.setInt(1, detalle.getIdAbastecimiento());
                    ps.setInt(2, detalle.getIdAbastecimiento());

                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }

                cn.commit();
                return true;

            } catch (Exception e) {
                cn.rollback();
                throw e;

            } finally {
                cn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al guardar detalle de abastecimiento: "
                    + e.getMessage()
            );
            return false;
        }
    }
    
public java.util.List<DetalleAbastecimiento> listarPorAbastecimiento(
        int idAbastecimiento) {

    java.util.List<DetalleAbastecimiento> detalles =
            new java.util.ArrayList<>();

    if (idAbastecimiento <= 0) {
        return detalles;
    }

    String sql = "SELECT ID_DETALLE, ID_ABASTECIMIENTO, "
            + "ID_PRODUCTO, CANTIDAD, COSTO_UNITARIO "
            + "FROM DETALLE_ABASTECIMIENTO "
            + "WHERE ID_ABASTECIMIENTO = ? "
            + "ORDER BY ID_DETALLE";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idAbastecimiento);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DetalleAbastecimiento detalle =
                        new DetalleAbastecimiento(
                                rs.getInt("ID_DETALLE"),
                                rs.getInt("ID_ABASTECIMIENTO"),
                                rs.getInt("ID_PRODUCTO"),
                                rs.getInt("CANTIDAD"),
                                rs.getDouble("COSTO_UNITARIO")
                        );

                detalles.add(detalle);
            }
        }

    } catch (Exception e) {
        System.out.println(
                "Error al listar detalles de abastecimiento: "
                + e.getMessage()
        );
    }

    return detalles;
}

}
