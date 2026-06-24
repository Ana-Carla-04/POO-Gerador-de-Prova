package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.entities.Professor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DisciplinaDAO implements DAO<Disciplina>{
    private static final String sql_inserir = "INSERT INTO disciplina (nome, codigo, professor_id) VALUES (?,?,?);";
    private static final String sql_alterar = "UPDATE disciplina SET nome = ?, codigo = ?, professor_id = ? WHERE id = ?;";
    private static final String sql_deletar = "DELETE FROM disciplina WHERE id = ?;";
    private static final String sql_listar = "SELECT * FROM disciplina;";
    private static final String sql_buscarPorId = "SELECT * FROM disciplina WHERE id = ?;";
    private static final String sql_buscarPorCodigo = "SELECT * FROM disciplina WHERE codigo = ?;";
    private static final String sql_buscarPorProfessor = "SELECT * FROM disciplina WHERE professor_id = ?;";
    private static final String sql_buscarPorNome = "SELECT * FROM disciplina WHERE nome = ?;"; // Adicionar constante

    private Connection conexao;

    public DisciplinaDAO(Connection conexao) throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            this.conexao = conexao;
        } else throw new IllegalArgumentException("Conexão inválida");
    }

    @Override
    public void inserir(Disciplina disciplina) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_inserir, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, disciplina.getNome());
            ps.setString(2, disciplina.getCodigo());
            ps.setInt(3, disciplina.getProfessor().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    disciplina.setId(idGerado);
                }
            }
        }
    }

    @Override
    public void deletar(Disciplina disciplina) throws SQLException{
        if (disciplina == null){
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_deletar)) {
            ps.setInt(1, disciplina.getId());
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Disciplina com ID " + disciplina.getId() + " não encontrada");
            }
        }
    }

    @Override
    public void alterar(Disciplina disciplina) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_alterar)) {
            ps.setString(1, disciplina.getNome());
            ps.setString(2, disciplina.getCodigo());
            ps.setInt(3, disciplina.getProfessor().getId());
            ps.setInt(4, disciplina.getId());
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Disciplina com ID " + disciplina.getId() + " não encontrada");
            }
        }
    }

    @Override
    public List<Disciplina> listar() throws SQLException {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql_listar)) {

            List<Disciplina> disciplinas = new ArrayList<>();
            while (rs.next()) {
                disciplinas.add(criarDisciplinaDoResultSet(rs));
            }
            return disciplinas;
        }
    }

    public Disciplina buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarPorId)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return criarDisciplinaDoResultSet(rs);
                }
            }
        }
        return null;
    }

    public Disciplina buscarPorCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código da disciplina não pode ser nulo ou vazio");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarPorCodigo)) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return criarDisciplinaDoResultSet(rs);
                }
            }
        }
        return null;
    }

    // 🔥 CORREÇÃO AQUI - Usa o construtor com nome e código
    public Disciplina buscarPorNome(String nome) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da disciplina não pode ser nulo ou vazio");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarPorNome)) {
            ps.setString(1, nome);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 🔥 CORRIGIDO: Usa o construtor com nome e código
                    Disciplina disciplina = new Disciplina(
                            rs.getString("nome"),
                            rs.getString("codigo")
                    );
                    disciplina.setId(rs.getInt("id"));

                    // Carrega o professor se existir
                    int professorId = rs.getInt("professor_id");
                    if (!rs.wasNull() && professorId > 0) {
                        Professor professor = new Professor();
                        professor.setId(professorId);
                        disciplina.setProfessor(professor);
                    }

                    return disciplina;
                }
            }
        }
        return null;
    }

    public List<Disciplina> buscarPorProfessor(Professor professor) throws SQLException {
        if (professor == null) {
            throw new IllegalArgumentException("Professor não pode ser nulo");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarPorProfessor)) {
            ps.setInt(1, professor.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Disciplina> disciplinas = new LinkedList<>();
                while (rs.next()) {
                    disciplinas.add(criarDisciplinaDoResultSet(rs));
                }
                return disciplinas;
            }
        }
    }

    private Disciplina criarDisciplinaDoResultSet(ResultSet rs) throws SQLException {
        // 🔥 CRIA A DISCIPLINA COM NOME E CÓDIGO
        Disciplina disciplina = new Disciplina(
                rs.getString("nome"),
                rs.getString("codigo")
        );
        disciplina.setId(rs.getInt("id"));

        // Carrega o professor se existir
        int professorId = rs.getInt("professor_id");
        if (!rs.wasNull() && professorId > 0) {
            Professor professor = new Professor();
            professor.setId(professorId);
            disciplina.setProfessor(professor);
        }

        return disciplina;
    }
}