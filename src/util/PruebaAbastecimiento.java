package util;

import dao.AbastecimientoDAO;
import model.Abastecimiento;
import java.sql.Date;

public class PruebaAbastecimiento {

    public static void main(String[] args) {

        Abastecimiento abastecimiento = new Abastecimiento(
            0,
            1,
            new Date(System.currentTimeMillis()),
            250.00
        );

        AbastecimientoDAO dao = new AbastecimientoDAO();

        if (dao.guardar(abastecimiento)) {
            System.out.println("Abastecimiento guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el abastecimiento");
        }
    }
}