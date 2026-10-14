package controller;

import dao.ClienteDAO;
import dao.VentaDAO;
import java.util.List;
import model.Cliente;
import model.ItemVenta;

public class VentaController {
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public Cliente buscarClienteActivo(int idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }

    public int crearPedido(int idCliente, List<ItemVenta> items) {
        return ventaDAO.crearPedido(idCliente, items);
    }
}
