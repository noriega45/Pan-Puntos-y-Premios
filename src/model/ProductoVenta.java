package model;

public class ProductoVenta extends ItemVenta {
    private final Producto producto;

    public ProductoVenta(Producto producto, int cantidad) {
        super(cantidad);
        if (producto == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }
        this.producto = producto;
    }

    public Producto getProducto() {
        return producto;
    }

    @Override
    public int getId() {
        return producto.getIdProducto();
    }

    @Override
    public String getNombre() {
        return producto.getNombre();
    }

    @Override
    public double getPrecioUnitario() {
        return producto.getPrecio();
    }

    @Override
    public String getTipo() {
        return "PRODUCTO";
    }

    @Override
    public int calcularPuntos() {
        return "SANDWICH".equalsIgnoreCase(producto.getCategoria())
                ? getCantidad() * 2 : 0;
    }
}
