package br.edu.ufersa.aplicativo.util;

import java.sql.Connection; //Interface que representa a conexão com o banco de dados
import java.sql.DriverManager; //Classe que gerencia os drivers JDBC e cria conexões
import java.sql.SQLException; //Exceção específica para erros de banco de dados

public class Conexao {
    private static final String url = "jdbc:mysql://localhost:3306/poo"; //É uma constante, URL de conexão com o banco
    private static final String usuario = "root"; //usuário
    private static final String senha = "root"; //senha

    public static Connection abrirConexao() throws SQLException { //retornar uma conexao tipo Connection, e  pode lançar erros de SQL caso necessário
        //o DriverManager verifica se o JDBC do MySQL foi carregado,
        return DriverManager.getConnection(url, usuario, senha); //estabelece conexão com o banco de dados usando url, usuario e senha
    }
}
