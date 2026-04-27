package br.edu.ufersa.Aplicativo.model.entity;

import java.util.List;

class Relatorio{
    private List<Prova> provas;

    
    public void setProvas(List<Prova> provas){
        if(provas != null){
            this.provas = provas;
        }
    }
    public List<Prova> getProvas(){
        return provas;
    }

}