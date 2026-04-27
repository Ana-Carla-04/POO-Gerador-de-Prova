package br.edu.ufersa.Aplicativo.model.entity;//localização da classe

public class Questao {
    //atributos
    private String codigo;
    private String enunciado;
    private String assunto;
    private Disciplina disciplina;
    private String nivel;
    private String tipo;

    //construtor
    public Questao(String codigo, String enunciado,String tipo, String assunto, Disciplina disciplina, String nivel) {
        setCodigo(codigo);
        setEnunciado(enunciado);
        setAssunto(assunto);
        setDisciplina(disciplina);
        setNivel(nivel);
        setTipo(tipo);
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

    public void setTipo(String tipo) {
        //validando se nivel é igual a qualquer variação de Multipla-Escolha,Discursiva,Verdadeiro ou Falso
        if (tipo.equalsIgnoreCase("Multipla-Escolha") || tipo.equalsIgnoreCase("Discursiva") || tipo.equalsIgnoreCase("Verdadeiro ou Falso")) {
            this.tipo = tipo;
        }
    }

    public void setAssunto(String assunto) {
        if (assunto != null && !assunto.trim().isEmpty()) { //se não for nulo ou vazio faça:
            this.assunto = assunto;
        }
    }
    
    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }

    public void setNivel(String nivel) {
        //validando se nivel é igual a qualquer variação de Facil,Medio e Dificil
        if (nivel.equalsIgnoreCase("Fácil") || nivel.equalsIgnoreCase("Médio") || nivel.equalsIgnoreCase("Difícil")) {
            this.nivel = nivel;
        }
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getTipo() {
        return tipo;
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

}
