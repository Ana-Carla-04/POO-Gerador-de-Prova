package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProvaDAO implements DAO<Prova> {
    private Connection conexao;

    public ProvaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(Prova prova) throws SQLException {
        if (prova == null) {
            throw new IllegalArgumentException("Prova não pode ser em branco");
        }

        String sqlProva = "INSERT INTO prova (codigo, instituicao, data_criacao, disciplina_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexao.prepareStatement(sqlProva, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, prova.getCodigo());
            ps.setString(2, prova.getInstituicao());
            ps.setDate(3, Date.valueOf(prova.getDataDeCriacao()));
            ps.setInt(4, prova.getDisciplina().getId());

            ps.executeUpdate();

            int idProvaGerado = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idProvaGerado = rs.getInt(1);
                }
            }

            if (idProvaGerado > 0 && prova.getQuestoes() != null && !prova.getQuestoes().isEmpty()) {
                String sqlLigacao = "INSERT INTO prova_questao (prova_id, questao_id) VALUES (?, ?)";
                try (PreparedStatement ps2 = conexao.prepareStatement(sqlLigacao)) {
                    for (Questao q : prova.getQuestoes()) {
                        ps2.setInt(1, idProvaGerado);
                        ps2.setInt(2, q.getCodigo());
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }
            System.out.println("Prova e suas questões geradas no banco com sucesso!");
        }
    }

    @Override
    public void alterar(Prova prova) throws SQLException {
        if (prova == null) {
            throw new IllegalArgumentException("Prova não pode ser em branco");
        }

        String sql = "UPDATE prova SET instituicao = ?, data_criacao = ?, disciplina_id = ? WHERE codigo = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, prova.getInstituicao());
            ps.setDate(2, Date.valueOf(prova.getDataDeCriacao()));
            ps.setInt(3, prova.getDisciplina().getId());
            ps.setString(4, prova.getCodigo());

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Prova com código " + prova.getCodigo() + " não encontrada para alteração");
            }
            System.out.println("Prova alterada com sucesso!");
        }
    }

    @Override
    public void deletar(Prova prova) throws SQLException {
        if (prova == null) {
            throw new IllegalArgumentException("Prova não pode ser em branca");
        }

        String sql = "DELETE FROM prova WHERE codigo = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, prova.getCodigo());

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Prova com código " + prova.getCodigo() + " não encontrada para deleção");
            }
            System.out.println("Prova deletada com sucesso!");
        }
    }

    @Override
    public List<Prova> listar() throws SQLException {
        List<Prova> listaProvas = new ArrayList<>();
        String sql = "SELECT * FROM prova;";

        DisciplinaDAO disciplinaDAO = new DisciplinaDAO(this.conexao);

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int disciplinaId = rs.getInt("disciplina_id");

                Disciplina disc = disciplinaDAO.buscarPorId(disciplinaId);

                List<Questao> questoesDaProva = new ArrayList<>();

                Prova prova = new Prova(questoesDaProva, disc, rs.getString("codigo"));
                prova.setInstituicao(rs.getString("instituicao"));

                if (rs.getDate("data_criacao") != null) {
                    prova.setDataDeCriacao(rs.getDate("data_criacao").toLocalDate());
                }

                listaProvas.add(prova);
            }
        }
        return listaProvas;
    }
}