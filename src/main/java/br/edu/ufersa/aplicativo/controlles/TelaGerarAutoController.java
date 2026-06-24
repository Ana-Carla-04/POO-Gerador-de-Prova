package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.VerdadeiroFalso;
import br.edu.ufersa.aplicativo.model.service.ProvaService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TelaGerarAutoController implements Initializable {

    @FXML private VBox folha;
    @FXML private ScrollPane folhaScroll;

    @FXML private StackPane btnEditar;
    @FXML private StackPane btnExcluir;
    @FXML private StackPane btnSalvar;

    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;

    private ProvaService provaService;

    // Constantes para controle de páginas - AJUSTADAS
    private static final double ALTURA_MAXIMA_PAGINA = 1080; // Altura máxima em pixels
    private static final double MARGEM_SUPERIOR = 45;
    private static final double MARGEM_INFERIOR = 30;
    private static final double ALTURA_CABECALHO = 160; // Altura estimada do cabeçalho completo
    private static final double ALTURA_TITULO = 60; // Altura do título do exercício
    private static final double ALTURA_RODAPE = 30; // Altura do rodapé
    private static final double ALTURA_LINHA = 30; // Altura de cada linha de alternativa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        provaService = ServiceFactory.criarProvaService();
        renderizarFolha();
    }

    // ================================================================
    // MONTAGEM DA "FOLHA" DA PROVA - COM PAGINAÇÃO
    // ================================================================

    private void renderizarFolha() {
        folha.getChildren().clear();

        ProvaSessao sessao = ProvaSessao.getInstance();
        List<Questao> questoes = sessao.getQuestoes();

        if (questoes.isEmpty()) {
            Label vazio = new Label("Nenhuma questão foi incluída nesta prova.");
            vazio.getStyleClass().add("folha-vazio");
            folha.getChildren().add(vazio);
            return;
        }

        String nomeDisciplina = sessao.getDisciplina() == null || sessao.getDisciplina().isBlank()
                ? "MÚLTIPLA ESCOLHA" : sessao.getDisciplina().toUpperCase();

        // Cria a primeira página com cabeçalho
        VBox paginaAtual = criarPagina(sessao, nomeDisciplina);
        double alturaAtual = MARGEM_SUPERIOR + ALTURA_CABECALHO + ALTURA_TITULO; // Altura do cabeçalho + título

        int numero = 1;
        int paginaAtualNumero = 1;
        int totalQuestoes = questoes.size();

        for (Questao q : questoes) {
            VBox blocoQuestao = criarBlocoQuestao(numero, q);
            double alturaBloco = calcularAlturaBloco(q, blocoQuestao);

            // Verifica se a questão cabe na página atual
            if (alturaAtual + alturaBloco + MARGEM_INFERIOR + ALTURA_RODAPE > ALTURA_MAXIMA_PAGINA) {
                // Adiciona rodapé à página atual

                folha.getChildren().add(paginaAtual);

                // Cria nova página (sem cabeçalho, só com título)
                paginaAtualNumero++;
                paginaAtual = criarPaginaSemCabecalho(sessao, nomeDisciplina, paginaAtualNumero);
                alturaAtual = 30 + ALTURA_TITULO; // Margem superior + título
            }

            paginaAtual.getChildren().add(blocoQuestao);
            alturaAtual += alturaBloco;
            numero++;
        }

        // Adiciona rodapé à última página

        folha.getChildren().add(paginaAtual);
    }

    private VBox criarPagina(ProvaSessao sessao, String nomeDisciplina) {
        VBox pagina = new VBox(4);
        pagina.getStyleClass().add("folha");
        pagina.setAlignment(Pos.TOP_CENTER);
        pagina.setPadding(new Insets(MARGEM_SUPERIOR, 45, MARGEM_INFERIOR, 45));

        // Cabeçalho completo
        pagina.getChildren().add(criarCabecalhoCompleto(sessao));

        // TÍTULO DO EXERCÍCIO COM A DISCIPLINA
        Label tituloExercicio = new Label("EXERCÍCIO DE " + nomeDisciplina);
        tituloExercicio.getStyleClass().add("folha-titulo-exercicio");
        tituloExercicio.setAlignment(Pos.CENTER);
        tituloExercicio.setMaxWidth(Double.MAX_VALUE);

        HBox tituloBox = new HBox(tituloExercicio);
        tituloBox.setAlignment(Pos.CENTER);
        tituloBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(tituloBox, new Insets(6, 0, 4, 0));
        pagina.getChildren().add(tituloBox);

        // Linha separadora após o título
        Label separador = new Label("---");
        separador.getStyleClass().add("folha-separador");
        separador.setAlignment(Pos.CENTER);
        separador.setMaxWidth(Double.MAX_VALUE);
        HBox separadorBox = new HBox(separador);
        separadorBox.setAlignment(Pos.CENTER);
        separadorBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(separadorBox, new Insets(2, 0, 6, 0));
        pagina.getChildren().add(separadorBox);

        return pagina;
    }

    private VBox criarPaginaSemCabecalho(ProvaSessao sessao, String nomeDisciplina, int numeroPagina) {
        VBox pagina = new VBox(4);
        pagina.getStyleClass().add("folha");
        pagina.setAlignment(Pos.TOP_CENTER);
        pagina.setPadding(new Insets(30, 45, MARGEM_INFERIOR, 45));

        // Título do exercício (sem cabeçalho completo)
        Label tituloExercicio = new Label("EXERCÍCIO DE " + nomeDisciplina + " (Página " + numeroPagina + ")");
        tituloExercicio.getStyleClass().add("folha-titulo-exercicio");
        tituloExercicio.setAlignment(Pos.CENTER);
        tituloExercicio.setMaxWidth(Double.MAX_VALUE);

        HBox tituloBox = new HBox(tituloExercicio);
        tituloBox.setAlignment(Pos.CENTER);
        tituloBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(tituloBox, new Insets(0, 0, 4, 0));
        pagina.getChildren().add(tituloBox);

        // Linha separadora
        Label separador = new Label("---");
        separador.getStyleClass().add("folha-separador");
        separador.setAlignment(Pos.CENTER);
        separador.setMaxWidth(Double.MAX_VALUE);
        HBox separadorBox = new HBox(separador);
        separadorBox.setAlignment(Pos.CENTER);
        separadorBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(separadorBox, new Insets(2, 0, 6, 0));
        pagina.getChildren().add(separadorBox);

        return pagina;
    }

    private void adicionarRodape(VBox pagina, int numeroPagina, int totalQuestoes) {
        Label rodapePagina = new Label("Página " + numeroPagina + " - Total de questões: " + totalQuestoes);
        rodapePagina.getStyleClass().add("folha-rodape");
        VBox.setMargin(rodapePagina, new Insets(8, 0, 0, 0));
        VBox rodapeWrap = new VBox(rodapePagina);
        rodapeWrap.setAlignment(Pos.CENTER);
        pagina.getChildren().add(rodapeWrap);
    }

    /**
     * Calcula a altura estimada do bloco da questão baseado no tipo e conteúdo
     */
    private double calcularAlturaBloco(Questao q, VBox bloco) {
        double alturaBase = 40; // Altura mínima para o enunciado + padding

        if (q instanceof MultiplaEscolha) {
            MultiplaEscolha me = (MultiplaEscolha) q;
            int numAlternativas = me.getAlternativas().size();
            // Cada alternativa ocupa aproximadamente 30px
            alturaBase += numAlternativas * ALTURA_LINHA;
        } else if (q instanceof VerdadeiroFalso) {
            // Verdadeiro/Falso tem 2 alternativas
            alturaBase += 2 * ALTURA_LINHA;
        } else {
            // Questão discursiva - adiciona espaço para 4 linhas em branco
            alturaBase += 4 * ALTURA_LINHA;
        }

        // Adiciona margem extra para espaçamento entre questões
        return alturaBase + 15;
    }

    // ================================================================
    // CABEÇALHO
    // ================================================================

    private VBox criarCabecalhoCompleto(ProvaSessao sessao) {
        VBox cabecalho = new VBox(3);
        cabecalho.getStyleClass().add("folha-cabecalho-completo");
        cabecalho.setAlignment(Pos.TOP_CENTER);
        cabecalho.setPadding(new Insets(0, 0, 6, 0));

        // Linha 1: Nome da Instituição em maiúsculo e centralizado
        String nomeInstituicao = sessao.getInstituicao() == null || sessao.getInstituicao().isBlank()
                ? "INSTITUIÇÃO" : sessao.getInstituicao().toUpperCase();
        Label instituicaoLabel = new Label(nomeInstituicao);
        instituicaoLabel.getStyleClass().add("folha-cabecalho-instituicao");
        instituicaoLabel.setAlignment(Pos.CENTER);
        instituicaoLabel.setMaxWidth(Double.MAX_VALUE);

        HBox instituicaoBox = new HBox(instituicaoLabel);
        instituicaoBox.setAlignment(Pos.CENTER);
        instituicaoBox.setMaxWidth(Double.MAX_VALUE);
        cabecalho.getChildren().add(instituicaoBox);

        // Linha 2: IDENTIFICAÇÃO DOS ALUNOS (subtítulo)
        Label identificacaoLabel = new Label("");
        identificacaoLabel.getStyleClass().add("folha-cabecalho-titulo");
        identificacaoLabel.setAlignment(Pos.CENTER);
        identificacaoLabel.setMaxWidth(Double.MAX_VALUE);

        HBox identificacaoBox = new HBox(identificacaoLabel);
        identificacaoBox.setAlignment(Pos.CENTER);
        identificacaoBox.setMaxWidth(Double.MAX_VALUE);
        cabecalho.getChildren().add(identificacaoBox);

        // LINHA 1: NOME (80%) | DATA (20%)
        HBox linha1 = new HBox(10);
        linha1.setAlignment(Pos.CENTER);
        linha1.setMaxWidth(Double.MAX_VALUE);
        linha1.setPadding(new Insets(0, 20, 0, 20));

        // NOME - 80%
        HBox nomeGroup = new HBox(4);
        nomeGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nomeGroup, Priority.ALWAYS);
        Label lblNome = new Label("NOME:");
        lblNome.getStyleClass().add("folha-cabecalho-rotulo");
        Label campoNome = new Label("");
        campoNome.getStyleClass().add("folha-cabecalho-valor");
        campoNome.setMinWidth(280);
        HBox.setHgrow(campoNome, Priority.ALWAYS);
        nomeGroup.getChildren().addAll(lblNome, campoNome);

        // DATA - 20%
        HBox dataGroup = new HBox(4);
        dataGroup.setAlignment(Pos.CENTER_LEFT);
        Label lblData = new Label("DATA:");
        lblData.getStyleClass().add("folha-cabecalho-rotulo");
        Label campoData = new Label("");
        campoData.getStyleClass().add("folha-cabecalho-valor");
        campoData.setMinWidth(100);
        dataGroup.getChildren().addAll(lblData, campoData);

        linha1.getChildren().addAll(nomeGroup, dataGroup);

        // LINHA 2: CURSO (50%) | TURMA (50%)
        HBox linha2 = new HBox(10);
        linha2.setAlignment(Pos.CENTER);
        linha2.setMaxWidth(Double.MAX_VALUE);
        linha2.setPadding(new Insets(0, 20, 0, 20));

        HBox cursoGroup = new HBox(4);
        cursoGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cursoGroup, Priority.ALWAYS);
        Label lblCurso = new Label("CURSO:");
        lblCurso.getStyleClass().add("folha-cabecalho-rotulo");
        Label campoCurso = new Label("");
        campoCurso.getStyleClass().add("folha-cabecalho-valor");
        campoCurso.setMinWidth(130);
        HBox.setHgrow(campoCurso, Priority.ALWAYS);
        cursoGroup.getChildren().addAll(lblCurso, campoCurso);

        HBox turmaGroup = new HBox(4);
        turmaGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(turmaGroup, Priority.ALWAYS);
        Label lblTurma = new Label("TURMA:");
        lblTurma.getStyleClass().add("folha-cabecalho-rotulo");
        Label campoTurma = new Label("");
        campoTurma.getStyleClass().add("folha-cabecalho-valor");
        campoTurma.setMinWidth(200);
        HBox.setHgrow(campoTurma, Priority.ALWAYS);
        turmaGroup.getChildren().addAll(lblTurma, campoTurma);

        linha2.getChildren().addAll(cursoGroup, turmaGroup);

        // LINHA 3: Docente (50%) | TURNO (50%)
        HBox linha3 = new HBox(10);
        linha3.setAlignment(Pos.CENTER);
        linha3.setMaxWidth(Double.MAX_VALUE);
        linha3.setPadding(new Insets(0, 20, 0, 20));

        HBox docenteGroup = new HBox(4);
        docenteGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(docenteGroup, Priority.ALWAYS);
        Label lblDocente = new Label("Docente:");
        lblDocente.getStyleClass().add("folha-cabecalho-rotulo");
        String nomeProfessor = sessao.getProfessor() == null || sessao.getProfessor().isBlank()
                ? "" : sessao.getProfessor();
        Label campoDocente = new Label(nomeProfessor);
        campoDocente.getStyleClass().add("folha-cabecalho-valor");
        campoDocente.setMinWidth(180);
        HBox.setHgrow(campoDocente, Priority.ALWAYS);
        docenteGroup.getChildren().addAll(lblDocente, campoDocente);

        HBox turnoGroup = new HBox(4);
        turnoGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(turnoGroup, Priority.ALWAYS);
        Label lblTurno = new Label("TURNO:");
        lblTurno.getStyleClass().add("folha-cabecalho-rotulo");
        Label campoTurno = new Label("");
        campoTurno.getStyleClass().add("folha-cabecalho-valor");
        campoTurno.setMinWidth(150);
        HBox.setHgrow(campoTurno, Priority.ALWAYS);
        turnoGroup.getChildren().addAll(lblTurno, campoTurno);

        linha3.getChildren().addAll(docenteGroup, turnoGroup);

        cabecalho.getChildren().addAll(linha1, linha2, linha3);

        return cabecalho;
    }

    /**
     * Cria o bloco da questão com base no seu tipo
     */
    private VBox criarBlocoQuestao(int numero, Questao q) {
        VBox bloco = new VBox(2);
        bloco.getStyleClass().add("folha-questao");

        Label enunciado = new Label(numero + ") " + q.getEnunciado());
        enunciado.getStyleClass().add("folha-enunciado");
        enunciado.setWrapText(true);
        bloco.getChildren().add(enunciado);

        // Verifica o tipo da questão e adiciona as alternativas apropriadas
        if (q instanceof MultiplaEscolha) {
            MultiplaEscolha me = (MultiplaEscolha) q;
            List<String> alternativas = me.getAlternativas();

            for (int i = 0; i < alternativas.size(); i++) {
                String texto = alternativas.get(i);
                bloco.getChildren().add(criarLinhaAlternativa(letra(i), texto));
            }
        } else if (q instanceof VerdadeiroFalso) {
            // Adiciona as alternativas de Verdadeiro/Falso
            bloco.getChildren().add(criarLinhaAlternativa("V - ", "Verdadeiro"));
            bloco.getChildren().add(criarLinhaAlternativa("F - ", "Falso"));
        } else {
            // Questão discursiva - adiciona 4 linhas em branco para resposta
            for (int i = 0; i < 4; i++) {
                HBox linhaBranca = new HBox();
                linhaBranca.getStyleClass().add("folha-discursiva-linha");
                linhaBranca.setMinHeight(30);
                linhaBranca.setMaxHeight(30);
                linhaBranca.setPrefHeight(30);
                // Adiciona uma borda inferior para simular linha de papel
                linhaBranca.setStyle("-fx-border-bottom: 1px solid #cccccc;");
                bloco.getChildren().add(linhaBranca);
            }
        }

        return bloco;
    }

    private HBox criarLinhaAlternativa(String letra, String texto) {
        HBox linha = new HBox(3);
        linha.getStyleClass().add("folha-alternativa");
        linha.setAlignment(Pos.CENTER_LEFT);

        Label marcador = new Label("(   )");
        marcador.getStyleClass().add("folha-marcador");
        marcador.setMinWidth(38);

        Label conteudo = new Label(letra + ". " + texto);
        conteudo.getStyleClass().add("folha-alternativa-texto");
        conteudo.setWrapText(true);
        HBox.setHgrow(conteudo, Priority.ALWAYS);

        linha.getChildren().addAll(marcador, conteudo);
        return linha;
    }

    private String letra(int index) {
        return String.valueOf((char) ('A' + index));
    }

    // ================================================================
    // AÇÕES: EDITAR / EXCLUIR / SALVAR
    // ================================================================

    @FXML
    private void handleEditar(MouseEvent event) {
        event.consume();
        navegarPara("TelaGerarProvaManualView", "TelaGerarProvaManualStyle", btnEditar);
    }

    @FXML
    private void handleExcluir(MouseEvent event) {
        event.consume();

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Excluir prova");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Tem certeza que deseja descartar esta prova? Essa ação não pode ser desfeita.");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            ProvaSessao.getInstance().limpar();
            navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle", btnExcluir);
        }
    }

    @FXML
    private void handleSalvar(MouseEvent event) {
        event.consume();

        ProvaSessao sessao = ProvaSessao.getInstance();
        if (sessao.getQuestoes().isEmpty()) {
            alerta("Nada para salvar", "Esta prova não possui questões.");
            return;
        }

        Prova prova = sessao.registrarProvaSalva();

        provaService.salvarProva(prova);

        alerta("Prova salva",
                "A prova \"" + prova.getCodigo() + "\" foi salva com sucesso!\n" +
                        prova.getQuestoes().size() + " questões.");
    }

    // ================================================================
    // NAVEGAÇÃO
    // ================================================================

    @FXML
    private void handleVoltar(MouseEvent e) {
        navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle", btnEditar);
    }

    @FXML
    private void handleMenuDisciplinas(MouseEvent e) {
        navegarPara("TelaInicialView", "TelaInicialStyle", menuDisciplinas);
    }

    @FXML
    private void handleMenuBuscar(MouseEvent e) {
        navegarPara("TelaBuscarView", "TelaBuscarStyle", menuBuscar);
    }

    @FXML
    private void handleMenuGerarProva(MouseEvent e) {
        navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle", menuGerarProva);
    }

    @FXML
    private void handleMenuRelatorio(MouseEvent e) {
        navegarPara("TelaRelatorioView", "TelaRelatorioStyle", menuRelatorio);
    }

    private void navegarPara(String nomeView, String nomeCSS, javafx.scene.Node origem) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/" + nomeView + ".fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/" + nomeCSS + ".css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) origem.getScene().getWindow();
            boolean fs = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas");

            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            alerta("Erro", "Não foi possível navegar para a tela solicitada.");
        }
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}