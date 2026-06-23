package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import br.edu.ufersa.aplicativo.model.DAO.ProfessorDAO;
import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;
import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO;
import br.edu.ufersa.aplicativo.model.DAO.ProvaDAO;
import br.edu.ufersa.aplicativo.util.Conexao;

public class ServiceFactory {
    public static AutenticacaoService criarAutenticacaoService() {
        try {
            return new AutenticacaoService(new ProfessorDAO(Conexao.abrirConexao()));
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static DisciplinaService criarDisciplinaService() {
        try {
            return new DisciplinaService(new DisciplinaDAO(Conexao.abrirConexao()), criarQuestaoService());
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static QuestaoService criarQuestaoService() {
        try {
            return new QuestaoService(new QuestaoDAO(Conexao.abrirConexao()));
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static ProvaService criarProvaService() {
        try {
            return new ProvaService(new ProvaDAO(Conexao.abrirConexao()));
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
