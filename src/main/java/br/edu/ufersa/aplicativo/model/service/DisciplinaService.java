package br.edu.ufersa.aplicativo.model.service;

import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;

public class DisciplinaService {
    private final DisciplinaDAO disciplinaDAO;

    public DisciplinaService(DisciplinaDAO disciplinaDAO) {
        if (disciplinaDAO != null) this.disciplinaDAO = disciplinaDAO;
        else throw new IllegalArgumentException("DisciplinaDAO invalido");
    }
}
