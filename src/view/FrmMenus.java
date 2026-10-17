package view;

import controller.MenuController;
import controller.ProductoController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
        setSize(880, 600);
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

        JPanel datos = new JPanel(new GridBagLayout());
        datos.setBorder(BorderFactory.createTitledBorder("Datos del menú"));
        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(8);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.gridx = 0;
        datos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        datos.add(txtNombre, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        datos.add(new JLabel("Precio de venta (Q):"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.3;
        datos.add(txtPrecio, gbc);

        JPanel componentesPanel = new JPanel(new GridBagLayout());
        componentesPanel.setBorder(BorderFactory.createTitledBorder(
                "Composición: un producto de tipo comida + uno de tipo bebida + uno de tipo acompañamiento"));
        cboProducto = new JComboBox<>();
        cboProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Producto) {
                    Producto producto = (Producto) valor;
                    setText(producto.getNombre() + "  [" + producto.getCategoria() + " - "
                            + nombreTipo(producto.getTipoCategoria()) + "]");
                }
                return this;
            }
        });
        txtCantidad = new JTextField("1", 4);
        JButton btnAgregar = new JButton("+ Agregar componente");
        btnAgregar.setFont(btnAgregar.getFont().deriveFont(Font.BOLD, 13f));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        componentesPanel.add(new JLabel("Producto:"), c);
        c.gridx = 1;
        c.weightx = 1;
        componentesPanel.add(cboProducto, c);
        c.gridx = 2;
        c.weightx = 0;
        componentesPanel.add(new JLabel("Cantidad por menú:"), c);
        c.gridx = 3;
        componentesPanel.add(txtCantidad, c);
        c.gridx = 4;
        componentesPanel.add(btnAgregar, c);

        modelo = new DefaultTableModel(new Object[]{"ID", "Producto", "Categoría", "Tipo", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Componentes del menú"));

        JPanel formularios = new JPanel(new BorderLayout(8, 8));
        formularios.add(datos, BorderLayout.NORTH);
        formularios.add(componentesPanel, BorderLayout.SOUTH);
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formularios, BorderLayout.NORTH);
        centro.add(scrollTabla, BorderLayout.CENTER);
        principal.add(centro, BorderLayout.CENTER);

        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnGuardar = new JButton("Guardar menú");
        btnGuardar.setFont(btnGuardar.getFont().deriveFont(Font.BOLD));
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
        cboProducto.removeAllItems();
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
                producto.getCategoria(), nombreTipo(producto.getTipoCategoria()), cantidad});
            txtCantidad.setText("1");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un entero positivo.");
        }
    }

    private void quitarComponente(int fila) {
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un componente de la tabla.");
            return;
        }
        int idProducto = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        componentes.remove(idProducto);
        modelo.removeRow(fila);
    }

    private static String nombreTipo(String tipo) {
        if ("COMIDA".equals(tipo)) {
            return "comida";
        }
        if ("BEBIDA".equals(tipo)) {
            return "bebida";
        }
        if ("ACOMPANAMIENTO".equals(tipo)) {
            return "acompañamientos";
        }
        return "extras";
    }

    private List<String> tiposFaltantes() {
        boolean comida = false;
        boolean bebida = false;
        boolean acompanamiento = false;
        for (Producto producto : componentes.values()) {
            String tipo = producto.getTipoCategoria();
            if ("COMIDA".equals(tipo)) {
                comida = true;
            } else if ("BEBIDA".equals(tipo)) {
                bebida = true;
            } else if ("ACOMPANAMIENTO".equals(tipo)) {
                acompanamiento = true;
            }
        }
        List<String> faltantes = new ArrayList<>();
        if (!comida) {
            faltantes.add("un producto de tipo comida");
        }
        if (!bebida) {
            faltantes.add("un producto de tipo bebida");
        }
        if (!acompanamiento) {
            faltantes.add("un producto de tipo acompañamiento");
        }
        return faltantes;
    }

    private void guardarMenu() {
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser numérico y mayor que cero.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty() || precio <= 0 || componentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa nombre, precio y componentes del menú.");
            return;
        }
        List<String> faltantes = tiposFaltantes();
        if (!faltantes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Al menú le falta:\n- " + String.join("\n- ", faltantes),
                    "Menú incompleto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Map<Integer, Integer> receta = new LinkedHashMap<>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            receta.put(Integer.parseInt(modelo.getValueAt(i, 0).toString()),
                    Integer.parseInt(modelo.getValueAt(i, 4).toString()));
        }
        if (!menuController.guardar(nombre, precio, receta)) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el menú. Verifica que los productos sigan activos.",
                    "Menú no guardado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Menú guardado correctamente.");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("1");
        componentes.clear();
        modelo.setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMenus().setVisible(true));
    }
}
