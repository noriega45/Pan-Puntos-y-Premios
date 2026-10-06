package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.Pedido;
import util.Conexion;

public class PedidoDAO {

    public boolean guardar(Pedido pedido) {

        String sql = "INSERT INTO PEDIDOS "
                + "(ID_PEDIDO, ID_CLIENTE, TOTAL, ESTADO) "
                + "VALUES (SEQ_PEDIDOS.NEXTVAL, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, pedido.getIdCliente());
            ps.setDouble(2, pedido.getTotal());
            ps.setString(3, pedido.getEstado());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar pedido: " + e.getMessage());
            return false;
        }
    }
}