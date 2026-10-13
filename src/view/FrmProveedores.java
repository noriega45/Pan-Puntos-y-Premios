
package view;

import controller.ProveedorController;
import model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmProveedores extends JFrame {

    private final ProveedorController controller = new ProveedorController();

    private JTextField txtNombre, txtNit, txtTelefono;
    private JTextField txtCorreo, txtDireccion;
    private JTable tblProveedores;
    private DefaultTableModel modelo;
    private int idSeleccionado = 0;

    public FrmProveedores() {
        construirInterfaz();
        ajustarColumnas();
        cargarProveedores();
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Gestión de Proveedores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 650);
        setMinimumSize(new Dimension(800, 550));

        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        principal.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("GESTIÓN DE PROVEEDORES");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setForeground(new Color(35, 55, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(10, 15));
        contenido.setOpaque(false);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del proveedor"));
        formulario.setBackground(Color.WHITE);

        txtNombre = new JTextField(22);
        txtNit = new JTextField(22);
        txtTelefono = new JTextField(22);
        txtCorreo = new JTextField(22);
        txtDireccion = new JTextField(22);

        agregarCampo(formulario, 0, "Nombre:", txtNombre);
        agregarCampo(formulario, 1, "NIT:", txtNit);
        agregarCampo(formulario, 2, "Teléfono:", txtTelefono);
        agregarCampo(formulario, 3, "Correo:", txtCorreo);
        agregarCampo(formulario, 4, "Dirección:", txtDireccion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.setOpaque(false);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnLimpiar = new JButton("Limpiar");

        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnDesactivar);
        botones.add(btnLimpiar);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        formulario.add(botones, gbc);

        modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "NIT", "Teléfono",
                         "Correo", "Dirección", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblProveedores = new JTable(modelo);
        tblProveedores.setRowHeight(25);
        tblProveedores.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        tblProveedores.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tblProveedores);
        scroll.setBorder(
            BorderFactory.createTitledBorder("Proveedores registrados")
        );

        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);
        principal.add(contenido, BorderLayout.CENTER);

        setContentPane(principal);

        btnGuardar.addActionListener(e -> guardarProveedor());
        btnEditar.addActionListener(e -> editarProveedor());
        btnDesactivar.addActionListener(e -> desactivarProveedor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProveedor();
            }
        });
    }

    private void agregarCampo(JPanel panel, int fila,
                              String etiqueta, JTextField campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.gridy = fila;

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }

    private void cargarProveedores() {
        modelo.setRowCount(0);

        List<Proveedor> proveedores = controller.listarProveedores();

        for (Proveedor p : proveedores) {
        if (p.isActivo()) {
        modelo.addRow(new Object[]{
            p.getIdProveedor(),
            p.getNombre(),
            p.getNit(),
            p.getTelefono(),
            p.getCorreo(),
            p.getDireccion(),
            "Sí"
        });
    }
}
    }

    private void guardarProveedor() {
        if (!validarCampos()) {
            return;
        }

        Proveedor p = new Proveedor(
            0,
            txtNombre.getText().trim(),
            txtNit.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim(),
            txtDireccion.getText().trim()
        );

        if (controller.guardarProveedor(p)) {
            JOptionPane.showMessageDialog(this,
                "Proveedor registrado correctamente.");
            limpiarFormulario();
            cargarProveedores();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo guardar el proveedor. Verifica que el NIT no exista.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProveedor() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un proveedor de la tabla.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Proveedor p = new Proveedor(
            idSeleccionado,
            txtNombre.getText().trim(),
            txtNit.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim(),
            txtDireccion.getText().trim()
        );

        if (controller.actualizarProveedor(p)) {
            JOptionPane.showMessageDialog(this,
                "Proveedor actualizado correctamente.");
            limpiarFormulario();
            cargarProveedores();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo actualizar. Verifica el NIT y los datos.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivarProveedor() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un proveedor de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas desactivar este proveedor?",
            "Confirmar desactivación",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        if (controller.desactivarProveedor(idSeleccionado)) {
            JOptionPane.showMessageDialog(this,
                "Proveedor desactivado correctamente.");
            limpiarFormulario();
            cargarProveedores();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo desactivar el proveedor.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarProveedor() {
        int filaVista = tblProveedores.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        int fila = tblProveedores.convertRowIndexToModel(filaVista);

        idSeleccionado = Integer.parseInt(
            modelo.getValueAt(fila, 0).toString()
        );

        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        txtNit.setText(modelo.getValueAt(fila, 2).toString());
        txtTelefono.setText(textoCelda(fila, 3));
        txtCorreo.setText(textoCelda(fila, 4));
        txtDireccion.setText(textoCelda(fila, 5));
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()
                || txtNit.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre y el NIT son obligatorios.");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtNit.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        idSeleccionado = 0;
        tblProveedores.clearSelection();
        txtNombre.requestFocus();
    }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new FrmProveedores().setVisible(true);
    });
}
    private void ajustarColumnas() {
    tblProveedores.setAutoResizeMode(
        javax.swing.JTable.AUTO_RESIZE_OFF
    );

    tblProveedores.getColumnModel().getColumn(0).setPreferredWidth(45);
    tblProveedores.getColumnModel().getColumn(1).setPreferredWidth(180);
    tblProveedores.getColumnModel().getColumn(2).setPreferredWidth(100);
    tblProveedores.getColumnModel().getColumn(3).setPreferredWidth(110);
    tblProveedores.getColumnModel().getColumn(4).setPreferredWidth(180);
    tblProveedores.getColumnModel().getColumn(5).setPreferredWidth(200);
    tblProveedores.getColumnModel().getColumn(6).setPreferredWidth(65);
}
}
