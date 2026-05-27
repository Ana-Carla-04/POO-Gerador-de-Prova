package br.edu.ufersa.aplicativo.model.entities;

public class VerdadeiroFalso extends Questao {
    private boolean resposta;

    public VerdadeiroFalso(
            int codigo, String enunciado, String assunto, Disciplina disciplina, Dificuldade dificuldade, String resposta
    ) {
        super(codigo, enunciado, assunto, disciplina, dificuldade);
        setResposta(resposta);
    }

    public void setResposta(String resposta) {
        if (resposta != null && !resposta.trim().isEmpty()) {
            switch (resposta.toLowerCase()) {
                case "true":
                case "verdadeiro":
                case "v":
                    this.resposta = true;
                    break;
                case "false":
                case "falso":
                case "f":
                    this.resposta = false;
                    break;
            }
        }
    }

    public String getResposta(){
        if (resposta) {
            return "true";
        } else {
            return "false";
        }
    }
}
