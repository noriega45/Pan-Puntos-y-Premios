package util;

import dao.ClienteDAO;
import model.Cliente;

public class PruebaCliente {

    public static void main(String[] args) {

        Cliente cliente = new Cliente(
            0,
            "Eduardo Noriega",
            "1234567-8",
            "55555555",
            "eduardo@gmail.com"
        );

        ClienteDAO dao = new ClienteDAO();

        if (dao.guardar(cliente)) {
            System.out.println("Cliente guardado correctamente");
        } else {
            System.out.println("No se pudo guardar el cliente");
        }
    }
}