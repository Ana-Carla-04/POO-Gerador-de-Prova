package br.edu.ufersa.aplicativo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String url = "jdbc:mysql://localhost:3306/poo";
    private static final String usuario = "root";
    private static final String senha = "root";

    public static Connection abrirConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
