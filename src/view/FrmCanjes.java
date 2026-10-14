package view;

import controller.CanjeController;
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
import model.Canje;
import model.Premio;

public class FrmCanjes extends JFrame {
    private final CanjeController controller = new CanjeController();
    private JTextField txtIdCliente;
    private JComboBox<Premio> cboPremios;
    private DefaultTableModel modelo;

    public FrmCanjes() {
        construirInterfaz();
        cargarPremios();
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Canje de premios");
        setSize(760, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("CANJE DE PREMIOS");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del canje"));
        txtIdCliente = new JTextField();
        cboPremios = new JComboBox<>();
        formulario.add(new JLabel("ID del cliente:"));
        formulario.add(txtIdCliente);
        formulario.add(new JLabel("Premio disponible:"));
        formulario.add(cboPremios);

        modelo = new DefaultTableModel(new Object[]{
            "ID canje", "ID cliente", "ID premio", "Fecha", "Puntos usados"
        }, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnCanjear = new JButton("Canjear premio");
        JButton btnHistorial = new JButton("Consultar historial");
        JButton btnActualizar = new JButton("Actualizar premios");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnHistorial);
        botones.add(btnActualizar);
        botones.add(btnCanjear);

        principal.add(centro, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);
        add(principal);

        btnCanjear.addActionListener(e -> canjear());
        btnHistorial.addActionListener(e -> cargarHistorial());
        btnActualizar.addActionListener(e -> cargarPremios());
    }

    private void cargarPremios() {
        cboPremios.removeAllItems();
        List<Premio> premios = controller.listarPremiosDisponibles();
        for (Premio premio : premios) {
            cboPremios.addItem(premio);
        }
        if (premios.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay premios activos con existencias disponibles.");
        }
    }

    private void canjear() {
        int idCliente;
        try {
            idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            if (idCliente <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID de cliente válido.");
            return;
        }

        Premio premio = (Premio) cboPremios.getSelectedItem();
        if (premio == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un premio disponible.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Canjear " + premio.getNombre() + " por "
                + premio.getPuntosRequeridos() + " puntos?",
                "Confirmar canje", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        if (controller.canjear(idCliente, premio)) {
            JOptionPane.showMessageDialog(this,
                    "Canje realizado. Se descontaron "
                    + premio.getPuntosRequeridos() + " puntos.");
            cargarPremios();
            cargarHistorial();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo realizar el canje. Verifica que el cliente esté activo, "
                    + "tenga puntos suficientes y el premio aún tenga existencias.",
                    "Canje no realizado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorial() {
        try {
            int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
            modelo.setRowCount(0);
            for (Canje canje : controller.listarPorCliente(idCliente)) {
                modelo.addRow(new Object[]{canje.getIdCanje(), canje.getIdCliente(),
                    canje.getIdPremio(), canje.getFecha(), canje.getPuntosUtilizados()});
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa un ID de cliente válido.");
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new FrmCanjes().setVisible(true));
    }
}
