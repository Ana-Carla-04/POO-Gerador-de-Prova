package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.application.GerenteDeCena;
import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import br.edu.ufersa.aplicativo.model.service.QuestaoService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;
import br.edu.ufersa.aplicativo.model.entities.Discursiva;
import br.edu.ufersa.aplicativo.model.entities.Questao;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class QuestoesController implements Initializable {

    private QuestaoService questaoService;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questaoService = ServiceFactory.criarQuestaoService();
        disciplinaInfo = Contexto.getDisciplinaSelecionada();
        menuItems = Arrays.asList(
                menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio
        );

        inicializarQuestoesPorDisciplina();

        nivelCombo.setItems(FXCollections.observableArrayList(
                "Todos", "Nível 1", "Nível 2", "Nível 3"
        ));
        nivelCombo.setPromptText("nível da questão");
        nivelCombo.getSelectionModel().selectFirst();

        carregarQuestoes();
    }

    private void inicializarQuestoesPorDisciplina() {
        questoesPorDisciplina = new HashMap<>();

        try {
            List<Questao> todasAsQuestoesDoBanco = questaoService.listarQuestoes();

            for (Questao q : todasAsQuestoesDoBanco) {
                String nomeDisciplina = (q.getDisciplina() != null) ? q.getDisciplina().getNome() : "Geral";

                questoesPorDisciplina.putIfAbsent(nomeDisciplina, new ArrayList<>());

                QuestaoInfo info = new QuestaoInfo(
                        String.valueOf(q.getCodigo()),
                        q.getAssunto() != null ? q.getAssunto() : "Sem Assunto",
                        "Nível " + q.getNivel(),
                        "discursiva",
                        q.getEnunciado(),
                        "",
                        new String[]{}
                );

                questoesPorDisciplina.get(nomeDisciplina).add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar dados do banco: " + e.getMessage());
        }
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
        if (itemSelecionado == null) {
            showAlert("Erro", "Selecione uma questão para excluir.");
            return;
        }

        QuestaoInfo info = (QuestaoInfo) itemSelecionado.getUserData();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir questão");
        alert.setHeaderText("Confirmar exclusão");
        alert.setContentText("Deseja realmente excluir a questão: " + info.getEnunciado().substring(0, Math.min(info.getEnunciado().length(), 20)) + "...?");
        
        alert.showAndWait().ifPresent(resp -> {
            if (resp == javafx.scene.control.ButtonType.OK) {
                try {
                    Discursiva q = new Discursiva();
                    q.setCodigo(Integer.parseInt(info.getCodigo()));
                    questaoService.deletar(q);
                    
                    // Remover da lista local para atualizar a UI sem recarregar tudo
                    questoesPorDisciplina.get(disciplinaInfo.getNome()).remove(info);
                    
                    carregarQuestoes();
                    showAlert("Sucesso", "Questão excluída com sucesso.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erro", "Erro ao excluir questão: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void handleEditar() {
        System.out.println("Editar questão: " + questaoCodigoLabel.getText());
    }

    @FXML
    private void handleVoltar() {
        try {
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml", "/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css", "Gerador de Provas - Disciplinas");
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
        abrirTelaGerarProva();
    }

    private void abrirTelaGerarProva(){
        try{
            System.out.println(" Abrindo tela de gerar prova...");
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaGerarProvaView.fxml", "/br/edu/ufersa/aplicativo/css/TelaGerarProvaStyle.css", "Gerador de Provas - Gerar Prova");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de gerar prova: " + e.getMessage());
        }
    }

    private void abrirTelaBuscar() {
        try {
            System.out.println("🔍 Abrindo tela de buscar...");
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml", "/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css", "Gerador de Provas - Buscar");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(" Erro ao abrir tela de buscar: " + ex.getMessage());
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

        int metade = (int) Math.ceil(alternativas.length / 2.0);

        for (int i = 0; i < alternativas.length; i++) {
            int col = i < metade ? 0 : 1;
            int row = i < metade ? i : i - metade;

            Label altLabel = new Label(alternativas[i]);
            altLabel.setWrapText(true);
            altLabel.setMaxWidth(Double.MAX_VALUE);

            if (alternativas[i].equals(gabarito)) {
                altLabel.getStyleClass().add("det-gabarito");
            } else {
                altLabel.getStyleClass().add("det-normal");
            }

            GridPane.setColumnIndex(altLabel, col);
            GridPane.setRowIndex(altLabel, row);
            GridPane.setFillWidth(altLabel, true);
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