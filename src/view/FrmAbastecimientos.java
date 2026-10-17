package view;

import controller.AbastecimientoController;
import controller.ProveedorController;
import model.Abastecimiento;
import model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrmAbastecimientos extends JFrame {

    private final AbastecimientoController abastecimientoController =
            new AbastecimientoController();

    private final ProveedorController proveedorController =
            new ProveedorController();

    private final Map<Integer, String> nombresProveedores = new HashMap<>();

    private JComboBox<ProveedorItem> cboProveedor;
    private JTable tblAbastecimientos;
    private DefaultTableModel modeloTabla;

    public FrmAbastecimientos() {
        setTitle("Gestión de Abastecimientos");
        setSize(820, 520);
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
                BorderFactory.createTitledBorder("Nuevo abastecimiento"));

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

        JButton btnGuardar = new JButton("Registrar abastecimiento");
        gbc.gridx = 2;
        gbc.weightx = 0;
        panelFormulario.add(btnGuardar, gbc);

        JLabel ayuda = new JLabel("Al registrarlo se abre la ventana para agregar los productos recibidos.");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panelFormulario.add(ayuda, gbc);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Proveedor", "Fecha", "Total (Q)"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAbastecimientos = new JTable(modeloTabla);
        tblAbastecimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tblAbastecimientos);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Abastecimientos registrados"));

        JButton btnDetalle = new JButton("Ver / agregar productos");
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnDetalle);

        add(panelFormulario, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarAbastecimiento());
        btnDetalle.addActionListener(e -> abrirDetalleSeleccionado());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarAbastecimientos();
            }
        });
    }

    private void cargarProveedores() {
        cboProveedor.removeAllItems();
        nombresProveedores.clear();

        List<Proveedor> proveedores =
                proveedorController.listarProveedores();

        for (Proveedor proveedor : proveedores) {
            nombresProveedores.put(proveedor.getIdProveedor(), proveedor.getNombre());
            if (proveedor.isActivo()) {
                cboProveedor.addItem(new ProveedorItem(
                        proveedor.getIdProveedor(),
                        proveedor.getNombre()));
            }
        }
    }

    private void cargarAbastecimientos() {
        int idSeleccionado = idSeleccionado();
        modeloTabla.setRowCount(0);

        List<Abastecimiento> lista =
                abastecimientoController.listarAbastecimientos();

        for (Abastecimiento a : lista) {
            modeloTabla.addRow(new Object[]{
                a.getIdAbastecimiento(),
                a.getIdProveedor() + " - " + nombresProveedores.getOrDefault(a.getIdProveedor(), ""),
                a.getFecha(),
                String.format("%.2f", a.getTotal())
            });
        }

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((int) modeloTabla.getValueAt(i, 0) == idSeleccionado) {
                tblAbastecimientos.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private int idSeleccionado() {
        int fila = tblAbastecimientos.getSelectedRow();
        return fila < 0 ? 0 : (int) modeloTabla.getValueAt(fila, 0);
    }

    private void guardarAbastecimiento() {
        ProveedorItem proveedor =
                (ProveedorItem) cboProveedor.getSelectedItem();

        if (proveedor == null) {
            JOptionPane.showMessageDialog(
                    this, "Primero registra un proveedor activo en el módulo Proveedores.");
            return;
        }

        Abastecimiento abastecimiento = new Abastecimiento();
        abastecimiento.setIdProveedor(proveedor.id);
        abastecimiento.setTotal(0);

        if (abastecimientoController.guardarAbastecimiento(abastecimiento)) {
            cargarAbastecimientos();
            Navegacion.abrir(this, new FrmDetalleAbastecimiento(
                    abastecimiento.getIdAbastecimiento(), proveedor.toString()));
        } else {
            JOptionPane.showMessageDialog(
                    this, "No se pudo registrar el abastecimiento.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDetalleSeleccionado() {
        int fila = tblAbastecimientos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un abastecimiento de la tabla.");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String proveedor = modeloTabla.getValueAt(fila, 1).toString();
        Navegacion.abrir(this, new FrmDetalleAbastecimiento(id, proveedor));
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
}
