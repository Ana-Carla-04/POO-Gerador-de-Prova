package br.edu.ufersa.aplicativo.model.entities;

public class Professor {
    private int id;
    private String nome;
    private String email;
    private String senha;

    //construtor
    public Professor(int id, String nome, String email, String senha) {
        setId(id);
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    public Professor(String nome, String email, String senha) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    public Professor() {

    }

    //setters
     public void setId(int id) {
        if (id > 0) {  // IDs devem ser positivos
            this.id = id;
        }
    }
    
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
    public int getId() {
        return id;
    }
    
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
