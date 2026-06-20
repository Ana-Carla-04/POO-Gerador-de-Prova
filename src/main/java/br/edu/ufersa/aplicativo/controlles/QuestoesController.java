package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class QuestoesController implements Initializable {

    /* ── FXML ─────────────────────────────────────────────────── */
    @FXML private Label topbarTitle;
    @FXML private ComboBox<String> nivelCombo;
    @FXML private VBox listaContainer;
    @FXML private AnchorPane detalhePanel;

    // Detalhe
    @FXML private Label questaoCodigoLabel;
    @FXML private Label questaoNivelLabel;
    @FXML private Label questaoTipoLabel;
    @FXML private Label enunciadoLabel;
    @FXML private Label assuntoLabel;
    @FXML private Label disciplinaLabel;
    @FXML private GridPane gabaritoGrid;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;
    @FXML private StackPane menuProvas;

    private List<StackPane> menuItems;
    private HBox itemSelecionado;

    private DisciplinaInfo disciplinaInfo;
    private Map<String, List<QuestaoInfo>> questoesPorDisciplina;

    public void setDisciplinaInfo(DisciplinaInfo info) {
        this.disciplinaInfo = info;
        if (topbarTitle != null) {
            topbarTitle.setText(info.getNome() + ": questões");
        }
        if (disciplinaLabel != null) {
            disciplinaLabel.setText(info.getNome());
        }
        carregarQuestoes();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItems = Arrays.asList(
                menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio, menuProvas
        );

        inicializarQuestoesPorDisciplina();

        nivelCombo.setItems(FXCollections.observableArrayList(
                "Todos", "Nível 1", "Nível 2", "Nível 3"
        ));
        nivelCombo.setPromptText("nível da questão");
        nivelCombo.getSelectionModel().selectFirst();
    }

    private void inicializarQuestoesPorDisciplina() {
        questoesPorDisciplina = new HashMap<>();

        // Questões de Matemática
        questoesPorDisciplina.put("Matemática", Arrays.asList(
                new QuestaoInfo("QST001", "Álgebra", "Nível 2", "multipla escolha",
                        "Qual é o valor de x na equação 2x + 3 = 7?",
                        "b) x = 2", new String[]{"a) x = 1", "b) x = 2", "c) x = 3", "d) x = 4"}),
                new QuestaoInfo("QST002", "Geometria", "Nível 1", "verdadeiro/falso",
                        "Um triângulo equilátero possui todos os lados iguais.",
                        "a) Verdadeiro", new String[]{"a) Verdadeiro", "b) Falso"}),
                new QuestaoInfo("QST003", "Trigonometria", "Nível 3", "multipla escolha",
                        "Qual o valor de sen(30°)?",
                        "b) 0,5", new String[]{"a) 0", "b) 0,5", "c) 1", "d) 1,5"})
                
        ));

        // Questões de Português
        questoesPorDisciplina.put("Português", Arrays.asList(
                new QuestaoInfo("QST007", "Gramática", "Nível 1", "multipla escolha",
                        "Qual é o plural de 'cidadão'?",
                        "a) cidadãos", new String[]{"a) cidadãos", "b) cidadões", "c) cidadães", "d) cidadãos"}),
                new QuestaoInfo("QST008", "Interpretação", "Nível 2", "discursiva",
                        "Analise a frase: 'O sol brilhava intensamente'.",
                        "a) O sol", new String[]{"a) O sol", "b) brilhava", "c) intensamente", "d) O sol brilhava"})
        ));

        // Questões de História
        questoesPorDisciplina.put("História", Arrays.asList(
                new QuestaoInfo("QST010", "Brasil", "Nível 2", "multipla escolha",
                        "Em que ano o Brasil foi descoberto?",
                        "b) 1500", new String[]{"a) 1492", "b) 1500", "c) 1502", "d) 1498"})
        ));

        // Questões de Física
        questoesPorDisciplina.put("Física", Arrays.asList(
                new QuestaoInfo("QST015", "Mecânica", "Nível 2", "multipla escolha",
                        "Qual a fórmula da segunda lei de Newton?",
                        "a) F = m.a", new String[]{"a) F = m.a", "b) F = m.v", "c) F = m/t", "d) F = v/t"})
        ));

        // Questões de Biologia
        questoesPorDisciplina.put("Biologia", Arrays.asList(
                new QuestaoInfo("QST019", "Biologia Geral", "Nível 1", "multipla escolha",
                        "Qual a unidade básica da vida?",
                        "c) Célula", new String[]{"a) Átomo", "b) Molécula", "c) Célula", "d) Tecido"})
        ));
    }

    @FXML
    private void handleNivelFiltro() {
        carregarQuestoes();
    }

    @FXML
    private void handleSelecionarQuestao(MouseEvent event) {
        HBox clicado = (HBox) event.getSource();
        if (itemSelecionado != null) {
            itemSelecionado.getStyleClass().remove("questao-item-selected");
        }
        clicado.getStyleClass().add("questao-item-selected");
        itemSelecionado = clicado;

        QuestaoInfo questao = (QuestaoInfo) clicado.getUserData();
        if (questao != null) {
            atualizarDetalhe(questao);
        }
    }

    @FXML
    private void handleExcluir() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir questão");
        alert.setHeaderText("Confirmar exclusão");
        alert.setContentText("Deseja realmente excluir esta questão?");
        alert.showAndWait().ifPresent(resp -> {
            carregarQuestoes();
        });
    }

    @FXML
    private void handleEditar() {
        System.out.println("Editar questão: " + questaoCodigoLabel.getText());
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) topbarTitle.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Disciplinas");

            if (isFullScreen) {
                stage.setFullScreen(true);
            }
            if (isMaximized) {
                stage.setMaximized(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleMenuDisciplinas(MouseEvent e) {
        selecionarMenu(menuDisciplinas);
        handleVoltar();
    }
    @FXML private void handleMenuBuscar(MouseEvent e) {
        abrirTelaBuscar();
    }
    @FXML
    private void handleMenuGerarProva(MouseEvent event) {
        try {
            System.out.println(" Abrindo tela de gerar prova...");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaGerarProvaView.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            // Carregar CSS específico
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaGerarProvaStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) menuGerarProva.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Gerar Prova");

            if (isFullScreen) {
                stage.setFullScreen(true);
            }
            if (isMaximized) {
                stage.setMaximized(true);
            }

            System.out.println(" Tela de gerar prova aberta com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de gerar prova: " + e.getMessage());
        }
    }

    @FXML private void handleMenuRelatorio(MouseEvent e) {
        selecionarMenu(menuRelatorio);
    }
    @FXML private void handleMenuProvas(MouseEvent e) {
        selecionarMenu(menuProvas);
    }



    private void abrirTelaBuscar() {
        try {
            System.out.println("🔍 Abrindo tela de buscar...");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) topbarTitle.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Buscar");

            if (isFullScreen) {
                stage.setFullScreen(true);
            }
            if (isMaximized) {
                stage.setMaximized(true);
            }

            System.out.println("✅ Tela de buscar aberta com sucesso!");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("❌ Erro ao abrir tela de buscar: " + ex.getMessage());
        }
    }

    private void carregarQuestoes() {
        listaContainer.getChildren().clear();

        if (disciplinaInfo == null) {
            System.out.println("Disciplina não definida!");
            return;
        }

        String nivelSelecionado = nivelCombo.getSelectionModel().getSelectedItem();
        List<QuestaoInfo> todasQuestoes = questoesPorDisciplina.get(disciplinaInfo.getNome());

        if (todasQuestoes == null || todasQuestoes.isEmpty()) {
            Label vazio = new Label("Nenhuma questão encontrada para esta disciplina.");
            vazio.setStyle("-fx-text-fill: #001D39; -fx-font-size: 14px;");
            listaContainer.getChildren().add(vazio);
            return;
        }

        boolean primeiro = true;
        for (QuestaoInfo q : todasQuestoes) {
            if (!nivelSelecionado.equals("Todos") && !q.getNivel().equals(nivelSelecionado)) {
                continue;
            }

            HBox item = new HBox();
            item.getStyleClass().add("questao-item");
            item.setUserData(q);

            Label lbl = new Label(q.getAssunto());
            lbl.getStyleClass().add("questao-item-label");
            item.getChildren().add(lbl);

            item.setOnMouseClicked(this::handleSelecionarQuestao);

            if (primeiro) {
                item.getStyleClass().add("questao-item-selected");
                itemSelecionado = item;
                atualizarDetalhe(q);
                primeiro = false;
            }

            listaContainer.getChildren().add(item);
        }
    }

    private void atualizarDetalhe(QuestaoInfo q) {
        questaoCodigoLabel.setText(q.getCodigo());
        questaoNivelLabel.setText(q.getNivel());
        questaoTipoLabel.setText(q.getTipo());
        enunciadoLabel.setText(q.getEnunciado());
        assuntoLabel.setText(q.getAssunto());

        if (disciplinaInfo != null) {
            disciplinaLabel.setText(disciplinaInfo.getNome());
        }

        preencherGabarito(q);
    }

    private void preencherGabarito(QuestaoInfo q) {
        gabaritoGrid.getChildren().clear();

        String[] alternativas = q.getAlternativas();
        String gabarito = q.getGabarito();

        if (alternativas == null || alternativas.length == 0) {
            Label msg = new Label("Sem alternativas");
            msg.getStyleClass().add("det-normal");
            GridPane.setColumnIndex(msg, 0);
            GridPane.setRowIndex(msg, 0);
            gabaritoGrid.getChildren().add(msg);
            return;
        }

        // Calcula quantas linhas serão necessárias (metade das alternativas)
        int metade = (int) Math.ceil(alternativas.length / 2.0);

        for (int i = 0; i < alternativas.length; i++) {
            // Coluna 0: Primeira metade das alternativas (a, b, c...)
            // Coluna 1: Segunda metade das alternativas (d, e, f...)
            int col = i < metade ? 0 : 1;
            int row = i < metade ? i : i - metade;

            Label altLabel = new Label(alternativas[i]);

            // ═══════════════════════════════════════════════════════════
            // HABILITAR QUEBRA DE LINHA E CRESCIMENTO
            // ═══════════════════════════════════════════════════════════
            altLabel.setWrapText(true);  // ← Quebra o texto
            altLabel.setMaxWidth(Double.MAX_VALUE);  // ← Permite crescer

            if (alternativas[i].equals(gabarito)) {
                altLabel.getStyleClass().add("det-gabarito");
            } else {
                altLabel.getStyleClass().add("det-normal");
            }

            GridPane.setColumnIndex(altLabel, col);
            GridPane.setRowIndex(altLabel, row);
            GridPane.setFillWidth(altLabel, true);  // ← Ocupa toda a largura
            gabaritoGrid.getChildren().add(altLabel);
        }

    }

    private void selecionarMenu(StackPane item) {
        for (StackPane m : menuItems) {
            m.getStyleClass().remove("menu-item-selected");
        }
        if (!item.getStyleClass().contains("menu-item-selected")) {
            item.getStyleClass().add("menu-item-selected");
        }
    }

    public static class QuestaoInfo {
        private String codigo;
        private String assunto;
        private String nivel;
        private String tipo;
        private String enunciado;
        private String gabarito;
        private String[] alternativas;

        public QuestaoInfo(String codigo, String assunto, String nivel, String tipo,
                           String enunciado, String gabarito, String[] alternativas) {
            this.codigo = codigo;
            this.assunto = assunto;
            this.nivel = nivel;
            this.tipo = tipo;
            this.enunciado = enunciado;
            this.gabarito = gabarito;
            this.alternativas = alternativas;
        }

        public String getCodigo() { return codigo; }
        public String getAssunto() { return assunto; }
        public String getNivel() { return nivel; }
        public String getTipo() { return tipo; }
        public String getEnunciado() { return enunciado; }
        public String getGabarito() { return gabarito; }
        public String[] getAlternativas() { return alternativas; }
    }
}