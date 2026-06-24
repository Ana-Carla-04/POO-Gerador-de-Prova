package br.edu.ufersa.aplicativo.model.entities;

import java.time.LocalDate;
import java.util.List;

public class Prova {
    private int id;
    private String codigo;
    private String instituicao;
    private String professor;
    private LocalDate dataDeCriacao;
    private List<Questao> questoes;
    private Disciplina disciplina;

    public Prova() {
        // Construtor vazio para o DAO
    }

    public Prova(List<Questao> questoes, Disciplina disciplina, String codigo, String professor) {
        this.dataDeCriacao = LocalDate.now();
        this.questoes = questoes;
        this.disciplina = disciplina;
        this.codigo = codigo;
        this.professor = professor;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) {
        if (codigo != null && !codigo.trim().isEmpty()) {
            this.codigo = codigo;
        }
    }

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) {
        if (instituicao != null && !instituicao.trim().isEmpty()) {
            this.instituicao = instituicao;
        }
    }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) {
        if (professor != null && !professor.trim().isEmpty()) {
            this.professor = professor;
        }
    }

    public LocalDate getDataDeCriacao() { return dataDeCriacao; }
    public void setDataDeCriacao(LocalDate dataDeCriacao) {
        if (dataDeCriacao != null) {
            this.dataDeCriacao = dataDeCriacao;
        }
    }

    public List<Questao> getQuestoes() { return questoes; }
    public void setQuestoes(List<Questao> questoes) {
        if (questoes != null && !questoes.isEmpty()) {
            this.questoes = questoes;
        }
    }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }

    public String getNomeDisciplina() {
        return disciplina != null ? disciplina.getNome() : "";
    }
}