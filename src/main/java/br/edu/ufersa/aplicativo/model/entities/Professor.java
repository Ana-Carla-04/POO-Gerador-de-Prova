package br.edu.ufersa.aplicativo.model.entities;

public class Professor {
    private String nome;
    private String email;
    private String senha;

    //construtor
    public Professor(String nome, String email, String senha) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    //setters
    public void setNome(String nome) {
        if(nome != null && !(nome.trim().isEmpty())){
            this.nome = nome;
        }
    }
    public void setEmail(String email) {
        if(email != null && !(email.trim().isEmpty())){
            this.email = email;
        }
    }
    public void setSenha(String senha) {
        if(senha != null && !(senha.trim().isEmpty())){
            this.senha = senha;
        }
    }


    //getters
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
