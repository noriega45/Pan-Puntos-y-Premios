package controller;

import dao.CanjeDAO;
import dao.PremioDAO;
import java.util.List;
import model.Premio;

public class CanjeController {
    private final CanjeDAO canjeDAO = new CanjeDAO();
    private final PremioDAO premioDAO = new PremioDAO();

    public String canjear(int idCliente, Premio premio) {
        if (premio == null) {
            return "Selecciona un premio.";
        }
        return canjeDAO.canjear(idCliente, premio.getIdPremio());
    }

    public List<Premio> listarPremiosDisponibles() {
        return premioDAO.listarActivos();
    }

    public List<Object[]> listarHistorialCliente(int idCliente) {
        return canjeDAO.listarHistorialCliente(idCliente);
    }
}
