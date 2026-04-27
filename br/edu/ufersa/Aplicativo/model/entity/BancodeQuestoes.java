package br.edu.ufersa.Aplicativo.model.entity; //Localização da classe

import java.util.ArrayList; //importa a interface ArrayList;
import java.util.List; //importa a interface List;

public class BancodeQuestoes {
    //atributos
    private List<Questao> questoes;

    //construtor
    public BancodeQuestoes() {
        this.questoes = new ArrayList<>(); //criando uma lista de questões
    }

    //metodos:
    public void colocarQuestao(Questao questao) {
        this.questoes.add(questao); //adiconando uma questao na lista de questao
    }

    public void excluirQuestao(String codigo) {
        this.questoes.removeIf(q -> q.getCodigo().equals(codigo));//excluindo uma questao pelo codigo
    }

    //metodo editar questao
    public void editarQuestao(String codigo, Questao questaoAtualizada) {
    int posicao = encontrarPosicaoQuestao(codigo); //pegando a posicao da questao dentro da lista 
    
    if (posicao != -1) { //se a posicao for diferente de -1(ou seja, dentro da lista)
        questoes.set(posicao, questaoAtualizada);//então coloque a questao nova na posição da antiga
        System.out.println("Questão editada com sucesso!"); 
    } else {
        System.out.println("Questão não encontrada");
    }
    }

    private int encontrarPosicaoQuestao(String codigo) { //encontrar em qual posição da lista esta a questao antiga
        for (int i = 0; i < questoes.size(); i++) { //loop enqunto i for menor que o tamanho da lista de questao
            if (questoes.get(i).getCodigo().equals(codigo)) { //subistitui o codigo da antiga pelo codigo da nova questao 
                return i;
        }
    }
        return -1;
    }

    public List<Questao> buscarPorDisciplina(String codigoDisciplina) {
        List<Questao> buscaDisciplina = new ArrayList<>(); //cria uma lista de questão que eu quero procurar

        for (Questao q : questoes) { //para cada questao dentro da minha lista de questões
            if (q.getDisciplina().getCodigo().equals(codigoDisciplina)) { //compara se o codigo 
            // da disciplina da questão é igual ao que tou procurando
                buscaDisciplina.add(q); //se for igual adicione na minha lista nova
            }
        }
        return buscaDisciplina;
    }

    public List<Questao> buscarPorAssunto(String assunto) {
        List<Questao> buscaAssunto = new ArrayList<>(); //cria uma nova lista

        for (Questao q : questoes) { 
            if (q.getAssunto().equalsIgnoreCase(assunto)) { //se o assunto da minha questao for igual ao meu assunto
                buscaAssunto.add(q); //adicione a minha lista de assunto
            }
        }
        return buscaAssunto;
    }

    public List<Questao> buscarPorDificuldade(String nivelDificuldade) {
        List<Questao> buscaNivel = new ArrayList<>(); //cria uma nova lista

        for (Questao q : questoes) {
            if (q.getNivel().equals(nivelDificuldade)) { //verificando igualdade dos niveis das
            //  questões com o que foi solicitado
                buscaNivel.add(q); //adicone a minha nova lista de niveis
            }
        }
        return buscaNivel;
    }
}