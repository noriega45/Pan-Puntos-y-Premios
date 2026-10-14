package view;

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
import model.Pago;
import model.Pedido;

public class FrmPago extends JFrame {

    private final PagoController pagoController = new PagoController();
    private final PedidoController pedidoController = new PedidoController();
    private JComboBox<Pedido> cboPedidos;
    private JTextField txtTotal;
    private JTextField txtDineroRecibido;
    private JTextField txtReferencia;
    private JComboBox<String> cboMetodo;
    private JTable tblPagos;
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
        setSize(760, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("COBRO DE PEDIDOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del cobro"));
        cboPedidos = new JComboBox<>();
        cboPedidos.setEditable(false);
        cboPedidos.setToolTipText("Selecciona un pedido pendiente de la lista");
        cboPedidos.addActionListener(e -> seleccionarPedido());
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtDineroRecibido = new JTextField();
        txtReferencia = new JTextField();
        cboMetodo = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
        formulario.add(new JLabel("Pedido pendiente:"));
        formulario.add(cboPedidos);
        formulario.add(new JLabel("Total del pedido (Q):"));
        formulario.add(txtTotal);
        formulario.add(new JLabel("Método de pago:"));
        formulario.add(cboMetodo);
        formulario.add(new JLabel("Efectivo recibido (Q):"));
        formulario.add(txtDineroRecibido);
        formulario.add(new JLabel("Referencia de tarjeta:"));
        formulario.add(txtReferencia);

        modelo = new DefaultTableModel(
                new Object[]{"ID pago", "Fecha", "Método", "Monto (Q)", "Estado", "Referencia"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblPagos = new JTable(modelo);
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(new JScrollPane(tblPagos), BorderLayout.CENTER);

        JButton btnConsultar = new JButton("Consultar pedido");
        JButton btnActualizar = new JButton("Actualizar pendientes");
        JButton btnCobrar = new JButton("Procesar pago");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnConsultar);
        botones.add(btnActualizar);
        botones.add(btnCobrar);
        botones.add(btnLimpiar);

        principal.add(centro, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);
        add(principal);

        btnConsultar.addActionListener(e -> consultarPedido());
        btnActualizar.addActionListener(e -> cargarPedidosPendientes());
        btnCobrar.addActionListener(e -> procesarPago());
        btnLimpiar.addActionListener(e -> limpiar());
        cboMetodo.addActionListener(e -> actualizarCamposMetodo());
        actualizarCamposMetodo();
        cargarPedidosPendientes();
    }

    private void consultarPedido() {
        try {
            pedidoActual = (Pedido) cboPedidos.getSelectedItem();
            if (pedidoActual == null) {
                txtTotal.setText("");
                modelo.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Selecciona un pedido pendiente.");
                return;
            }
            int idPedido = pedidoActual.getIdPedido();
            txtTotal.setText(String.format("%.2f", pedidoActual.getTotal()));
            cargarPagos(idPedido);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudieron consultar los pedidos pendientes: "
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarPedido() {
        Pedido seleccionado = (Pedido) cboPedidos.getSelectedItem();
        pedidoActual = seleccionado;
        if (seleccionado == null) {
            txtTotal.setText("");
            modelo.setRowCount(0);
            return;
        }
        txtTotal.setText(String.format("%.2f", seleccionado.getTotal()));
    }

    private void procesarPago() {
        if (pedidoActual == null
                || cboPedidos.getSelectedItem() != pedidoActual) {
            JOptionPane.showMessageDialog(this,
                    "Primero consulta un pedido pendiente.");
            return;
        }

        String metodo = cboMetodo.getSelectedItem().toString();
        String estado = "APROBADO";
        String referencia = txtReferencia.getText().trim();
        double recibido = pedidoActual.getTotal();
        double cambio = 0;

        if ("EFECTIVO".equals(metodo)) {
            try {
                recibido = Double.parseDouble(txtDineroRecibido.getText().trim());
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
            referencia = "";
        } else {
            if (referencia.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Ingresa la referencia de la tarjeta.");
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿La transacción con tarjeta fue aprobada?",
                    "Simular pago con tarjeta",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) {
                estado = "RECHAZADO";
            }
        }

        Pago pago = new Pago();
        pago.setIdPedido(pedidoActual.getIdPedido());
        pago.setMetodoPago(metodo);
        pago.setMonto(pedidoActual.getTotal());
        pago.setEstado(estado);
        pago.setReferencia(referencia);

        if (!pagoController.guardarPago(pago)) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo procesar el pago. Puede faltar inventario o el pedido ya cambió de estado.",
                    "Cobro no realizado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("RECHAZADO".equals(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Pago rechazado. No se descontaron existencias ni puntos.");
        } else {
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
            cargarPagos(pago.getIdPedido());
            cargarPedidosPendientes();
        }
    }

    private void cargarPagos(int idPedido) {
        modelo.setRowCount(0);
        List<Pago> pagos = pagoController.listarPagosPorPedido(idPedido);
        for (Pago pago : pagos) {
            modelo.addRow(new Object[]{
                pago.getIdPago(), pago.getFechaPago(), pago.getMetodoPago(),
                pago.getMonto(), pago.getEstado(), pago.getReferencia()
            });
        }
    }

    private void actualizarCamposMetodo() {
        boolean efectivo = "EFECTIVO".equals(cboMetodo.getSelectedItem());
        txtDineroRecibido.setEnabled(efectivo);
        txtReferencia.setEnabled(!efectivo);
    }

    private void cargarPedidosPendientes() {
        cboPedidos.removeAllItems();
        pedidoActual = null;
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

    private void limpiar() {
        pedidoActual = null;
        cboPedidos.setSelectedIndex(-1);
        txtTotal.setText("");
        txtDineroRecibido.setText("");
        txtReferencia.setText("");
        modelo.setRowCount(0);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new FrmPago().setVisible(true));
    }
}
