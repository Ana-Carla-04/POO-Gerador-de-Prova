package br.edu.ufersa.Aplicativo.model;

import java.util.ArrayList;
import java.util.List;

public class BancodeQuestoes {

    private List<Questao> questoes;

    public BancodeQuestoes() {
        this.questoes = new ArrayList<>();
    }
    public void colocarQuestao(Questao questao) {
        this.questoes.add(questao);
    }

    public void excluirQuestao(String codigo) {
        this.questoes.removeIf(q -> q.getCodigo().equals(codigo));
    }

    public void editarQuestao(String codigo, Questao questaoAtualizada) {
        for (int i = 0; i < questoes.size(); i++) {
            if (questoes.get(i).getCodigo().equals(codigo)) {
                questoes.set(i, questaoAtualizada);
                System.out.println("Questão editada");
            }
        }

        System.out.println("Questão não encontrada");

    }

    public List<Questao> buscarPorDisciplina(String codigoDisciplina) {
        List<Questao> buscaDisciplina = new ArrayList<>();

        for (Questao q : questoes) {
            if (q.getDisciplina().getCodigo().equals(codigoDisciplina)) {
                buscaDisciplina.add(q);
            }
        }
        return buscaDisciplina;
    }

    public List<Questao> buscarPorAssunto(String assunto) {
        List<Questao> buscaAssunto = new ArrayList<>();

        for (Questao q : questoes) {
            if (q.getAssunto().equalsIgnoreCase(assunto)) {
                buscaAssunto.add(q);
            }
        }
        return buscaAssunto;
    }

    public List<Questao> buscarPorDificuldade(String nivelDificuldade) {
        List<Questao> buscaNivel = new ArrayList<>();

        for (Questao q : questoes) {
            if (q.getNivel().equals(nivelDificuldade)) {
                buscaNivel.add(q);
            }
        }
        return buscaNivel;
    }
}