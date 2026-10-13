
package model;

import java.sql.Timestamp;

public class Pago {

    private int idPago;
    private int idPedido;
    private Timestamp fechaPago;
    private String metodoPago;
    private double monto;
    private String estado;
    private String referencia;

    public Pago() {
    }

    public Pago(int idPago, int idPedido, Timestamp fechaPago,
            String metodoPago, double monto, String estado,
            String referencia) {
        this.idPago = idPago;
        this.idPedido = idPedido;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
        this.referencia = referencia;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Timestamp getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Timestamp fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
