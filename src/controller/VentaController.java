package controller;

import dao.ClienteDAO;
import dao.VentaDAO;
import model.Cliente;
import model.Pedido;

public class VentaController {
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public Cliente buscarClienteActivo(int idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }

    public int crearPedido(Pedido pedido) {
        if (pedido == null || !pedido.tieneDetalles()) {
            return -1;
        }
        int idPedido = ventaDAO.crearPedido(pedido.getIdCliente(), pedido.getDetalles());
        if (idPedido > 0) {
            pedido.setIdPedido(idPedido);
        }
        return idPedido;
    }
}
