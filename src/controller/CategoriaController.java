package controller;

import dao.CategoriaDAO;
import model.Categoria;
import java.util.List;

public class CategoriaController {

    public static final String[] TIPOS = {"COMIDA", "BEBIDA", "ACOMPANAMIENTO", "EXTRA"};

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public boolean guardarCategoria(Categoria categoria) {
        if (!tipoValido(categoria.getTipo())) {
            return false;
        }
        return categoriaDAO.guardar(categoria);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    public boolean actualizarCategoria(Categoria categoria) {
        if (!tipoValido(categoria.getTipo())) {
            return false;
        }
        return categoriaDAO.actualizar(categoria);
    }

    public boolean desactivarCategoria(int idCategoria) {
        return categoriaDAO.desactivar(idCategoria);
    }

    public int contarProductos(int idCategoria) {
        return categoriaDAO.contarProductos(idCategoria);
    }

    public int contarProductosActivos(int idCategoria) {
        return categoriaDAO.contarProductosActivos(idCategoria);
    }

    public static String describirTipo(String tipo) {
        if ("COMIDA".equals(tipo)) {
            return "Comida";
        }
        if ("BEBIDA".equals(tipo)) {
            return "Bebida";
        }
        if ("ACOMPANAMIENTO".equals(tipo)) {
            return "Acompañamientos";
        }
        return "Extras";
    }

    private boolean tipoValido(String tipo) {
        for (String valido : TIPOS) {
            if (valido.equals(tipo)) {
                return true;
            }
        }
        return false;
    }
}
