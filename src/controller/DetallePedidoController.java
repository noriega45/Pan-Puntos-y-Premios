
package controller;

import dao.DetallePedidoDAO;
import model.DetallePedido;
import java.util.List;

public class DetallePedidoController {

    private final DetallePedidoDAO detalleDAO = new DetallePedidoDAO();


public boolean guardarDetalle(DetallePedido detalle) {
    if (detalle.getIdPedido() <= 0
            || detalle.getIdProducto() <= 0
            || detalle.getCantidad() <= 0
            || detalle.getPrecioUnitario() < 0) {
        return false;
    }

    boolean guardado = detalleDAO.guardar(detalle);

    if (!guardado) {
        return false;
    }

    return detalleDAO.actualizarTotalPedido(detalle.getIdPedido());
}


    public List<DetallePedido> listarDetallesPorPedido(int idPedido) {
        if (idPedido <= 0) {
            return java.util.Collections.emptyList();
        }

        return detalleDAO.listarPorPedido(idPedido);
    }
    
    public boolean actualizarTotalPedido(int idPedido) {
        if (idPedido <= 0) {
            return false;
        }

        return detalleDAO.actualizarTotalPedido(idPedido);
    }

}
