package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.*;

import java.sql.*;
import java.util.*;

public class QuestaoDAO implements DAO<Questao> {
    private static final String sql_inserir = "INSERT INTO questao (enunciado, nivel, tipo, disciplina_id) VALUES (?, ?, ?, ?);";
    private static final String sql_alterar = "UPDATE questao SET enunciado = ?, nivel = ?, tipo = ?, disciplina_id = ? WHERE id = ?;";
    private static final String sql_deletar = "DELETE FROM questao WHERE id = ?;";
    private static final String sql_listar = "SELECT q.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo " +
            "FROM questao q, disciplina d WHERE q.disciplina_id = d.id;";
    private static final String sql_buscarPorId = "SELECT q.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo " +
            "FROM questao q, disciplina d WHERE q.disciplina_id = d.id AND q.id = ?;";

    private static final String sql_buscarAlternativas = "SELECT texto_alternativa FROM alternativa WHERE questao_id = ? ORDER BY id;";
    private static final String sql_inserirAlternativa = "INSERT INTO alternativa (questao_id, texto_alternativa) VALUES (?, ?);";
    private static final String sql_deletarAlternativas = "DELETE FROM alternativa WHERE questao_id = ?;";

    private Connection conexao;

    public QuestaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void inserir(Questao questao) throws SQLException {
        if (questao == null) {
            throw new IllegalArgumentException("Questão não pode ser nula");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_inserir, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, questao.getEnunciado());
            ps.setInt(2, questao.getNivel().getValor());

            if (questao instanceof MultiplaEscolha) {
                ps.setString(3, "MultiplaEscolha");
            } else if (questao instanceof VerdadeiroFalso) {
                ps.setString(3, "VerdadeiroFalso");
            } else {
                ps.setString(3, "Discursiva");
            }

            ps.setInt(4, questao.getDisciplina().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    questao.setCodigo(rs.getInt(1));
                }
            }
        }

        if (questao instanceof MultiplaEscolha) {
            MultiplaEscolha me = (MultiplaEscolha) questao;
            if (me.getAlternativas() != null && !me.getAlternativas().isEmpty()) {
                inserirAlternativas(me.getCodigo(), me.getAlternativas());
            }
        }

        if (questao instanceof VerdadeiroFalso) {
            VerdadeiroFalso vf = (VerdadeiroFalso) questao;
            String sqlAlt = "INSERT INTO alternativa (texto_alternativa, questao_id, verdadeira) VALUES (?, ?, ?);";

            try (PreparedStatement psAlt = conexao.prepareStatement(sqlAlt)) {
                for (int i = 0; i < vf.getAlternativas().size(); i++) {
                    psAlt.setString(1, vf.getAlternativas().get(i));
                    psAlt.setInt(2, vf.getCodigo());
                    psAlt.setBoolean(3, vf.getRespostas().get(i));

                    psAlt.executeUpdate();
                }
            }
        }
    }

    private void inserirAlternativas(int questaoId, List<String> alternativas) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_inserirAlternativa)) {
            for (String alt : alternativas) {
                ps.setInt(1, questaoId);
                ps.setString(2, alt);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void deletar(Questao questao) throws SQLException {
        if (questao == null) {
            throw new IllegalArgumentException("Questão não pode ser nula");
        }

        deletarAlternativas(questao.getCodigo());

        try (PreparedStatement ps = conexao.prepareStatement(sql_deletar)) {
            ps.setInt(1, questao.getCodigo());
            ps.executeUpdate();
        }
    }

    private void deletarAlternativas(int questaoId) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_deletarAlternativas)) {
            ps.setInt(1, questaoId);
            ps.executeUpdate();
        }
    }

    @Override
    public void alterar(Questao questao) throws SQLException {
        if (questao == null) {
            throw new IllegalArgumentException("Questão não pode ser nula");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql_alterar)) {
            ps.setString(1, questao.getEnunciado());
            ps.setInt(2, questao.getNivel().getValor());

            if (questao instanceof MultiplaEscolha) {
                ps.setString(3, "MultiplaEscolha");
            } else if (questao instanceof VerdadeiroFalso) {
                ps.setString(3, "VerdadeiroFalso");
            } else {
                ps.setString(3, "Discursiva");
            }

            ps.setInt(4, questao.getDisciplina().getId());
            ps.setInt(5, questao.getCodigo());
            ps.executeUpdate();
        }

        deletarAlternativas(questao.getCodigo());
        if (questao instanceof MultiplaEscolha) {
            MultiplaEscolha me = (MultiplaEscolha) questao;
            if (me.getAlternativas() != null && !me.getAlternativas().isEmpty()) {
                inserirAlternativas(me.getCodigo(), me.getAlternativas());
            }
        }
    }

    @Override
    public List<Questao> listar() throws SQLException {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql_listar)) {

            List<Questao> questoes = new LinkedList<>();
            while (rs.next()) {
                questoes.add(criarQuestaoDoResultSet(rs));
            }
            return questoes;
        }
    }

    public List<Questao> buscarPorNivel(Nivel nivel) throws SQLException {
        String sql = sql_listar + " AND q.nivel = ?;";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, nivel.getValor());
            try (ResultSet rs = ps.executeQuery()) {
                List<Questao> questoes = new LinkedList<>();
                while (rs.next()) {
                    questoes.add(criarQuestaoDoResultSet(rs));
                }
                return questoes;
            }
        }
    }

    private Questao criarQuestaoDoResultSet(ResultSet rs) throws SQLException {
        Disciplina disciplina = new Disciplina(
                rs.getInt("disciplina_id"),
                rs.getString("disciplina_nome"),
                rs.getString("disciplina_codigo"),
                null,
                null
        );

        String tipo = rs.getString("tipo");
        Questao questao;

        if ("MultiplaEscolha".equals(tipo)) {
            MultiplaEscolha me = new MultiplaEscolha();
            me.setCodigo(rs.getInt("id"));
            me.setEnunciado(rs.getString("enunciado"));

            int valorNivel = rs.getInt("nivel");
            me.setNivel(Nivel.deInt(valorNivel));

            me.setDisciplina(disciplina);

            me.setAlternativas(buscarAlternativas(me.getCodigo()));
            questao = me;
        } else {
            questao = new MultiplaEscolha();
            questao.setCodigo(rs.getInt("id"));
            questao.setEnunciado(rs.getString("enunciado"));

            int valorNivel = rs.getInt("nivel");
            questao.setNivel(Nivel.deInt(valorNivel));

            questao.setDisciplina(disciplina);
        }

        return questao;
    }

    private List<String> buscarAlternativas(int questaoId) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarAlternativas)) {
            ps.setInt(1, questaoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> alternativas = new LinkedList<>();
                while (rs.next()) {
                    alternativas.add(rs.getString("texto_alternativa"));
                }
                return alternativas;
            }
        }
    }

    public void deletarTodas() throws SQLException {
        String sql = "DELETE FROM questao";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Banco de questões zerado com sucesso!");
        }
    }
}