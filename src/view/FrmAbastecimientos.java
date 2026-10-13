
package view;

import controller.AbastecimientoController;
import controller.ProveedorController;
import model.Abastecimiento;
import model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmAbastecimientos extends JFrame {

    private final AbastecimientoController abastecimientoController =
            new AbastecimientoController();

    private final ProveedorController proveedorController =
            new ProveedorController();

    private JComboBox<ProveedorItem> cboProveedor;
    private JTextField txtTotal;
    private JTable tblAbastecimientos;
    private DefaultTableModel modeloTabla;

    public FrmAbastecimientos() {
        setTitle("Gestión de Abastecimientos");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        construirInterfaz();
        cargarProveedores();
        cargarAbastecimientos();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Registrar abastecimiento"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Proveedor:"), gbc);

        cboProveedor = new JComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 1;
        panelFormulario.add(cboProveedor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Total inicial (Q):"), gbc);

        txtTotal = new JTextField("0.00");
        gbc.gridx = 1;
        gbc.weightx = 1;
        panelFormulario.add(txtTotal, gbc);

        JButton btnGuardar = new JButton("Registrar abastecimiento");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnActualizar = new JButton("Actualizar lista");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panelFormulario.add(panelBotones, gbc);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "ID Proveedor", "Fecha", "Total (Q)"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAbastecimientos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tblAbastecimientos);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Abastecimientos registrados"));

        add(panelFormulario, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardarAbastecimiento());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> cargarAbastecimientos());
    }

    private void cargarProveedores() {
        cboProveedor.removeAllItems();

        List<Proveedor> proveedores =
                proveedorController.listarProveedores();

        for (Proveedor proveedor : proveedores) {
            if (proveedor.isActivo()) {
                cboProveedor.addItem(new ProveedorItem(
                        proveedor.getIdProveedor(),
                        proveedor.getNombre()));
            }
        }
    }

    private void cargarAbastecimientos() {
        modeloTabla.setRowCount(0);

        List<Abastecimiento> lista =
                abastecimientoController.listarAbastecimientos();

        for (Abastecimiento a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdAbastecimiento(),
                a.getIdProveedor(),
                a.getFecha(),
                String.format("%.2f", a.getTotal())
            });
        }
    }

    private void guardarAbastecimiento() {
        ProveedorItem proveedor =
                (ProveedorItem) cboProveedor.getSelectedItem();

        if (proveedor == null) {
            JOptionPane.showMessageDialog(
                    this, "Primero debes registrar un proveedor activo.");
            return;
        }

        try {
            double total = Double.parseDouble(
                    txtTotal.getText().trim());

            if (!Double.isFinite(total) || total < 0) {
                JOptionPane.showMessageDialog(
                        this, "El total debe ser un número válido no negativo.");
                return;
            }

            Abastecimiento abastecimiento = new Abastecimiento();
            abastecimiento.setIdProveedor(proveedor.id);
            abastecimiento.setTotal(total);

            boolean guardado =
                    abastecimientoController.guardarAbastecimiento(abastecimiento);

            if (guardado) {
                JOptionPane.showMessageDialog(
                        this, "Abastecimiento registrado correctamente.");

                limpiarFormulario();
                cargarAbastecimientos();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se pudo registrar el abastecimiento.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Ingresa un total numérico válido.");
        }
    }

    private void limpiarFormulario() {
        txtTotal.setText("0.00");

        if (cboProveedor.getItemCount() > 0) {
            cboProveedor.setSelectedIndex(0);
        }
    }

    private static class ProveedorItem {
        private final int id;
        private final String nombre;

        private ProveedorItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return id + " - " + nombre;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new FrmAbastecimientos().setVisible(true));
    }
    
public java.util.List<model.DetalleAbastecimiento> listarPorAbastecimiento(
        int idAbastecimiento) {

    java.util.List<model.DetalleAbastecimiento> detalles =
            new java.util.ArrayList<>();

    if (idAbastecimiento <= 0) {
        return detalles;
    }

    String sql = "SELECT ID_DETALLE, ID_ABASTECIMIENTO, "
            + "ID_PRODUCTO, CANTIDAD, COSTO_UNITARIO "
            + "FROM DETALLE_ABASTECIMIENTO "
            + "WHERE ID_ABASTECIMIENTO = ? "
            + "ORDER BY ID_DETALLE";

    try (java.sql.Connection cn = util.Conexion.conectar();
         java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idAbastecimiento);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.DetalleAbastecimiento detalle =
                        new model.DetalleAbastecimiento(
                                rs.getInt("ID_DETALLE"),
                                rs.getInt("ID_ABASTECIMIENTO"),
                                rs.getInt("ID_PRODUCTO"),
                                rs.getInt("CANTIDAD"),
                                rs.getDouble("COSTO_UNITARIO")
                        );

                detalles.add(detalle);
            }
        }

    } catch (Exception e) {
        System.out.println(
                "Error al listar detalles de abastecimiento: "
                + e.getMessage()
        );
    }

    return detalles;
}
   
}
