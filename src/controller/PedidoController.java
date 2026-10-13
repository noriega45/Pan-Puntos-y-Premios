
package controller;

import dao.PedidoDAO;
import model.Pedido;
import java.util.List;

public class PedidoController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public boolean guardarPedido(Pedido pedido) {
        return pedidoDAO.guardar(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listar();
    }
    
    public java.util.List<model.DetallePedido> listarDetallesPedido(int idPedido) {
        dao.DetallePedidoDAO detalleDAO = new dao.DetallePedidoDAO();
        return detalleDAO.listarPorPedido(idPedido);
    }
    
    public boolean actualizarPedido(Pedido pedido) {
        return pedidoDAO.actualizar(pedido);
    }
    public boolean existeCliente(int idCliente) {
    dao.ClienteDAO clienteDAO = new dao.ClienteDAO();
    return clienteDAO.existe(idCliente);
    }
}
