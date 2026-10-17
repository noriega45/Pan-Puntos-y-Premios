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
                sql = "SELECT NVL(METODO_PAGO, 'TOTAL VENDIDO') AS \"Método de pago\", "
                        + "COUNT(*) AS \"Transacciones\", SUM(MONTO) AS \"Total (Q)\" FROM PAGOS "
                        + "WHERE UPPER(ESTADO) = 'APROBADO' AND TRUNC(FECHA_PAGO) BETWEEN ? AND ? "
                        + "GROUP BY ROLLUP(METODO_PAGO) "
                        + "ORDER BY GROUPING(METODO_PAGO), METODO_PAGO";
                break;
            case 1:
                sql = "SELECT ID_PRODUCTO AS \"ID\", NOMBRE AS \"Producto\", "
                        + "SUM(INDIVIDUAL) AS \"Vendidos individualmente\", "
                        + "SUM(EN_MENU) AS \"Vendidos en menús\", "
                        + "SUM(INDIVIDUAL + EN_MENU) AS \"Total unidades\" "
                        + "FROM (SELECT P.ID_PRODUCTO, P.NOMBRE, DP.CANTIDAD AS INDIVIDUAL, 0 AS EN_MENU "
                        + "FROM DETALLE_PEDIDO DP JOIN PEDIDOS PE ON PE.ID_PEDIDO = DP.ID_PEDIDO "
                        + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = DP.ID_PRODUCTO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ? "
                        + "UNION ALL SELECT P.ID_PRODUCTO, P.NOMBRE, 0, DPM.CANTIDAD * DM.CANTIDAD "
                        + "FROM DETALLE_PEDIDO_MENU DPM JOIN PEDIDOS PE ON PE.ID_PEDIDO = DPM.ID_PEDIDO "
                        + "JOIN DETALLE_MENU DM ON DM.ID_MENU = DPM.ID_MENU "
                        + "JOIN PRODUCTOS P ON P.ID_PRODUCTO = DM.ID_PRODUCTO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ?) "
                        + "GROUP BY ID_PRODUCTO, NOMBRE "
                        + "ORDER BY SUM(INDIVIDUAL + EN_MENU) DESC, NOMBRE";
                break;
            case 2:
                sql = "SELECT ID_PRODUCTO AS \"ID\", CODIGO AS \"Código\", NOMBRE AS \"Producto\", "
                        + "CATEGORIA AS \"Categoría\", EXISTENCIA AS \"Existencia\", "
                        + "CASE WHEN EXISTENCIA = 0 THEN 'AGOTADO' ELSE 'BAJO' END AS \"Estado\" "
                        + "FROM PRODUCTOS WHERE ACTIVO = 1 AND EXISTENCIA <= 5 "
                        + "ORDER BY EXISTENCIA, NOMBRE";
                break;
            case 3:
                sql = "SELECT ID_CLIENTE AS \"ID\", NOMBRE AS \"Cliente\", PUNTOS AS \"Puntos acumulados\" "
                        + "FROM CLIENTES WHERE ACTIVO = 1 ORDER BY PUNTOS DESC, NOMBRE";
                break;
            case 4:
                sql = "SELECT C.ID_CANJE AS \"Canje\", C.FECHA AS \"Fecha\", C.ID_CLIENTE AS \"ID cliente\", "
                        + "CL.NOMBRE AS \"Cliente\", PR.NOMBRE AS \"Premio\", "
                        + "C.PUNTOS_UTILIZADOS AS \"Puntos utilizados\" FROM CANJES C "
                        + "JOIN CLIENTES CL ON CL.ID_CLIENTE = C.ID_CLIENTE "
                        + "JOIN PREMIOS PR ON PR.ID_PREMIO = C.ID_PREMIO "
                        + "ORDER BY C.FECHA DESC, C.ID_CANJE DESC";
                break;
            case 5:
                sql = "SELECT M.ID_MENU AS \"ID\", M.NOMBRE AS \"Menú\", "
                        + "COUNT(DPM.ID_DETALLE_PEDIDO_MENU) AS \"Pedidos\", "
                        + "SUM(DPM.CANTIDAD) AS \"Unidades vendidas\", "
                        + "SUM(DPM.CANTIDAD * DPM.PRECIO_UNITARIO) AS \"Total (Q)\" "
                        + "FROM DETALLE_PEDIDO_MENU DPM JOIN MENUS M ON M.ID_MENU = DPM.ID_MENU "
                        + "JOIN PEDIDOS PE ON PE.ID_PEDIDO = DPM.ID_PEDIDO "
                        + "WHERE PE.ESTADO IN ('PAGADO','ENTREGADO') AND TRUNC(PE.FECHA) BETWEEN ? AND ? "
                        + "GROUP BY M.ID_MENU, M.NOMBRE ORDER BY SUM(DPM.CANTIDAD) DESC, M.NOMBRE";
                break;
            default:
                sql = "SELECT MI.FECHA AS \"Fecha\", P.ID_PRODUCTO AS \"ID producto\", "
                        + "P.NOMBRE AS \"Producto\", MI.TIPO AS \"Movimiento\", "
                        + "MI.CANTIDAD AS \"Cantidad\", MI.REFERENCIA AS \"Referencia\" "
                        + "FROM MOVIMIENTOS_INVENTARIO MI "
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
