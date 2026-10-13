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
        public java.util.List<Pedido> listar() {
    java.util.List<Pedido> pedidos = new java.util.ArrayList<>();

    String sql = "SELECT ID_PEDIDO, ID_CLIENTE, FECHA, TOTAL, ESTADO "
               + "FROM PEDIDOS "
               + "ORDER BY ID_PEDIDO DESC";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Pedido pedido = new Pedido(
                rs.getInt("ID_PEDIDO"),
                rs.getInt("ID_CLIENTE"),
                rs.getDate("FECHA"),
                rs.getDouble("TOTAL"),
                rs.getString("ESTADO")
            );

            pedidos.add(pedido);
        }

    } catch (Exception e) {
        System.out.println("Error al listar pedidos: " + e.getMessage());
    }

    return pedidos;
}
        
public Pedido buscarPorId(int idPedido) {
    String sql = "SELECT ID_PEDIDO, ID_CLIENTE, FECHA, TOTAL, ESTADO "
               + "FROM PEDIDOS WHERE ID_PEDIDO = ?";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idPedido);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Pedido(
                    rs.getInt("ID_PEDIDO"),
                    rs.getInt("ID_CLIENTE"),
                    rs.getDate("FECHA"),
                    rs.getDouble("TOTAL"),
                    rs.getString("ESTADO")
                );
            }
        }

    } catch (Exception e) {
        System.out.println("Error al buscar pedido: " + e.getMessage());
    }

    return null;
}
        
public boolean actualizar(Pedido pedido) {
    String sql = "UPDATE PEDIDOS "
               + "SET ID_CLIENTE = ?, TOTAL = ?, ESTADO = ? "
               + "WHERE ID_PEDIDO = ?";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, pedido.getIdCliente());
        ps.setDouble(2, pedido.getTotal());
        ps.setString(3, pedido.getEstado());
        ps.setInt(4, pedido.getIdPedido());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
    e.printStackTrace();
    return false;
}
}
        
}