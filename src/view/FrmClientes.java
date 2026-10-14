package view;

import controller.ClienteController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Cliente;

public class FrmClientes extends JFrame {
    private final ClienteController controller = new ClienteController();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Teléfono", "Correo", "Puntos"}, 0);
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtIdBuscar;
    private JTable tblClientes;
    private int idSeleccionado;

    public FrmClientes() {
        construirInterfaz();
        cargarClientes();
    }

    private void construirInterfaz() {
        setTitle("Gestión de clientes");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("GESTIÓN DE CLIENTES");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        txtIdBuscar = new JTextField();

        JPanel campos = new JPanel(new GridLayout(4, 2, 8, 8));
        campos.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
        campos.add(new JLabel("Nombre:*"));
        campos.add(txtNombre);
        campos.add(new JLabel("Teléfono:"));
        campos.add(txtTelefono);
        campos.add(new JLabel("Correo:"));
        campos.add(txtCorreo);
        campos.add(new JLabel("Buscar por ID:"));
        campos.add(txtIdBuscar);

        tblClientes = new JTable(modelo);
        tblClientes.setDefaultEditor(Object.class, null);
        tblClientes.setAutoCreateRowSorter(true);
        tblClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblClientes.getSelectedRow() >= 0) {
                cargarSeleccionTabla();
            }
        });

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(campos, BorderLayout.NORTH);
        centro.add(new JScrollPane(tblClientes), BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Registrar");
        JButton btnEditar = new JButton("Actualizar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnBuscar = new JButton("Buscar ID");
        JButton btnPedidos = new JButton("Historial de pedidos");
        JButton btnPuntos = new JButton("Movimientos de puntos");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnBuscar);
        acciones.add(btnGuardar);
        acciones.add(btnEditar);
        acciones.add(btnDesactivar);
        acciones.add(btnPedidos);
        acciones.add(btnPuntos);
        acciones.add(btnLimpiar);

        principal.add(centro, BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> actualizar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnBuscar.addActionListener(e -> buscar());
        btnPedidos.addActionListener(e -> mostrarHistorialPedidos());
        btnPuntos.addActionListener(e -> mostrarMovimientosPuntos());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void cargarClientes() {
        modelo.setRowCount(0);
        for (Cliente cliente : controller.listarClientes()) {
            modelo.addRow(new Object[]{cliente.getIdCliente(), cliente.getNombre(),
                cliente.getTelefono(), cliente.getCorreo(), cliente.getPuntos()});
        }
    }

    private void cargarSeleccionTabla() {
        int fila = tblClientes.convertRowIndexToModel(tblClientes.getSelectedRow());
        idSeleccionado = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        txtIdBuscar.setText(String.valueOf(idSeleccionado));
        txtNombre.setText(valorCelda(fila, 1));
        txtTelefono.setText(valorCelda(fila, 2));
        txtCorreo.setText(valorCelda(fila, 3));
    }

    private String valorCelda(int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del cliente es obligatorio.");
            return;
        }
        Cliente cliente = new Cliente(0, nombre, null,
                txtTelefono.getText().trim(), txtCorreo.getText().trim());
        if (controller.guardarCliente(cliente)) {
            JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.");
            limpiar();
            cargarClientes();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el cliente. Revisa la conexión y los datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente o búscalo por ID.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del cliente es obligatorio.");
            return;
        }
        Cliente cliente = new Cliente(idSeleccionado, nombre, null,
                txtTelefono.getText().trim(), txtCorreo.getText().trim());
        if (controller.actualizarCliente(cliente)) {
            JOptionPane.showMessageDialog(this, "Cliente actualizado.");
            limpiar();
            cargarClientes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            int id = Integer.parseInt(txtIdBuscar.getText().trim());
            Cliente cliente = controller.buscarCliente(id);
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente activo con ese ID.");
                return;
            }
            idSeleccionado = cliente.getIdCliente();
            txtNombre.setText(cliente.getNombre());
            txtTelefono.setText(cliente.getTelefono() == null ? "" : cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo() == null ? "" : cliente.getCorreo());
            JOptionPane.showMessageDialog(this,
                    "Cliente: " + cliente.getNombre() + "\nPuntos: " + cliente.getPuntos());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escribe un ID numérico válido.");
        }
    }

    private void desactivar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente o búscalo por ID.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Desactivar al cliente #" + idSeleccionado + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION
                && controller.desactivarCliente(idSeleccionado)) {
            JOptionPane.showMessageDialog(this, "Cliente desactivado.");
            limpiar();
            cargarClientes();
        }
    }

    private void mostrarHistorialPedidos() {
        mostrarHistorial("Pedidos del cliente",
                new String[]{"ID pedido", "Fecha", "Total (Q)", "Estado"},
                controller.listarPedidosCliente(idSeleccionado));
    }

    private void mostrarMovimientosPuntos() {
        mostrarHistorial("Movimientos de puntos",
                new String[]{"Fecha", "Tipo", "Puntos", "Descripción"},
                controller.listarMovimientosPuntos(idSeleccionado));
    }

    private void mostrarHistorial(String titulo, String[] columnas, List<Object[]> filas) {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente o búscalo por ID.");
            return;
        }
        DefaultTableModel historial = new DefaultTableModel(columnas, 0);
        for (Object[] fila : filas) {
            historial.addRow(fila);
        }
        JTable tablaHistorial = new JTable(historial);
        tablaHistorial.setDefaultEditor(Object.class, null);
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setPreferredSize(new java.awt.Dimension(680, 300));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtIdBuscar.setText("");
        tblClientes.clearSelection();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new FrmClientes().setVisible(true));
    }
}
