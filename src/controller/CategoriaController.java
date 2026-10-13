
package controller;

import dao.CategoriaDAO;
import model.Categoria;
import java.util.List;

public class CategoriaController {

    private CategoriaDAO categoriaDAO;

    public CategoriaController() {
        categoriaDAO = new CategoriaDAO();
    }

    public boolean guardarCategoria(Categoria categoria) {
        return categoriaDAO.guardar(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public boolean actualizarCategoria(Categoria categoria) {
        return categoriaDAO.actualizar(categoria);
    }

    public boolean desactivarCategoria(int idCategoria) {
        return categoriaDAO.desactivar(idCategoria);
    }
}
