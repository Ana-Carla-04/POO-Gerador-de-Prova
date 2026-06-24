package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import java.util.List;

import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO;

public class QuestaoService {
    private final QuestaoDAO questaoDAO;

    public QuestaoService(QuestaoDAO questaoDAO) {
        if (questaoDAO != null) this.questaoDAO = questaoDAO;
        else throw new IllegalArgumentException("QuestaoDAO invalido");
    }

    public List<Questao> listarQuestoes() {
        try {
            return questaoDAO.listar();
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void inserir(Questao questao) {
        try {
            questaoDAO.inserir(questao);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void alterar(Questao questao) {
        try {
            questaoDAO.alterar(questao);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void deletar(Questao questao) {
        try {
            questaoDAO.deletar(questao);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public int contarPorDisciplina(int disciplinaId) {
        try {
            return questaoDAO.contarPorDisciplina(disciplinaId);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
