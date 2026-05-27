package br.edu.ufersa.aplicativo.model.entities;

import java.util.List;

public class Disciplina {
    private String nome;
    private String codigo;
    private List<String> assuntos;

    public Disciplina(String nome, String codigo, List<String> assuntos) {
        setNome(nome);
        setCodigo(codigo);
        setAssuntos(assuntos);
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

    public void setAssuntos(List<String> assuntos) {
        if (assuntos != null && !assuntos.isEmpty()) {
            this.assuntos = assuntos;
        }
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public List<String> getAssuntos() {
        return assuntos;
    }

}