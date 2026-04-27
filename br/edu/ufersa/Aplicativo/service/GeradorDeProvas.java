package br.edu.ufersa.Aplicativo.service;

import br.edu.ufersa.Aplicativo.model.entity.BancodeQuestoes;
import br.edu.ufersa.Aplicativo.model.entity.Prova;
import br.edu.ufersa.Aplicativo.model.entity.Questao;
import br.edu.ufersa.Aplicativo.model.entity.Disciplina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeradorDeProvas {
        private BancodeQuestoes repositorio;

        public GeradorDeProvas(BancodeQuestoes repositorio) {
                this.repositorio = repositorio;
        }

        public Prova criarProva(Disciplina disciplina, int qtdFaceis, int qtdMedias, int qtdDificeis) {
                List<Questao> questoesFaceis = buscareshuffle(disciplina, "Fácil", qtdFaceis);
                List<Questao> questoesMedias = buscareshuffle(disciplina, "Médio", qtdMedias);
                List<Questao> questoesDificeis = buscareshuffle(disciplina, "Difícil", qtdDificeis);
                List<Questao> todasAsQuestoesDaProva = new ArrayList<>();
                todasAsQuestoesDaProva.addAll(questoesFaceis);
                todasAsQuestoesDaProva.addAll(questoesMedias);
                todasAsQuestoesDaProva.addAll(questoesDificeis);

                Prova novaProva = new Prova(todasAsQuestoesDaProva, disciplina);

                return novaProva;
        }

        private List<Questao> buscareshuffle(Disciplina disciplina, String dificuldade, int quantidadeDesejada) {
                if (quantidadeDesejada <= 0) {
                        return new ArrayList<>();
                }

                List<Questao> todasDaDificuldade = repositorio.buscarPorDificuldade(dificuldade);

                List<Questao> questoesFiltradas = new ArrayList<>();

                for (Questao q : todasDaDificuldade) {
                        if (q.getDisciplina().getCodigo().equals(disciplina.getCodigo())) {
                                questoesFiltradas.add(q);
                        }
                }

                if (questoesFiltradas.size() < quantidadeDesejada) {
                        System.out.println("Não tem questões suficientes!");
                }

                Collections.shuffle(questoesFiltradas);
                List<Questao> questoesSorteadas = questoesFiltradas.subList(0, quantidadeDesejada);

                return new ArrayList<>(questoesSorteadas);
        }
}