package model;

public class MenuVenta extends ItemVenta {
    private final Menu menu;

    public MenuVenta(Menu menu, int cantidad) {
        super(cantidad);
        if (menu == null) {
            throw new IllegalArgumentException("Selecciona un menú.");
        }
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public int getId() {
        return menu.getIdMenu();
    }

    @Override
    public String getNombre() {
        return menu.getNombre();
    }

    @Override
    public double getPrecioUnitario() {
        return menu.getPrecio();
    }

    @Override
    public String getTipo() {
        return "MENÚ";
    }

    @Override
    public int calcularPuntos() {
        return getCantidad() * 8;
    }
}
