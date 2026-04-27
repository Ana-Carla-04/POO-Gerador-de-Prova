package br.edu.ufersa.Aplicativo.model.entity;

public class Questao {
    private String codigo;
    private String enunciado;
    private String assunto;
    private Disciplina disciplina;
    private String nivel;

    public Questao(String codigo, String enunciado, String assunto, Disciplina disciplina, String nivel) {
        setCodigo(codigo);
        setEnunciado(enunciado);
        setAssunto(assunto);
        setDisciplina(disciplina);
        setNivel(nivel);
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getAssunto() {
        return assunto;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public String getNivel() {
        return nivel;
    }

    // Setters
    public void setCodigo(String codigo) {
        if (codigo != null && !codigo.trim().isEmpty()) {
            this.codigo = codigo;
        }
    }

    public void setEnunciado(String enunciado) {
        if (enunciado != null && !enunciado.trim().isEmpty()) {
            this.enunciado = enunciado;
        }
    }

    public void setAssunto(String assunto) {
        if (assunto != null && !assunto.trim().isEmpty()) {
            this.assunto = assunto;
        }
    }
    
    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }

    public void setNivel(String nivel) {
        if (nivel.equalsIgnoreCase("Fácil") || nivel.equalsIgnoreCase("Médio") || nivel.equalsIgnoreCase("Difícil")) {
            this.nivel = nivel;
        }
    }
}
