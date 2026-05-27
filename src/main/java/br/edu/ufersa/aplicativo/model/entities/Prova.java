package br.edu.ufersa.aplicativo.model.entities;

import java.time.LocalDate;
import java.util.List;

public class Prova {
    private String codigo;
    private String instituicao;
    private LocalDate dataDeCriacao;
    private List<Questao> questoes;
    private Disciplina disciplina;

    public Prova(List<Questao> questoes, Disciplina disciplina,String codigo) {
        setDataDeCriacao(LocalDate.now());
        setQuestoes(questoes);
        setDisciplina(disciplina);
        setCodigo(codigo);
    }

    public void setCodigo(String codigo){
        if(codigo != null && !codigo.trim().isEmpty()){
            this.codigo = codigo;
        }
    }

    public void setInstituicao(String instituicao){
        if(instituicao != null && !instituicao.trim().isEmpty()){
            this.instituicao = instituicao;
        }
    }

    public void setDataDeCriacao(LocalDate dataDeCriacao) {
        if (dataDeCriacao != null) {
            this.dataDeCriacao = dataDeCriacao;
        }
    }

    public void setQuestoes(List<Questao> questoes) {
        if (questoes != null && !questoes.isEmpty()) {
            this.questoes = questoes;
        }
    }

    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }

    public String getCodigo(){
        return codigo;
    }

    public String getInstituicao(){
        return instituicao;
    }

    public LocalDate getDataDeCriacao() {
        return dataDeCriacao;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

}
