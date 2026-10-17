package view;

import controller.CategoriaController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Categoria;

public class FrmCategorias extends JFrame {

    private final CategoriaController categoriaController = new CategoriaController();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripción", "Tipo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tblCategorias = new JTable(modelo);
    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDescripcion = new JTextField(25);
    private final JComboBox<String> cboTipo = new JComboBox<>();
    private int idSeleccionado = 0;
    private String nombreOriginal = "";
    private String tipoOriginal = "";

    public FrmCategorias() {
        setTitle("Gestión de Categorías");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        for (String tipo : CategoriaController.TIPOS) {
            cboTipo.addItem(CategoriaController.describirTipo(tipo));
        }
        construirInterfaz();
        cargarCategorias();
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("GESTIÓN DE CATEGORÍAS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la categoría"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        agregarFila(formulario, gbc, 0, "Nombre:", txtNombre);
        agregarFila(formulario, gbc, 1, "Descripción:", txtDescripcion);
        agregarFila(formulario, gbc, 2, "Tipo:", cboTipo);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnDesactivar);
        botones.add(btnLimpiar);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formulario.add(botones, gbc);

        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tblCategorias);
        scroll.setBorder(BorderFactory.createTitledBorder("Categorías activas"));

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);
        principal.add(centro, BorderLayout.CENTER);
        add(principal);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnDesactivar.addActionListener(e -> desactivar());
        btnLimpiar.addActionListener(e -> limpiar());
        tblCategorias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private void cargarCategorias() {
        modelo.setRowCount(0);
        List<Categoria> categorias = categoriaController.listarCategorias();
        for (Categoria categoria : categorias) {
            modelo.addRow(new Object[]{
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion() == null ? "" : categoria.getDescripcion(),
                CategoriaController.describirTipo(categoria.getTipo())
            });
        }
    }

    private String tipoSeleccionado() {
        return CategoriaController.TIPOS[cboTipo.getSelectedIndex()];
    }

    private void seleccionarTipo(String descripcionTipo) {
        for (int i = 0; i < CategoriaController.TIPOS.length; i++) {
            if (CategoriaController.describirTipo(CategoriaController.TIPOS[i]).equals(descripcionTipo)) {
                cboTipo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionar() {
        int fila = tblCategorias.getSelectedRow();
        if (fila < 0) {
            return;
        }
        idSeleccionado = (int) modelo.getValueAt(fila, 0);
        nombreOriginal = modelo.getValueAt(fila, 1).toString();
        txtNombre.setText(nombreOriginal);
        txtDescripcion.setText(modelo.getValueAt(fila, 2).toString());
        seleccionarTipo(modelo.getValueAt(fila, 3).toString());
        tipoOriginal = tipoSeleccionado();
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.");
            return;
        }
        Categoria categoria = new Categoria(0, nombre, txtDescripcion.getText().trim());
        categoria.setTipo(tipoSeleccionado());
        if (categoriaController.guardarCategoria(categoria)) {
            JOptionPane.showMessageDialog(this, "Categoría guardada correctamente.");
            limpiar();
            cargarCategorias();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la categoría. Verifica que el nombre no exista ya.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría de la tabla para editar.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.");
            return;
        }
        int productos = categoriaController.contarProductos(idSeleccionado);
        if (!nombre.equals(nombreOriginal) && productos > 0) {
            JOptionPane.showMessageDialog(this,
                    "No se puede cambiar el nombre: " + productos + " producto(s) usan esta categoría.\n"
                    + "Sí puedes cambiar su descripción y su tipo.",
                    "Nombre en uso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String tipo = tipoSeleccionado();
        if (!tipo.equals(tipoOriginal) && productos > 0) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "Los " + productos + " producto(s) de esta categoría pasarán a ser de tipo "
                    + CategoriaController.describirTipo(tipo) + ". ¿Continuar?",
                    "Cambiar tipo", JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        Categoria categoria = new Categoria(idSeleccionado, nombre, txtDescripcion.getText().trim());
        categoria.setTipo(tipo);
        if (categoriaController.actualizarCategoria(categoria)) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada correctamente.");
            limpiar();
            cargarCategorias();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar la categoría. Verifica que el nombre no exista ya.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivar() {
        if (idSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría de la tabla para desactivar.");
            return;
        }
        int productos = categoriaController.contarProductosActivos(idSeleccionado);
        String mensaje = "¿Desactivar la categoría " + nombreOriginal + "?";
        if (productos > 0) {
            mensaje += "\nTiene " + productos + " producto(s) activo(s), que se seguirán vendiendo.";
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, mensaje,
                "Desactivar categoría", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        if (categoriaController.desactivarCategoria(idSeleccionado)) {
            JOptionPane.showMessageDialog(this, "Categoría desactivada correctamente.");
            limpiar();
            cargarCategorias();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo desactivar la categoría.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        cboTipo.setSelectedIndex(CategoriaController.TIPOS.length - 1);
        idSeleccionado = 0;
        nombreOriginal = "";
        tipoOriginal = "";
        tblCategorias.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmCategorias().setVisible(true));
    }
}
