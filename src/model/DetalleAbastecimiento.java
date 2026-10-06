package model;

public class DetalleAbastecimiento {

    private int idDetalle;
    private int idAbastecimiento;
    private int idProducto;
    private int cantidad;
    private double costoUnitario;

    public DetalleAbastecimiento() {
        this.cantidad = 0;
        this.costoUnitario = 0;
    }

    public DetalleAbastecimiento(int idDetalle, int idAbastecimiento,
                                 int idProducto, int cantidad,
                                 double costoUnitario) {

        this.idDetalle = idDetalle;
        this.idAbastecimiento = idAbastecimiento;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdAbastecimiento() {
        return idAbastecimiento;
    }

    public void setIdAbastecimiento(int idAbastecimiento) {
        this.idAbastecimiento = idAbastecimiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
}