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

     public java.util.List<Pedido> listarPendientesConDetalles() {
    java.util.List<Pedido> pedidos = new java.util.ArrayList<>();

    String sql = "SELECT P.ID_PEDIDO, P.ID_CLIENTE, P.FECHA, P.TOTAL, P.ESTADO "
            + "FROM PEDIDOS P "
            + "WHERE P.ESTADO = 'PENDIENTE' "
            + "AND (EXISTS (SELECT 1 FROM DETALLE_PEDIDO D "
            + "WHERE D.ID_PEDIDO = P.ID_PEDIDO) "
            + "OR EXISTS (SELECT 1 FROM DETALLE_PEDIDO_MENU DM "
            + "WHERE DM.ID_PEDIDO = P.ID_PEDIDO)) "
            + "ORDER BY P.ID_PEDIDO DESC";

    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            pedidos.add(new Pedido(
                rs.getInt("ID_PEDIDO"),
                rs.getInt("ID_CLIENTE"),
                rs.getDate("FECHA"),
                rs.getDouble("TOTAL"),
                rs.getString("ESTADO")
            ));
        }

    } catch (Exception e) {
        System.out.println("Error al listar pedidos pendientes: " + e.getMessage());
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
        
public boolean marcarEntregado(int idPedido) {
    String sql = "UPDATE PEDIDOS SET ESTADO = 'ENTREGADO' "
            + "WHERE ID_PEDIDO = ? AND ESTADO = 'PAGADO'";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idPedido);
        return ps.executeUpdate() == 1;
    } catch (Exception e) {
        System.out.println("Error al marcar pedido como entregado: " + e.getMessage());
        return false;
    }
}

public boolean anularPendiente(int idPedido) {
    String sql = "UPDATE PEDIDOS SET ESTADO = 'ANULADO' "
            + "WHERE ID_PEDIDO = ? AND ESTADO = 'PENDIENTE' "
            + "AND NOT EXISTS (SELECT 1 FROM PAGOS P WHERE P.ID_PEDIDO = PEDIDOS.ID_PEDIDO "
            + "AND UPPER(P.ESTADO) = 'APROBADO')";
    try (Connection cn = Conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setInt(1, idPedido);
        return ps.executeUpdate() == 1;
    } catch (Exception e) {
        System.out.println("Error al anular pedido: " + e.getMessage());
        return false;
    }
}
}
