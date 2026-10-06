package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.DetallePedido;
import util.Conexion;

public class DetallePedidoDAO {

    public boolean guardar(DetallePedido detalle) {

        String sql = "INSERT INTO DETALLE_PEDIDO "
                + "(ID_DETALLE, ID_PEDIDO, ID_PRODUCTO, CANTIDAD, PRECIO_UNITARIO) "
                + "VALUES (SEQ_DETALLE_PEDIDO.NEXTVAL, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar detalle de pedido: " + e.getMessage());
            return false;
        }
    }
}
