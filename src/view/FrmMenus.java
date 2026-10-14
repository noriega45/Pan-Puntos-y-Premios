package view;

import controller.MenuController;
import controller.ProductoController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Producto;

public class FrmMenus extends JFrame {
    private final MenuController menuController = new MenuController();
    private final ProductoController productoController = new ProductoController();
    private final Map<Integer, Producto> componentes = new LinkedHashMap<>();
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCantidad;
    private JComboBox<Producto> cboProducto;
    private DefaultTableModel modelo;

    public FrmMenus() {
        setTitle("Pan, Puntos y Premios - Menús");
        setSize(760, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        construirInterfaz();
        cargarProductos();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("CREAR MENÚ COMPLETO");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel datos = new JPanel(new GridLayout(2, 2, 8, 8));
        datos.setBorder(BorderFactory.createTitledBorder("Datos del menú"));
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        datos.add(new JLabel("Nombre:"));
        datos.add(new JLabel("Precio de venta (Q):"));
        datos.add(txtNombre);
        datos.add(txtPrecio);

        JPanel componentesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        componentesPanel.setBorder(BorderFactory.createTitledBorder("Composición"));
        cboProducto = new JComboBox<>();
        txtCantidad = new JTextField("1", 5);
        JButton btnAgregar = new JButton("Agregar componente");
        componentesPanel.add(new JLabel("Producto:"));
        componentesPanel.add(cboProducto);
        componentesPanel.add(new JLabel("Cantidad por menú:"));
        componentesPanel.add(txtCantidad);
        componentesPanel.add(btnAgregar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Producto", "Categoría", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        JPanel formularios = new JPanel(new GridLayout(2, 1, 8, 8));
        formularios.add(datos);
        formularios.add(componentesPanel);
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formularios, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(centro, BorderLayout.CENTER);

        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnGuardar = new JButton("Guardar menú");
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnQuitar);
        acciones.add(btnGuardar);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        btnAgregar.addActionListener(e -> agregarComponente());
        btnQuitar.addActionListener(e -> quitarComponente(tabla.getSelectedRow()));
        btnGuardar.addActionListener(e -> guardarMenu());
    }

    private void cargarProductos() {
        for (Producto producto : productoController.listarProductos()) {
            cboProducto.addItem(producto);
        }
    }

    private void agregarComponente() {
        Producto producto = (Producto) cboProducto.getSelectedItem();
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "No hay productos activos disponibles.");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
            if (componentes.containsKey(producto.getIdProducto())) {
                JOptionPane.showMessageDialog(this, "Ese producto ya está en la composición.");
                return;
            }
            componentes.put(producto.getIdProducto(), producto);
            modelo.addRow(new Object[]{producto.getIdProducto(), producto.getNombre(),
                producto.getCategoria(), cantidad});
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un entero positivo.");
        }
    }

    private void quitarComponente(int fila) {
        if (fila < 0) {
            return;
        }
        int idProducto = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        componentes.remove(idProducto);
        modelo.removeRow(fila);
    }

    private void guardarMenu() {
        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            Map<Integer, Integer> receta = new LinkedHashMap<>();
            for (int i = 0; i < modelo.getRowCount(); i++) {
                receta.put(Integer.parseInt(modelo.getValueAt(i, 0).toString()),
                        Integer.parseInt(modelo.getValueAt(i, 3).toString()));
            }
            if (nombre.isEmpty() || precio <= 0 || receta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa nombre, precio y componentes del menú.");
                return;
            }
            if (!menuController.guardar(nombre, precio, receta)) {
                JOptionPane.showMessageDialog(this,
                        "El menú debe incluir al menos un sándwich, una bebida y un acompañamiento activos.",
                        "Menú no guardado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Menú guardado correctamente.");
            txtNombre.setText("");
            txtPrecio.setText("");
            txtCantidad.setText("1");
            componentes.clear();
            modelo.setRowCount(0);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser numérico y mayor que cero.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMenus().setVisible(true));
    }
}
