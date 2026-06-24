package br.edu.ufersa.aplicativo.model.entities;

import java.util.List;

public class Disciplina {
    private int id;
    private String nome;
    private String codigo;
    private Professor professor;  
    private List<Assunto> assuntos;

    public Disciplina(String nome, String codigo,Professor prof, List<Assunto> assuntos) {
        setNome(nome);
        setCodigo(codigo);
        setProfessor(prof);
        setAssuntos(assuntos);
    }

    public Disciplina(String nome, String codigo, Professor prof){

        setNome(nome);
        setCodigo(codigo);
        setProfessor(prof);
    }

    public Disciplina(){};

    public Disciplina(String disciplinaNome, String disciplinaCodigo) {
        setNome(disciplinaNome);
        setCodigo(disciplinaCodigo);
    }

    //setters
    public void setId(int id){
        this.id = id;
    }

    public void setNome(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            this.nome = nome;
        }
    }

    public void setCodigo(String codigo) {
        if (codigo != null && !codigo.trim().isEmpty()) {
            this.codigo = codigo;
        }
    }
    public void setProfessor(Professor professor) {
        if (professor != null) {
            this.professor = professor;
        }
    }

    public void setAssuntos(List<Assunto> assuntos) {
        if (assuntos != null && !assuntos.isEmpty()) {
            this.assuntos = assuntos;
        }
    }

    //getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public Professor getProfessor() {
        return professor;
    }

    public List<Assunto> getAssuntos() {
        return assuntos;
    }

}
