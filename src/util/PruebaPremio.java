package util;

import dao.PremioDAO;
import model.Premio;

public class PruebaPremio {

    public static void main(String[] args) {

        Premio premio = new Premio(
            0,
            "Canasta de Pan",
            "Canasta con productos de panadería",
            100,
            10
        );

        PremioDAO dao = new PremioDAO();

        if (dao.guardar(premio)) {
            System.out.println("Premio guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el premio");
        }
    }
}