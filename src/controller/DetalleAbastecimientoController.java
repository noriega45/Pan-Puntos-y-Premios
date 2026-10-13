
package controller;

import dao.DetalleAbastecimientoDAO;
import model.DetalleAbastecimiento;

public class DetalleAbastecimientoController {

    private final DetalleAbastecimientoDAO detalleDAO =
            new DetalleAbastecimientoDAO();

    public boolean guardarDetalle(DetalleAbastecimiento detalle) {

        if (detalle == null
                || detalle.getIdAbastecimiento() <= 0
                || detalle.getIdProducto() <= 0
                || detalle.getCantidad() <= 0
                || detalle.getCostoUnitario() < 0) {
            return false;
        }

        return detalleDAO.guardar(detalle);
    }
    
public java.util.List<model.DetalleAbastecimiento> listarPorAbastecimiento(
        int idAbastecimiento) {

    if (idAbastecimiento <= 0) {
        return java.util.Collections.emptyList();
    }

    return detalleDAO.listarPorAbastecimiento(idAbastecimiento);
}

}
