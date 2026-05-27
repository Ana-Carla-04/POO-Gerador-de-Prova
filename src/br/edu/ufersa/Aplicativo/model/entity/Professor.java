package br.edu.ufersa.Aplicativo.model.entity; //localização do pacote

public class Professor {
    //atributos
    private String nome;
    private String email;
    private String senha;
    //private List<Disciplina> disciplinas;

    //construtor
    public Professor(String nome, String email, String senha) {
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        //setDisciplinas(new ArrayList<>());
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


    // public void adicionarDisciplinanoProfessor(Disciplina disciplina) {
    //     if (disciplina != null && !this.disciplinas.contains(disciplina)) {
    //         this.disciplinas.add(disciplina);
    //     }
    // }
    // public List<Disciplina> getDisciplinas() {
    //     return disciplinas;
    // }


}