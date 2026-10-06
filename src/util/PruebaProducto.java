package util;


import dao.ProductoDAO;
import model.Producto;

public class PruebaProducto {

    public static void main(String[] args) {

        Producto producto = new Producto(
            0,
            "PAN001",
            "Pan Dulce",
            "Pan de Banano",
            15.00,
            20
        );

        ProductoDAO dao = new ProductoDAO();

        if (dao.guardar(producto)) {
            System.out.println("Producto guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el producto");
        }
    }
}