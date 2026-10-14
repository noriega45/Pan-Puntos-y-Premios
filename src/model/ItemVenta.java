package model;

public abstract class ItemVenta {
    private final int cantidad;

    protected ItemVenta(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public abstract int getId();

    public abstract String getNombre();

    public abstract double getPrecioUnitario();

    public abstract String getTipo();

    public abstract int calcularPuntos();
}
