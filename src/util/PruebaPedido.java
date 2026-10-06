package util;

import dao.PedidoDAO;
import model.Pedido;
import java.sql.Date;

public class PruebaPedido {

    public static void main(String[] args) {

        Pedido pedido = new Pedido(
            0,
            1,
            new Date(System.currentTimeMillis()),
            75.00,
            "PENDIENTE"
        );

        PedidoDAO dao = new PedidoDAO();

        if (dao.guardar(pedido)) {
            System.out.println("Pedido guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el pedido");
        }
    }
}
