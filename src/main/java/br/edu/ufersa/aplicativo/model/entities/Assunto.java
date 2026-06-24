package br.edu.ufersa.aplicativo.model.entities;

public class Assunto {
    private int id;
    private String assunto;

    public Assunto(int id, String assunto) {
        setId(id);
        setAssunto(assunto);
    }

    public int getId() {
        return id;
    }

    public String getAssunto() {
        return assunto;
    }

    public void  setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public void setAssunto(String assunto) {
        if (assunto != null && !assunto.trim().isEmpty()) {
            this.assunto = assunto;
        }
    }
}
