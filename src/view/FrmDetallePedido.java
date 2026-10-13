
package view;

import controller.DetallePedidoController;
import model.DetallePedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmDetallePedido extends JFrame {

    private final DetallePedidoController controller =
            new DetallePedidoController();

    private JTextField txtIdPedido;
    private JTextField txtIdProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTable tblDetalles;
    private DefaultTableModel modelo;

    public FrmDetallePedido() {
        setTitle("Pan, Puntos y Premios - Detalle de Pedido");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("DETALLE DE PEDIDO");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(2, 4, 10, 8));
        formulario.setBorder(BorderFactory.createTitledBorder(
                "Datos del producto"));

        formulario.add(new JLabel("ID del pedido:"));
        formulario.add(new JLabel("ID del producto:"));
        formulario.add(new JLabel("Cantidad:"));
        formulario.add(new JLabel("Precio unitario (Q):"));

        txtIdPedido = new JTextField();
        txtIdProducto = new JTextField();
        txtCantidad = new JTextField();
        txtPrecio = new JTextField();

        formulario.add(txtIdPedido);
        formulario.add(txtIdProducto);
        formulario.add(txtCantidad);
        formulario.add(txtPrecio);

        modelo = new DefaultTableModel(
                new Object[]{"ID Detalle", "ID Pedido", "ID Producto",
                    "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblDetalles = new JTable(modelo);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(new JScrollPane(tblDetalles), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        JButton btnCargar = new JButton("Consultar pedido");
        JButton btnGuardar = new JButton("Agregar producto");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCargar);
        botones.add(btnGuardar);
        botones.add(btnLimpiar);

        botones.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(botones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarDetalles());
        btnGuardar.addActionListener(e -> guardarDetalle());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarDetalles() {
        try {
            int idPedido = Integer.parseInt(txtIdPedido.getText().trim());

            modelo.setRowCount(0);

            List<DetallePedido> detalles =
                    controller.listarDetallesPorPedido(idPedido);

            for (DetallePedido detalle : detalles) {
                double subtotal = detalle.getCantidad()
                        * detalle.getPrecioUnitario();

                modelo.addRow(new Object[]{
                    detalle.getIdDetalle(),
                    detalle.getIdPedido(),
                    detalle.getIdProducto(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    subtotal
                });
            }

            JOptionPane.showMessageDialog(this,
                    "Detalles consultados: " + detalles.size());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa un ID de pedido válido.");
        }
    }

    private void guardarDetalle() {
        try {
            int idPedido = Integer.parseInt(txtIdPedido.getText().trim());
            int idProducto = Integer.parseInt(txtIdProducto.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            DetallePedido detalle = new DetallePedido(
                    0, idPedido, idProducto, cantidad, precio);

            if (controller.guardarDetalle(detalle)) {
                JOptionPane.showMessageDialog(this,
                        "Producto agregado y total actualizado.");

                txtIdProducto.setText("");
                txtCantidad.setText("");
                txtPrecio.setText("");

                cargarDetalles();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo agregar el producto. "
                        + "Verifica los IDs y los datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Verifica que todos los campos numéricos sean válidos.");
        }
    }

    private void limpiarCampos() {
        txtIdPedido.setText("");
        txtIdProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        modelo.setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmDetallePedido().setVisible(true);
        });
    }

}
