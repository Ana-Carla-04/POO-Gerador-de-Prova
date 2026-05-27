package br.edu.ufersa.aplicativo.model.entities;

import java.util.List;

public class MultiplaEscolha extends Questao {
    private List<String> alternativas;
    private String resposta;

    public MultiplaEscolha(
            int codigo, String enunciado, String assunto, Disciplina disciplina, Dificuldade dificuldade,
            List<String> alternativas, String resposta
    ) {
        super(codigo, enunciado, assunto, disciplina, dificuldade);
        setAlternativas(alternativas);
        setResposta(resposta);
    }

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

    public List<String> getAlternativas() {
        return alternativas;
    }

    public String getResposta() {
        return resposta;
    }
}
