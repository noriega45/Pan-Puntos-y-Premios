
package view;

import controller.AbastecimientoController;
import controller.DetalleAbastecimientoController;
import controller.ProductoController;
import model.Abastecimiento;
import model.DetalleAbastecimiento;
import model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmDetalleAbastecimiento extends JFrame {

    private final AbastecimientoController abastecimientoController =
            new AbastecimientoController();

    private final DetalleAbastecimientoController detalleController =
            new DetalleAbastecimientoController();

    private final ProductoController productoController =
            new ProductoController();

    private JComboBox<AbastecimientoItem> cboAbastecimiento;
    private JComboBox<ProductoItem> cboProducto;
    private JTextField txtCantidad;
    private JTextField txtCosto;
    private JLabel lblTotal;

    private JTable tblDetalles;
    private DefaultTableModel modeloTabla;

    public FrmDetalleAbastecimiento() {
        setTitle("Detalles de abastecimientos");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        construirInterfaz();
        cargarAbastecimientos();
        cargarProductos();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(
                BorderFactory.createTitledBorder("Agregar producto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Abastecimiento:"), gbc);

        cboAbastecimiento = new JComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(cboAbastecimiento, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formulario.add(new JLabel("Producto:"), gbc);

        cboProducto = new JComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(cboProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formulario.add(new JLabel("Cantidad:"), gbc);

        txtCantidad = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(txtCantidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formulario.add(new JLabel("Costo unitario (Q):"), gbc);

        txtCosto = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(txtCosto, gbc);

        JButton btnAgregar = new JButton("Agregar producto");
        JButton btnConsultar = new JButton("Consultar detalles");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnAgregar);
        botones.add(btnConsultar);
        botones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formulario.add(botones, gbc);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID Detalle", "ID Producto", "Cantidad",
                    "Costo unitario (Q)", "Subtotal (Q)"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDetalles = new JTable(modeloTabla);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(
                BorderFactory.createTitledBorder("Productos del abastecimiento"));
        panelTabla.add(new JScrollPane(tblDetalles), BorderLayout.CENTER);

        lblTotal = new JLabel("Total del abastecimiento: Q0.00");
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panelTabla.add(lblTotal, BorderLayout.SOUTH);

        add(formulario, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnConsultar.addActionListener(e -> cargarDetalles());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        cboAbastecimiento.addActionListener(e -> cargarDetalles());

        cboProducto.addActionListener(e -> completarCosto());
    }

    private void cargarAbastecimientos() {
        cboAbastecimiento.removeAllItems();

        List<Abastecimiento> lista =
                abastecimientoController.listarAbastecimientos();

        for (Abastecimiento a : lista) {
            cboAbastecimiento.addItem(new AbastecimientoItem(
                    a.getIdAbastecimiento(),
                    a.getIdProveedor(),
                    a.getTotal()
            ));
        }

        cargarDetalles();
    }

    private void cargarProductos() {
        cboProducto.removeAllItems();

        List<Producto> productos = productoController.listarProductos();

        for (Producto p : productos) {
            cboProducto.addItem(new ProductoItem(
                    p.getIdProducto(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getPrecio()
            ));
        }

        completarCosto();
    }

    private void completarCosto() {
        ProductoItem producto =
                (ProductoItem) cboProducto.getSelectedItem();

        if (producto != null) {
            txtCosto.setText(String.format(
                    java.util.Locale.US, "%.2f", producto.precio));
        }
    }

    private void agregarProducto() {
        AbastecimientoItem abastecimiento =
                (AbastecimientoItem) cboAbastecimiento.getSelectedItem();

        ProductoItem producto =
                (ProductoItem) cboProducto.getSelectedItem();

        if (abastecimiento == null) {
            JOptionPane.showMessageDialog(
                    this, "Primero registra un abastecimiento.");
            return;
        }

        if (producto == null) {
            JOptionPane.showMessageDialog(
                    this, "No hay productos activos disponibles.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            double costo = Double.parseDouble(
                    txtCosto.getText().trim().replace(",", "."));

            if (cantidad <= 0 || !Double.isFinite(costo) || costo < 0) {
                JOptionPane.showMessageDialog(
                        this, "La cantidad debe ser positiva y el costo no negativo.");
                return;
            }

            DetalleAbastecimiento detalle = new DetalleAbastecimiento(
                    0,
                    abastecimiento.id,
                    producto.id,
                    cantidad,
                    costo
            );

            if (detalleController.guardarDetalle(detalle)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Producto agregado. Inventario y total actualizados.");

                txtCantidad.setText("");
                cargarAbastecimientos();
                cargarDetalles();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar el producto. Verifica que el "
                        + "abastecimiento y el producto estén activos.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Ingresa una cantidad entera y un costo válido.");
        }
    }

    private void cargarDetalles() {
        modeloTabla.setRowCount(0);

        AbastecimientoItem abastecimiento =
                (AbastecimientoItem) cboAbastecimiento.getSelectedItem();

        if (abastecimiento == null) {
            lblTotal.setText("Total del abastecimiento: Q0.00");
            return;
        }

        List<DetalleAbastecimiento> detalles =
                detalleController.listarPorAbastecimiento(abastecimiento.id);

        double total = 0;

        for (DetalleAbastecimiento d : detalles) {
            double subtotal = d.getCantidad() * d.getCostoUnitario();
            total += subtotal;

            modeloTabla.addRow(new Object[]{
                d.getIdDetalle(),
                d.getIdProducto(),
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

    private void limpiarFormulario() {
        txtCantidad.setText("");
        completarCosto();
    }

    private static class AbastecimientoItem {
        private final int id;
        private final int idProveedor;
        private final double total;

        private AbastecimientoItem(int id, int idProveedor, double total) {
            this.id = id;
            this.idProveedor = idProveedor;
            this.total = total;
        }

        @Override
        public String toString() {
            return "Abastecimiento #" + id
                    + " - Proveedor " + idProveedor
                    + " - Q" + String.format("%.2f", total);
        }
    }

    private static class ProductoItem {
        private final int id;
        private final String codigo;
        private final String nombre;
        private final double precio;

        private ProductoItem(int id, String codigo, String nombre,
                             double precio) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
        }

        @Override
        public String toString() {
            return id + " - " + codigo + " - " + nombre;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new FrmDetalleAbastecimiento().setVisible(true));
    }
}
