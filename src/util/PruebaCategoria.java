package util;

import dao.CategoriaDAO;
import model.Categoria;

public class PruebaCategoria {

    public static void main(String[] args) {

        Categoria categoria = new Categoria(
            0,
            "Pan Dulce",
            "Productos de panadería dulce"
        );

        CategoriaDAO dao = new CategoriaDAO();

        if (dao.guardar(categoria)) {
            System.out.println("Categoría guardada correctamente");
        } else {
            System.out.println("No se pudo guardar la categoría");
        }
    }
}
