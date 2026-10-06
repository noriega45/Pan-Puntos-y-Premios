package util;

import dao.DetalleAbastecimientoDAO;
import model.DetalleAbastecimiento;

public class PruebaDetalleAbastecimiento {

    public static void main(String[] args) {

        DetalleAbastecimiento detalle = new DetalleAbastecimiento(
            0,
            1,
            1,
            10,
            12.50
        );

        DetalleAbastecimientoDAO dao = new DetalleAbastecimientoDAO();

        if (dao.guardar(detalle)) {
            System.out.println("Detalle de abastecimiento guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el detalle de abastecimiento");
        }
    }
}