package view;

import controller.CanjeController;
import controller.ClienteController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Cliente;
import model.Premio;

public class FrmCanjes extends JFrame {

    private final CanjeController canjeController = new CanjeController();
    private final ClienteController clienteController = new ClienteController();
    private final List<Premio> premios = new ArrayList<>();
    private final DefaultTableModel modeloPremios = crearModelo(
            "Premio", "Descripción", "Puntos requeridos", "Unidades disponibles", "¿Le alcanza?");
    private final DefaultTableModel modeloHistorial = crearModelo(
            "Fecha", "Premio", "Puntos utilizados");
    private final JTable tblPremios = new JTable(modeloPremios);
    private final JTextField txtIdCliente = new JTextField(8);
    private final JLabel lblCliente = new JLabel("Ningún cliente seleccionado.");
    private Cliente cliente;

    public FrmCanjes() {
        setTitle("Pan, Puntos y Premios - Canje de premios");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        construirInterfaz();
        cargarPremios();
    }

    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("CANJE DE PREMIOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));

        JPanel panelCliente = new JPanel(new BorderLayout(6, 6));
        panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnElegir = new JButton("Elegir cliente");
        buscador.add(new JLabel("ID:"));
        buscador.add(txtIdCliente);
        buscador.add(btnBuscar);
        buscador.add(btnElegir);
        lblCliente.setFont(lblCliente.getFont().deriveFont(Font.BOLD, 14f));
        lblCliente.setBorder(BorderFactory.createEmptyBorder(0, 8, 6, 8));
        panelCliente.add(buscador, BorderLayout.NORTH);
        panelCliente.add(lblCliente, BorderLayout.CENTER);

        JPanel norte = new JPanel(new BorderLayout(8, 8));
        norte.add(titulo, BorderLayout.NORTH);
        norte.add(panelCliente, BorderLayout.CENTER);
        principal.add(norte, BorderLayout.NORTH);

        tblPremios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPremios = new JScrollPane(tblPremios);
        scrollPremios.setBorder(BorderFactory.createTitledBorder("Premios disponibles"));
        JTable tblHistorial = new JTable(modeloHistorial);
        JScrollPane scrollHistorial = new JScrollPane(tblHistorial);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Canjes realizados por el cliente"));
        JPanel tablas = new JPanel(new GridLayout(2, 1, 8, 8));
        tablas.add(scrollPremios);
        tablas.add(scrollHistorial);
        principal.add(tablas, BorderLayout.CENTER);

        JButton btnCanjear = new JButton("Canjear premio seleccionado");
        btnCanjear.setFont(btnCanjear.getFont().deriveFont(Font.BOLD));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnCanjear);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        btnBuscar.addActionListener(e -> buscarCliente());
        txtIdCliente.addActionListener(e -> buscarCliente());
        btnElegir.addActionListener(e -> Navegacion.abrir(this, new FrmClientes(this::asignarCliente)));
        btnCanjear.addActionListener(e -> canjear());
    }

    private void buscarCliente() {
        String texto = txtIdCliente.getText().trim();
        if (texto.isEmpty()) {
            Navegacion.abrir(this, new FrmClientes(this::asignarCliente));
            return;
        }
        try {
            Cliente encontrado = clienteController.buscarCliente(Integer.parseInt(texto));
            if (encontrado == null) {
                JOptionPane.showMessageDialog(this, "No existe un cliente activo con ese ID.");
                limpiarCliente();
                return;
            }
            asignarCliente(encontrado);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del cliente debe ser un número.");
        }
    }

    private void asignarCliente(Cliente seleccionado) {
        cliente = clienteController.buscarCliente(seleccionado.getIdCliente());
        if (cliente == null) {
            limpiarCliente();
            return;
        }
        txtIdCliente.setText(String.valueOf(cliente.getIdCliente()));
        lblCliente.setText(cliente.getNombre() + "   |   Saldo: " + cliente.getPuntos() + " puntos");
        cargarPremios();
        cargarHistorial();
        toFront();
    }

    private void limpiarCliente() {
        cliente = null;
        lblCliente.setText("Ningún cliente seleccionado.");
        modeloHistorial.setRowCount(0);
        cargarPremios();
    }

    private void cargarPremios() {
        int filaSeleccionada = tblPremios.getSelectedRow();
        premios.clear();
        premios.addAll(canjeController.listarPremiosDisponibles());
        modeloPremios.setRowCount(0);
        for (Premio premio : premios) {
            String alcanza = cliente == null ? "-"
                    : (cliente.getPuntos() >= premio.getPuntosRequeridos() ? "Sí"
                    : "No (faltan " + (premio.getPuntosRequeridos() - cliente.getPuntos()) + ")");
            modeloPremios.addRow(new Object[]{
                premio.getNombre(),
                premio.getDescripcion() == null ? "" : premio.getDescripcion(),
                premio.getPuntosRequeridos(),
                premio.getExistencia(),
                alcanza
            });
        }
        if (filaSeleccionada >= 0 && filaSeleccionada < modeloPremios.getRowCount()) {
            tblPremios.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
        }
    }

    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        if (cliente == null) {
            return;
        }
        for (Object[] fila : canjeController.listarHistorialCliente(cliente.getIdCliente())) {
            modeloHistorial.addRow(fila);
        }
    }

    private void canjear() {
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Primero busca o elige un cliente.");
            return;
        }
        int fila = tblPremios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un premio de la tabla.");
            return;
        }
        Premio premio = premios.get(fila);
        int saldoAnterior = cliente.getPuntos();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Canjear " + premio.getNombre() + " para " + cliente.getNombre() + "\n"
                + "Puntos: " + saldoAnterior + " - " + premio.getPuntosRequeridos()
                + " = " + (saldoAnterior - premio.getPuntosRequeridos()) + "\n¿Confirmar?",
                "Confirmar canje", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        String error = canjeController.canjear(cliente.getIdCliente(), premio);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Canje no realizado", JOptionPane.WARNING_MESSAGE);
            asignarCliente(cliente);
            return;
        }
        asignarCliente(cliente);
        JOptionPane.showMessageDialog(this,
                "COMPROBANTE DE CANJE\n\n"
                + "Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "\n"
                + "Cliente: " + cliente.getNombre() + " (ID " + cliente.getIdCliente() + ")\n"
                + "Premio: " + premio.getNombre() + "\n"
                + "Puntos utilizados: " + premio.getPuntosRequeridos() + "\n"
                + "Saldo anterior: " + saldoAnterior + "\n"
                + "Saldo actual: " + cliente.getPuntos(),
                "Canje realizado", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmCanjes().setVisible(true));
    }
}
