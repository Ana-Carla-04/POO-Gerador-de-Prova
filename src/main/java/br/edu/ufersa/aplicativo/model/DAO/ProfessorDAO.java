package br.edu.ufersa.aplicativo.model.DAO;

//esta classe que será feito a implementação dos metodos e como eles vão mexe no banco de dados  

import br.edu.ufersa.aplicativo.model.entities.Professor; //importa a classe Professor

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.LinkedList;

public class ProfessorDAO implements DAO<Professor> { //usou a interface para definou que não será mais tipo genérico e sim tipo Professor
    //inseri na tabela Professor do banco de dados:
    private static final String sql_inserir = "INSERT INTO professor (nome, email, senha) VALUES (?, ?, ?);"; //inseri nome, email e senha
    private static final String sql_alterar = "UPDATE professor SET nome = ?, email = ?, senha = ? WHERE id = ?;"; //altera o email e a senha baseado no nome
    private static final String sql_deletar = "DELETE FROM professor WHERE id = ?;"; //deleta baseado no nome
    private static final String sql_listar = "SELECT * FROM professor;"; //list a todos os professores

    private Connection conexao; //armazena a conexao com o banco de dados, como um atributo

    //construtor da classe usando o atributo de conexão
    public ProfessorDAO(Connection conexao) throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            this.conexao = conexao;
        } else throw new IllegalArgumentException("Conexão inválida");
    }

    @Override //subescrita do metodo da interface DAO
    public void inserir(Professor professor) throws SQLException {
        if (professor == null) {
            throw new IllegalArgumentException("Professor não pode ser nulo");
        }
        try (PreparedStatement ps =  conexao.prepareStatement(sql_inserir, Statement.RETURN_GENERATED_KEYS)) {
            // PreparedStatement prepara o "formulário" e faz sql_inserir ter ligação com o banco
            // preenche cada ? com os valores
            ps.setString(1, professor.getNome()); // 1º ? = nome
            ps.setString(2, professor.getEmail()); // 2º ? = email
            ps.setString(3, professor.getSenha()); // 3º ? = senha
            //Executa o comando já montado
            ps.executeUpdate();
            //id gerado pelo banco
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    professor.setId(idGerado);  // Coloca o ID no objeto
                } else throw new SQLException("Erro lendo o ID gerado");
            }
        }
    }

    @Override //sobrecrita do método da inteface
    public void deletar(Professor professor) throws SQLException {
        if (professor == null){
             throw new IllegalArgumentException("Professor não pode ser nulo");
        }
        try(PreparedStatement ps =  conexao.prepareStatement(sql_deletar)) {//cria o formulário e ja faz conexão sql_deletar com o banco de dados
            ps.setInt(1, professor.getId()); // 1º ? = Id
            int linhasAfetadas = ps.executeUpdate(); //executa o formulário
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma linha afetada");
            }
        }
    }

    @Override //sobrescrita do método da interface
    public void alterar(Professor professor) throws SQLException {
        if (professor == null){
            throw new IllegalArgumentException("Professor não pode ser nulo");
        }
        try(PreparedStatement ps = conexao.prepareStatement(sql_alterar)) { //cria um formulário e conecta sql_alterar com o banco de dados
            ps.setString(1, professor.getNome()); //1° ? = nome
            ps.setString(2, professor.getEmail()); //2° ? = email
            ps.setString(3, professor.getSenha()); // 3° ? = senha
            ps.setInt(4, professor.getId()); //4° ? = Id
            int linhasAlteradas = ps.executeUpdate(); //exacuta o formulario e manda pro banco
            if (linhasAlteradas == 0) {
                throw new SQLException("Nenhuma linha alterada");
            }
        }
    }

    @Override //sobrescrita do método da interface
    public List<Professor> listar() throws SQLException {
        try(Statement st = conexao.createStatement()) {  //Stantemente é usada quando não tem parâmetros, diferente de PreparedStatement
            try(ResultSet rs = st.executeQuery(sql_listar)) { //ResultSet é como se fosse uma Tabela virtual que contem os dados retornado pela exaução da query sql_listar
                LinkedList<Professor> professores = new LinkedList<>(); //cria uma lista vazia para armazenar os resultados
                while (rs.next()) { //.next avança pra proxima linha, e o while retorna falso quando não há mais linha
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome"); //pega o valor da coluna nome e transforma de sql para String
                    String email = rs.getString("email");  //pega o valor da coluna email e transforma de sql para String
                    String senha = rs.getString("senha");  //pega o valor da coluna senha e transforma de sql para String
                    Professor professor = new Professor(nome, email, senha); //chama o construtor de Professor e cria um objeto Professor com seus parâmetros
                    professor.setId(id);
                    professores.add(professor); //adiciona o objeto professor a lista de professores
                }
                return professores; //retorna a lista de professores
            }
        }
    }
}
