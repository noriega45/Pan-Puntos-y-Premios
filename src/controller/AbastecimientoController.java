
package controller;

import dao.AbastecimientoDAO;
import model.Abastecimiento;
import java.util.List;

public class AbastecimientoController {

    private final AbastecimientoDAO abastecimientoDAO = new AbastecimientoDAO();

    public boolean guardarAbastecimiento(Abastecimiento abastecimiento) {
        if (abastecimiento == null
                || abastecimiento.getIdProveedor() <= 0
                || abastecimiento.getTotal() < 0) {
            return false;
        }

        return abastecimientoDAO.guardar(abastecimiento);
    }

    public List<Abastecimiento> listarAbastecimientos() {
        return abastecimientoDAO.listar();
    }
}
