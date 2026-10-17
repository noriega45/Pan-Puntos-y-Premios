
package view;

import controller.CategoriaController;
import controller.ProductoController;
import model.Categoria;
import model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmProductosCodigo extends JFrame {

    private final ProductoController controller = new ProductoController();
    private final CategoriaController categoriaController = new CategoriaController();

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cboCategoria;
    private JTextField txtPrecio;
    private JTextField txtExistencia;

    private JTable tblProductos;
    private DefaultTableModel modelo;

    private int idSeleccionado = 0;

    public FrmProductosCodigo() {
        setTitle("Gestión de Productos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construirInterfaz();
        cargarProductos();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("GESTIÓN DE PRODUCTOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        principal.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del producto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo = new JTextField(18);
        txtCodigo.setEditable(false);
        txtNombre = new JTextField(18);
        cboCategoria = new JComboBox<>();
        cargarCategorias();
        txtPrecio = new JTextField(18);
        txtExistencia = new JTextField(18);

        agregarCampo(formulario, gbc, 0, "Código:", txtCodigo);
        agregarCampo(formulario, gbc, 1, "Nombre:", txtNombre);
        agregarCampo(formulario, gbc, 2, "Categoría:", cboCategoria);
        agregarCampo(formulario, gbc, 3, "Precio (Q):", txtPrecio);
        agregarCampo(formulario, gbc, 4, "Existencia:", txtExistencia);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnDesactivar);
        botones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formulario.add(botones, gbc);

        contenido.add(formulario, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
            new Object[]{"ID", "Código", "Nombre", "Categoría",
                         "Precio (Q)", "Existencia"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblProductos = new JTable(modelo);
        tblProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProductos.setRowHeight(24);
        tblProductos.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tblProductos);
        scroll.setBorder(BorderFactory.createTitledBorder("Productos registrados"));

        contenido.add(scroll, BorderLayout.CENTER);
        principal.add(contenido, BorderLayout.CENTER);

        setContentPane(principal);

        btnGuardar.addActionListener(e -> guardarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnDesactivar.addActionListener(e -> desactivarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProducto();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                cargarCategorias();
            }
        });
    }

    private void cargarCategorias() {
        Object actual = cboCategoria.getSelectedItem();
        cboCategoria.removeAllItems();
        for (Categoria categoria : categoriaController.listarCategorias()) {
            cboCategoria.addItem(categoria.getNombre());
        }
        if (actual != null) {
            seleccionarCategoria(actual.toString());
        } else {
            cboCategoria.setSelectedIndex(-1);
        }
    }

    private void seleccionarCategoria(String nombre) {
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            if (cboCategoria.getItemAt(i).equalsIgnoreCase(nombre)) {
                cboCategoria.setSelectedIndex(i);
                return;
            }
        }
        cboCategoria.addItem(nombre);
        cboCategoria.setSelectedItem(nombre);
    }

    private String categoriaSeleccionada() {
        Object seleccion = cboCategoria.getSelectedItem();
        return seleccion == null ? "" : seleccion.toString();
    }

    private void agregarCampo(
            JPanel panel, GridBagConstraints gbc,
            int fila, String etiqueta, JComponent campo) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void cargarProductos() {
        modelo.setRowCount(0);

        List<Producto> productos = controller.listarProductos();

        for (Producto p : productos) {
            if (p.isActivo()) {
                modelo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecio(),
                    p.getExistencia()
                });
            }
        }
    }

    private void guardarProducto() {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String categoria = categoriaSeleccionada();
            String precioTexto = txtPrecio.getText().trim();
            String existenciaTexto = txtExistencia.getText().trim();

            if (cboCategoria.getItemCount() == 0) {
                JOptionPane.showMessageDialog(
                    this, "No hay categorías registradas. Registra una en el módulo Categorías.",
                    "Validación", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (nombre.isEmpty() || categoria.isEmpty()
                || precioTexto.isEmpty() || existenciaTexto.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Completa todos los campos y elige una categoría.",
                    "Validación", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double precio = Double.parseDouble(precioTexto);
            int existencia = Integer.parseInt(existenciaTexto);

            if (precio < 0 || existencia < 0) {
                JOptionPane.showMessageDialog(
                    this, "El precio y la existencia no pueden ser negativos."
                );
                return;
            }

            Producto producto = new Producto(
                0, codigo, categoria, nombre, precio, existencia
            );

            if (controller.guardarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(
                    this, "No se pudo guardar el producto. Verifica que la categoría esté activa.",
                    "Error", JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Precio y existencia deben ser valores numéricos."
            );
        }
    }

        private void seleccionarProducto() {
            int filaVista = tblProductos.getSelectedRow();

            if (filaVista < 0) {
                idSeleccionado = 0;
                return;
            }

            int fila = tblProductos.convertRowIndexToModel(filaVista);

            idSeleccionado = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString()
            );

            txtCodigo.setText(modelo.getValueAt(fila, 1).toString());
            txtNombre.setText(modelo.getValueAt(fila, 2).toString());
            seleccionarCategoria(modelo.getValueAt(fila, 3).toString());
            txtPrecio.setText(modelo.getValueAt(fila, 4).toString());
            txtExistencia.setText(modelo.getValueAt(fila, 5).toString());
            txtExistencia.setEditable(false);
            txtExistencia.setToolTipText("Para cambiar la existencia usa el módulo Abastecimientos.");
        }

    private void editarProducto() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String categoria = categoriaSeleccionada();
            String precioTexto = txtPrecio.getText().trim();
            String existenciaTexto = txtExistencia.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || categoria.isEmpty()
                    || precioTexto.isEmpty() || existenciaTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.");
                return;
            }

            double precio = Double.parseDouble(precioTexto);
            int existencia = Integer.parseInt(existenciaTexto);

            if (precio < 0 || existencia < 0) {
                JOptionPane.showMessageDialog(
                    this, "El precio y la existencia no pueden ser negativos."
                );
                return;
            }

            Producto producto = new Producto(
                idSeleccionado, codigo, categoria, nombre, precio, existencia
            );

            if (controller.actualizarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(
                    this, "No se pudo actualizar el producto. Verifica que la categoría esté activa.",
                    "Error", JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Precio y existencia deben ser valores numéricos."
            );
        }
    }

    private void desactivarProducto() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
            this, "¿Deseas desactivar este producto?",
            "Confirmar", JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        if (controller.desactivarProducto(idSeleccionado)) {
            JOptionPane.showMessageDialog(this, "Producto desactivado correctamente.");
            limpiarFormulario();
            cargarProductos();
        } else {
            JOptionPane.showMessageDialog(
                this, "No se pudo desactivar el producto.",
                "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        cboCategoria.setSelectedIndex(-1);
        txtExistencia.setEditable(true);
        txtExistencia.setToolTipText(null);
        txtPrecio.setText("");
        txtExistencia.setText("");

        idSeleccionado = 0;
        tblProductos.clearSelection();
        txtNombre.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmProductosCodigo().setVisible(true));
    }
}

