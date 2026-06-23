package br.edu.ufersa.aplicativo.model.entities;

public abstract class Questao {
    private int codigo;
    private String enunciado;
    private String assunto;
    private Disciplina disciplina;
    private Nivel nivel;

    public Questao(int codigo, String enunciado, String assunto, Disciplina disciplina, Nivel nivel) {
        setCodigo(codigo);
        setEnunciado(enunciado);
        setAssunto(assunto);
        setDisciplina(disciplina);
        setNivel(nivel);
    }

    public Questao(){};

    public void setCodigo(int codigo) {
        if (codigo > 0) {
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

    public void setNivel(Nivel nivel) {
        if (nivel != null) {
            this.nivel = nivel;
        }
    }

    public abstract void setResposta(String resposta);

    public int getCodigo() {
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

    public Nivel getNivel() {
        return this.nivel;
    }

    public abstract String getResposta();
}
