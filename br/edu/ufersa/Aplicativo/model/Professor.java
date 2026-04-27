package br.edu.ufersa.Aplicativo.model;

import java.util.ArrayList;
import java.util.List;

public class Professor {

    private String login;
    private String senha;
    private String nome;
    private List<Disciplina> disciplinas;

    public Professor(String nome, String login, String senha) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.disciplinas = new ArrayList<>();
    }

    public void adicionarDisciplinanoProfessor(Disciplina disciplina) {
        if (disciplina != null && !this.disciplinas.contains(disciplina)) {
            this.disciplinas.add(disciplina);
        }
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}