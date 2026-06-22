package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import br.edu.ufersa.aplicativo.model.DAO.ProfessorDAO;
import br.edu.ufersa.aplicativo.util.Conexao;

public class ServiceFactory {
    public static AutenticacaoService criarAutenticacaoService() {
        try {
            return new AutenticacaoService(new ProfessorDAO(Conexao.abrirConexao()));
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
