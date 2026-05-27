package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Professor;

import java.sql.SQLException;

import java.util.List;

public interface DAO <T> {
    void inserir(T objeto) throws SQLException;
    void alterar(T objeto) throws SQLException;
    void deletar(T objeto) throws SQLException;
    List<T> listar() throws SQLException;
}
