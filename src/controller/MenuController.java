package controller;

import dao.MenuDAO;
import java.util.List;
import java.util.Map;
import model.Menu;

public class MenuController {

    private final MenuDAO dao = new MenuDAO();

    public List<Menu> listarActivos() {
        return dao.listarActivos();
    }

    public boolean guardar(String nombre, double precio, Map<Integer, Integer> componentes) {
        return dao.guardar(nombre, precio, componentes);
    }
}
