
package controller;

import dao.PedidoDAO;
import model.Pedido;
import java.util.List;

public class PedidoController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public List<Pedido> listarPendientesConDetalles() {
        return pedidoDAO.listarPendientesConDetalles();
    }

    public List<Object[]> listarDetalleConNombres(int idPedido) {
        return pedidoDAO.listarDetalleConNombres(idPedido);
    }

    public List<Object[]> listarHistorial(String estado) {
        return pedidoDAO.listarHistorial(estado);
    }

    public boolean marcarEntregado(int idPedido) {
        return pedidoDAO.marcarEntregado(idPedido);
    }

    public boolean anularPendiente(int idPedido) {
        return pedidoDAO.anularPendiente(idPedido);
    }
}
