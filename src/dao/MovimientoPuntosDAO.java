package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.MovimientoPuntos;
import util.Conexion;

public class MovimientoPuntosDAO {

    public boolean guardar(MovimientoPuntos movimiento) {

        String sql = "INSERT INTO MOVIMIENTOS_PUNTOS "
                + "(ID_MOVIMIENTO, ID_CLIENTE, TIPO, PUNTOS, DESCRIPCION) "
                + "VALUES (SEQ_MOVIMIENTOS_PUNTOS.NEXTVAL, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, movimiento.getIdCliente());
            ps.setString(2, movimiento.getTipo());
            ps.setInt(3, movimiento.getPuntos());
            ps.setString(4, movimiento.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar movimiento de puntos: "
                    + e.getMessage());
            return false;
        }
    }
}