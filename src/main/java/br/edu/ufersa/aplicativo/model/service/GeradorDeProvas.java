package br.edu.ufersa.aplicativo.model.service;

import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO;
import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeradorDeProvas {
        private QuestaoDAO repositorio;

        public GeradorDeProvas(QuestaoDAO repositorio) {
                this.repositorio = repositorio;
        }

        public Prova criarProva(Disciplina disciplina, Nivel nivel, int qtdFaceis, int qtdMedias, int qtdDificeis, String codigo) throws SQLException {

                List<Questao> questoesFaceis = buscareshuffle(disciplina, Nivel.FACIL, qtdFaceis);
                List<Questao> questoesMedias = buscareshuffle(disciplina, Nivel.MEDIO, qtdMedias);
                List<Questao> questoesDificeis = buscareshuffle(disciplina, Nivel.DIFICIL, qtdDificeis);

                List<Questao> todasAsQuestoesDaProva = new ArrayList<>();
                todasAsQuestoesDaProva.addAll(questoesFaceis);
                todasAsQuestoesDaProva.addAll(questoesMedias);
                todasAsQuestoesDaProva.addAll(questoesDificeis);

                return new Prova(todasAsQuestoesDaProva, disciplina, codigo);
        }

        private List<Questao> buscareshuffle(Disciplina disciplina, Nivel nivel, int quantidadeDesejada) throws SQLException {
                if (quantidadeDesejada <= 0) {
                        return new ArrayList<>();
                }

                List<Questao> todasDoNivel = repositorio.buscarPorNivel(nivel);

                List<Questao> questoesFiltradas = new ArrayList<>();

                for (Questao q : todasDoNivel) {
                        if (q.getDisciplina().getCodigo().equals(disciplina.getCodigo())) {
                                questoesFiltradas.add(q);
                        }
                }

                int quantidadeDisponivel = questoesFiltradas.size();
                if (quantidadeDisponivel < quantidadeDesejada) {
                        System.out.println("Aviso: Não há questões suficientes de nível '" + nivel + "' para a disciplina " + disciplina.getNome());
                        System.out.println("Quantidade solicitada: " + quantidadeDesejada + " | Quantidade disponível no banco: " + quantidadeDisponivel);

                        quantidadeDesejada = quantidadeDisponivel;
                }

                if (quantidadeDesejada == 0) {
                        return new ArrayList<>();
                }

                Collections.shuffle(questoesFiltradas);

                List<Questao> questoesSorteadas = questoesFiltradas.subList(0, quantidadeDesejada);

                return new ArrayList<>(questoesSorteadas);
        }
}