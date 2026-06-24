package br.edu.ufersa.aplicativo.model.entities;

public enum Nivel {
    FACIL(1),
    MEDIO(2),
    DIFICIL(3);

    private int valor;

    Nivel(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static Nivel deInt(int valorBanco) {
        for (Nivel n : Nivel.values()) {
            if (n.getValor() == valorBanco) {
                return n;
            }
        }

        throw new IllegalArgumentException("Nível não existe no banco: " + valorBanco);
    }

    public String getDescricaoTela() {
        switch (this) {
            case FACIL:
                return "Fácil";
            case MEDIO:
                return "Médio";
            case DIFICIL:
                return "Difícil";
            default:
                return "Indefinido";
        }
    }
}
