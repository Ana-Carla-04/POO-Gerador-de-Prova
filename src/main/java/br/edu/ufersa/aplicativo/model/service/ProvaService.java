package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;

import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.DAO.ProvaDAO;

public class ProvaService {
    private final ProvaDAO provaDAO;

    public ProvaService(ProvaDAO provaDAO) {
        if (provaDAO != null) this.provaDAO = provaDAO;
        else throw new IllegalArgumentException("ProvaDAO invalido");
    }

    public void salvarProva(Prova prova) {
        if (prova == null) throw new IllegalArgumentException("Prova invalida");
        try {
            provaDAO.inserir(prova);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
