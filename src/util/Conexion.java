package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    public static void main(String[] args) {
    try {
        conectar();
        System.out.println("Conexión exitosa");
    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
}

    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USUARIO = "PANPUNTOS";
    private static final String PASSWORD = "Pan12345";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}