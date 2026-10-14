package model;

public class Menu {

    private final int idMenu;
    private final String nombre;
    private final double precio;

    public Menu(int idMenu, String nombre, double precio) {
        this.idMenu = idMenu;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return nombre + " (Q" + String.format("%.2f", precio) + ")";
    }
}
