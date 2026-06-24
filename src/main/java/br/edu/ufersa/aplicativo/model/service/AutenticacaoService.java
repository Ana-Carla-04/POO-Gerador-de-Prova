package br.edu.ufersa.aplicativo.model.service;

import java.sql.SQLException;
import java.util.Optional;

import br.edu.ufersa.aplicativo.model.entities.Professor;
import br.edu.ufersa.aplicativo.model.DAO.ProfessorDAO;
import br.edu.ufersa.aplicativo.model.dto.TentarLoginDTO;
import br.edu.ufersa.aplicativo.model.dto.CadastroDTO;

public class AutenticacaoService {
    private final ProfessorDAO professorDAO;

    public AutenticacaoService(ProfessorDAO professorDAO) {
        if (professorDAO != null) this.professorDAO = professorDAO;
        else throw new IllegalArgumentException("ProfessorDAO invalido");
    }

    public Optional<Professor> tentarLogin(TentarLoginDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO invalido");
        try {
            Optional<Professor> professor = professorDAO.buscarPorEmail(dto.getEmail());
            if (professor.isEmpty()) return Optional.empty();
            if (professor.get().getSenha().equals(dto.getSenha())) return professor;
            return Optional.empty();
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public Professor tentarCadastro(CadastroDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO invalido");
        Professor novoProfessor = new Professor(dto.getNome(), dto.getEmail(), dto.getSenha());
        try {
            professorDAO.inserir(novoProfessor);
            return novoProfessor;
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
