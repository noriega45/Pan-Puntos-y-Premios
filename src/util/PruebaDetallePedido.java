package util;

import dao.DetallePedidoDAO;
import model.DetallePedido;

public class PruebaDetallePedido {

    public static void main(String[] args) {

        DetallePedido detalle = new DetallePedido(
            0,
            1,
            1,
            2,
            15.00
        );

        DetallePedidoDAO dao = new DetallePedidoDAO();

        if (dao.guardar(detalle)) {
            System.out.println("Detalle de pedido guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el detalle de pedido");
        }
    }
}
