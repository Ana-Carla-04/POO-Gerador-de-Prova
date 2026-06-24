package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.Discursiva;
import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.VerdadeiroFalso;
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
            if (!sessao.getQuestoes().isEmpty()) {
                renderizarAmbas(
                        sessao.getInstituicao(),
                        sessao.getDisciplina(),
                        sessao.getProfessor(),
                        sessao.getQuestoes()
                );
            } else {
                List<Prova> provasSalvas = sessao.getProvasSalvas();
                if (!provasSalvas.isEmpty()) {
                    setProva(provasSalvas.get(provasSalvas.size() - 1));
                } else {
                    mostrarVazio();
                }
            }
        }
    }

    public void setProva(Prova prova) {
        this.provaAtual = prova;
        if (prova != null) {
            renderizarAmbas(
                    prova.getInstituicao() != null ? prova.getInstituicao() : "",
                    prova.getNomeDisciplina(),
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
                ? "QUESTÕES" : disciplina.toUpperCase();

        renderizarColuna(folhaSemGabarito, instituicao, nomeDisciplina, professor, questoes, false);
        renderizarColuna(folhaComGabarito, instituicao, nomeDisciplina, professor, questoes, true);
    }

    private void renderizarColuna(VBox coluna, String instituicao, String nomeDisciplina,
                                  String professor, List<Questao> questoes, boolean comGabarito) {
        coluna.getChildren().clear();

        VBox paginaAtual   = criarPagina(instituicao, nomeDisciplina, professor);
        double alturaAtual = MARGEM_SUPERIOR + 38;

        int numero    = 1;
        int paginaNum = 1;
        int total     = questoes.size();

        for (Questao q : questoes) {
            VBox bloco         = criarBlocoQuestao(numero, q, comGabarito);
            double alturaBloco = estimarAlturaBloco(q, comGabarito);

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
    // QUESTÕES — trata Múltipla Escolha, Verdadeiro/Falso e Discursiva
    // ================================================================

    private VBox criarBlocoQuestao(int numero, Questao q, boolean comGabarito) {
        VBox bloco = new VBox(2);
        bloco.getStyleClass().add("folha-questao");

        Label enunciado = new Label(numero + ") " + q.getEnunciado());
        enunciado.getStyleClass().add("folha-enunciado");
        enunciado.setWrapText(true);
        bloco.getChildren().add(enunciado);

        if (q instanceof MultiplaEscolha) {
            // ── MÚLTIPLA ESCOLHA ──────────────────────────────────────
            MultiplaEscolha me = (MultiplaEscolha) q;
            List<String> alternativas = me.getAlternativas();
            String resposta = me.getResposta();

            for (int i = 0; i < alternativas.size(); i++) {
                String texto    = alternativas.get(i);
                boolean gabarito = comGabarito && texto.equals(resposta);
                bloco.getChildren().add(criarLinhaAlternativa(letra(i), texto, gabarito));
            }

        } else if (q instanceof VerdadeiroFalso) {
            // ── VERDADEIRO / FALSO ────────────────────────────────────
            VerdadeiroFalso vf = (VerdadeiroFalso) q;
            List<String> alternativas = vf.getAlternativas();
            String respostaVF = "";

            if (comGabarito) {
                // Pega o texto da alternativa correta via lista de booleanos
                List<Boolean> respostas = vf.getRespostas();
                if (respostas != null && alternativas != null) {
                    for (int i = 0; i < respostas.size(); i++) {
                        if (respostas.get(i) && i < alternativas.size()) {
                            respostaVF = alternativas.get(i);
                            break;
                        }
                    }
                }
            }

            // Se o banco salvou alternativas reais, usa elas; senão usa padrão
            if (alternativas != null && !alternativas.isEmpty()) {
                for (String alt : alternativas) {
                    boolean gabarito = comGabarito && alt.equals(respostaVF);
                    bloco.getChildren().add(criarLinhaAlternativa("", alt, gabarito));
                }
            } else {
                // Fallback: "Verdadeiro" e "Falso"
                boolean verdadeiroGabarito = comGabarito && respostaVF.equalsIgnoreCase("Verdadeiro");
                boolean falsoGabarito      = comGabarito && respostaVF.equalsIgnoreCase("Falso");
                bloco.getChildren().add(criarLinhaAlternativa("", "Verdadeiro", verdadeiroGabarito));
                bloco.getChildren().add(criarLinhaAlternativa("", "Falso",      falsoGabarito));
            }

        } else if (q instanceof Discursiva) {
            // ── DISCURSIVA ────────────────────────────────────────────
            if (comGabarito) {
                // Lado com gabarito: mostra a resposta
                Discursiva d = (Discursiva) q;
                String resposta = d.getResposta();
                Label lblGab = new Label(resposta != null && !resposta.isBlank()
                        ? resposta : "(sem gabarito cadastrado)");
                lblGab.getStyleClass().add("folha-alternativa-gabarito");
                lblGab.setWrapText(true);
                VBox.setMargin(lblGab, new Insets(4, 0, 0, 8));
                bloco.getChildren().add(lblGab);
            } else {
                // Lado sem gabarito: 4 linhas em branco para o aluno escrever
                for (int i = 0; i < 4; i++) {
                    HBox linhaBranca = new HBox();
                    linhaBranca.setMinHeight(22);
                    linhaBranca.setMaxHeight(22);
                    linhaBranca.setPrefHeight(22);
                    linhaBranca.setMaxWidth(Double.MAX_VALUE);
                    linhaBranca.setStyle(
                            "-fx-border-color: transparent transparent #aaaaaa transparent;" +
                                    "-fx-border-width: 0 0 1 0;"
                    );
                    VBox.setMargin(linhaBranca, new Insets(4, 4, 0, 4));
                    bloco.getChildren().add(linhaBranca);
                }
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

        String prefixo = (letra == null || letra.isBlank()) ? "" : letra + ". ";
        Label conteudo = new Label(prefixo + texto);
        conteudo.getStyleClass().add(eGabarito ? "folha-alternativa-gabarito" : "folha-alternativa-texto");
        conteudo.setWrapText(true);
        HBox.setHgrow(conteudo, Priority.ALWAYS);

        linha.getChildren().addAll(marcador, conteudo);
        return linha;
    }

    private String letra(int index) {
        return String.valueOf((char) ('A' + index));
    }

    private double estimarAlturaBloco(Questao q, boolean comGabarito) {
        if (q instanceof MultiplaEscolha) {
            int n = ((MultiplaEscolha) q).getAlternativas().size();
            return 22 + n * 16 + 8;
        } else if (q instanceof VerdadeiroFalso) {
            return 22 + 2 * 16 + 8;
        } else {
            // Discursiva: sem gabarito = 4 linhas (~22px cada); com gabarito = texto variável
            return comGabarito ? 22 + 30 + 8 : 22 + 4 * 22 + 8;
        }
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

        // Contadores para controle de páginas impressas
        int totalPaginasSemGabarito = folhaSemGabarito.getChildren().size();
        int totalPaginasComGabarito = folhaComGabarito.getChildren().size();
        int paginasImpressas = 0;
        int totalPaginas = totalPaginasSemGabarito + totalPaginasComGabarito;

        boolean sucesso = true;

        // Imprime todas as páginas da coluna sem gabarito
        for (Node node : folhaSemGabarito.getChildren()) {
            if (!(node instanceof VBox)) continue;
            VBox folha = (VBox) node;

            paginasImpressas++;
            boolean ok = imprimirPagina(job, layout, folha, paginasImpressas, totalPaginas);
            if (!ok) {
                sucesso = false;
                break;
            }
        }

        // Se a primeira coluna foi bem sucedida, imprime a coluna com gabarito
        if (sucesso) {
            for (Node node : folhaComGabarito.getChildren()) {
                if (!(node instanceof VBox)) continue;
                VBox folha = (VBox) node;

                paginasImpressas++;
                boolean ok = imprimirPagina(job, layout, folha, paginasImpressas, totalPaginas);
                if (!ok) {
                    sucesso = false;
                    break;
                }
            }
        }

        if (sucesso) {
            job.endJob();
            alerta("Impressão", "Impressão enviada!\n\n" +
                    "Páginas impressas: " + totalPaginas + "\n" +
                    "Sem gabarito: " + totalPaginasSemGabarito + " página(s)\n" +
                    "Com gabarito: " + totalPaginasComGabarito + " página(s)\n\n" +
                    "Dica: selecione 'Salvar como PDF' para gerar um PDF.");
        } else {
            alerta("Erro", "Ocorreu um erro durante a impressão.");
        }
    }

    /**
     * Imprime uma única página com escala adequada
     */
    private boolean imprimirPagina(PrinterJob job, PageLayout layout, VBox folha, int paginaAtual, int totalPaginas) {
        double larguraPagina = layout.getPrintableWidth();
        double alturaPagina = layout.getPrintableHeight();

        // Calcula a escala para caber na página
        double escalaX = larguraPagina / folha.getPrefWidth();
        double escalaY = alturaPagina / folha.getMinHeight();
        double escala = Math.min(escalaX, escalaY);

        // Aplica a escala
        Scale transform = new Scale(escala, escala);
        folha.getTransforms().add(transform);

        // Imprime a página
        boolean ok = job.printPage(layout, folha);

        // Remove a escala
        folha.getTransforms().remove(transform);

        if (!ok) {
            System.err.println("Erro ao imprimir página " + paginaAtual + " de " + totalPaginas);
        } else {
            System.out.println("Página " + paginaAtual + " de " + totalPaginas + " impressa com sucesso");
        }

        return ok;
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

    @FXML private void handleVoltar(MouseEvent e)          { navegarPara("TelaRelatorioView",       "TelaRelatorioStyle",       btnEditar);      }
    @FXML private void handleMenuDisciplinas(MouseEvent e) { navegarPara("TelaInicialView",          "TelaInicialStyle",          menuDisciplinas); }
    @FXML private void handleMenuBuscar(MouseEvent e)      { navegarPara("TelaBuscarView",           "TelaBuscarStyle",           menuBuscar);      }
    @FXML private void handleMenuGerarProva(MouseEvent e)  { navegarPara("TelaGerarProvaView",       "TelaGerarProvaStyle",       menuGerarProva);  }
    @FXML private void handleMenuRelatorio(MouseEvent e)   { navegarPara("TelaRelatorioView",        "TelaRelatorioStyle",        menuRelatorio);   }

    private void navegarPara(String nomeView, String nomeCSS, Node origem) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/" + nomeView + ".fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/" + nomeCSS + ".css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

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