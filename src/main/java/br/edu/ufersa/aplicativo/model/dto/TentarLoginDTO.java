package br.edu.ufersa.aplicativo.model.dto;

public class TentarLoginDTO {
    private String email;
    private String senha;

    public TentarLoginDTO(String email, String senha) {
        setEmail(email);
        setSenha(senha);
    }

    public void setEmail(String email) {
        if (email != null && !email.isBlank()) this.email = email;
        else throw new IllegalArgumentException("Email invalido");
    }

    public void setSenha(String senha) {
        if (senha != null && !senha.isBlank()) this.senha = senha;
        else throw new IllegalArgumentException("Senha invalida");
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
