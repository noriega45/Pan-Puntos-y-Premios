package controller;

import dao.ReporteDAO;
import java.sql.Date;
import model.ReporteResultado;

public class ReporteController {
    private final ReporteDAO reporteDAO = new ReporteDAO();

    public String[] listarReportes() {
        return reporteDAO.getTitulos();
    }

    public ReporteResultado consultar(int tipo, Date desde, Date hasta) throws Exception {
        return reporteDAO.consultar(tipo, desde, hasta);
    }
}
