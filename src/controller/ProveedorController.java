package controller;

import dao.ProveedorDAO;
import model.Proveedor;
import java.util.List;

public class ProveedorController {

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    public boolean guardarProveedor(Proveedor proveedor) {
        return proveedorDAO.guardar(proveedor);
    }

    public List<Proveedor> listarProveedores() {
        return proveedorDAO.listar();
    }

    public boolean actualizarProveedor(Proveedor proveedor) {
        return proveedorDAO.actualizar(proveedor);
    }

    public boolean desactivarProveedor(int idProveedor) {
        return proveedorDAO.desactivar(idProveedor);
    }
}