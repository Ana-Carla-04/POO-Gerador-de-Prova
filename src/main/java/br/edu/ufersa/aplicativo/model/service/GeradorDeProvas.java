package br.edu.ufersa.aplicativo.model.service;

import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeradorDeProvas {
        private BancoDeQuestoes repositorio;

        public GeradorDeProvas(BancoDeQuestoes repositorio) {
                this.repositorio = repositorio;
        }

        // metodo para criar prova com os atributos informados no sistema
        public Prova criarProva(Disciplina disciplina, int qtdFaceis, int qtdMedias, int qtdDificeis, String codigo) {
                // declaração de lista com atribuição das questões ao chamar o metodo de buscar e embaralhar as questões
                List<Questao> questoesFaceis = buscareshuffle(disciplina, "Fácil", qtdFaceis);
                List<Questao> questoesMedias = buscareshuffle(disciplina, "Médio", qtdMedias);
                List<Questao> questoesDificeis = buscareshuffle(disciplina, "Difícil", qtdDificeis);

                // reunir todas as questões embaralhadas em uma lista de questões que vai pra prova
                List<Questao> todasAsQuestoesDaProva = new ArrayList<>();
                todasAsQuestoesDaProva.addAll(questoesFaceis);
                todasAsQuestoesDaProva.addAll(questoesMedias);
                todasAsQuestoesDaProva.addAll(questoesDificeis);

                // criação da prova com todas as questões
                Prova novaProva = new Prova(todasAsQuestoesDaProva, disciplina, codigo);

                return novaProva;
        }

        private List<Questao> buscareshuffle(Disciplina disciplina, String dificuldade, int quantidadeDesejada) {
                // se pedir a quantidade 0 ou negativo retorna lista vazia
                if (quantidadeDesejada <= 0) {
                        return new ArrayList<>();
                }

                // busca de questões na dificuldade pedida
                List<Questao> todasDaDificuldade = repositorio.buscarPorDificuldade(dificuldade);

                // lista para guardar as questões da disciplina escolhida
                List<Questao> questoesFiltradas = new ArrayList<>();

                // filtro para escolher somente as questões da disciplina escolhida
                for (Questao q : todasDaDificuldade) {
                        // compara o codigo da disciplina
                        if (q.getDisciplina().getCodigo().equals(disciplina.getCodigo())) {
                                questoesFiltradas.add(q);
                        }
                }

                // aviso para caso as questões não forem o suficiente
                if (questoesFiltradas.size() < quantidadeDesejada) {
                        System.out.println("Não tem questões suficientes!");
                }

                // embaralhar as questões
                Collections.shuffle(questoesFiltradas);

                // pega as questões na ordem do sorteio
                List<Questao> questoesSorteadas = questoesFiltradas.subList(0, quantidadeDesejada);

                return new ArrayList<>(questoesSorteadas);
        }
}