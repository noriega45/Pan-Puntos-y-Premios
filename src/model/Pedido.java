package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {

    private int idPedido;
    private int idCliente;
    private Date fecha;
    private double total;
    private String estado;
    private final List<ItemVenta> detalles = new ArrayList<>();

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

    public void agregarDetalle(ItemVenta item) {
        if (item == null) {
            throw new IllegalArgumentException("El artículo no puede ser nulo.");
        }
        detalles.add(item);
        total = calcularTotal();
    }

    public void quitarDetalle(int indice) {
        detalles.remove(indice);
        total = calcularTotal();
    }

    public void limpiarDetalles() {
        detalles.clear();
        total = 0;
        idPedido = 0;
    }

    public List<ItemVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public boolean tieneDetalles() {
        return !detalles.isEmpty();
    }

    public double calcularTotal() {
        double suma = 0;
        for (ItemVenta item : detalles) {
            suma += item.calcularSubtotal();
        }
        return suma;
    }

    public int calcularPuntos() {
        int puntos = 0;
        for (ItemVenta item : detalles) {
            puntos += item.calcularPuntos();
        }
        return puntos;
    }

    @Override
    public String toString() {
        return "Pedido #" + idPedido + " - Q" + String.format("%.2f", total);
    }
}
