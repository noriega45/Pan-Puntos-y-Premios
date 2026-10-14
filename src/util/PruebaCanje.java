package util;

import dao.CanjeDAO;

public class PruebaCanje {

    public static void main(String[] args) {

        CanjeDAO dao = new CanjeDAO();

        if (dao.canjear(1, 1)) {
            System.out.println("Canje realizado: se descontaron los puntos y el premio.");
        } else {
            System.out.println("No se pudo guardar el canje");
        }
    }
}
