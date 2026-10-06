package util;

import dao.MovimientoPuntosDAO;
import model.MovimientoPuntos;
import java.sql.Date;

public class PruebaMovimientoPuntos {

    public static void main(String[] args) {

        MovimientoPuntos movimiento = new MovimientoPuntos(
            0,
            1,
            new Date(System.currentTimeMillis()),
            "ACUMULACION",
            100,
            "Puntos obtenidos por compra"
        );

        MovimientoPuntosDAO dao = new MovimientoPuntosDAO();

        if (dao.guardar(movimiento)) {
            System.out.println("Movimiento de puntos guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el movimiento de puntos");
        }
    }
}