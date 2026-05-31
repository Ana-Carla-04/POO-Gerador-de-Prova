package br.edu.ufersa.aplicativo.model.entities;

import java.util.ArrayList;
import java.util.List;

public class VerdadeiroFalso extends Questao {
    private List<String> alternativas;
    private List<Boolean> respostas;

    public VerdadeiroFalso(int codigo, String enunciado, String assunto, Disciplina disciplina, Nivel nivel) {
        super(codigo, enunciado, assunto, disciplina, nivel);
        this.alternativas = new ArrayList<>();
        this.respostas = new ArrayList<>();
    }

    // Método para você usar na Main para montar a questão linha por linha
    public void adicionarAlternativa(String texto, boolean Verdadeira) {
        this.alternativas.add(texto);
        this.respostas.add(Verdadeira);
    }

    // O Java exige este método por causa da classe Questao.
    // Ele pega a sua lista interna e transforma na String que o Java espera.
    @Override
    public String getResposta() {
        if (respostas.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < respostas.size(); i++) {
            sb.append(respostas.get(i) ? "V" : "F");
            if (i < respostas.size() - 1) {
                sb.append("-"); // Junta os valores separados por hífen: V-F-V
            }
        }
        return sb.toString();
    }

    // O Java exige este método por causa da classe Questao.
    // Se você passar "V-F-V", ele quebra o texto e preenche a sua lista automaticamente.
    @Override
    public void setResposta(String resposta) {
        if (resposta != null && !resposta.trim().isEmpty()) {
            this.respostas.clear();
            String[] partes = resposta.toUpperCase().split("[- ,]");
            for (String parte : partes) {
                this.respostas.add(parte.equals("V") || parte.equals("TRUE") || parte.equals("VERDADEIRO"));
            }
        }
    }

    // Getters normais para o seu QuestaoDAO conseguir ler as listas na hora de salvar no banco
    public List<String> getAlternativas() {
        return alternativas;
    }

    public List<Boolean> getRespostas() {
        return respostas;
    }
}