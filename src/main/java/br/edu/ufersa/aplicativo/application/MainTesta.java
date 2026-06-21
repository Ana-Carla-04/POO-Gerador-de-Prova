package br.edu.ufersa.aplicativo.application;

import br.edu.ufersa.aplicativo.model.DAO.*;
import br.edu.ufersa.aplicativo.model.entities.*;
import br.edu.ufersa.aplicativo.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainTesta {
    public static void main(String[] args) {
    // Main para testar criação das classes Professor, Disciplina, Questão e Prova implementados no bd
        try (Connection conexao = Conexao.abrirConexao()) {

            System.out.println("\nLimpando a database");
            limparBancoDeDados(conexao);
            System.out.println("Banco de dados limpo e IDs reiniciados!");

            ProfessorDAO professorDAO = new ProfessorDAO(conexao);
            DisciplinaDAO disciplinaDAO = new DisciplinaDAO(conexao);
            QuestaoDAO questaoDAO = new QuestaoDAO(conexao);
            ProvaDAO provaDAO = new ProvaDAO(conexao);

            Professor prof = new Professor("Gadelha", "gadelha@ufersa.edu.br", "poomelhormateria");
            professorDAO.inserir(prof);

            Disciplina disciplinaTeste = new Disciplina("Programação Orientada a Objetos", "POO-2026");
            disciplinaTeste.setProfessor(prof);
            disciplinaDAO.inserir(disciplinaTeste);

            System.out.println("\nCriando e colocando no banco 5 questões (Múltipla Escolha, Discursiva e V ou F)");

            List<String> opc1 = new ArrayList<>();
            opc1.add("A) Encapsulamento"); opc1.add("B) Compilação"); opc1.add("C) Herança"); opc1.add("D) Polimorfismo");
            MultiplaEscolha q1 = new MultiplaEscolha(0, "Qual NÃO é um pilar da POO?", "Pilares", disciplinaTeste, Nivel.MEDIO, opc1, "B) Compilação");
            questaoDAO.inserir(q1);

            Discursiva q2 = new Discursiva();
            q2.setEnunciado("Explique o conceito de herança e como ela ajuda na reutilização de código.");
            q2.setAssunto("Herança"); q2.setNivel(Nivel.FACIL); q2.setDisciplina(disciplinaTeste);
            questaoDAO.inserir(q2);

            List<String> opc3 = new ArrayList<>();
            opc3.add("A) public"); opc3.add("B) private"); opc3.add("C) protected"); opc3.add("D) package");
            MultiplaEscolha q3 = new MultiplaEscolha(0, "Qual modificador restringe o acesso apenas à própria classe?", "Modificadores", disciplinaTeste, Nivel.FACIL, opc3, "B) private");
            questaoDAO.inserir(q3);

            VerdadeiroFalso q4 = new VerdadeiroFalso(0, "Sobre os conceitos gerais de Java, julgue as afirmações abaixo:", "Conceitos Java", disciplinaTeste, Nivel.MEDIO);
            q4.adicionarAlternativa("Java permite herança múltipla de classes.", false);
            q4.adicionarAlternativa("Uma classe abstrata não pode ser instanciada diretamente.", true);
            q4.adicionarAlternativa("O modificador 'final' impede que uma classe seja herdada.", true);
            q4.adicionarAlternativa("Interfaces podem conter assinaturas de métodos abstratos.", true);
            questaoDAO.inserir(q4);

            Discursiva q5 = new Discursiva();
            q5.setEnunciado("Diferencie sobrecarga (overloading) de sobrescrita (overriding) de métodos.");
            q5.setAssunto("Polimorfismo"); q5.setNivel(Nivel.DIFICIL); q5.setDisciplina(disciplinaTeste);
            questaoDAO.inserir(q5);

            List<Questao> listaBaseQuestoes = new ArrayList<>();
            listaBaseQuestoes.add(q1); listaBaseQuestoes.add(q2); listaBaseQuestoes.add(q3); listaBaseQuestoes.add(q4); listaBaseQuestoes.add(q5);


            System.out.println("\nCriando a PROVA A");
            List<Questao> questoesProvaA = new ArrayList<>(listaBaseQuestoes);
            Collections.shuffle(questoesProvaA);

            Prova provaA = new Prova(questoesProvaA, disciplinaTeste, "PROVA-VERSAO-A");
            provaA.setInstituicao("UFERSA");
            provaDAO.inserir(provaA);


            System.out.println("\nCriando a PROVA B");
            List<Questao> questoesProvaB = new ArrayList<>(listaBaseQuestoes);

            // Força a ordem a ser obrigatoriamente diferente da Prova A
            while (questoesProvaB.equals(questoesProvaA)) {
                Collections.shuffle(questoesProvaB); // 🔥 Embaralha para a Prova B
            }

            Prova provaB = new Prova(questoesProvaB, disciplinaTeste, "PROVA-VERSAO-B");
            provaB.setInstituicao("UFERSA");
            provaDAO.inserir(provaB);


            System.out.println("\nExibindo provas");

            System.out.println("\n=== PROVA A ===");
            for (int i = 0; i < questoesProvaA.size(); i++) {
                System.out.println((i + 1) + "ª Questão - ID Questão: " + questoesProvaA.get(i).getCodigo() + " | Tipo: " + questoesProvaA.get(i).getClass().getSimpleName() + " | Assunto: " + questoesProvaA.get(i).getAssunto());
            }

            System.out.println("\n=== PROVA B ===");
            for (int i = 0; i < questoesProvaB.size(); i++) {
                System.out.println((i + 1) + "ª Questão - ID Questão: " + questoesProvaB.get(i).getCodigo() + " | Tipo: " + questoesProvaB.get(i).getClass().getSimpleName() + " | Assunto: " + questoesProvaB.get(i).getAssunto());
            }

        } catch (SQLException e) {
            System.err.println("\n[ERRO DE BANCO]:");
            e.printStackTrace();
        }
    }

    private static void limparBancoDeDados(Connection conexao) throws SQLException {
        String[] tabelas = {"prova_questao", "alternativa", "prova", "assunto", "questao", "disciplina", "professor"};
        try (PreparedStatement pstDisable = conexao.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
            pstDisable.executeUpdate();
        }
        for (String tabela : tabelas) {
            try (PreparedStatement pstTruncate = conexao.prepareStatement("TRUNCATE TABLE " + tabela)) {
                pstTruncate.executeUpdate();
            }
        }
        try (PreparedStatement pstEnable = conexao.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
            pstEnable.executeUpdate();
        }
    }
}