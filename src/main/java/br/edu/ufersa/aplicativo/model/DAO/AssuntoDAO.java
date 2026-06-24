package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Assunto;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class AssuntoDAO implements DAO<Assunto> {
    private static final String sql_inserir = "INSERT INTO assunto (assunto) VALUES (?);";
    private static final String sql_alterar = "UPDATE assunto SET assunto = ? WHERE id = ?;";
    private static final String sql_deletar = "DELETE FROM assunto WHERE id = ?;";
    private static final String sql_listar = "SELECT * FROM assunto;";

    private static final String sql_associar_em_disciplina = "INSERT INTO assunto_em_disciplina (id_assunto, codigo_disciplina) VALUES (?, ?);";
    private static final String sql_listar_por_disciplina = "SELECT assunto.* FROM assunto INNER JOIN assunto_em_disciplina ON assunto.id = assunto_em_disciplina.id_assunto WHERE assunto_em_disciplina.codigo_disciplina = ?;";
    private static final String sql_desassociar_por_disciplina = "DELETE FROM assunto_em_disciplina WHERE codigo_disciplina = ?;";

    private Connection conexao;

    public AssuntoDAO(Connection conexao) throws SQLException{
        if (conexao != null && !conexao.isClosed()) {
            this.conexao = conexao;
        } else throw new IllegalArgumentException("Conexão inválida");
    }

    @Override
    public void inserir(Assunto assunto) throws SQLException {
        if (assunto == null) {
            throw new IllegalArgumentException("Assunto não pode ser nulo");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_inserir);
        stmt.setString(1, assunto.getAssunto());
        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            assunto.setId(rs.getInt(1));
        }
        rs.close();
        stmt.close();
    }

    @Override
    public void alterar(Assunto assunto) throws SQLException {
        if (assunto == null) {
            throw new IllegalArgumentException("Assunto não pode ser nulo");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_alterar);
        stmt.setString(1, assunto.getAssunto());
        stmt.setInt(2, assunto.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void deletar(Assunto assunto) throws SQLException {
        if (assunto == null) {
            throw new IllegalArgumentException("Assunto não pode ser nulo");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_deletar);
        stmt.setInt(1, assunto.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public List<Assunto> listar() throws SQLException {
        List<Assunto> lista = new ArrayList<Assunto>();
        Statement st = conexao.createStatement();
        ResultSet rs = st.executeQuery(sql_listar);
        while (rs.next()) {
            int id =  rs.getInt("id");
            String assunto = rs.getString("assunto");
            lista.add(new Assunto(id, assunto));
        }
        return lista;
    }

    public void associarAssunto(Disciplina disciplina, Assunto assunto) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser null");
        }
        if (assunto == null) {
            throw new IllegalArgumentException("Assunto não pode ser null");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_associar_em_disciplina);
        stmt.setString(1, disciplina.getCodigo());
    }

    public List<Assunto> listarAssuntosEmDisciplina(Disciplina disciplina) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nulo");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_listar_por_disciplina);
        stmt.setString(1, disciplina.getCodigo());
        ResultSet rs = stmt.executeQuery();
        List<Assunto> lista = new ArrayList<Assunto>();
        while (rs.next()) {
            int id =  rs.getInt("id");
            String assunto = rs.getString("assunto");
            lista.add(new Assunto(id, assunto));
        }
        return lista;
    }

    public void desassociarAssuntosEmDisciplina(Disciplina disciplina) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nulo");
        }
        PreparedStatement stmt = conexao.prepareStatement(sql_desassociar_por_disciplina);
        stmt.setString(1, disciplina.getCodigo());
        stmt.executeUpdate();
        stmt.close();
    }
}
