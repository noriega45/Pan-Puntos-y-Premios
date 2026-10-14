package controller;

import dao.CanjeDAO;
import dao.PremioDAO;
import java.util.List;
import model.Canje;
import model.Premio;

public class CanjeController {
    private final CanjeDAO canjeDAO = new CanjeDAO();
    private final PremioDAO premioDAO = new PremioDAO();

    public boolean canjear(int idCliente, Premio premio) {
        return premio != null && canjeDAO.canjear(idCliente, premio.getIdPremio());
    }

    public List<Premio> listarPremiosDisponibles() {
        return premioDAO.listarActivos();
    }

    public List<Canje> listarPorCliente(int idCliente) {
        return canjeDAO.listarPorCliente(idCliente);
    }
}
