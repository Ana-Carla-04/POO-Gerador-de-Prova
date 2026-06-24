package br.edu.ufersa.aplicativo.controlles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.application.GerenteDeCena;
import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;
import br.edu.ufersa.aplicativo.model.entities.*;
import br.edu.ufersa.aplicativo.model.service.DisciplinaService;
import br.edu.ufersa.aplicativo.model.service.QuestaoService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;
import br.edu.ufersa.aplicativo.util.Conexao;

public class TelaAdicionarQuestController implements Initializable {

    private QuestaoService questaoService;

    // ── FXML fixos ──────────────────────────────────────────────────
    @FXML private TextField    fieldCodigo;
    @FXML private StackPane    tipoSelector;
    @FXML private Label        tipoLabel;
    @FXML private VBox         camposDinamicos;
    @FXML private HBox         boxBtnAdicionar;
    @FXML private Button       btnAdicionar;

    // Popup de tipo
    @FXML private VBox  tipoPopup;
    @FXML private Label popupOpcaoMultipla;
    @FXML private Label popupOpcaoDiscursiva;
    @FXML private Label popupOpcaoVF;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;

    // ── Estado ──────────────────────────────────────────────────────
    private enum TipoQuestao { MULTIPLA_ESCOLHA, DISCURSIVA, VERDADEIRO_FALSO, NENHUM }
    private TipoQuestao tipoAtual = TipoQuestao.NENHUM;

    // Modo edição
    private boolean modoEdicao = false;
    private Questao questaoEmEdicao = null;

    // Campos dinâmicos reutilizados
    private TextField   fieldEnunciado;
    private TextField   fieldGabarito;
    private TextField   fieldDisciplina;
    private TextField   fieldAssunto;
    private ToggleGroup grupoGabarito;
    private ToggleGroup grupoNivel;

    // Alternativas (múltipla escolha)
    private TextField fieldAltA, fieldAltB, fieldAltC, fieldAltD;

    // ================================================================
    // INITIALIZE
    // ================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questaoService = ServiceFactory.criarQuestaoService();

        // fieldCodigo é apenas informativo (ID do banco)
        fieldCodigo.setEditable(false);
        fieldCodigo.setFocusTraversable(false);
        fieldCodigo.setStyle("-fx-opacity: 0.7;");

        // Verifica se veio uma questão para editar
        questaoEmEdicao = Contexto.getQuestaoParaEditar();
        Contexto.limparQuestaoParaEditar();

