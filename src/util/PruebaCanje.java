package util;

import dao.CanjeDAO;
import model.Canje;
import java.sql.Date;

public class PruebaCanje {

    public static void main(String[] args) {

        Canje canje = new Canje(
            0,
            1,
            1,
            new Date(System.currentTimeMillis()),
            100
        );

        CanjeDAO dao = new CanjeDAO();

        if (dao.guardar(canje)) {
            System.out.println("Canje guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el canje");
        }
    }
}