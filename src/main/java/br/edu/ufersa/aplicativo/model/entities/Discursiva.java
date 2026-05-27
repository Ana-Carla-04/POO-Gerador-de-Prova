package br.edu.ufersa.aplicativo.model.entities;

public class Discursiva extends Questao {
    private String resposta;

    public Discursiva(
            int codigo, String enunciado, String assunto, Disciplina disciplina, Dificuldade dificuldade, String resposta
    ) {
        super(codigo, enunciado, assunto, disciplina, dificuldade);
        setResposta(resposta);
    }

    public void setResposta(String resposta){
        if(resposta != null && !(resposta.trim().isEmpty())){
            this.resposta = resposta;
        }
    }

    public String getResposta(){
        return resposta;
    }
}
