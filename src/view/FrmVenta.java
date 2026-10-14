package view;

import controller.MenuController;
import controller.ProductoController;
import controller.VentaController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Cliente;
import model.ItemVenta;
import model.Menu;
import model.MenuVenta;
import model.Producto;
import model.ProductoVenta;

public class FrmVenta extends JFrame {
    private final VentaController ventaController = new VentaController();
    private final ProductoController productoController = new ProductoController();
    private final MenuController menuController = new MenuController();
    private final List<ItemVenta> carrito = new ArrayList<>();
    private JTextField txtIdCliente;
    private JLabel lblCliente;
    private JComboBox<String> cboTipo;
    private JComboBox<Object> cboCatalogo;
    private JComboBox<String> cboMetodo;
    private JTextField txtCantidad;
    private JLabel lblTotal;
    private JLabel lblPuntos;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Cliente cliente;

    public FrmVenta() {
        construirInterfaz();
        actualizarCatalogo();
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Nueva venta");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("NUEVA VENTA");
        titulo.setFont(titulo.getFont().deriveFont(23f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel datosCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        datosCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
        txtIdCliente = new JTextField(8);
        JButton btnBuscarCliente = new JButton("Buscar cliente");
        lblCliente = new JLabel("Ingresa el ID del cliente para consultar sus puntos.");
        datosCliente.add(new JLabel("ID:") );
        datosCliente.add(txtIdCliente);
        datosCliente.add(btnBuscarCliente);
        datosCliente.add(lblCliente);

        JPanel selector = new JPanel(new GridLayout(2, 4, 8, 8));
        selector.setBorder(BorderFactory.createTitledBorder("Agregar producto o menú"));
        cboTipo = new JComboBox<>(new String[]{"Producto", "Menú completo"});
        cboCatalogo = new JComboBox<>();
        txtCantidad = new JTextField("1");
        JButton btnAgregar = new JButton("Agregar al pedido");
        selector.add(new JLabel("Tipo:"));
        selector.add(new JLabel("Artículo:"));
        selector.add(new JLabel("Cantidad:"));
        selector.add(new JLabel("Acción:"));
        selector.add(cboTipo);
        selector.add(cboCatalogo);
        selector.add(txtCantidad);
        selector.add(btnAgregar);

        JPanel encabezado = new JPanel(new BorderLayout(8, 8));
        encabezado.add(datosCliente, BorderLayout.NORTH);
        encabezado.add(selector, BorderLayout.CENTER);

        modelo = new DefaultTableModel(new Object[]{
            "Tipo", "Artículo", "Cantidad", "Precio unitario (Q)", "Subtotal (Q)", "Puntos"
        }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JPanel resumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        lblTotal = new JLabel("Total: Q0.00");
        lblPuntos = new JLabel("Puntos que se acreditarán al pagar: 0");
        resumen.add(lblPuntos);
        resumen.add(lblTotal);

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(encabezado, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(resumen, BorderLayout.SOUTH);
        principal.add(centro, BorderLayout.CENTER);

        cboMetodo = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
        JButton btnQuitar = new JButton("Quitar artículo");
        JButton btnLimpiar = new JButton("Vaciar pedido");
        JButton btnGuardar = new JButton("Crear pedido y cobrar");
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(new JLabel("Método de pago:"));
        acciones.add(cboMetodo);
        acciones.add(btnQuitar);
        acciones.add(btnLimpiar);
        acciones.add(btnGuardar);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        cboTipo.addActionListener(e -> actualizarCatalogo());
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnAgregar.addActionListener(e -> agregarArticulo());
        btnQuitar.addActionListener(e -> quitarArticulo());
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        btnGuardar.addActionListener(e -> crearPedido());
    }

    private void actualizarCatalogo() {
        cboCatalogo.removeAllItems();
        if ("Producto".equals(cboTipo.getSelectedItem())) {
            for (Producto producto : productoController.listarProductos()) {
                if (producto.getExistencia() > 0) {
                    cboCatalogo.addItem(producto);
                }
            }
        } else {
            for (Menu menu : menuController.listarActivos()) {
                cboCatalogo.addItem(menu);
            }
        }
    }

    private void buscarCliente() {
        try {
            int id = Integer.parseInt(txtIdCliente.getText().trim());
            cliente = ventaController.buscarClienteActivo(id);
            if (cliente == null) {
                lblCliente.setText("No existe un cliente activo con ese ID.");
                return;
            }
            lblCliente.setText(cliente.getNombre() + " | Puntos disponibles: " + cliente.getPuntos());
        } catch (NumberFormatException e) {
            cliente = null;
            JOptionPane.showMessageDialog(this, "Ingresa un ID de cliente válido.");
        }
    }

    private void agregarArticulo() {
        Object seleccionado = cboCatalogo.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "No hay artículos disponibles en este catálogo.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            ItemVenta item;
            if (seleccionado instanceof Producto) {
                Producto producto = (Producto) seleccionado;
                item = new ProductoVenta(producto, cantidad);
            } else {
                item = new MenuVenta((Menu) seleccionado, cantidad);
            }
            carrito.add(item);
            actualizarTabla();
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero positivo.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void actualizarTabla() {
        modelo.setRowCount(0);
        double total = 0;
        int puntos = 0;
        for (ItemVenta item : carrito) {
            double subtotal = item.getPrecioUnitario() * item.getCantidad();
            total += subtotal;
            puntos += item.calcularPuntos();
            modelo.addRow(new Object[]{item.getTipo(), item.getNombre(), item.getCantidad(),
                String.format("%.2f", item.getPrecioUnitario()), String.format("%.2f", subtotal),
                item.calcularPuntos()});
        }
        lblTotal.setText(String.format("Total: Q%.2f", total));
        lblPuntos.setText("Puntos que se acreditarán al pagar: " + puntos);
    }

    private void quitarArticulo() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un artículo de la tabla.");
            return;
        }
        carrito.remove(fila);
        actualizarTabla();
    }

    private void limpiarCarrito() {
        carrito.clear();
        cliente = null;
        txtIdCliente.setText("");
        lblCliente.setText("Ingresa el ID del cliente para consultar sus puntos.");
        actualizarTabla();
    }

    private void crearPedido() {
        buscarCliente();
        if (cliente == null) {
            return;
        }
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un artículo al pedido.");
            return;
        }
        int idPedido = ventaController.crearPedido(cliente.getIdCliente(), carrito);
        if (idPedido <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo crear el pedido. Revisa existencias, productos activos y menús completos.",
                    "Pedido no creado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String metodo = cboMetodo.getSelectedItem().toString();
        limpiarCarrito();
        FrmPago pago = new FrmPago(idPedido, metodo);
        pago.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmVenta().setVisible(true));
    }
}
