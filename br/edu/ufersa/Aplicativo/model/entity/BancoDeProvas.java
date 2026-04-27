package br.edu.ufersa.Aplicativo.model.entity; //local que esta a classe

import java.util.ArrayList; //importa a interface ArrayList
import java.util.List;  //importa a interface List

public class BancoDeProvas {
    //atributos
    private List<Prova> provas; 

    //construtor
    public BancoDeProvas() {
        this.provas = new ArrayList<>(); //criando uma lista de provas
    }

    //metodos:
    
    // adicionar prova
    public void colocarProva(Prova prova) {
        if (prova != null) { //se não for nulo
            this.provas.add(prova); //addicionou a prova a lista de provas
            System.out.println("Prova adicionada com sucesso!");
        } else {
            System.out.println("Erro: Prova não pode ser nula!");
        }
    }
    
    // excluir prova por código 
    public void excluirProva(String codigo) {
        boolean removida = this.provas.removeIf(p -> p.getCodigo().equals(codigo));
        
        if (removida) {
            System.out.println("Prova com código " + codigo + " excluída com sucesso!");
        } else {
            System.out.println("Prova com código " + codigo + " não encontrada!");
        }
    }
    
    // editar prova por código
    public void editarProva(String codigo, Prova provaAtualizada) {
        int posicao = encontrarPosicaoProva(codigo);
        
        if (posicao != -1) {
            provas.set(posicao, provaAtualizada);
            System.out.println("Prova editada com sucesso!");
        } else {
            System.out.println("Prova com código " + codigo + " não encontrada!");
        }
    }
    
    //encontrar posição pelo código
    private int encontrarPosicaoProva(String codigo) {
        for (int i = 0; i < provas.size(); i++) {
            if (provas.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
    }
    
    // buscar prova por código (retorna uma única prova, pq código é único)
    public Prova buscarProvaPorCodigo(String codigo) {
        for (Prova p : provas) {
            if (p.getCodigo().equals(codigo)) {
                return p;
            }
        }
        System.out.println("Prova com código " + codigo + " não encontrada!");
        return null;
    }

    // buscar provas por disciplina (retorna lista)
    public List<Prova> buscarProvasPorDisciplina(String codigoDisciplina) {
        List<Prova> provasEncontradas = new ArrayList<>();
        
        for (Prova p : provas) {
            if (p.getDisciplina().getCodigo().equals(codigoDisciplina)) {
                provasEncontradas.add(p);
            }
        }
        
        return provasEncontradas;
    }

     //  listar todas as provas
    public void listarTodasProvas() {
        if (provas.isEmpty()) {
            System.out.println("Nenhuma prova cadastrada!");
            return;
        }
        
        System.out.println("=== LISTA DE PROVAS ===");
        for (Prova p : provas) {
            System.out.println("Código: " + p.getCodigo() + 
                               " | Disciplina: " + p.getDisciplina().getNome() + 
                               " | Data: " + p.getDataDeCriacao() +
                               " | Questões: " + p.getQuestoes().size());
        }
    }
}