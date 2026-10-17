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

    public Cliente buscarCliente(int idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }

    public List<Object[]> listarPedidosCliente(int idCliente) {
        return clienteDAO.listarPedidosCliente(idCliente);
    }

    public List<Object[]> listarMovimientosPuntos(int idCliente) {
        return clienteDAO.listarMovimientosPuntos(idCliente);
    }

public int buscarClienteDuplicado(String telefono, String correo, int idExcluir) {
    return clienteDAO.buscarIdDuplicado(telefono, correo, idExcluir);
}

}
