package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FrmInicio extends JFrame {
    public FrmInicio() {
        setTitle("Pan, Puntos y Premios");
        setSize(680, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JLabel titulo = new JLabel("PAN, PUNTOS Y PREMIOS", JLabel.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(24f));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(6, 2, 12, 12));
        agregar(botones, "Nueva venta", FrmVenta::new);
        agregar(botones, "Cobro de pedidos", FrmPago::new);
        agregar(botones, "Clientes", FrmClientes::new);
        agregar(botones, "Productos", FrmProductosCodigo::new);
        agregar(botones, "Abastecimientos", FrmAbastecimientos::new);
        agregar(botones, "Menús", FrmMenus::new);
        agregar(botones, "Canje de premios", FrmCanjes::new);
        agregar(botones, "Reportes", FrmReportes::new);
        agregar(botones, "Categorías", FrmCategorias::new);
        agregar(botones, "Proveedores", FrmProveedores::new);
        agregar(botones, "Historial de pedidos", FrmPedidos::new);
        principal.add(botones, BorderLayout.CENTER);
        add(principal);
    }

    private void agregar(JPanel panel, String texto, java.util.function.Supplier<JFrame> abrir) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> Navegacion.abrir(this, abrir.get()));
        panel.add(boton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmInicio().setVisible(true));
    }
}
