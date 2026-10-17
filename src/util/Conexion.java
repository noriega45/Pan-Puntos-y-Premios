package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USUARIO = "PANPUNTOS";
    private static final String PASSWORD = "Pan12345";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}