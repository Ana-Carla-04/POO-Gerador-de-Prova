package br.edu.ufersa.Aplicativo.model.entity;

import java.time.LocalDate;
import java.util.List;

public class Prova {
    private LocalDate dataDeCriacao;
    private List<Questao> questoes;
    private Disciplina disciplina;

    public Prova(List<Questao> questoes, Disciplina disciplina) {
        setDataDeCriacao(LocalDate.now());
        setQuestoes(questoes);
        setDisciplina(disciplina);
    }

    // Getters
    public LocalDate getDataDeCriacao() {
        return dataDeCriacao;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    // Setters
    public void setDataDeCriacao(LocalDate dataDeCriacao) {
        if (dataDeCriacao != null) {
            this.dataDeCriacao = dataDeCriacao;
        }
    }

    public void setQuestoes(List<Questao> questoes) {
        if (!questoes.isEmpty()) {
            this.questoes = questoes;
        }
    }

    public void setDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            this.disciplina = disciplina;
        }
    }
}
