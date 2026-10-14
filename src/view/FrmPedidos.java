package view;

import controller.PedidoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Pedido;

public class FrmPedidos extends JFrame {
    private final PedidoController controller = new PedidoController();
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{
        "ID pedido", "ID cliente", "Fecha", "Total (Q)", "Estado"
    }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public FrmPedidos() {
        setTitle("Pan, Puntos y Premios - Estado de pedidos");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("SEGUIMIENTO DE PEDIDOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEntregar = new JButton("Marcar entregado");
        JButton btnAnular = new JButton("Anular pendiente");
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnActualizar);
        acciones.add(btnAnular);
        acciones.add(btnEntregar);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        btnActualizar.addActionListener(e -> cargarPedidos());
        btnEntregar.addActionListener(e -> cambiarEstado(true));
        btnAnular.addActionListener(e -> cambiarEstado(false));
        cargarPedidos();
    }

    private void cargarPedidos() {
        modelo.setRowCount(0);
        List<Pedido> pedidos = controller.listarPedidos();
        for (Pedido pedido : pedidos) {
            modelo.addRow(new Object[]{pedido.getIdPedido(), pedido.getIdCliente(),
                pedido.getFecha(), String.format("%.2f", pedido.getTotal()), pedido.getEstado()});
        }
    }

    private void cambiarEstado(boolean entregar) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido de la tabla.");
            return;
        }
        int idPedido = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        String estado = modelo.getValueAt(fila, 4).toString();
        if ((entregar && !"PAGADO".equalsIgnoreCase(estado))
                || (!entregar && !"PENDIENTE".equalsIgnoreCase(estado))) {
            JOptionPane.showMessageDialog(this, entregar
                    ? "Solo se pueden marcar como entregados los pedidos pagados."
                    : "Solo se pueden anular pedidos pendientes.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                entregar ? "¿Confirmas que el pedido " + idPedido + " fue entregado?"
                        : "¿Anular el pedido pendiente " + idPedido + "?",
                "Confirmar cambio", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        boolean actualizado = entregar
                ? controller.marcarEntregado(idPedido)
                : controller.anularPendiente(idPedido);
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
