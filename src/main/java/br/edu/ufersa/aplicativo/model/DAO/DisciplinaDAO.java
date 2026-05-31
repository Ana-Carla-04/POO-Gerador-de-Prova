package br.edu.ufersa.aplicativo.model.DAO;


import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.entities.Professor;
import java.sql.*; //import tudo do sql
import java.util.*;

public class DisciplinaDAO implements DAO<Disciplina>{
    //sql querys
    private static final String sql_inserir = "INSERT INTO disciplina (nome, codigo, professor_id) VALUES (?,?,?);";
    private static final String sql_alterar = "UPDATE disciplina SET nome = ?, codigo = ?, professor_id = ? WHERE id =?;";
    private static final String sql_deletar = "DELETE FROM disciplina WHERE id = ?;";
    private static final String sql_listar = "SELECT d.*, p.id AS prof_id, p.nome AS prof_nome, p.email AS prof_email, p.senha AS prof_senha " +
            "FROM disciplina d, professor p WHERE d.professor_id = p.id;";

    private static final String sql_buscarPorId = "SELECT d.*, p.id AS prof_id, p.nome AS prof_nome, p.email AS prof_email, p.senha AS prof_senha " +
            "FROM disciplina d, professor p WHERE d.professor_id = p.id AND d.id = ?;";//outra tabela, tabela assunto
    private static final String sql_buscarAssuntos = "SELECT nome_assunto FROM assunto WHERE disciplina_id = ? ORDER BY id;";  
    private static final String sql_inserirAssunto = "INSERT INTO assunto (disciplina_id, nome_assunto) VALUES (?,?);";
    private static final String sql_deletarAssunto = "DELETE FROM assunto WHERE disciplina_id = ?;";

    //criar a conexao
    private Connection conexao; //atributo conexao

    //construtor da conexao
    public DisciplinaDAO(Connection conexao){
        this.conexao = conexao;
    }

    @Override //sobrescrita do método da interface
    public void inserir(Disciplina disciplina) throws SQLException{
        if (disciplina == null){
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }

        //iserindo a disciplina
        try(PreparedStatement ps = conexao.prepareStatement(sql_inserir,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, disciplina.getNome());
            ps.setString(2,disciplina.getCodigo());
            ps.setInt(3,disciplina.getProfessor().getId());
            ps.executeUpdate();

            //recuperando o id gerado
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    disciplina.setId(rs.getInt(1));
                }
            }
        }

        //inserindo assuntos, se tiver
        if(disciplina.getAssuntos() != null && !disciplina.getAssuntos().isEmpty()){
            inserirAssuntos(disciplina.getId(), disciplina.getAssuntos());
        }
        
    }

    //método próprio do DAO
    private void inserirAssuntos(int disciplinaId,List<String> assuntos) throws SQLException{
        try(PreparedStatement ps = conexao.prepareStatement(sql_inserirAssunto)){
            for (String assunto : assuntos){ //para cada assunto em assuntos
                ps.setInt(1,disciplinaId); //coloque o id da disciplina no primeiro ?
                ps.setString(2, assunto); //coloque o nome do assunto no segundo ?
                ps.addBatch(); //adiciona na fila para exacutar todos de uma vez
                }
                ps.executeBatch(); //executa todos os inserts no batch
            }
        }
    
    @Override //sobrecrita do método da interface
    public void deletar(Disciplina disciplina) throws SQLException{
        if (disciplina == null){
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }
        //deleta o assunto com base no Id da disciplina
        try (PreparedStatement ps = conexao.prepareStatement(sql_deletar)) {
            ps.setInt(1, disciplina.getId());
            int linhasAfetadas = ps.executeUpdate();
            
            if (linhasAfetadas == 0) {
                throw new SQLException("Disciplina com ID " + disciplina.getId() + " não encontrada");
            }
        }
    }
    @Override
    public void alterar(Disciplina disciplina) throws SQLException {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não pode ser nula");
        }
        //atualiza os dados da disciplina
        try (PreparedStatement ps = conexao.prepareStatement(sql_alterar)) {
            ps.setString(1, disciplina.getNome());
            ps.setString(2, disciplina.getCodigo());
            ps.setInt(3, disciplina.getProfessor().getId());
            ps.setInt(4, disciplina.getId());
            int linhasAfetadas = ps.executeUpdate();
            
             if (linhasAfetadas == 0) {
                throw new SQLException("Disciplina com ID " + disciplina.getId() + " não encontrada");
            }
        }
        //deleta todos os assuntos e insere novamente
        deletarAssuntos(disciplina.getId());
        if (disciplina.getAssuntos() != null && !disciplina.getAssuntos().isEmpty()) {
            inserirAssuntos(disciplina.getId(), disciplina.getAssuntos());
        }
    }

    //metodo do Dao para deletar 
    private void deletarAssuntos(int disciplinaId) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_deletarAssunto)) {
            ps.setInt(1, disciplinaId);
            ps.executeUpdate();
        }
    }

    
    @Override //sobrescrita do método da interface
    public List<Disciplina> listar() throws SQLException {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql_listar)) {
            
            List<Disciplina> disciplinas = new LinkedList<>();
            
            while (rs.next()) {
                Disciplina disciplina = criarDisciplinaDoResultSet(rs);
                disciplinas.add(disciplina);
            }
            
            return disciplinas;
        }
    }
    //buscar disciplina por Id
     public Disciplina buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarPorId)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return criarDisciplinaDoResultSet(rs);
                }
                return null;
            }
        }
    }

    //buscar disciplina por professor expecífico pelo id dele
    public List<Disciplina> buscarPorProfessor(Professor professor) throws SQLException {
        String sql = sql_listar + " WHERE d.professor_id = ?";
        
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, professor.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                List<Disciplina> disciplinas = new LinkedList<>();
                while (rs.next()) {
                    disciplinas.add(criarDisciplinaDoResultSet(rs));
                }
                return disciplinas;
            }
        }
    }

    //método para criar objeto Disciplina a partir do ResultSet
    private Disciplina criarDisciplinaDoResultSet(ResultSet rs) throws SQLException {

        // criando o objeto Professor (que é dono da disciplina)
        Professor professor = new Professor(
                rs.getInt("prof_id"),
                rs.getString("prof_nome"),
                rs.getString("prof_email"),
                rs.getString("prof_senha")
        );
        
        //criando a Disciplina
        Disciplina disciplina = new Disciplina(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("codigo"),
            professor,
            null  // os assuntos serão carregados separadamente
        );
        
        // carregando os assuntos da disciplina
        List<String> assuntos = buscarAssuntos(disciplina.getId());
        disciplina.setAssuntos(assuntos);
        
        return disciplina;
    }

    //método para buscar assuntos de uma disciplina
    private List<String> buscarAssuntos(int disciplinaId) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql_buscarAssuntos)) {
            ps.setInt(1, disciplinaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                List<String> assuntos = new LinkedList<>();
                while (rs.next()) {
                    assuntos.add(rs.getString("nome_assunto"));
                }
                return assuntos;
            }
        }
    }

}   
    

