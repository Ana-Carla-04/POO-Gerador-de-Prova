package br.edu.ufersa.aplicativo.application;

import br.edu.ufersa.aplicativo.model.entities.Professor;
import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;

public class Contexto {
    private static Professor professorLogado;
    private static DisciplinaInfo disciplinaSelecionada;

    public static void setProfessorLogado(Professor professorLogado) {
        if (professorLogado != null) Contexto.professorLogado = professorLogado;
        else throw new IllegalArgumentException("Professor logado invalido");
    }

    public static void setDisciplinaSelecionada(DisciplinaInfo disciplina) {
        if (disciplina != null)  Contexto.disciplinaSelecionada = disciplina;
        else throw new IllegalArgumentException("Disciplina invalida");
    }

    public static DisciplinaInfo getDisciplinaSelecionada() {
        return disciplinaSelecionada;
    }
}
