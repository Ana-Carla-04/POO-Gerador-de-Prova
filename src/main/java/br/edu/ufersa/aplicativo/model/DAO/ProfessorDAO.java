package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Professor;

import java.sql.*;
import java.util.List;
import java.util.LinkedList;

public class ProfessorDAO implements DAO<Professor> {
    private static final String sql_inserir = "INSERT INTO professor (nome, email, senha) VALUES (?, ?, ?);";
    private static final String sql_alterar = "UPDATE professor SET email = ?, senha = ? WHERE nome = ?;";
    private static final String sql_deletar = "DELETE FROM professor WHERE nome = ?;";
    private static final String sql_listar = "SELECT * FROM professor";

    private Connection conexao;

    public ProfessorDAO(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(Professor professor) throws SQLException {
        if (professor == null)
            return;
        PreparedStatement ps =  conexao.prepareStatement(sql_inserir);
        ps.setString(1, professor.getNome());
        ps.setString(2, professor.getEmail());
        ps.setString(3, professor.getSenha());
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void deletar(Professor professor) throws SQLException {
        if (professor == null)
            return;
        PreparedStatement ps =  conexao.prepareStatement(sql_deletar);
        ps.setString(1, professor.getNome());
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void alterar(Professor professor) throws SQLException {
        if (professor == null)
            return;
        PreparedStatement ps =  conexao.prepareStatement(sql_alterar);
        ps.setString(3, professor.getNome());
        ps.setString(1, professor.getEmail());
        ps.setString(2, professor.getSenha());
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public List<Professor> listar() throws SQLException {
        Statement st = conexao.createStatement();
        ResultSet rs = st.executeQuery(sql_listar);
        LinkedList<Professor> professores = new LinkedList<>();
        while (rs.next()) {
            String nome = rs.getString("nome");
            String email = rs.getString("email");
            String senha  = rs.getString("senha");
            Professor professor = new Professor(nome, email, senha);
            professores.add(professor);
        }
        rs.close();
        return professores;
    }
}
