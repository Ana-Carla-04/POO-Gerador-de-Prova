package br.edu.ufersa.aplicativo.model.service;

import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO; // Corrigido o import
import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeradorDeProvas {
        private QuestaoDAO questaoDAO;

        public GeradorDeProvas(QuestaoDAO questaoDAO) {
                this.questaoDAO = questaoDAO;
        }

        public Prova criarProva(Disciplina disciplina,
                                int qtdFaceis, int qtdMedias, int qtdDificeis,
                                String codigo) throws SQLException {

                // Removido o parâmetro Nivel nivel que não era usado
                List<Questao> questoesFaceis = buscarQuestoesPorNivel(disciplina, Nivel.FACIL, qtdFaceis);
                List<Questao> questoesMedias = buscarQuestoesPorNivel(disciplina, Nivel.MEDIO, qtdMedias);
                List<Questao> questoesDificeis = buscarQuestoesPorNivel(disciplina, Nivel.DIFICIL, qtdDificeis);

                List<Questao> todasAsQuestoesDaProva = new ArrayList<>();
                todasAsQuestoesDaProva.addAll(questoesFaceis);
                todasAsQuestoesDaProva.addAll(questoesMedias);
                todasAsQuestoesDaProva.addAll(questoesDificeis);

                if (todasAsQuestoesDaProva.isEmpty()) {
                        throw new IllegalStateException("Não foi possível criar a prova: nenhuma questão disponível.");
                }

                return new Prova(todasAsQuestoesDaProva, disciplina, codigo);
        }

        private List<Questao> buscarQuestoesPorNivel(Disciplina disciplina, Nivel nivel, int quantidadeDesejada) throws SQLException {
                if (quantidadeDesejada <= 0) {
                        return new ArrayList<>();
                }

                // Buscar todas as questões do nível
                List<Questao> todasDoNivel = questaoDAO.buscarPorNivel(nivel);

                // Filtrar por disciplina
                List<Questao> questoesFiltradas = new ArrayList<>();
                for (Questao q : todasDoNivel) {
                        if (q.getDisciplina().getCodigo().equals(disciplina.getCodigo())) {
                                questoesFiltradas.add(q);
                        }
                }

                int quantidadeDisponivel = questoesFiltradas.size();
                if (quantidadeDisponivel == 0) {
                        System.out.println("Aviso: Nenhuma questão de nível '" + nivel +
                                "' disponível para a disciplina " + disciplina.getNome());
                        return new ArrayList<>();
                }

                if (quantidadeDisponivel < quantidadeDesejada) {
                        System.out.println("Aviso: Não há questões suficientes de nível '" + nivel +
                                "' para a disciplina " + disciplina.getNome());
                        System.out.println("Solicitado: " + quantidadeDesejada +
                                " | Disponível: " + quantidadeDisponivel);
                        quantidadeDesejada = quantidadeDisponivel;
                }

                Collections.shuffle(questoesFiltradas);
                return new ArrayList<>(questoesFiltradas.subList(0, quantidadeDesejada));
        }
}