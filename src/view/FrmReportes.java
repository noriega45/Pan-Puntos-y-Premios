package view;

import controller.ReporteController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import model.ReporteResultado;

public class FrmReportes extends JFrame {
    private final ReporteController controller = new ReporteController();
    private JComboBox<String> cboReporte;
    private JTextField txtDesde;
    private JTextField txtHasta;
    private JTable tabla;

    public FrmReportes() {
        construirInterfaz();
        actualizarFechas();
    }

    private void construirInterfaz() {
        setTitle("Pan, Puntos y Premios - Reportes");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titulo = new JLabel("REPORTES DEL SISTEMA");
        titulo.setFont(titulo.getFont().deriveFont(22f));
        principal.add(titulo, BorderLayout.NORTH);

        cboReporte = new JComboBox<>(controller.listarReportes());
        txtDesde = new JTextField(10);
        txtHasta = new JTextField(10);
        JButton btnConsultar = new JButton("Consultar");
        JButton btnLimpiar = new JButton("Limpiar tabla");

        JPanel filtros = new JPanel(new GridLayout(2, 3, 8, 8));
        filtros.setBorder(BorderFactory.createTitledBorder("Criterios"));
        filtros.add(new JLabel("Reporte:"));
        filtros.add(new JLabel("Desde (AAAA-MM-DD):"));
        filtros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        filtros.add(cboReporte);
        filtros.add(txtDesde);
        filtros.add(txtHasta);

        tabla = new JTable(new DefaultTableModel());
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnLimpiar);
        acciones.add(btnConsultar);
        principal.add(centro, BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);
        add(principal);

        cboReporte.addActionListener(e -> actualizarFechas());
        btnConsultar.addActionListener(e -> consultar());
        btnLimpiar.addActionListener(e -> tabla.setModel(new DefaultTableModel()));
    }

    private void consultar() {
        try {
            int tipo = cboReporte.getSelectedIndex();
            Date desde = null;
            Date hasta = null;
            if (tipo == 0 || tipo == 1 || tipo == 5 || tipo == 6) {
                desde = Date.valueOf(LocalDate.parse(txtDesde.getText().trim()));
                hasta = Date.valueOf(LocalDate.parse(txtHasta.getText().trim()));
            }
            ReporteResultado resultado = controller.consultar(tipo, desde, hasta);
            DefaultTableModel modelo = new DefaultTableModel(resultado.getColumnas(), 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
            for (Object[] fila : resultado.getFilas()) {
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);
            if (modelo.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "El reporte no encontró resultados.");
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Escribe las fechas con formato AAAA-MM-DD, por ejemplo 2026-10-13.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo generar el reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarFechas() {
        LocalDate hoy = LocalDate.now();
        txtDesde.setText(hoy.withDayOfMonth(1).toString());
        txtHasta.setText(hoy.toString());
        boolean requierePeriodo = cboReporte.getSelectedIndex() == 0
                || cboReporte.getSelectedIndex() == 1
                || cboReporte.getSelectedIndex() == 5
                || cboReporte.getSelectedIndex() == 6;
        txtDesde.setEnabled(requierePeriodo);
        txtHasta.setEnabled(requierePeriodo);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new FrmReportes().setVisible(true));
    }
}
