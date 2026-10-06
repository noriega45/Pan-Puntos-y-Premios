package model;

import java.sql.Date;

public class Canje {

    private int idCanje;
    private int idCliente;
    private int idPremio;
    private Date fecha;
    private int puntosUtilizados;

    public Canje() {
        this.fecha = new Date(System.currentTimeMillis());
        this.puntosUtilizados = 0;
    }

    public Canje(int idCanje, int idCliente, int idPremio,
                 Date fecha, int puntosUtilizados) {

        this.idCanje = idCanje;
        this.idCliente = idCliente;
        this.idPremio = idPremio;
        this.fecha = fecha;
        this.puntosUtilizados = puntosUtilizados;
    }

    public int getIdCanje() {
        return idCanje;
    }

    public void setIdCanje(int idCanje) {
        this.idCanje = idCanje;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdPremio() {
        return idPremio;
    }

    public void setIdPremio(int idPremio) {
        this.idPremio = idPremio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getPuntosUtilizados() {
        return puntosUtilizados;
    }

    public void setPuntosUtilizados(int puntosUtilizados) {
        this.puntosUtilizados = puntosUtilizados;
    }
}
