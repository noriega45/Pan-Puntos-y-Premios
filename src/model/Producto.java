/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author eduar
 */
public class Producto {
    private int idProducto;
    private String codigo;
    private String categoria;
    private double precio;
    private int existencia;
    private String nombre;
    private boolean activo;     
    
    public Producto() {
    this.existencia = 0;
    this.activo = true;
}

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getExistencia() {
        return existencia;
    }

    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

public Producto(int idProducto, String codigo, String categoria, String nombre, double precio, int existencia) {
    this.idProducto = idProducto;
    this.codigo = codigo;
    this.categoria = categoria;
    this.nombre = nombre;
    this.precio = precio;
    this.existencia = existencia;
    this.activo = true;
}
    
}
