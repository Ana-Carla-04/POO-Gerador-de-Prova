package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.VerdadeiroFalso;
import br.edu.ufersa.aplicativo.model.entities.Discursiva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuestaoDAO implements DAO<Questao> {
    private static final String sql_inserir = "INSERT INTO questao (enunciado, nivel, tipo, disciplina_id) VALUES (?, ?, ?, ?);";
    private static final String sql_alterar = "UPDATE questao SET enunciado = ?, nivel = ?, tipo = ?, disciplina_id = ? WHERE id = ?;";
    private static final String sql_deletar = "DELETE FROM questao WHERE id = ?;";

    private static final String sql_listar = "SELECT q.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo " +
            "FROM questao q INNER JOIN disciplina d ON q.disciplina_id = d.id;";
    private static final String sql_buscarPorId = "SELECT q.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo " +
            "FROM questao q INNER JOIN disciplina d ON q.disciplina_id = d.id WHERE q.id = ?;";

    private static final String sql_buscarAlternativas = "SELECT texto_alternativa, verdadeira FROM alternativa WHERE questao_id = ? ORDER BY id;";
    private static final String sql_inserirAlternativa = "INSERT INTO alternativa (questao_id, texto_alternativa, verdadeira) VALUES (?, ?, ?);";
    private static final String sql_deletarAlternativas = "DELETE FROM alternativa WHERE questao_id = ?;";

    private Connection conexao;

    public QuestaoDAO(Connection conexao) throws SQLException{
        if (conexao != null && !conexao.isClosed()) {
            this.conexao = conexao;
        } else throw new IllegalArgumentException("Conexão inválida");
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
                salvarAlternativasEmLote(me.getCodigo(), me.getAlternativas(), null);
            }
        }

        if (questao instanceof VerdadeiroFalso) {
            VerdadeiroFalso vf = (VerdadeiroFalso) questao;
            if (vf.getAlternativas() != null && !vf.getAlternativas().isEmpty()) {
                salvarAlternativasEmLote(vf.getCodigo(), vf.getAlternativas(), vf.getRespostas());
            }
        }
    }

    private void salvarAlternativasEmLote(int questaoId, List<String> alternativas, List<Boolean> respostas) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_inserirAlternativa)) {
            for (int i = 0; i < alternativas.size(); i++) {
                ps.setInt(1, questaoId);
                ps.setString(2, alternativas.get(i));

                if (respostas != null && i < respostas.size()) {
                    ps.setBoolean(3, respostas.get(i));
                } else {
                    ps.setNull(3, Types.BOOLEAN);
                }
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
                salvarAlternativasEmLote(me.getCodigo(), me.getAlternativas(), null);
            }
        } else if (questao instanceof VerdadeiroFalso) {
            VerdadeiroFalso vf = (VerdadeiroFalso) questao;
            if (vf.getAlternativas() != null && !vf.getAlternativas().isEmpty()) {
                salvarAlternativasEmLote(vf.getCodigo(), vf.getAlternativas(), vf.getRespostas());
            }
        }
    }

    public int contarPorDisciplina(int disciplinaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questao WHERE disciplina_id = ?;";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, disciplinaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
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
        String sql = sql_listar + " WHERE q.nivel = ?;";
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
        Disciplina disciplina = new Disciplina(rs.getString("disciplina_nome"), rs.getString("disciplina_codigo"));
        disciplina.setId(rs.getInt("disciplina_id"));

        String tipo = rs.getString("tipo");
        int idQuestao = rs.getInt("id");
        String enunciado = rs.getString("enunciado");
        Nivel nivel = Nivel.deInt(rs.getInt("nivel"));
        String assunto = rs.getString("assunto");

        if ("MultiplaEscolha".equals(tipo)) {
            MultiplaEscolha me = new MultiplaEscolha();
            me.setCodigo(idQuestao);
            me.setEnunciado(enunciado);
            me.setNivel(nivel);
            me.setDisciplina(disciplina);
            me.setAssunto(assunto);

            List<String> alternativas = new ArrayList<>();
            try (PreparedStatement ps = conexao.prepareStatement(sql_buscarAlternativas)) {
                ps.setInt(1, idQuestao);
                try (ResultSet rsA = ps.executeQuery()) {
                    while (rsA.next()) {
                        alternativas.add(rsA.getString("texto_alternativa"));
                    }
                }
            }
            me.setAlternativas(alternativas);
            return me;

        } else if ("VerdadeiroFalso".equals(tipo)) {
            VerdadeiroFalso vf = new VerdadeiroFalso();
            vf.setCodigo(idQuestao);
            vf.setEnunciado(enunciado);
            vf.setNivel(nivel);
            vf.setDisciplina(disciplina);
            vf.setAssunto(assunto);

            StringBuilder gabaritoMontado = new StringBuilder();

            try (PreparedStatement ps = conexao.prepareStatement(sql_buscarAlternativas)) {
                ps.setInt(1, idQuestao);
                try (ResultSet rsA = ps.executeQuery()) {
                    while (rsA.next()) {
                        String textoAlt = rsA.getString("texto_alternativa");
                        boolean valorVerdade = rsA.getBoolean("verdadeira");

                        vf.getAlternativas().add(textoAlt);

                        gabaritoMontado.append(valorVerdade ? "V" : "F").append("-");
                    }
                }
            }

            if (gabaritoMontado.length() > 0) {
                gabaritoMontado.setLength(gabaritoMontado.length() - 1);
                vf.setResposta(gabaritoMontado.toString());
            }

            return vf;

        } else {
            Discursiva discursiva = new Discursiva();
            discursiva.setCodigo(idQuestao);
            discursiva.setEnunciado(enunciado);
            discursiva.setNivel(nivel);
            discursiva.setDisciplina(disciplina);
            discursiva.setAssunto(assunto);
            // Assumindo que a resposta discursiva está em 'assunto' ou campo equivalente se 'tipo' for discursiva
            // Você pode precisar ajustar como a resposta é recuperada se estiver em outra tabela.
            return discursiva;
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