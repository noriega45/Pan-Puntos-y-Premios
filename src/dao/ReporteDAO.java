package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import model.ReporteResultado;
import util.Conexion;

public class ReporteDAO {
    private static final String[] TITULOS = {
        "Ventas por período y método",
        "Productos más vendidos (incluye componentes de menús)",
        "Productos con existencia baja o agotada",
        "Puntos acumulados por cliente",
        "Historial de canjes",
        "Ventas de menús por período",
        "Movimientos de inventario"
    };

    public String[] getTitulos() {
        return TITULOS.clone();
    }

    public ReporteResultado consultar(int tipo, Date desde, Date hasta) throws Exception {
        if (tipo < 0 || tipo >= TITULOS.length) {
            throw new IllegalArgumentException("Selecciona un reporte válido.");
        }
        if ((tipo == 0 || tipo == 1 || tipo == 5 || tipo == 6)
                && (desde == null || hasta == null || desde.after(hasta))) {
            throw new IllegalArgumentException("Indica un período válido.");
        }

        String sql;
        switch (tipo) {
            case 0:
                sql = "SELECT METODO_PAGO AS METODO, COUNT(*) AS TRANSACCIONES, "
                        + "SUM(MONTO) AS TOTAL_Q FROM PAGOS "
                        + "WHERE UPPER(ESTADO) = 'APROBADO' AND TRUNC(FECHA_PAGO) BETWEEN ? AND ? "
                        + "GROUP BY METODO_PAGO ORDER BY METODO_PAGO";
                break;
            case 1:
                sql = "SELECT ID_PRODUCTO, NOMBRE, SUM(CANTIDAD) AS UNIDADES_VENDIDAS "
                        + "FROM (SELECT P.ID_PRODUCTO, P.NOMBRE, DP.CANTIDAD "
                        + "FROM DETALLE_PEDIDO DP JOIN PEDIDOS PE ON PE.ID_PEDIDO = DP.ID_PEDIDO "
                        + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = DP.ID_PRODUCTO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ? "
                        + "UNION ALL SELECT P.ID_PRODUCTO, P.NOMBRE, DPM.CANTIDAD * DM.CANTIDAD "
                        + "FROM DETALLE_PEDIDO_MENU DPM JOIN PEDIDOS PE ON PE.ID_PEDIDO = DPM.ID_PEDIDO "
                        + "JOIN DETALLE_MENU DM ON DM.ID_MENU = DPM.ID_MENU "
                        + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ?) "
                        + "GROUP BY ID_PRODUCTO, NOMBRE ORDER BY UNIDADES_VENDIDAS DESC, NOMBRE";
                break;
            case 2:
                sql = "SELECT ID_PRODUCTO, CODIGO, NOMBRE, CATEGORIA, EXISTENCIA "
                        + "FROM PRODUCTOS WHERE ACTIVO = 1 AND EXISTENCIA <= 5 "
                        + "ORDER BY EXISTENCIA, NOMBRE";
                break;
            case 3:
                sql = "SELECT ID_CLIENTE, NOMBRE, PUNTOS FROM CLIENTES "
                        + "WHERE ACTIVO = 1 ORDER BY PUNTOS DESC, NOMBRE";
                break;
            case 4:
                sql = "SELECT C.ID_CANJE, C.FECHA, C.ID_CLIENTE, CL.NOMBRE AS CLIENTE, "
                        + "PR.NOMBRE AS PREMIO, C.PUNTOS_UTILIZADOS FROM CANJES C "
                        + "JOIN CLIENTES CL ON CL.ID_CLIENTE = C.ID_CLIENTE "
                        + "JOIN PREMIOS PR ON PR.ID_PREMIO = C.ID_PREMIO "
                        + "ORDER BY C.FECHA DESC, C.ID_CANJE DESC";
                break;
            case 5:
                sql = "SELECT M.ID_MENU, M.NOMBRE, COUNT(DPM.ID_DETALLE_PEDIDO_MENU) AS PEDIDOS, "
                        + "SUM(DPM.CANTIDAD) AS UNIDADES, SUM(DPM.CANTIDAD * DPM.PRECIO_UNITARIO) AS TOTAL_Q "
                        + "FROM DETALLE_PEDIDO_MENU DPM JOIN MENUS M ON M.ID_MENU = DPM.ID_MENU "
                        + "JOIN PEDIDOS PE ON PE.ID_PEDIDO = DPM.ID_PEDIDO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ? "
                        + "GROUP BY M.ID_MENU, M.NOMBRE ORDER BY UNIDADES DESC, M.NOMBRE";
                break;
            default:
                sql = "SELECT MI.FECHA, P.ID_PRODUCTO, P.NOMBRE AS PRODUCTO, MI.TIPO, "
                        + "MI.CANTIDAD, MI.REFERENCIA FROM MOVIMIENTOS_INVENTARIO MI "
                        + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = MI.ID_PRODUCTO "
                        + "WHERE TRUNC(MI.FECHA) BETWEEN ? AND ? "
                        + "ORDER BY MI.FECHA DESC, MI.ID_MOVIMIENTO DESC";
                break;
        }

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (tipo == 0 || tipo == 5 || tipo == 6) {
                ps.setDate(1, desde);
                ps.setDate(2, hasta);
            } else if (tipo == 1) {
                ps.setDate(1, desde);
                ps.setDate(2, hasta);
                ps.setDate(3, desde);
                ps.setDate(4, hasta);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                String[] columnas = new String[md.getColumnCount()];
                for (int i = 0; i < columnas.length; i++) {
                    columnas[i] = md.getColumnLabel(i + 1);
                }
                List<Object[]> filas = new ArrayList<>();
                while (rs.next()) {
                    Object[] fila = new Object[columnas.length];
                    for (int i = 0; i < fila.length; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    filas.add(fila);
                }
                return new ReporteResultado(columnas, filas);
            }
        }
    }
}
