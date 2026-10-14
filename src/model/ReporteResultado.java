package model;

import java.util.ArrayList;
import java.util.List;

public class ReporteResultado {
    private final String[] columnas;
    private final List<Object[]> filas;

    public ReporteResultado(String[] columnas, List<Object[]> filas) {
        this.columnas = columnas;
        this.filas = new ArrayList<>(filas);
    }

    public String[] getColumnas() {
        return columnas.clone();
    }

    public List<Object[]> getFilas() {
        return new ArrayList<>(filas);
    }
}
