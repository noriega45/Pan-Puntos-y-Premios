
package view;

import controller.PedidoController;
import model.Pedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.sql.Date;

public class FrmPedidos extends JFrame {

    private final PedidoController controller = new PedidoController();

    private JTextField txtIdCliente;
    private JTextField txtTotal;
    private JComboBox<String> cboEstado;
    private JTable tblPedidos;
    private DefaultTableModel modelo;

    public FrmPedidos() {
        construirInterfaz();
        cargarPedidos();
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Gestión de Pedidos");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        principal.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("GESTIÓN DE PEDIDOS");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(new Color(35, 65, 100));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(2, 4, 10, 10));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del pedido"));
        formulario.setBackground(Color.WHITE);

        txtIdCliente = new JTextField();
        txtTotal = new JTextField();
        cboEstado = new JComboBox<>(new String[]{
            "PENDIENTE"
        });

        formulario.add(new JLabel("ID del cliente:"));
        formulario.add(new JLabel("Total (Q):"));
        formulario.add(new JLabel("Estado:"));
        formulario.add(new JLabel(""));

        formulario.add(txtIdCliente);
        formulario.add(txtTotal);
        formulario.add(cboEstado);

        JButton btnGuardar = new JButton("Guardar pedido");
        formulario.add(btnGuardar);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(new Color(245, 247, 250));
        centro.add(formulario, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
            new String[]{"ID Pedido", "ID Cliente", "Fecha", "Total (Q)", "Estado"},
            0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblPedidos = new JTable(modelo);
        tblPedidos.setRowHeight(25);
        tblPedidos.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(tblPedidos);
        scroll.setBorder(
            BorderFactory.createTitledBorder("Pedidos registrados")
        );
        
        tblPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tblPedidos.getSelectedRow();

                if (fila >= 0) {
                    txtIdCliente.setText(
                        modelo.getValueAt(fila, 1).toString()
                    );

                    txtTotal.setText(
                        modelo.getValueAt(fila, 3).toString()
                    );

                    cboEstado.setSelectedItem(
                        modelo.getValueAt(fila, 4).toString()
                    );
                }
            }
        });


        centro.add(scroll, BorderLayout.CENTER);

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnActualizar = new JButton("Actualizar pedido");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(new Color(245, 247, 250));
        botones.add(btnLimpiar);
        botones.add(btnActualizar);

        centro.add(botones, BorderLayout.SOUTH);

        principal.add(centro, BorderLayout.CENTER);
        add(principal);

        btnGuardar.addActionListener(e -> guardarPedido());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> actualizarPedido());
    }

    private void cargarPedidos() {
        modelo.setRowCount(0);

        List<Pedido> pedidos = controller.listarPedidos();

        for (Pedido p : pedidos) {
            modelo.addRow(new Object[]{
                p.getIdPedido(),
                p.getIdCliente(),
                p.getFecha(),
                p.getTotal(),
                p.getEstado()
            });
        }
    }

    private void guardarPedido() {
        try {
            if (txtIdCliente.getText().trim().isEmpty()
                    || txtTotal.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Ingresa el ID del cliente y el total."
                );
                return;
            }

            int idCliente = Integer.parseInt(
                txtIdCliente.getText().trim()
            );
            if (!controller.existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(
                this,
                "El cliente no existe o está inactivo. Verifica el ID."
            );
            return;
            }

            double total = Double.parseDouble(
                txtTotal.getText().trim()
            );

            if (idCliente <= 0 || total < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "El ID debe ser positivo y el total no puede ser negativo."
                );
                return;
            }

            Pedido pedido = new Pedido();
            pedido.setIdCliente(idCliente);
            pedido.setTotal(total);
            pedido.setEstado(cboEstado.getSelectedItem().toString());

            if (controller.guardarPedido(pedido)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pedido guardado correctamente."
                );
                limpiarFormulario();
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el pedido. Verifica que el cliente exista."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "El ID del cliente y el total deben ser números válidos."
            );
        }
    }

    private void limpiarFormulario() {
        txtIdCliente.setText("");
        txtTotal.setText("");
        cboEstado.setSelectedIndex(0);
        txtIdCliente.requestFocus();
        
    }
        private void actualizarPedido() {
        int fila = tblPedidos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un pedido de la tabla para actualizar."
            );
            return;
        }

        try {
            int idPedido = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
            );

            if (txtIdCliente.getText().trim().isEmpty()
                    || txtTotal.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Ingresa el ID del cliente y el total."
                );
                return;
            }

            int idCliente = Integer.parseInt(
                txtIdCliente.getText().trim()
            );
            if (!controller.existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(
                this,
                "El cliente no existe o está inactivo. Verifica el ID."
            );
            return;
            }

            double total = Double.parseDouble(
                txtTotal.getText().trim()
            );

            if (idCliente <= 0 || total < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "El ID debe ser positivo y el total no puede ser negativo."
                );
                return;
            }

            Pedido pedido = new Pedido();
            pedido.setIdPedido(idPedido);
            pedido.setIdCliente(idCliente);
            pedido.setTotal(total);
            pedido.setEstado(cboEstado.getSelectedItem().toString());

            if (controller.actualizarPedido(pedido)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pedido actualizado correctamente."
                );
                limpiarFormulario();
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar el pedido. Verifica que el cliente exista."
                );
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "El ID del cliente y el total deben ser números válidos."
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmPedidos().setVisible(true);
        });
    }
}
