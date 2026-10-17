
package controller;

import dao.ProductoDAO;
import model.Producto;
import java.util.List;

public class ProductoController {

    private final ProductoDAO productoDAO = new ProductoDAO();

    public boolean guardarProducto(Producto producto) {
        return productoDAO.guardar(producto);
    }

    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }

    public boolean actualizarProducto(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public boolean desactivarProducto(int idProducto) {
        return productoDAO.desactivar(idProducto);
    }
}
