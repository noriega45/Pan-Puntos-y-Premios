package controller;

import dao.DetallePedidoMenuDAO;
import model.DetallePedidoMenu;
import java.util.List;

public class DetallePedidoMenuController {

    private final DetallePedidoMenuDAO dao = new DetallePedidoMenuDAO();

    public boolean guardar(DetallePedidoMenu detalle) {
        return dao.guardar(detalle);
    }
    public List<DetallePedidoMenu> listarPorPedido(int idPedido) {
    return dao.listarPorPedido(idPedido);
    }
}