package br.edu.ufersa.Aplicativo.model.entity;

public class VerdadeiroFalso extends Questao{
    //atributos de Questao e de VerdadeiroFalso
    private String resposta; //V ou F

    //construtor
    public VerdadeiroFalso(String codigo, String enunciado,String tipo, String assunto, Disciplina disciplina, String nivel, String resposta) {
        super(codigo, enunciado,tipo, assunto, disciplina, nivel);
        setResposta(resposta);
    }

    //setters
    public void setResposta(String resposta){
        if(resposta.equalsIgnoreCase("Verdadeiro") || resposta.equalsIgnoreCase("Falso")){
            this.resposta = resposta;
        }
    }

    //getters
    public String getResposta(){
        return resposta;
    }
    
}
