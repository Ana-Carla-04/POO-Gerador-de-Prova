package br.edu.ufersa.aplicativo.model.DAO;

//interface generica que define o contrato para todos os DAOS do programa

import java.sql.SQLException; //importa a excessao do sql
import java.util.List;

public interface DAO <T> { //declarando uma interface genérica DAO
    //como é uma interface então são apenas as assinaturas dos métodos a serem implementados
    void inserir(T objeto) throws SQLException; //define o método, inseri o objeto do tipo genererico no banco de dados, se não retorna o erro 
    void alterar(T objeto) throws SQLException; //define o método,altera o objeto do tipo genererico no banco de dados, se não retorna o erro 
    void deletar(T objeto) throws SQLException; //define o método, inseri o objeto do tipo genererico no banco de dados, se não retorna o erro 
    List<T> listar() throws SQLException; //define o metódo que lista e traz todas as informações da tabela
}
