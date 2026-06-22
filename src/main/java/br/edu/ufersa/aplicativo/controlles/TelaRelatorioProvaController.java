package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TelaRelatorioProvaController implements Initializable {

    @FXML private VBox folhaSemGabarito;
    @FXML private VBox folhaComGabarito;
    @FXML private ScrollPane scrollPrincipal;

    @FXML private StackPane btnEditar;
    @FXML private StackPane btnExcluir;
    @FXML private StackPane btnImprimir;

    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;

    private static final double ALTURA_MAXIMA_PAGINA = 620;
    private static final double MARGEM_SUPERIOR      = 36;
    private static final double MARGEM_INFERIOR      = 24;

    private Prova provaAtual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (provaAtual == null) {
            ProvaSessao sessao = ProvaSessao.getInstance();
            if (sessao != null && !sessao.getQuestoes().isEmpty()) {
                renderizarAmbas(
                        sessao.getInstituicao(),
                        sessao.getDisciplina(),
                        sessao.getProfessor(),
                        sessao.getQuestoes()
                );
            } else {
                mostrarVazio();
            }
        }
    }

    public void setProva(Prova prova) {
        this.provaAtual = prova;
        if (prova != null) {
            renderizarAmbas(
                    prova.getInstituicao() != null ? prova.getInstituicao() : "",
                    prova.getNomeDisciplina(),  // Usar getNomeDisciplina() em vez de getDisciplina()
                    prova.getProfessor() != null ? prova.getProfessor() : "",
                    prova.getQuestoes()
            );
        } else {
            mostrarVazio();
        }
    }

    // ================================================================
    // RENDERIZAÇÃO
    // ================================================================

    private void renderizarAmbas(String instituicao, String disciplina,
                                 String professor, List<Questao> questoes) {
        folhaSemGabarito.getChildren().clear();
        folhaComGabarito.getChildren().clear();

        if (questoes == null || questoes.isEmpty()) {
            mostrarVazio();
            return;
        }

        String nomeDisciplina = (disciplina == null || disciplina.isBlank())
                ? "MÚLTIPLA ESCOLHA" : disciplina.toUpperCase();

        renderizarColuna(folhaSemGabarito, instituicao, nomeDisciplina, professor, questoes, false);
        renderizarColuna(folhaComGabarito, instituicao, nomeDisciplina, professor, questoes, true);
    }

    private void renderizarColuna(VBox coluna, String instituicao, String nomeDisciplina,
                                  String professor, List<Questao> questoes, boolean comGabarito) {
        coluna.getChildren().clear();

        VBox paginaAtual  = criarPagina(instituicao, nomeDisciplina, professor);
        double alturaAtual = MARGEM_SUPERIOR + 38;

        int numero    = 1;
        int paginaNum = 1;
        int total     = questoes.size();

        for (Questao q : questoes) {
            VBox bloco         = criarBlocoQuestao(numero, q, comGabarito);
            double alturaBloco = estimarAlturaBloco(q);

            if (alturaAtual + alturaBloco + MARGEM_INFERIOR > ALTURA_MAXIMA_PAGINA) {
                adicionarRodape(paginaAtual, paginaNum, total);
                coluna.getChildren().add(paginaAtual);

                paginaNum++;
                paginaAtual = criarPaginaSemCabecalho(nomeDisciplina, paginaNum);
                alturaAtual = 28 + 38;
            }

            paginaAtual.getChildren().add(bloco);
            alturaAtual += alturaBloco;
            numero++;
        }

        adicionarRodape(paginaAtual, paginaNum, total);
        coluna.getChildren().add(paginaAtual);
    }

    // ================================================================
    // PÁGINAS
    // ================================================================

    private VBox criarPagina(String instituicao, String nomeDisciplina, String professor) {
        VBox pagina = new VBox(3);
        pagina.getStyleClass().add("folha");
        pagina.setAlignment(Pos.TOP_CENTER);
        pagina.setPadding(new Insets(MARGEM_SUPERIOR, 36, MARGEM_INFERIOR, 36));

        pagina.getChildren().add(criarCabecalho(instituicao, professor));
        pagina.getChildren().add(criarTituloBox("EXERCÍCIO DE " + nomeDisciplina));
        pagina.getChildren().add(criarSeparador());

        return pagina;
    }

    private VBox criarPaginaSemCabecalho(String nomeDisciplina, int numeroPagina) {
        VBox pagina = new VBox(3);
        pagina.getStyleClass().add("folha");
        pagina.setAlignment(Pos.TOP_CENTER);
        pagina.setPadding(new Insets(24, 36, MARGEM_INFERIOR, 36));

        pagina.getChildren().add(criarTituloBox("EXERCÍCIO DE " + nomeDisciplina + " (Página " + numeroPagina + ")"));
        pagina.getChildren().add(criarSeparador());

        return pagina;
    }

    private HBox criarTituloBox(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().add("folha-titulo-exercicio");
        titulo.setAlignment(Pos.CENTER);
        titulo.setMaxWidth(Double.MAX_VALUE);

        HBox box = new HBox(titulo);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(box, new Insets(5, 0, 3, 0));
        return box;
    }

    private HBox criarSeparador() {
        Label sep = new Label("---");
        sep.getStyleClass().add("folha-separador");
        sep.setAlignment(Pos.CENTER);
        sep.setMaxWidth(Double.MAX_VALUE);

        HBox box = new HBox(sep);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(box, new Insets(1, 0, 4, 0));
        return box;
    }

    private void adicionarRodape(VBox pagina, int numeroPagina, int totalQuestoes) {
        Label rodape = new Label("Página " + numeroPagina + " - Total de questões: " + totalQuestoes);
        rodape.getStyleClass().add("folha-rodape");

        VBox wrap = new VBox(rodape);
        wrap.setAlignment(Pos.CENTER);
        VBox.setMargin(wrap, new Insets(8, 0, 0, 0));
        VBox.setVgrow(wrap, Priority.ALWAYS);
        pagina.getChildren().add(wrap);
    }

    // ================================================================
    // CABEÇALHO
    // ================================================================

    private VBox criarCabecalho(String instituicao, String professor) {
        VBox cab = new VBox(2);
        cab.getStyleClass().add("folha-cabecalho-completo");
        cab.setAlignment(Pos.TOP_CENTER);
        cab.setPadding(new Insets(0, 0, 6, 0));

        String nomeInst = (instituicao == null || instituicao.isBlank()) ? "INSTITUIÇÃO" : instituicao.toUpperCase();
        Label lblInst = new Label(nomeInst);
        lblInst.getStyleClass().add("folha-cabecalho-instituicao");
        lblInst.setAlignment(Pos.CENTER);
        lblInst.setMaxWidth(Double.MAX_VALUE);
        HBox instBox = new HBox(lblInst);
        instBox.setAlignment(Pos.CENTER);
        instBox.setMaxWidth(Double.MAX_VALUE);
        cab.getChildren().add(instBox);

        Label lblIdent = new Label("1. IDENTIFICAÇÃO DOS ALUNOS");
        lblIdent.getStyleClass().add("folha-cabecalho-titulo");
        lblIdent.setAlignment(Pos.CENTER);
        lblIdent.setMaxWidth(Double.MAX_VALUE);
        HBox identBox = new HBox(lblIdent);
        identBox.setAlignment(Pos.CENTER);
        identBox.setMaxWidth(Double.MAX_VALUE);
        cab.getChildren().add(identBox);

        // NOME | DATA
        HBox linha1 = new HBox(8);
        linha1.setAlignment(Pos.CENTER);
        linha1.setMaxWidth(Double.MAX_VALUE);
        linha1.setPadding(new Insets(0, 16, 0, 16));
        HBox nomeGrp = new HBox(3);
        nomeGrp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nomeGrp, Priority.ALWAYS);
        Label lNome = new Label("NOME:");
        lNome.getStyleClass().add("folha-cabecalho-rotulo");
        Label cNome = new Label("");
        cNome.getStyleClass().add("folha-cabecalho-valor");
        cNome.setMinWidth(200);
        HBox.setHgrow(cNome, Priority.ALWAYS);
        nomeGrp.getChildren().addAll(lNome, cNome);
        HBox dataGrp = new HBox(3);
        dataGrp.setAlignment(Pos.CENTER_LEFT);
        Label lData = new Label("DATA:");
        lData.getStyleClass().add("folha-cabecalho-rotulo");
        Label cData = new Label("");
        cData.getStyleClass().add("folha-cabecalho-valor");
        cData.setMinWidth(80);
        dataGrp.getChildren().addAll(lData, cData);
        linha1.getChildren().addAll(nomeGrp, dataGrp);

        // CURSO | TURMA
        HBox linha2 = new HBox(8);
        linha2.setAlignment(Pos.CENTER);
        linha2.setMaxWidth(Double.MAX_VALUE);
        linha2.setPadding(new Insets(0, 16, 0, 16));
        HBox cursoGrp = new HBox(3);
        cursoGrp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cursoGrp, Priority.ALWAYS);
        Label lCurso = new Label("CURSO:");
        lCurso.getStyleClass().add("folha-cabecalho-rotulo");
        Label cCurso = new Label("");
        cCurso.getStyleClass().add("folha-cabecalho-valor");
        cCurso.setMinWidth(100);
        HBox.setHgrow(cCurso, Priority.ALWAYS);
        cursoGrp.getChildren().addAll(lCurso, cCurso);
        HBox turmaGrp = new HBox(3);
        turmaGrp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(turmaGrp, Priority.ALWAYS);
        Label lTurma = new Label("TURMA:");
        lTurma.getStyleClass().add("folha-cabecalho-rotulo");
        Label cTurma = new Label("");
        cTurma.getStyleClass().add("folha-cabecalho-valor");
        cTurma.setMinWidth(120);
        HBox.setHgrow(cTurma, Priority.ALWAYS);
        turmaGrp.getChildren().addAll(lTurma, cTurma);
        linha2.getChildren().addAll(cursoGrp, turmaGrp);

        // DOCENTE | TURNO
        HBox linha3 = new HBox(8);
        linha3.setAlignment(Pos.CENTER);
        linha3.setMaxWidth(Double.MAX_VALUE);
        linha3.setPadding(new Insets(0, 16, 0, 16));
        HBox docGrp = new HBox(3);
        docGrp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(docGrp, Priority.ALWAYS);
        Label lDoc = new Label("Docente:");
        lDoc.getStyleClass().add("folha-cabecalho-rotulo");
        String nomeProfStr = (professor == null || professor.isBlank()) ? "" : professor;
        Label cDoc = new Label(nomeProfStr);
        cDoc.getStyleClass().add("folha-cabecalho-valor");
        cDoc.setMinWidth(140);
        HBox.setHgrow(cDoc, Priority.ALWAYS);
        docGrp.getChildren().addAll(lDoc, cDoc);
        HBox turnoGrp = new HBox(3);
        turnoGrp.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(turnoGrp, Priority.ALWAYS);
        Label lTurno = new Label("TURNO:");
        lTurno.getStyleClass().add("folha-cabecalho-rotulo");
        Label cTurno = new Label("");
        cTurno.getStyleClass().add("folha-cabecalho-valor");
        cTurno.setMinWidth(100);
        HBox.setHgrow(cTurno, Priority.ALWAYS);
        turnoGrp.getChildren().addAll(lTurno, cTurno);
        linha3.getChildren().addAll(docGrp, turnoGrp);

        cab.getChildren().addAll(linha1, linha2, linha3);
        return cab;
    }

    // ================================================================
    // QUESTÕES
    // ================================================================

    private VBox criarBlocoQuestao(int numero, Questao q, boolean comGabarito) {
        VBox bloco = new VBox(2);
        bloco.getStyleClass().add("folha-questao");

        Label enunciado = new Label(numero + ") " + q.getEnunciado());
        enunciado.getStyleClass().add("folha-enunciado");
        enunciado.setWrapText(true);
        bloco.getChildren().add(enunciado);

        if (q instanceof MultiplaEscolha) {
            MultiplaEscolha me = (MultiplaEscolha) q;
            List<String> alternativas = me.getAlternativas();
            String resposta = me.getResposta();

            for (int i = 0; i < alternativas.size(); i++) {
                String texto = alternativas.get(i);
                boolean eGabarito = comGabarito && texto.equals(resposta);
                bloco.getChildren().add(criarLinhaAlternativa(letra(i), texto, eGabarito));
            }
        }

        return bloco;
    }

    private HBox criarLinhaAlternativa(String letra, String texto, boolean eGabarito) {
        HBox linha = new HBox(3);
        linha.getStyleClass().add("folha-alternativa");
        linha.setAlignment(Pos.CENTER_LEFT);

        Label marcador = new Label(eGabarito ? "( X )" : "(   )");
        marcador.getStyleClass().add(eGabarito ? "folha-marcador-gabarito" : "folha-marcador");
        marcador.setMinWidth(34);

        Label conteudo = new Label(letra + ". " + texto);
        conteudo.getStyleClass().add(eGabarito ? "folha-alternativa-gabarito" : "folha-alternativa-texto");
        conteudo.setWrapText(true);
        HBox.setHgrow(conteudo, Priority.ALWAYS);

        linha.getChildren().addAll(marcador, conteudo);
        return linha;
    }

    private String letra(int index) {
        return String.valueOf((char) ('A' + index));
    }

    private double estimarAlturaBloco(Questao q) {
        int numAlternativas = (q instanceof MultiplaEscolha)
                ? ((MultiplaEscolha) q).getAlternativas().size()
                : 0;
        return 22 + numAlternativas * 16 + 8;
    }

    // ================================================================
    // VAZIO
    // ================================================================

    private void mostrarVazio() {
        Label v1 = new Label("Nenhuma prova para exibir.");
        v1.getStyleClass().add("folha-vazio");
        Label v2 = new Label("Nenhuma prova para exibir.");
        v2.getStyleClass().add("folha-vazio");
        folhaSemGabarito.getChildren().add(v1);
        folhaComGabarito.getChildren().add(v2);
    }

    // ================================================================
    // AÇÕES
    // ================================================================

    @FXML
    private void handleEditar(MouseEvent event) {
        event.consume();
        navegarPara("TelaGerarProvaManualView", "TelaGerarProvaManualStyle", btnEditar);
    }

    @FXML
    private void handleExcluir(MouseEvent event) {
        event.consume();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Excluir prova");
        confirm.setHeaderText(null);
        confirm.setContentText("Tem certeza que deseja excluir esta prova? Essa ação não pode ser desfeita.");

        Optional<ButtonType> resposta = confirm.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            if (provaAtual != null) {
                ProvaSessao.getInstance().removerProvaSalva(provaAtual);
            }
            ProvaSessao.getInstance().limpar();
            navegarPara("TelaRelatorioView", "TelaRelatorioStyle", btnExcluir);
        }
    }

    @FXML
    private void handleImprimir(MouseEvent event) {
        event.consume();
        imprimirProvas();
    }

    private void imprimirProvas() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            alerta("Erro", "Nenhuma impressora encontrada. Use 'Salvar como PDF' no diálogo de impressão.");
            return;
        }

        Printer printer = job.getPrinter();
        PageLayout layout = printer.createPageLayout(
                Paper.A4, PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );
        job.getJobSettings().setPageLayout(layout);

        boolean showDialog = job.showPrintDialog(btnImprimir.getScene().getWindow());
        if (!showDialog) return;

        boolean sucesso = imprimirColuna(job, layout, folhaSemGabarito)
                && imprimirColuna(job, layout, folhaComGabarito);

        if (sucesso) {
            job.endJob();
            alerta("Impressão", "Impressão enviada!\n\nDica: selecione 'Salvar como PDF' para gerar um arquivo PDF.");
        } else {
            alerta("Erro", "Ocorreu um erro durante a impressão.");
        }
    }

    private boolean imprimirColuna(PrinterJob job, PageLayout layout, VBox coluna) {
        double larguraPagina = layout.getPrintableWidth();
        double alturaPagina  = layout.getPrintableHeight();

        for (Node node : coluna.getChildren()) {
            if (!(node instanceof VBox)) continue;
            VBox folha = (VBox) node;

            double escalaX = larguraPagina / folha.getPrefWidth();
            double escalaY = alturaPagina  / folha.getMinHeight();
            double escala  = Math.min(escalaX, escalaY);

            Scale transform = new Scale(escala, escala);
            folha.getTransforms().add(transform);

            boolean ok = job.printPage(layout, folha);

            folha.getTransforms().remove(transform);

            if (!ok) return false;
        }
        return true;
    }

    // ================================================================
    // NAVEGAÇÃO
    // ================================================================

    @FXML
    private void handleVoltar(MouseEvent e) {
        navegarPara("TelaRelatorioView", "TelaRelatorioStyle", btnEditar);
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

    private void navegarPara(String nomeView, String nomeCSS, Node origem) {
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
            boolean fs  = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas");

            if (fs)  stage.setFullScreen(true);
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