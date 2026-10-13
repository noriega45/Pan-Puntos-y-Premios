
package controller;

import dao.PagoDAO;
import model.Pago;
import java.util.List;

public class PagoController {

    private final PagoDAO pagoDAO = new PagoDAO();

    public boolean guardarPago(Pago pago) {

        if (pago == null
                || pago.getIdPedido() <= 0
                || pago.getMonto() <= 0
                || pago.getMetodoPago() == null
                || pago.getEstado() == null) {
            return false;
        }

        String metodo = pago.getMetodoPago().toUpperCase();
        String estado = pago.getEstado().toUpperCase();

        if (!metodo.equals("EFECTIVO")
                && !metodo.equals("TARJETA")) {
            return false;
        }

        if (!estado.equals("APROBADO")
                && !estado.equals("RECHAZADO")) {
            return false;
        }

        return pagoDAO.guardar(pago);
    }

    public List<Pago> listarPagosPorPedido(int idPedido) {

        if (idPedido <= 0) {
            return java.util.Collections.emptyList();
        }

        return pagoDAO.listarPorPedido(idPedido);
    }
}
