package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.service.ProvaService;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO;
import br.edu.ufersa.aplicativo.util.Conexao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ProvaSessao {

    private static final ProvaSessao INSTANCE = new ProvaSessao();

    private String professor = "";
    private String instituicao = "";
    private String disciplina = "";
    private String tipo = "";
    private int totalQuestoes = 0;
    private static QuestaoDAO questaoDAO;

    private final Set<Questao> questoes = new LinkedHashSet<>();
    private final List<Prova> provasSalvas = new ArrayList<>();

    private ProvaSessao() {
        try {
            questaoDAO = new QuestaoDAO(Conexao.abrirConexao());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static ProvaSessao getInstance() {
        return INSTANCE;
    }

    public void limpar() {
        professor = "";
        instituicao = "";
        disciplina = "";
        tipo = "";
        totalQuestoes = 0;
        questoes.clear();
    }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getTotalQuestoes() { return totalQuestoes; }
    public void setTotalQuestoes(int totalQuestoes) { this.totalQuestoes = totalQuestoes; }

    public List<Questao> getQuestoes() {
        return new ArrayList<>(questoes);
    }

    public void setQuestoes(List<Questao> novasQuestoes) {
        questoes.clear();
        questoes.addAll(novasQuestoes);
    }

    public Prova registrarProvaSalva() {
        List<Questao> lista = getQuestoes();
        Disciplina disciplina = lista.isEmpty() ? null : lista.get(0).getDisciplina();
        String codigo = "PROVA-" + System.currentTimeMillis();

        // Passar o professor como parâmetro
        Prova prova = new Prova(lista, disciplina, codigo, this.professor);
        prova.setInstituicao(instituicao);
        prova.setDataDeCriacao(LocalDate.now());

        provasSalvas.add(prova);
        return prova;
    }

    public List<Prova> getProvasSalvas() {
        return Collections.unmodifiableList(provasSalvas);
    }

    private static List<Questao> bancoCache = null;

    public static synchronized List<Questao> bancoDeQuestoes() {
        if (bancoCache != null) {
            return bancoCache;
        }
        List<Questao> banco;
        try {
            banco = questaoDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        bancoCache = banco;
        return banco;
    }

    public void removerProvaSalva(Prova prova) {
        if (prova != null) {
            provasSalvas.remove(prova);
        }
    }
}