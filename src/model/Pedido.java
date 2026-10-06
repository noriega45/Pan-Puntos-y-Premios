package model;

import java.sql.Date;

public class Pedido {

    private int idPedido;
    private int idCliente;
    private Date fecha;
    private double total;
    private String estado;

    public Pedido() {
        this.fecha = new Date(System.currentTimeMillis());
        this.total = 0;
        this.estado = "PENDIENTE";
    }

    public Pedido(int idPedido, int idCliente, Date fecha,
                  double total, String estado) {

        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
