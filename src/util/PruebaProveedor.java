package util;

import dao.ProveedorDAO;
import model.Proveedor;

public class PruebaProveedor {

    public static void main(String[] args) {

        Proveedor proveedor = new Proveedor(
            0,
            "Distribuidora El Pan",
            "987654-3",
            "44444444",
            "proveedor@gmail.com",
            "Ciudad de Guatemala"
        );

        ProveedorDAO dao = new ProveedorDAO();

        if (dao.guardar(proveedor)) {
            System.out.println("Proveedor guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el proveedor");
        }
    }
}