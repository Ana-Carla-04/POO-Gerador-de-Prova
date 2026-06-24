package br.edu.ufersa.aplicativo.model.dto;

public class CadastroDTO {
    private String nome;
    private String email;
    private String senha;

    public CadastroDTO(String nome, String email, String senha) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    public void setNome(String nome) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        else throw new IllegalArgumentException("Nome invalido");
    }

    public void setEmail(String email) {
        if (email != null && !email.isBlank()) this.email = email;
        else throw new IllegalArgumentException("Email invalido");
    }

    public void setSenha(String senha) {
        if (senha != null && !senha.isBlank()) this.senha = senha;
        else throw new IllegalArgumentException("Senha invalido");
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
