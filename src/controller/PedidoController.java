
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

    public List<Pedido> listarPendientesConDetalles() {
        return pedidoDAO.listarPendientesConDetalles();
    }
    
    public java.util.List<model.DetallePedido> listarDetallesPedido(int idPedido) {
        dao.DetallePedidoDAO detalleDAO = new dao.DetallePedidoDAO();
        return detalleDAO.listarPorPedido(idPedido);
    }
    
    public boolean marcarEntregado(int idPedido) {
        return pedidoDAO.marcarEntregado(idPedido);
    }

    public boolean anularPendiente(int idPedido) {
        return pedidoDAO.anularPendiente(idPedido);
    }
    public boolean existeCliente(int idCliente) {
    dao.ClienteDAO clienteDAO = new dao.ClienteDAO();
    return clienteDAO.existe(idCliente);
    }
}
