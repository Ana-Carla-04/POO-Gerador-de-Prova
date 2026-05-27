package br.edu.ufersa.Aplicativo.model.entity; //localização da classe

public class Discursiva extends Questao{
    //atributos de Questao mais os da subclasse Discursiva
    private String resposta;

    //construtor
    public Discursiva(String codigo, String enunciado,String tipo, String assunto, Disciplina disciplina, String nivel, String resposta) {
        super(codigo, enunciado,tipo, assunto, disciplina, nivel);
        setResposta(resposta);
    }

    //setters
    public void setResposta(String resposta){
        if(resposta != null && !(resposta.trim().isEmpty())){
            this.resposta = resposta;
        }
    }

    //getters
    public String getResposta(){
        return resposta;
    }
}
