package model;

public class DetallePedidoMenu {

    private int idDetallePedidoMenu;
    private int idPedido;
    private int idMenu;
    private int cantidad;
    private double precioUnitario;

    public DetallePedidoMenu() {
    }

    public DetallePedidoMenu(int idDetallePedidoMenu, int idPedido,
            int idMenu, int cantidad, double precioUnitario) {
        this.idDetallePedidoMenu = idDetallePedidoMenu;
        this.idPedido = idPedido;
        this.idMenu = idMenu;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetallePedidoMenu() {
        return idDetallePedidoMenu;
    }

    public void setIdDetallePedidoMenu(int idDetallePedidoMenu) {
        this.idDetallePedidoMenu = idDetallePedidoMenu;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}