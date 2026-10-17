package view;

import controller.ClienteController;
import controller.PagoController;
import controller.PedidoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.table.DefaultTableModel;
import model.Cliente;
import model.Pago;
import model.Pedido;

public class FrmPago extends JFrame {

    private final PagoController pagoController = new PagoController();
    private final PedidoController pedidoController = new PedidoController();
    private final ClienteController clienteController = new ClienteController();
    private JComboBox<Pedido> cboPedidos;
    private JTextField txtCliente;
    private JTextField txtTotal;
    private JTextField txtDineroRecibido;
    private JComboBox<String> cboMetodo;
    private JTextField txtReferenciaPOS;
    private JTable tblDetalle;
    private DefaultTableModel modelo;
    private Pedido pedidoActual;

    public FrmPago() {
        construirInterfaz();
    }

    public FrmPago(int idPedido, String metodo) {
        this();
        cboMetodo.setSelectedItem(metodo);
        for (int i = 0; i < cboPedidos.getItemCount(); i++) {
            if (cboPedidos.getItemAt(i).getIdPedido() == idPedido) {
                cboPedidos.setSelectedIndex(i);
                seleccionarPedido();
                break;
            }
        }
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Cobro de pedidos");
        setSize(760, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("COBRO DE PEDIDOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del cobro"));
        cboPedidos = new JComboBox<>();
        cboPedidos.setEditable(false);
        cboPedidos.setToolTipText("Selecciona un pedido pendiente de la lista");
        cboPedidos.addActionListener(e -> seleccionarPedido());
        txtCliente = new JTextField();
        txtCliente.setEditable(false);
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtDineroRecibido = new JTextField();
        cboMetodo = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
        txtReferenciaPOS = new JTextField();
        txtReferenciaPOS.setEditable(false);
        formulario.add(new JLabel("Pedido pendiente:"));
        formulario.add(cboPedidos);
        formulario.add(new JLabel("Cliente:"));
        formulario.add(txtCliente);
        formulario.add(new JLabel("Total del pedido (Q):"));
        formulario.add(txtTotal);
        formulario.add(new JLabel("Método de pago:"));
        formulario.add(cboMetodo);
        formulario.add(new JLabel("Efectivo recibido (Q):"));
        formulario.add(txtDineroRecibido);
        formulario.add(new JLabel("Referencia POS (automática):"));
        formulario.add(txtReferenciaPOS);

        modelo = new DefaultTableModel(
                new Object[]{"Tipo", "Descripción", "Cantidad", "Precio unitario (Q)", "Subtotal (Q)"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblDetalle = new JTable(modelo);
        JScrollPane scrollDetalle = new JScrollPane(tblDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del pedido"));
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(scrollDetalle, BorderLayout.CENTER);

        JButton btnHistorial = new JButton("Historial de pedidos");
        JButton btnAnular = new JButton("Anular pedido");
        JButton btnCobrar = new JButton("Procesar pago");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnHistorial);
        botones.add(btnAnular);
        botones.add(btnCobrar);

        principal.add(centro, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);
        add(principal);

        btnCobrar.addActionListener(e -> procesarPago());
        btnAnular.addActionListener(e -> anularPedido());
        btnHistorial.addActionListener(e -> Navegacion.abrir(this, new FrmPedidos()));
        cboMetodo.addActionListener(e -> actualizarCamposMetodo());
        actualizarCamposMetodo();
        cargarPedidosPendientes();
    }

    private void seleccionarPedido() {
        Pedido seleccionado = (Pedido) cboPedidos.getSelectedItem();
        pedidoActual = seleccionado;
        if (seleccionado == null) {
            txtCliente.setText("");
            txtTotal.setText("");
            modelo.setRowCount(0);
            return;
        }
        Cliente cliente = clienteController.buscarCliente(seleccionado.getIdCliente());
        txtCliente.setText(seleccionado.getIdCliente()
                + (cliente != null ? " - " + cliente.getNombre() + " (" + cliente.getPuntos() + " puntos)" : ""));
        txtTotal.setText(String.format("%.2f", seleccionado.getTotal()));
        cargarDetalle(seleccionado.getIdPedido());
    }

    private void cargarDetalle(int idPedido) {
        modelo.setRowCount(0);
        for (Object[] fila : pedidoController.listarDetalleConNombres(idPedido)) {
            modelo.addRow(fila);
        }
    }

    private void anularPedido() {
        if (pedidoActual == null) {
            JOptionPane.showMessageDialog(this, "No hay un pedido pendiente seleccionado.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Anular el pedido #" + pedidoActual.getIdPedido() + "?\n"
                + "No se modificarán existencias ni puntos.",
                "Anular pedido", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        if (pedidoController.anularPendiente(pedidoActual.getIdPedido())) {
            JOptionPane.showMessageDialog(this, "Pedido #" + pedidoActual.getIdPedido() + " anulado.");
            cargarPedidosPendientes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo anular el pedido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarPago() {
        if (pedidoActual == null
                || cboPedidos.getSelectedItem() != pedidoActual) {
            JOptionPane.showMessageDialog(this,
                    "Primero consulta un pedido pendiente.");
            return;
        }

        String metodo = cboMetodo.getSelectedItem().toString();
        String referencia = "";
        double cambio = 0;

        if ("TARJETA".equals(metodo)) {
            referencia = txtReferenciaPOS.getText().trim();
            if (referencia.isEmpty()) {
                referencia = generarReferenciaPOS();
            }
        }

        List<String> faltantes = pagoController.listarFaltantesInventario(pedidoActual.getIdPedido());
        if (!faltantes.isEmpty()) {
            Pago rechazado = crearPago(metodo, "RECHAZADO", referencia);
            pagoController.guardarPago(rechazado);
            JOptionPane.showMessageDialog(this,
                    "PAGO RECHAZADO: no hay existencias suficientes.\n- "
                    + String.join("\n- ", faltantes)
                    + "\n\nEl pedido sigue PENDIENTE. No se descontaron existencias ni puntos."
                    + "\nAbastece el inventario o anula el pedido.",
                    "Cobro rechazado", JOptionPane.ERROR_MESSAGE);
            actualizarCamposMetodo();
            return;
        }

        if ("EFECTIVO".equals(metodo)) {
            try {
                double recibido = Double.parseDouble(txtDineroRecibido.getText().trim());
                if (recibido < pedidoActual.getTotal()) {
                    JOptionPane.showMessageDialog(this,
                            "El efectivo recibido no cubre el total. Ingresa otro monto.");
                    return;
                }
                cambio = recibido - pedidoActual.getTotal();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Ingresa cuánto dinero recibiste en efectivo.");
                return;
            }
        }

        Pago pago = crearPago(metodo, "APROBADO", referencia);

        if (!pagoController.guardarPago(pago)) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo procesar el pago. Puede faltar inventario o el pedido ya cambió de estado.",
                    "Cobro no realizado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "COMPROBANTE DE VENTA\nPedido: " + pedidoActual.getIdPedido()
                + "\nTotal: Q" + String.format("%.2f", pedidoActual.getTotal())
                + "\nMétodo: " + metodo
                + ("EFECTIVO".equals(metodo)
                        ? "\nCambio: Q" + String.format("%.2f", cambio)
                        : "\nReferencia: " + referencia)
                + "\nEstado: PAGADO");
        pedidoActual = null;
        txtTotal.setText("");
        txtDineroRecibido.setText("");
        cargarPedidosPendientes();
        actualizarCamposMetodo();
    }

    private Pago crearPago(String metodo, String estado, String referencia) {
        Pago pago = new Pago();
        pago.setIdPedido(pedidoActual.getIdPedido());
        pago.setMetodoPago(metodo);
        pago.setMonto(pedidoActual.getTotal());
        pago.setEstado(estado);
        pago.setReferencia(referencia);
        return pago;
    }

    private void actualizarCamposMetodo() {
        boolean efectivo = "EFECTIVO".equals(cboMetodo.getSelectedItem());
        txtDineroRecibido.setEnabled(efectivo);
        txtReferenciaPOS.setText(efectivo ? "" : generarReferenciaPOS());
    }

    private void cargarPedidosPendientes() {
        cboPedidos.removeAllItems();
        pedidoActual = null;
        txtCliente.setText("");
        txtTotal.setText("");
        for (Pedido pedido : pedidoController.listarPendientesConDetalles()) {
            cboPedidos.addItem(pedido);
        }
        if (cboPedidos.getItemCount() == 0) {
            modelo.setRowCount(0);
        } else {
            seleccionarPedido();
        }
    }

    private String generarReferenciaPOS() {
        String fecha = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int autorizacion = java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "POS-" + fecha + "-" + autorizacion;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new FrmPago().setVisible(true));
    }
}
