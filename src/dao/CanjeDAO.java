package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Canje;
import util.Conexion;

public class CanjeDAO {

    public boolean guardar(Canje canje) {

        String sql = "INSERT INTO CANJES "
                + "(ID_CANJE, ID_CLIENTE, ID_PREMIO, PUNTOS_UTILIZADOS) "
                + "VALUES (SEQ_CANJES.NEXTVAL, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, canje.getIdCliente());
            ps.setInt(2, canje.getIdPremio());
            ps.setInt(3, canje.getPuntosUtilizados());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar canje: " + e.getMessage());
            return false;
        }
    }
}