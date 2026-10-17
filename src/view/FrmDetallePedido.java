
package view;

import controller.DetallePedidoController;
import model.DetallePedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controller.DetallePedidoMenuController;
import controller.MenuController;
import model.DetallePedidoMenu;
import model.Menu;

public class FrmDetallePedido extends JFrame {

    private final DetallePedidoController controller =
            new DetallePedidoController();
    private final DetallePedidoMenuController controllerMenu =
        new DetallePedidoMenuController();
    private final MenuController menuController = new MenuController();

    private JTextField txtIdPedido;
    private JTextField txtIdProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JComboBox<Menu> cboMenus;
    private JTextField txtCantidadMenu;
    private JTable tblDetalles;
    private DefaultTableModel modelo;

    public FrmDetallePedido() {
        setTitle("Pan, Puntos y Premios - Detalle de Pedido");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
        cargarMenus();
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

        JPanel formularioMenu = new JPanel(new GridLayout(2, 2, 10, 8));
        formularioMenu.setBorder(BorderFactory.createTitledBorder("Agregar menú"));

        formularioMenu.add(new JLabel("Menú disponible:"));
        formularioMenu.add(new JLabel("Cantidad:"));

        cboMenus = new JComboBox<>();
        txtCantidadMenu = new JTextField();

        formularioMenu.add(cboMenus);
        formularioMenu.add(txtCantidadMenu);

        JPanel formularios = new JPanel(new GridLayout(2, 1, 10, 10));
        formularios.add(formulario);
        formularios.add(formularioMenu);

        modelo = new DefaultTableModel(
                new Object[]{"ID Detalle", "ID Pedido", "Producto / Menú",
                    "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblDetalles = new JTable(modelo);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centro.add(formularios, BorderLayout.NORTH);
        centro.add(new JScrollPane(tblDetalles), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        JButton btnCargar = new JButton("Consultar pedido");
        JButton btnGuardar = new JButton("Agregar producto");
        JButton btnAgregarMenu = new JButton("Agregar menú");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCargar);
        botones.add(btnGuardar);
        botones.add(btnLimpiar);
        botones.add(btnAgregarMenu);

        botones.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(botones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> cargarDetalles());
        btnGuardar.addActionListener(e -> guardarDetalle());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnAgregarMenu.addActionListener(e -> guardarMenu());
    }

    private void cargarMenus() {
        cboMenus.removeAllItems();
        for (Menu menu : menuController.listarActivos()) {
            cboMenus.addItem(menu);
        }
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
            List<DetallePedidoMenu> menus =
        controllerMenu.listarPorPedido(idPedido);

            for (DetallePedidoMenu menu : menus) {
                double subtotal = menu.getCantidad() * menu.getPrecioUnitario();

                modelo.addRow(new Object[]{
                    menu.getIdDetallePedidoMenu(),
                    menu.getIdPedido(),
                    "MENÚ " + menu.getIdMenu(),
                    menu.getCantidad(),
                    menu.getPrecioUnitario(),
                    subtotal
                });
            }

            JOptionPane.showMessageDialog(this,
                "Detalles consultados: " + (detalles.size() + menus.size()));

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
    private void guardarMenu() {
    try {
        int idPedido = Integer.parseInt(txtIdPedido.getText().trim());
        int cantidad = Integer.parseInt(txtCantidadMenu.getText().trim());
        Menu menu = (Menu) cboMenus.getSelectedItem();
        if (menu == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay menús activos disponibles.");
            return;
        }

        DetallePedidoMenu detalle = new DetallePedidoMenu(
                0, idPedido, menu.getIdMenu(), cantidad, 0
        );

        if (controllerMenu.guardar(detalle)) {
            JOptionPane.showMessageDialog(this,
                    "Menú agregado y total actualizado.");
            txtCantidadMenu.setText("");
            cargarDetalles();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo agregar. Verifica el pedido, el menú "
                    + "y las existencias de sus componentes.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "Ingresa números válidos para pedido, menú y cantidad.");
    }
}

    private void limpiarCampos() {
        txtIdPedido.setText("");
        txtIdProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        modelo.setRowCount(0);
        if (cboMenus.getItemCount() > 0) {
            cboMenus.setSelectedIndex(0);
        }
        txtCantidadMenu.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmDetallePedido().setVisible(true);
        });
    }

}
