package model;

public class Premio {

    private int idPremio;
    private String nombre;
    private String descripcion;
    private int puntosRequeridos;
    private int existencia;
    private boolean activo;

    public Premio() {
        this.puntosRequeridos = 0;
        this.existencia = 0;
        this.activo = true;
    }

    public Premio(int idPremio, String nombre, String descripcion,
                  int puntosRequeridos, int existencia) {

        this.idPremio = idPremio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRequeridos = puntosRequeridos;
        this.existencia = existencia;
        this.activo = true;
    }

    public int getIdPremio() {
        return idPremio;
    }

    public void setIdPremio(int idPremio) {
        this.idPremio = idPremio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntosRequeridos() {
        return puntosRequeridos;
    }

    public void setPuntosRequeridos(int puntosRequeridos) {
        this.puntosRequeridos = puntosRequeridos;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
