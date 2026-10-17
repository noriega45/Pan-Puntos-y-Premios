package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

public final class Navegacion {

    private static final Set<JFrame> REEMPLAZADAS = new HashSet<>();

    private Navegacion() {
    }

    public static void abrir(JFrame origen, JFrame destino) {
        conectar(origen, destino);
        destino.setLocationRelativeTo(origen);
        destino.setVisible(true);
        origen.setVisible(false);
    }

    public static void reemplazar(JFrame actual, JFrame nueva) {
        JFrame origen = (JFrame) actual.getRootPane().getClientProperty("origen");
        if (origen != null) {
            REEMPLAZADAS.add(actual);
            conectar(origen, nueva);
        }
        nueva.setLocationRelativeTo(actual);
        nueva.setVisible(true);
        actual.dispose();
    }

    private static void conectar(JFrame origen, JFrame destino) {
        destino.getRootPane().putClientProperty("origen", origen);
        destino.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton volver = new JButton(origen instanceof FrmInicio ? "← Volver al inicio" : "← Volver");
        volver.setFocusable(false);
        volver.addActionListener(e -> destino.dispose());
        JMenuBar barra = new JMenuBar();
        barra.add(volver);
        destino.setJMenuBar(barra);

        destino.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (!REEMPLAZADAS.remove(destino)) {
                    origen.setVisible(true);
                    origen.toFront();
                }
            }
        });
    }
}
