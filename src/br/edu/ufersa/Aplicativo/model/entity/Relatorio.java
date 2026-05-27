package br.edu.ufersa.Aplicativo.model.entity; //localização da classe

import java.util.List; //importa a interface List

class Relatorio{
    //atributos
    private List<Prova> provas;

    //setters
    public void setProvas(List<Prova> provas){
        if(provas != null){
            this.provas = provas;
        }
    }

    //getters
    public List<Prova> getProvas(){
        return provas;
    }

}