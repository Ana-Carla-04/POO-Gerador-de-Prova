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

    public VerdadeiroFalso() {
        this.alternativas = new ArrayList<>();
        this.respostas = new ArrayList<>();
    };

    public void adicionarAlternativa(String texto, boolean Verdadeira) {
        this.alternativas.add(texto);
        this.respostas.add(Verdadeira);
    }

    @Override
    public String getResposta() {
        if (respostas.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < respostas.size(); i++) {
            sb.append(respostas.get(i) ? "V" : "F");
            if (i < respostas.size() - 1) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

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

    public List<String> getAlternativas() {
        return alternativas;
    }

    public List<Boolean> getRespostas() {
        return respostas;
    }
}