package model;

import java.sql.Date;

public class MovimientoPuntos {

    private int idMovimiento;
    private int idCliente;
    private Date fecha;
    private String tipo;
    private int puntos;
    private String descripcion;

    public MovimientoPuntos() {
        this.fecha = new Date(System.currentTimeMillis());
        this.puntos = 0;
    }

    public MovimientoPuntos(int idMovimiento, int idCliente,
                            Date fecha, String tipo,
                            int puntos, String descripcion) {

        this.idMovimiento = idMovimiento;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.tipo = tipo;
        this.puntos = puntos;
        this.descripcion = descripcion;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}