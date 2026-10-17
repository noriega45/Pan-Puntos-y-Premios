package view;

import controller.PagoController;
import controller.PedidoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Pago;

public class FrmPedidos extends JFrame {

    private final PedidoController pedidoController = new PedidoController();
    private final PagoController pagoController = new PagoController();
    private final DefaultTableModel modeloPedidos = crearModelo(
            "ID pedido", "Cliente", "Fecha", "Total (Q)", "Estado", "Último pago", "Pagos rechazados");
    private final DefaultTableModel modeloPagos = crearModelo(
            "ID pago", "Fecha", "Método", "Monto (Q)", "Resultado", "Referencia");
    private final JTable tblPedidos = new JTable(modeloPedidos);
    private final JTable tblPagos = new JTable(modeloPagos);
    private final JComboBox<String> cboEstado = new JComboBox<>(
            new String[]{"TODOS", "PENDIENTE", "PAGADO", "ENTREGADO", "ANULADO", "CON PAGO RECHAZADO"});

    public FrmPedidos() {
        setTitle("Pan, Puntos y Premios - Historial de pedidos");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel encabezado = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("HISTORIAL DE PEDIDOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtro.add(new JLabel("Mostrar:"));
        filtro.add(cboEstado);
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(filtro, BorderLayout.EAST);
        principal.add(encabezado, BorderLayout.NORTH);

        tblPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPedidos = new JScrollPane(tblPedidos);
        scrollPedidos.setBorder(BorderFactory.createTitledBorder("Pedidos"));
        JScrollPane scrollPagos = new JScrollPane(tblPagos);
        scrollPagos.setBorder(BorderFactory.createTitledBorder(
                "Intentos de pago del pedido seleccionado (aprobados y rechazados)"));
        JPanel tablas = new JPanel(new GridLayout(2, 1, 8, 8));
        tablas.add(scrollPedidos);
        tablas.add(scrollPagos);
        principal.add(tablas, BorderLayout.CENTER);

        JButton btnCobrar = new JButton("Cobrar");
        JButton btnEntregar = new JButton("Marcar entregado");
        JButton btnAnular = new JButton("Anular pendiente");
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnAnular);
        acciones.add(btnEntregar);
        acciones.add(btnCobrar);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        cboEstado.addActionListener(e -> cargarPedidos());
        tblPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarPagos();
            }
        });
        btnCobrar.addActionListener(e -> cobrar());
        btnEntregar.addActionListener(e -> cambiarEstado(true));
        btnAnular.addActionListener(e -> cambiarEstado(false));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarPedidos();
            }
        });
        cargarPedidos();
    }

    private static DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void cargarPedidos() {
        int idSeleccionado = idPedidoSeleccionado();
        modeloPedidos.setRowCount(0);
        List<Object[]> pedidos = pedidoController.listarHistorial(cboEstado.getSelectedItem().toString());
        for (Object[] fila : pedidos) {
            modeloPedidos.addRow(fila);
        }
        for (int i = 0; i < modeloPedidos.getRowCount(); i++) {
            if ((int) modeloPedidos.getValueAt(i, 0) == idSeleccionado) {
                tblPedidos.setRowSelectionInterval(i, i);
                break;
            }
        }
        cargarPagos();
    }

    private void cargarPagos() {
        modeloPagos.setRowCount(0);
        int idPedido = idPedidoSeleccionado();
        if (idPedido <= 0) {
            return;
        }
        for (Pago pago : pagoController.listarPagosPorPedido(idPedido)) {
            modeloPagos.addRow(new Object[]{
                pago.getIdPago(), pago.getFechaPago(), pago.getMetodoPago(),
                String.format("%.2f", pago.getMonto()), pago.getEstado(),
                pago.getReferencia() == null ? "" : pago.getReferencia()
            });
        }
    }

    private int idPedidoSeleccionado() {
        int fila = tblPedidos.getSelectedRow();
        return fila < 0 ? 0 : (int) modeloPedidos.getValueAt(fila, 0);
    }

    private String estadoSeleccionado() {
        int fila = tblPedidos.getSelectedRow();
        return fila < 0 ? "" : modeloPedidos.getValueAt(fila, 4).toString();
    }

    private void cobrar() {
        int idPedido = idPedidoSeleccionado();
        if (idPedido <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla.");
            return;
        }
        if (!"PENDIENTE".equalsIgnoreCase(estadoSeleccionado())) {
            JOptionPane.showMessageDialog(this, "Solo se pueden cobrar pedidos pendientes.");
            return;
        }
        Navegacion.abrir(this, new FrmPago(idPedido, "EFECTIVO"));
    }

    private void cambiarEstado(boolean entregar) {
        int idPedido = idPedidoSeleccionado();
        if (idPedido <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla.");
            return;
        }
        String estado = estadoSeleccionado();
        if ((entregar && !"PAGADO".equalsIgnoreCase(estado))
                || (!entregar && !"PENDIENTE".equalsIgnoreCase(estado))) {
            JOptionPane.showMessageDialog(this, entregar
                    ? "Solo se pueden marcar como entregados los pedidos pagados."
                    : "Solo se pueden anular pedidos pendientes.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                entregar ? "¿Confirmas que el pedido " + idPedido + " fue entregado?"
                        : "¿Anular el pedido pendiente " + idPedido + "?\nNo se modificarán existencias ni puntos.",
                "Confirmar cambio", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        boolean actualizado = entregar
                ? pedidoController.marcarEntregado(idPedido)
                : pedidoController.anularPendiente(idPedido);
        if (actualizado) {
            cargarPedidos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado del pedido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPedidos().setVisible(true));
    }
}
