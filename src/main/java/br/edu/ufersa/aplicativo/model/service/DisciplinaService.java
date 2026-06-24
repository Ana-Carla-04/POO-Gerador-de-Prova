package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import java.util.List;

import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;

public class DisciplinaService {
    private final DisciplinaDAO disciplinaDAO;
    private final QuestaoService questaoService;

    public DisciplinaService(DisciplinaDAO disciplinaDAO, QuestaoService questaoService) {
        if (disciplinaDAO != null) this.disciplinaDAO = disciplinaDAO;
        else throw new IllegalArgumentException("DisciplinaDAO invalido");
        this.questaoService = questaoService;
    }

    public List<Disciplina> listarDisciplinas() {
        try {
            return disciplinaDAO.listar();
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void inserir(Disciplina disciplina) {
        try {
            disciplinaDAO.inserir(disciplina);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public List<DisciplinaInfo> listarDisciplinasInfo() {
        List<Disciplina> disciplinas = listarDisciplinas();
        return disciplinas.stream()
            .map(d -> new DisciplinaInfo(
                d.getNome(),
                d.getCodigo(),
                questaoService != null ? questaoService.contarPorDisciplina(d.getId()) : 0,
                d.getProfessor() != null ? d.getProfessor().getNome() : "Sem Professor"
            ))
            .collect(java.util.stream.Collectors.toList());
    }
    // No DisciplinaService.java
    public boolean excluirDisciplina(String nomeDisciplina) {
        try {
            // Primeiro busca a disciplina pelo nome
            Disciplina disciplina = disciplinaDAO.buscarPorNome(nomeDisciplina);
            if (disciplina == null) {
                System.err.println("Disciplina não encontrada: " + nomeDisciplina);
                return false;
            }

            // Exclui a disciplina (ON DELETE CASCADE vai excluir assuntos, questões e provas)
            disciplinaDAO.deletar(disciplina);
            System.out.println("Disciplina excluída com sucesso: " + nomeDisciplina);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao excluir disciplina: " + e.getMessage());
            return false;
        }
    }


}
