package br.edu.ufersa.aplicativo.model.DAO;

import br.edu.ufersa.aplicativo.model.entities.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class ProvaDAO implements DAO<Prova> {
    private Connection conexao;

    public ProvaDAO(Connection conexao) throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            this.conexao = conexao;
        } else throw new IllegalArgumentException("Conexão inválida");
    }

    @Override
    public void inserir(Prova prova) throws SQLException {
        if (prova == null) {
            throw new IllegalArgumentException("Prova não pode ser em branco");
        }

        // CORREÇÃO: Adicionar professor na query
        String sqlProva = "INSERT INTO prova (codigo, instituicao, professor, data_criacao, disciplina_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexao.prepareStatement(sqlProva, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, prova.getCodigo());
            ps.setString(2, prova.getInstituicao());
            ps.setString(3, prova.getProfessor()); // Adicionar professor
            ps.setDate(4, Date.valueOf(prova.getDataDeCriacao()));
            ps.setInt(5, prova.getDisciplina().getId());

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

        // CORREÇÃO: Adicionar professor no UPDATE
        String sql = "UPDATE prova SET instituicao = ?, professor = ?, data_criacao = ?, disciplina_id = ? WHERE codigo = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, prova.getInstituicao());
            ps.setString(2, prova.getProfessor()); // Adicionar professor
            ps.setDate(3, Date.valueOf(prova.getDataDeCriacao()));
            ps.setInt(4, prova.getDisciplina().getId());
            ps.setString(5, prova.getCodigo());

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

    // Substitua o método listar() e adicione buscarQuestoesDaProva() no ProvaDAO.java

    @Override
    public List<Prova> listar() throws SQLException {
        List<Prova> listaProvas = new ArrayList<>();

        // JOIN com disciplina para pegar o nome e código corretamente
        String sql = "SELECT p.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo_str " +
                "FROM prova p " +
                "LEFT JOIN disciplina d ON p.disciplina_id = d.id " +
                "ORDER BY p.data_criacao DESC";

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Disciplina disc = new Disciplina(
                        rs.getString("disciplina_nome"),
                        rs.getString("disciplina_codigo_str")
                );
                disc.setId(rs.getInt("disciplina_id"));

                // Busca as questões da prova
                List<Questao> questoesDaProva = buscarQuestoesDaProva(rs.getInt("id"));

                String professor = rs.getString("professor");

                Prova prova = new Prova(
                        questoesDaProva,
                        disc,
                        rs.getString("codigo"),
                        professor != null ? professor : ""
                );

                prova.setId(rs.getInt("id"));
                prova.setInstituicao(rs.getString("instituicao"));

                if (rs.getDate("data_criacao") != null) {
                    prova.setDataDeCriacao(rs.getDate("data_criacao").toLocalDate());
                }

                listaProvas.add(prova);
            }
        }
        return listaProvas;
    }

    // Busca as questões vinculadas a uma prova via tabela prova_questao
    // Versão otimizada de buscarQuestoesDaProva — evita N+1 queries
// Substitua o método acima por este no ProvaDAO.java

    private List<Questao> buscarQuestoesDaProva(int provaId) throws SQLException {
        List<Questao> questoes = new ArrayList<>();

        String sql = "SELECT q.id FROM prova_questao pq " +
                "INNER JOIN questao q ON pq.questao_id = q.id " +
                "WHERE pq.prova_id = ? ORDER BY q.id";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, provaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }

                if (ids.isEmpty()) return questoes;

                // Busca cada questão completa (com alternativas) pelo id
                String sqlBuscarAlternativas =
                        "SELECT texto_alternativa, verdadeira FROM alternativa WHERE questao_id = ? ORDER BY id";
                String sqlBuscarQuestao =
                        "SELECT q.*, d.nome as disciplina_nome, d.codigo as disciplina_codigo, d.id as disc_id " +
                                "FROM questao q LEFT JOIN disciplina d ON q.disciplina_id = d.id WHERE q.id = ?";

                for (int qId : ids) {
                    try (PreparedStatement psQ = conexao.prepareStatement(sqlBuscarQuestao)) {
                        psQ.setInt(1, qId);
                        try (ResultSet rsQ = psQ.executeQuery()) {
                            if (!rsQ.next()) continue;

                            Disciplina disc = new Disciplina(
                                    rsQ.getString("disciplina_nome"),
                                    rsQ.getString("disciplina_codigo")
                            );
                            disc.setId(rsQ.getInt("disc_id"));

                            String tipo      = rsQ.getString("tipo");
                            String enunciado = rsQ.getString("enunciado");
                            Nivel nivel      = Nivel.deInt(rsQ.getInt("nivel"));
                            String assunto   = rsQ.getString("assunto");

                            // Busca alternativas
                            List<String> alternativas = new ArrayList<>();
                            String respostaCorreta = "";

                            try (PreparedStatement psA = conexao.prepareStatement(sqlBuscarAlternativas)) {
                                psA.setInt(1, qId);
                                try (ResultSet rsA = psA.executeQuery()) {
                                    while (rsA.next()) {
                                        String texto = rsA.getString("texto_alternativa");
                                        boolean verdadeira = rsA.getBoolean("verdadeira");
                                        alternativas.add(texto);
                                        if (verdadeira) respostaCorreta = texto;
                                    }
                                }
                            }

                            if ("MultiplaEscolha".equals(tipo)) {
                                MultiplaEscolha me = new MultiplaEscolha();
                                me.setCodigo(qId);
                                me.setEnunciado(enunciado);
                                me.setNivel(nivel);
                                me.setDisciplina(disc);
                                me.setAssunto(assunto);
                                me.setAlternativas(alternativas);
                                if (!respostaCorreta.isEmpty()) me.setResposta(respostaCorreta);
                                questoes.add(me);

                            } else if ("VerdadeiroFalso".equals(tipo)) {
                                VerdadeiroFalso vf = new VerdadeiroFalso();
                                vf.setCodigo(qId);
                                vf.setEnunciado(enunciado);
                                vf.setNivel(nivel);
                                vf.setDisciplina(disc);
                                vf.setAssunto(assunto);
                                for (String alt : alternativas) {
                                    vf.adicionarAlternativa(alt, alt.equals(respostaCorreta));
                                }
                                questoes.add(vf);

                            } else {
                                Discursiva d = new Discursiva();
                                d.setCodigo(qId);
                                d.setEnunciado(enunciado);
                                d.setNivel(nivel);
                                d.setDisciplina(disc);
                                d.setAssunto(assunto);
                                if (!respostaCorreta.isEmpty()) d.setResposta(respostaCorreta);
                                questoes.add(d);
                            }
                        }
                    }
                }
            }
        }
        return questoes;
    }
}