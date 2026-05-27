package br.edu.ufersa.Aplicativo.model.entity; //local da classe

import java.util.List;//importando a interface List

public class MultiplaEscolha extends Questao {
    //atributos de Questão e mais os da subclasse MultiplaEscolha
    private List<String> alternativas;
    private String resposta;
    
    //construtor
    public MultiplaEscolha(String codigo, String enunciado,String tipo, String assunto, Disciplina disciplina, String nivel, String resposta, List<String> alternativas) {
        super(codigo, enunciado,tipo, assunto, disciplina, nivel);
        setAlternativas(alternativas);
        setResposta(resposta);
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
    // Getters
    public List<String> getAlternativas() {
        return alternativas;
    }

    public String getResposta() {
        return resposta;
    }

}
