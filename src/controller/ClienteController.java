package controller;

import dao.ClienteDAO;
import model.Cliente;
import java.util.List;

public class ClienteController {

    private ClienteDAO clienteDAO;

    public ClienteController() {
        clienteDAO = new ClienteDAO();
    }

    public boolean guardarCliente(Cliente cliente) {
        return clienteDAO.guardar(cliente);
    }
    public boolean actualizarCliente(Cliente cliente) {
    return clienteDAO.actualizar(cliente);
    }
    public boolean desactivarCliente(int idCliente) {
    return clienteDAO.desactivar(idCliente);
    }
    public List<Cliente> listarClientes() {
    return clienteDAO.listar();
    }
}
