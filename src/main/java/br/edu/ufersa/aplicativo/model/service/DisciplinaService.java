package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;
import br.edu.ufersa.aplicativo.model.observer.Observer;
import br.edu.ufersa.aplicativo.model.observer.Subject;

public class DisciplinaService implements Subject {
    private final DisciplinaDAO disciplinaDAO;
    private final QuestaoService questaoService;
    private final List<Observer> observers = new ArrayList<>();

    public DisciplinaService(DisciplinaDAO disciplinaDAO, QuestaoService questaoService) {
        if (disciplinaDAO != null) this.disciplinaDAO = disciplinaDAO;
        else throw new IllegalArgumentException("DisciplinaDAO invalido");
        this.questaoService = questaoService;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
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
            notifyObservers();
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
}
