package view;

import controller.DetalleAbastecimientoController;
import controller.ProductoController;
import model.DetalleAbastecimiento;
import model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrmDetalleAbastecimiento extends JFrame {

    private final DetalleAbastecimientoController detalleController =
            new DetalleAbastecimientoController();

    private final ProductoController productoController =
            new ProductoController();

    private final int idAbastecimiento;
    private final Map<Integer, String> nombresProductos = new HashMap<>();

    private JComboBox<ProductoItem> cboProducto;
    private JLabel lblExistencia;
    private JTextField txtCantidad;
    private JTextField txtCosto;
    private JLabel lblTotal;
    private DefaultTableModel modeloTabla;

    public FrmDetalleAbastecimiento(int idAbastecimiento, String proveedor) {
        this.idAbastecimiento = idAbastecimiento;
        setTitle("Abastecimiento #" + idAbastecimiento + " - Proveedor " + proveedor);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        construirInterfaz(proveedor);
        cargarProductos(0);
        cargarDetalles();
    }

    private void construirInterfaz(String proveedor) {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder(
                "Agregar producto al abastecimiento #" + idAbastecimiento + " (" + proveedor + ")"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboProducto = new JComboBox<>();
        lblExistencia = new JLabel("-");
        txtCantidad = new JTextField();
        txtCosto = new JTextField();

        agregarFila(formulario, gbc, 0, "Producto:", cboProducto);
        agregarFila(formulario, gbc, 1, "Existencia actual:", lblExistencia);
        agregarFila(formulario, gbc, 2, "Cantidad a ingresar:", txtCantidad);
        agregarFila(formulario, gbc, 3, "Costo unitario de compra (Q):", txtCosto);

        JButton btnAgregar = new JButton("Agregar producto");
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnAgregar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formulario.add(botones, gbc);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID detalle", "Producto", "Cantidad",
                    "Costo unitario (Q)", "Subtotal (Q)"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tblDetalles = new JTable(modeloTabla);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(
                BorderFactory.createTitledBorder("Productos recibidos en este abastecimiento"));
        panelTabla.add(new JScrollPane(tblDetalles), BorderLayout.CENTER);

        lblTotal = new JLabel("Total del abastecimiento: Q0.00");
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panelTabla.add(lblTotal, BorderLayout.SOUTH);

        add(formulario, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> agregarProducto());
        cboProducto.addActionListener(e -> mostrarProductoSeleccionado());
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc,
                             int fila, String etiqueta, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void cargarProductos(int idSeleccionar) {
        cboProducto.removeAllItems();
        nombresProductos.clear();

        List<Producto> productos = productoController.listarProductos();

        for (Producto p : productos) {
            nombresProductos.put(p.getIdProducto(), p.getNombre());
            ProductoItem item = new ProductoItem(
                    p.getIdProducto(), p.getCodigo(), p.getNombre(),
                    p.getPrecio(), p.getExistencia());
            cboProducto.addItem(item);
            if (p.getIdProducto() == idSeleccionar) {
                cboProducto.setSelectedItem(item);
            }
        }

        mostrarProductoSeleccionado();
    }

    private void mostrarProductoSeleccionado() {
        ProductoItem producto = (ProductoItem) cboProducto.getSelectedItem();

        if (producto == null) {
            lblExistencia.setText("-");
            return;
        }
        lblExistencia.setText(producto.existencia + " unidades");
        txtCosto.setText(String.format(java.util.Locale.US, "%.2f", producto.precio));
    }

    private void agregarProducto() {
        ProductoItem producto = (ProductoItem) cboProducto.getSelectedItem();

        if (producto == null) {
            JOptionPane.showMessageDialog(
                    this, "No hay productos activos. Regístralos en el módulo Productos.");
            return;
        }

        int cantidad;
        double costo;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "La cantidad debe ser un número entero, por ejemplo 15.");
            return;
        }
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(
                    this, "La cantidad a ingresar debe ser mayor que cero.");
            return;
        }
        try {
            costo = Double.parseDouble(txtCosto.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "El costo unitario debe ser un número, por ejemplo 12.50.");
            return;
        }
        if (!Double.isFinite(costo) || costo < 0) {
            JOptionPane.showMessageDialog(
                    this, "El costo unitario no puede ser negativo.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Ingresar " + cantidad + " unidades de " + producto.nombre + "?\n"
                + "Existencia: " + producto.existencia + " -> " + (producto.existencia + cantidad),
                "Confirmar abastecimiento", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        DetalleAbastecimiento detalle = new DetalleAbastecimiento(
                0, idAbastecimiento, producto.id, cantidad, costo);

        if (detalleController.guardarDetalle(detalle)) {
            JOptionPane.showMessageDialog(this,
                    "Abastecimiento registrado.\n"
                    + producto.nombre + ": " + producto.existencia
                    + " + " + cantidad + " = " + (producto.existencia + cantidad) + " unidades.");
            txtCantidad.setText("");
            cargarProductos(producto.id);
            cargarDetalles();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo agregar el producto. Verifica que el producto siga activo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDetalles() {
        modeloTabla.setRowCount(0);

        List<DetalleAbastecimiento> detalles =
                detalleController.listarPorAbastecimiento(idAbastecimiento);

        double total = 0;

        for (DetalleAbastecimiento d : detalles) {
            double subtotal = d.getCantidad() * d.getCostoUnitario();
            total += subtotal;

            modeloTabla.addRow(new Object[]{
                d.getIdDetalle(),
                d.getIdProducto() + " - " + nombresProductos.getOrDefault(d.getIdProducto(), ""),
                d.getCantidad(),
                String.format("%.2f", d.getCostoUnitario()),
                String.format("%.2f", subtotal)
            });
        }

        lblTotal.setText(String.format(
                java.util.Locale.US,
                "Total del abastecimiento: Q%.2f",
                total
        ));
    }

    private static class ProductoItem {
        private final int id;
        private final String codigo;
        private final String nombre;
        private final double precio;
        private final int existencia;

        private ProductoItem(int id, String codigo, String nombre,
                             double precio, int existencia) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
            this.existencia = existencia;
        }

        @Override
        public String toString() {
            return codigo + " - " + nombre;
        }
    }
}
