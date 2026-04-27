package br.edu.ufersa.Aplicativo.model;

import java.util.List;

public class MultiplaEscolha extends Questao {
    private List<String> alternativas;
    private String resposta;

    public MultiplaEscolha(String codigo, String enunciado, String assunto, Disciplina disciplina, String nivel, List<String> alternativas, String resposta) {
        super(codigo, enunciado, assunto, disciplina, nivel);
        setAlternativas(alternativas);
        setResposta(resposta);
    }

    // Getters
    public List<String> getAlternativas() {
        return alternativas;
    }

    public String getResposta() {
        return resposta;
    }

    // Setters
    public void setAlternativas(List<String> alternativas) {
        if (alternativas != null && !alternativas.isEmpty()) {
            this.alternativas = alternativas;
        }
    }

    public void setResposta(String resposta) {
        if (resposta != null && !resposta.trim().isEmpty() && alternativas.contains(resposta)) {
            this.resposta = resposta;
        }
    }
}
