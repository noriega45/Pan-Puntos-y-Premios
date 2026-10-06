package model;

import java.sql.Date;

public class Abastecimiento {

    private int idAbastecimiento;
    private int idProveedor;
    private Date fecha;
    private double total;

    public Abastecimiento() {
        this.fecha = new Date(System.currentTimeMillis());
        this.total = 0;
    }

    public Abastecimiento(int idAbastecimiento, int idProveedor,
                          Date fecha, double total) {

        this.idAbastecimiento = idAbastecimiento;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.total = total;
    }

    public int getIdAbastecimiento() {
        return idAbastecimiento;
    }

    public void setIdAbastecimiento(int idAbastecimiento) {
        this.idAbastecimiento = idAbastecimiento;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