        if (questaoEmEdicao != null) {
            modoEdicao = true;
            btnAdicionar.setText("Salvar alterações");
            preencherCamposParaEdicao(questaoEmEdicao);
        }
    }

    // ================================================================
    // PRÉ-PREENCHIMENTO PARA EDIÇÃO
    // ================================================================

    private void preencherCamposParaEdicao(Questao questao) {
        // Mostra o ID real do banco
        fieldCodigo.setText(String.valueOf(questao.getCodigo()));

        // Determina o tipo e aplica os campos dinâmicos
        TipoQuestao tipo;
        if (questao instanceof MultiplaEscolha) {
            tipo = TipoQuestao.MULTIPLA_ESCOLHA;
        } else if (questao instanceof VerdadeiroFalso) {
            tipo = TipoQuestao.VERDADEIRO_FALSO;
        } else {
            tipo = TipoQuestao.DISCURSIVA;
        }

        // Monta os campos (igual ao fluxo normal)
        aplicarTipo(tipo);

        // Desabilita o seletor de tipo (não faz sentido mudar o tipo na edição)
        tipoSelector.setDisable(true);
        tipoSelector.setOpacity(0.6);

        // Preenche campos comuns
        fieldEnunciado.setText(questao.getEnunciado() != null ? questao.getEnunciado() : "");
        fieldAssunto.setText(questao.getAssunto() != null ? questao.getAssunto() : "");

        // Disciplina: usa o código da disciplina
        if (questao.getDisciplina() != null) {
            fieldDisciplina.setText(questao.getDisciplina().getCodigo() != null
                    ? questao.getDisciplina().getCodigo() : "");
        }

        // Nível
        if (grupoNivel != null && questao.getNivel() != null) {
            String nivelStr = switch (questao.getNivel()) {
                case FACIL -> "Fácil";
                case MEDIO -> "Médio";
                case DIFICIL -> "Difícil";
            };
            for (Toggle t : grupoNivel.getToggles()) {
                RadioButton rb = (RadioButton) t;
                if (rb.getText().equalsIgnoreCase(nivelStr)) {
                    grupoNivel.selectToggle(rb);
                    break;
                }
            }
        }

        // Campos específicos por tipo
        switch (tipo) {
            case MULTIPLA_ESCOLHA -> preencherMultipla((MultiplaEscolha) questao);
            case DISCURSIVA       -> preencherDiscursiva((Discursiva) questao);
            case VERDADEIRO_FALSO -> preencherVF((VerdadeiroFalso) questao);
        }
    }

    private void preencherMultipla(MultiplaEscolha me) {
        List<String> alts = me.getAlternativas();
        if (alts != null) {
            if (alts.size() > 0 && fieldAltA != null) fieldAltA.setText(alts.get(0));
            if (alts.size() > 1 && fieldAltB != null) fieldAltB.setText(alts.get(1));
            if (alts.size() > 2 && fieldAltC != null) fieldAltC.setText(alts.get(2));
            if (alts.size() > 3 && fieldAltD != null) fieldAltD.setText(alts.get(3));
        }

        // Seleciona o radio do gabarito (a/b/c/d)
        String resposta = me.getResposta();
        if (resposta != null && grupoGabarito != null && alts != null) {
            int idx = alts.indexOf(resposta);
            String letra = switch (idx) {
                case 0 -> "a";
                case 1 -> "b";
                case 2 -> "c";
                case 3 -> "d";
                default -> "";
            };
            for (Toggle t : grupoGabarito.getToggles()) {
                RadioButton rb = (RadioButton) t;
                if (rb.getText().equals(letra)) {
                    grupoGabarito.selectToggle(rb);
                    break;
                }
            }
        }
    }

    private void preencherDiscursiva(Discursiva d) {
        if (fieldGabarito != null && d.getResposta() != null) {
            fieldGabarito.setText(d.getResposta());
        }
    }

    private void preencherVF(VerdadeiroFalso vf) {
        // Descobre qual é o gabarito (Verdadeiro ou Falso)
        String gabarito = "";
        List<String> alts = vf.getAlternativas();
        List<Boolean> resps = vf.getRespostas();
        if (alts != null && resps != null) {
            for (int i = 0; i < resps.size(); i++) {
                if (resps.get(i) && i < alts.size()) {
                    gabarito = alts.get(i);
                    break;
                }
            }
        }
        if (gabarito.isEmpty() && vf.getResposta() != null) {
            gabarito = vf.getResposta().equals("V") ? "Verdadeiro" : "Falso";
        }

        if (grupoGabarito != null && !gabarito.isEmpty()) {
            final String gab = gabarito;
            for (Toggle t : grupoGabarito.getToggles()) {
                RadioButton rb = (RadioButton) t;
                if (rb.getText().equalsIgnoreCase(gab)) {
                    grupoGabarito.selectToggle(rb);
                    break;
                }
            }
        }
    }

    // ================================================================
    // MÉTODO ADICIONAR / SALVAR
    // ================================================================

    @FXML
    private void handleAdicionar(javafx.event.ActionEvent event) {
        try {
            // Validações
            if (tipoAtual == TipoQuestao.NENHUM) {
                alerta("Erro", "Selecione um tipo de questão.");
                return;
            }
            if (fieldEnunciado == null || fieldEnunciado.getText().isEmpty()) {
                alerta("Erro", "O campo Enunciado é obrigatório.");
                return;
            }
            if (fieldDisciplina == null || fieldDisciplina.getText().isEmpty()) {
                alerta("Erro", "O campo Disciplina é obrigatório.");
                return;
            }
            if (fieldAssunto == null || fieldAssunto.getText().isEmpty()) {
                alerta("Erro", "O campo Assunto é obrigatório.");
                return;
            }
            if (grupoNivel == null || grupoNivel.getSelectedToggle() == null) {
                alerta("Erro", "Selecione o nível de dificuldade.");
                return;
            }

            switch (tipoAtual) {
                case MULTIPLA_ESCOLHA:
                    if (fieldAltA.getText().isEmpty() || fieldAltB.getText().isEmpty() ||
                            fieldAltC.getText().isEmpty() || fieldAltD.getText().isEmpty()) {
                        alerta("Erro", "Preencha todas as alternativas.");
                        return;
                    }
                    if (grupoGabarito == null || grupoGabarito.getSelectedToggle() == null) {
                        alerta("Erro", "Selecione o gabarito.");
                        return;
                    }
                    break;
                case DISCURSIVA:
                    if (fieldGabarito.getText().isEmpty()) {
                        alerta("Erro", "Preencha o gabarito.");
                        return;
                    }
                    break;
                case VERDADEIRO_FALSO:
                    if (grupoGabarito == null || grupoGabarito.getSelectedToggle() == null) {
                        alerta("Erro", "Selecione o gabarito (Verdadeiro ou Falso).");
                        return;
                    }
                    break;
            }

            // Busca disciplina
            DisciplinaDAO disciplinaDAO = new DisciplinaDAO(Conexao.abrirConexao());
            Disciplina disciplina = disciplinaDAO.buscarPorCodigo(fieldDisciplina.getText());
            if (disciplina == null) {
                alerta("Erro", "Disciplina não encontrada com o código: " + fieldDisciplina.getText());
                return;
            }

            String nivelText = ((RadioButton) grupoNivel.getSelectedToggle()).getText();
            Nivel nivel;
            if (nivelText.equalsIgnoreCase("Fácil")) nivel = Nivel.FACIL;
            else if (nivelText.equalsIgnoreCase("Médio")) nivel = Nivel.MEDIO;
            else nivel = Nivel.DIFICIL;

            Questao questao = construirQuestao(disciplina, nivel);
            if (questao == null) return;

            if (modoEdicao && questaoEmEdicao != null) {
                // Mantém o mesmo ID para sobrescrever
                questao.setCodigo(questaoEmEdicao.getCodigo());
                questaoService.alterar(questao);
                alerta("Sucesso", "Questão atualizada com sucesso!");
            } else {
                questaoService.inserir(questao);
                // Exibe o ID gerado pelo banco
                fieldCodigo.setText(String.valueOf(questao.getCodigo()));
                alerta("Sucesso", "Questão adicionada com sucesso! ID: " + questao.getCodigo());
                limparCampos();
            }

        } catch (Exception e) {
            e.printStackTrace();
            alerta("Erro", "Erro ao salvar questão: " + e.getMessage());
        }
    }

    private Questao construirQuestao(Disciplina disciplina, Nivel nivel) {
        switch (tipoAtual) {
            case MULTIPLA_ESCOLHA -> {
                MultiplaEscolha me = new MultiplaEscolha();
                me.setEnunciado(fieldEnunciado.getText());
                me.setDisciplina(disciplina);
                me.setNivel(nivel);
                me.setAssunto(fieldAssunto.getText());

                List<String> alts = Arrays.asList(
                        fieldAltA.getText(),
                        fieldAltB.getText(),
                        fieldAltC.getText(),
                        fieldAltD.getText()
                );
                me.setAlternativas(alts);

                RadioButton rbSelecionado = (RadioButton) grupoGabarito.getSelectedToggle();
                String letraSelecionada = rbSelecionado.getText();
                String respostaTexto = switch (letraSelecionada) {
                    case "a" -> fieldAltA.getText();
                    case "b" -> fieldAltB.getText();
                    case "c" -> fieldAltC.getText();
                    case "d" -> fieldAltD.getText();
                    default  -> "";
                };
                me.setResposta(respostaTexto);
                return me;
            }
            case DISCURSIVA -> {
                Discursiva d = new Discursiva();
                d.setEnunciado(fieldEnunciado.getText());
                d.setDisciplina(disciplina);
                d.setNivel(nivel);
                d.setAssunto(fieldAssunto.getText());
                d.setResposta(fieldGabarito.getText());
                return d;
            }
            case VERDADEIRO_FALSO -> {
                VerdadeiroFalso vf = new VerdadeiroFalso();
                vf.setEnunciado(fieldEnunciado.getText());
                vf.setDisciplina(disciplina);
                vf.setNivel(nivel);
                vf.setAssunto(fieldAssunto.getText());

                String respostaSelecionada = ((RadioButton) grupoGabarito.getSelectedToggle()).getText();
                vf.adicionarAlternativa("Verdadeiro", "Verdadeiro".equals(respostaSelecionada));
                vf.adicionarAlternativa("Falso",      "Falso".equals(respostaSelecionada));
                return vf;
            }
        }
        return null;
    }

    private void limparCampos() {
        fieldCodigo.clear();
        if (fieldEnunciado != null) fieldEnunciado.clear();
        if (fieldGabarito != null) fieldGabarito.clear();
        if (fieldDisciplina != null) fieldDisciplina.clear();
        if (fieldAssunto != null) fieldAssunto.clear();
        if (fieldAltA != null) fieldAltA.clear();
        if (fieldAltB != null) fieldAltB.clear();
        if (fieldAltC != null) fieldAltC.clear();
        if (fieldAltD != null) fieldAltD.clear();
        if (grupoGabarito != null) grupoGabarito.selectToggle(null);
        if (grupoNivel != null) grupoNivel.selectToggle(null);
        tipoAtual = TipoQuestao.NENHUM;
        tipoLabel.setText("escolha o tipo de questão");
        camposDinamicos.getChildren().clear();
        boxBtnAdicionar.setVisible(false);
        boxBtnAdicionar.setManaged(false);
        tipoSelector.setDisable(false);
        tipoSelector.setOpacity(1.0);
        modoEdicao = false;
        questaoEmEdicao = null;
        btnAdicionar.setText("Adicionar");
    }

    // ================================================================
    // POPUP DE TIPO (igual ao original)
    // ================================================================

    @FXML
    private void handleAbrirTipoPopup(MouseEvent event) {
        event.consume();
        boolean estaAberto = tipoPopup.isVisible();
        tipoPopup.setVisible(!estaAberto);
        tipoPopup.setManaged(!estaAberto);
    }

    @FXML private void handleSelecionarMultipla(MouseEvent event) { event.consume(); fecharPopup(); aplicarTipo(TipoQuestao.MULTIPLA_ESCOLHA); }
    @FXML private void handleSelecionarDiscursiva(MouseEvent event) { event.consume(); fecharPopup(); aplicarTipo(TipoQuestao.DISCURSIVA); }
    @FXML private void handleSelecionarVF(MouseEvent event) { event.consume(); fecharPopup(); aplicarTipo(TipoQuestao.VERDADEIRO_FALSO); }

    private void fecharPopup() {
        tipoPopup.setVisible(false);
        tipoPopup.setManaged(false);
    }

    // ================================================================
    // MONTAGEM DINÂMICA DOS CAMPOS (igual ao original)
    // ================================================================

    private void aplicarTipo(TipoQuestao tipo) {
        tipoAtual = tipo;
        camposDinamicos.getChildren().clear();

        switch (tipo) {
            case MULTIPLA_ESCOLHA  -> setTipoLabel("multipla escolha");
            case DISCURSIVA        -> setTipoLabel("discursiva");
            case VERDADEIRO_FALSO  -> setTipoLabel("verdadeiro ou falso");
        }

        fieldEnunciado  = criarTextField(340);
        fieldDisciplina = criarTextField(180);
        fieldAssunto    = criarTextField(180);
        grupoNivel      = new ToggleGroup();

        switch (tipo) {
            case MULTIPLA_ESCOLHA  -> montarCamposMultipla();
            case DISCURSIVA        -> montarCamposDiscursiva();
            case VERDADEIRO_FALSO  -> montarCamposVF();
        }

        boxBtnAdicionar.setVisible(true);
        boxBtnAdicionar.setManaged(true);
    }

    private void montarCamposMultipla() {
        fieldAltA = criarTextField(280);
        fieldAltB = criarTextField(280);
        fieldAltC = criarTextField(280);
        fieldAltD = criarTextField(280);
        grupoGabarito = new ToggleGroup();

        RadioButton rbA = criarRadio("a", grupoGabarito);
        RadioButton rbB = criarRadio("b", grupoGabarito);
        RadioButton rbC = criarRadio("c", grupoGabarito);
        RadioButton rbD = criarRadio("d", grupoGabarito);

        camposDinamicos.getChildren().add(labeledField("Enunciado:", fieldEnunciado));

        VBox colAlternativas = new VBox(4);
        colAlternativas.getChildren().add(label("alternativas:"));
        colAlternativas.getChildren().add(altRow("a:", fieldAltA));
        colAlternativas.getChildren().add(altRow("b:", fieldAltB));
        colAlternativas.getChildren().add(altRow("c:", fieldAltC));
        colAlternativas.getChildren().add(altRow("d:", fieldAltD));
        camposDinamicos.getChildren().add(colAlternativas);

        HBox linhaGabarito = new HBox(12);
        linhaGabarito.setAlignment(Pos.CENTER_LEFT);
        Label gabLabel = new Label("Gabarito:");
        gabLabel.getStyleClass().add("field-label");
        linhaGabarito.getChildren().addAll(gabLabel, rbA, rbB, rbC, rbD);
        camposDinamicos.getChildren().add(linhaGabarito);

        Region spacer = new Region(); spacer.setPrefHeight(10);
        camposDinamicos.getChildren().add(spacer);

        HBox linhaDiscAssunto = new HBox(30);
        linhaDiscAssunto.setAlignment(Pos.TOP_LEFT);
        VBox colDisc = new VBox(4); colDisc.getChildren().addAll(label("Disciplina:"), fieldDisciplina);
        VBox colAssunto = new VBox(4); colAssunto.getChildren().addAll(label("Assunto:"), fieldAssunto);
        linhaDiscAssunto.getChildren().addAll(colDisc, colAssunto);
        camposDinamicos.getChildren().add(linhaDiscAssunto);
        camposDinamicos.getChildren().add(criarNivelRow());
    }

    private void montarCamposDiscursiva() {
        fieldGabarito = criarTextField(340);
        camposDinamicos.getChildren().add(labeledField("Enunciado:", fieldEnunciado));
        camposDinamicos.getChildren().add(labeledField("Gabarito:", fieldGabarito));

        Region spacer = new Region(); spacer.setPrefHeight(10);
        camposDinamicos.getChildren().add(spacer);

        HBox linhaDiscAssunto = new HBox(30);
        linhaDiscAssunto.setAlignment(Pos.TOP_LEFT);
        VBox colDisc = new VBox(4); colDisc.getChildren().addAll(label("Disciplina:"), fieldDisciplina);
        VBox colAssunto = new VBox(4); colAssunto.getChildren().addAll(label("Assunto:"), fieldAssunto);
        linhaDiscAssunto.getChildren().addAll(colDisc, colAssunto);
        camposDinamicos.getChildren().add(linhaDiscAssunto);
        camposDinamicos.getChildren().add(criarNivelRow());
    }

    private void montarCamposVF() {
        grupoGabarito = new ToggleGroup();
        RadioButton rbFalso      = criarRadio("Falso",      grupoGabarito);
        RadioButton rbVerdadeiro = criarRadio("Verdadeiro", grupoGabarito);

        camposDinamicos.getChildren().add(labeledField("Enunciado:", fieldEnunciado));

        VBox vbGab = new VBox(5);
        vbGab.getChildren().add(label("Gabarito:"));
        HBox radios = new HBox(16, rbFalso, rbVerdadeiro);
        radios.setAlignment(Pos.CENTER_LEFT);
        vbGab.getChildren().add(radios);
        camposDinamicos.getChildren().add(vbGab);

        Region spacer = new Region(); spacer.setPrefHeight(10);
        camposDinamicos.getChildren().add(spacer);

        HBox linhaDiscAssunto = new HBox(30);
        linhaDiscAssunto.setAlignment(Pos.TOP_LEFT);
        VBox colDisc = new VBox(4); colDisc.getChildren().addAll(label("Disciplina:"), fieldDisciplina);
        VBox colAssunto = new VBox(4); colAssunto.getChildren().addAll(label("Assunto:"), fieldAssunto);
        linhaDiscAssunto.getChildren().addAll(colDisc, colAssunto);
        camposDinamicos.getChildren().add(linhaDiscAssunto);
        camposDinamicos.getChildren().add(criarNivelRow());
    }

    private HBox criarNivelRow() {
        HBox hb = new HBox(12);
        hb.setAlignment(Pos.CENTER_LEFT);
        Label nivelLabel = new Label("Nível da dificuldade:");
        nivelLabel.getStyleClass().add("field-label");
        RadioButton rbFacil   = criarRadio("Fácil",   grupoNivel);
        RadioButton rbMedio   = criarRadio("Médio",   grupoNivel);
        RadioButton rbDificil = criarRadio("Difícil", grupoNivel);
        hb.getChildren().addAll(nivelLabel, rbFacil, rbMedio, rbDificil);
        return hb;
    }

    // ================================================================
    // NAVEGAÇÃO SIDEBAR (igual ao original)
    // ================================================================

    @FXML private void handleVoltar(MouseEvent e)          { voltarInicial(); }
    @FXML private void handleMenuDisciplinas(MouseEvent e) { voltarInicial(); }
    @FXML private void handleMenuBuscar(MouseEvent e)      { abrirTelaBuscar(); }
    @FXML private void handleMenuGerarProva(MouseEvent e)  { abrirTelaGerarProva(); }
    @FXML private void handleMenuRelatorio(MouseEvent e)   { abrirRelatorio(); }

    private void abrirRelatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaRelatorioView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaRelatorioStyle.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            Stage stage = (Stage) menuRelatorio.getScene().getWindow();
            boolean fs = stage.isFullScreen(), max = stage.isMaximized();
            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Relatório");
            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void abrirTelaGerarProva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaGerarProvaView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaGerarProvaStyle.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            Stage stage = (Stage) menuGerarProva.getScene().getWindow();
            boolean fs = stage.isFullScreen(), max = stage.isMaximized();
            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Gerar Prova");
            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void abrirTelaBuscar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            Stage stage = (Stage) fieldCodigo.getScene().getWindow();
            boolean fs = stage.isFullScreen(), max = stage.isMaximized();
            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Buscar");
            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);
        } catch (Exception e) { e.printStackTrace(); alerta("Erro", "Não foi possível abrir a tela de buscar: " + e.getMessage()); }
    }

    private void voltarInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) fieldCodigo.getScene().getWindow();
            boolean fs = stage.isFullScreen(), max = stage.isMaximized();
            stage.setScene(new Scene(root, 1280, 750));
            stage.setTitle("Gerador de Provas - Disciplinas");
            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // ================================================================
    // HELPERS DE UI
    // ================================================================

    private void setTipoLabel(String texto) {
        tipoLabel.setText(texto);
        tipoLabel.getStyleClass().remove("tipo-label");
        tipoLabel.getStyleClass().add("tipo-label-selected");
    }

    private VBox labeledField(String labelText, TextField field) {
        VBox vb = new VBox(4);
        vb.getChildren().addAll(label(labelText), field);
        return vb;
    }

    private HBox altRow(String letra, TextField field) {
        HBox hb = new HBox(6);
        hb.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(letra);
        l.getStyleClass().add("radio-label");
        l.setMinWidth(18);
        hb.getChildren().addAll(l, field);
        return hb;
    }

    private Label label(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("field-label");
        return l;
    }

    private TextField criarTextField(double largura) {
        TextField tf = new TextField();
        tf.getStyleClass().add("field-input");
        tf.setPrefWidth(largura);
        tf.setMaxWidth(largura);
        return tf;
    }

    private RadioButton criarRadio(String texto, ToggleGroup grupo) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupo);
        rb.getStyleClass().add("radio-btn");
        return rb;
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}