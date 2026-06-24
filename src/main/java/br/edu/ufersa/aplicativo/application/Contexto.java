package br.edu.ufersa.aplicativo.application;

import br.edu.ufersa.aplicativo.model.entities.Professor;
import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import br.edu.ufersa.aplicativo.model.entities.Questao;

public class Contexto {
    private static Professor professorLogado;
    private static DisciplinaInfo disciplinaSelecionada;
    private static Questao questaoSelecionada;
    private static String nomeDisciplinaSelecionada; // Novo campo

    public static void setProfessorLogado(Professor professorLogado) {
        if (professorLogado != null) {
            Contexto.professorLogado = professorLogado;
        } else {
            throw new IllegalArgumentException("Professor logado invalido");
        }
    }

    public static void setDisciplinaSelecionada(DisciplinaInfo disciplina) {
        if (disciplina != null) {
            Contexto.disciplinaSelecionada = disciplina;
            Contexto.nomeDisciplinaSelecionada = disciplina.getNome();
        } else {
            throw new IllegalArgumentException("Disciplina invalida");
        }
    }

    // Novo método: set apenas o nome da disciplina
    public static void setNomeDisciplinaSelecionada(String nome) {
        if (nome != null && !nome.isEmpty()) {
            Contexto.nomeDisciplinaSelecionada = nome;
            // Cria um DisciplinaInfo simplificado
            Contexto.disciplinaSelecionada = new DisciplinaInfo(nome, "", 0, "");
        } else {
            throw new IllegalArgumentException("Nome da disciplina invalido");
        }
    }

    public static void setQuestaoSelecionada(Questao questao) {
        if (questao != null) {
            Contexto.questaoSelecionada = questao;
        } else {
            throw new IllegalArgumentException("Questão inválida");
        }
    }

    public static DisciplinaInfo getDisciplinaSelecionada() {
        return disciplinaSelecionada;
    }

    public static String getNomeDisciplinaSelecionada() {
        return nomeDisciplinaSelecionada;
    }

    public static Professor getProfessorLogado() {
        return professorLogado;
    }

    public static Questao getQuestaoSelecionada() {
        return questaoSelecionada;
    }

    public static void limparQuestaoSelecionada() {
        questaoSelecionada = null;
    }

    public static void limpar() {
        professorLogado = null;
        disciplinaSelecionada = null;
        questaoSelecionada = null;
        nomeDisciplinaSelecionada = null;
    }
}