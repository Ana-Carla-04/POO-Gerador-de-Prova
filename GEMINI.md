# Tarefa: Conectar os Controladores JavaFX ao Banco de Dados através de Services

## Objetivo

Conectar os controladores das telas JavaFX que ainda não possuem integração com o banco de dados, utilizando os DAOs já existentes. A camada de interface **não deve acessar os DAOs diretamente**. Toda interação com persistência deve passar por classes de serviço (`Service`), seguindo o mesmo padrão já utilizado no projeto.

## Referências Obrigatórias

Antes de implementar qualquer alteração, analise os seguintes exemplos já existentes:

### Services de referência
- `AutenticacaoService`
- `DisciplinaService`

### Controladores já integrados
- `LoginController`
- `CadastroController`

Essas classes representam o padrão arquitetural que deve ser seguido.

---

## Regras Gerais

### 1. Não acessar DAOs diretamente nos Controllers

❌ Incorreto:

```java
UsuarioDAO dao = new UsuarioDAO();
dao.salvar(usuario);
```

✅ Correto:

```java
UsuarioService service = new UsuarioService();
service.salvar(usuario);
```

---

### 2. Criar Services quando necessário

Para cada entidade que ainda não possuir uma classe de serviço, criar uma classe na camada `service`, utilizando os services existentes como modelo.

Os services devem:

- Encapsular o uso dos DAOs.
- Centralizar regras de negócio.
- Realizar validações necessárias.
- Traduzir exceções técnicas em exceções mais adequadas para a aplicação.
- Expor apenas operações relevantes para os controladores.

Exemplo:

```java
public class AlunoService {

    private final AlunoDAO alunoDAO;

    public AlunoService() {
        this.alunoDAO = new AlunoDAO();
    }

    public void cadastrar(Aluno aluno) {
        // validações
        alunoDAO.salvar(aluno);
    }

    public List<Aluno> listarTodos() {
        return alunoDAO.buscarTodos();
    }
}
```

---

### 3. Adaptar os Controllers

Modificar os controladores JavaFX para que utilizem exclusivamente os services.

Os controllers devem ser responsáveis apenas por:

- Ler os componentes da interface.
- Converter dados da tela em objetos de domínio.
- Chamar métodos dos services.
- Atualizar a interface com os resultados.
- Exibir mensagens de sucesso ou erro.

Os controllers não devem:

- Possuir SQL.
- Instanciar DAOs.
- Conter regras de negócio complexas.

---

### 4. Aproveitar DAOs existentes

Não criar novos DAOs se já existirem implementações equivalentes.

Os services devem reutilizar integralmente os DAOs já presentes no projeto.

---

### 5. Manter o padrão do projeto

Seguir a mesma organização utilizada pelos exemplos existentes:

```
controller/
    ...
service/
    AutenticacaoService
    DisciplinaService
    ...
dao/
    ...
model/
    entity/
        ...
```

Utilizar o mesmo estilo de:

- nomenclatura;
- tratamento de exceções;
- instanciação de dependências;
- retorno de métodos;
- organização do código.

---

## Procedimento

Para cada controlador ainda não integrado:

1. Identificar quais operações do banco são necessárias.
2. Verificar se já existe um Service correspondente.
3. Caso não exista, criar um Service utilizando como modelo:
    - `AutenticacaoService`
    - `DisciplinaService`
4. Reutilizar os DAOs existentes.
5. Alterar o Controller para utilizar o Service.
6. Garantir que nenhuma classe da camada `controller` acesse diretamente a camada `dao`.
7. Preservar toda a funcionalidade atual da interface.

---

## Restrições

- Não modificar os DAOs existentes, exceto se houver erro evidente.
- Não alterar a estrutura do banco de dados.
- Não mover arquivos entre pacotes sem necessidade.
- Não criar lógica duplicada.
- Não inserir código temporário ou comentários do tipo TODO.
- Não quebrar funcionalidades existentes.

---

## Saída Esperada

Para cada tela processada, fornecer:

1. O código completo do(s) novo(s) Service(s) criados.
2. O código completo do Controller modificado.
3. Explicação breve das alterações realizadas.
4. Indicação explícita de quais DAOs foram reutilizados.
5. Confirmação de que não há acesso direto a DAOs dentro dos Controllers.