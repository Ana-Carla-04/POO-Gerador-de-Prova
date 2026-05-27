//define o pacote(pasta) onde a class esta localizada
package br.edu.ufersa.Aplicativo.model.entity; 

import java.util.List;//importa a interface List

public class Disciplina {
    //atributos
    private String nome;
    private String codigo;
    private List<String> assuntos;

    //construtor
    public Disciplina(String nome, String codigo, List<String> assuntos) {
        setNome(nome);
        setCodigo(codigo);
        setAssuntos(assuntos);
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

}