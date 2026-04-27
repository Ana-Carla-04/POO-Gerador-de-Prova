package br.edu.ufersa.Aplicativo.model.entity;

import java.util.List;

public class Disciplina {
    private String nome;
    private String codigo;
    private List<String> assuntos;

    Disciplina(String nome, String codigo, List<String> assuntos) {
        setNome(nome);
        setCodigo(codigo);
        setAssuntos(assuntos);
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<String> getAssuntos() {
        return assuntos;
    }

    // Setters
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

    public void setAssuntos(List<String> assuntos) {
        if (assuntos != null && !assuntos.isEmpty()) {
            this.assuntos = assuntos;
        }
    }
}