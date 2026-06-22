package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.Contexto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import br.edu.ufersa.aplicativo.application.GerenteDeCena;

public class TelaInicialController implements Initializable {

    @FXML private Label topbarTitle;
    @FXML private GridPane disciplinasGrid;
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;
    @FXML private Button fabButton;

    private List<StackPane> menuItems;
    private List<DisciplinaInfo> disciplinasInfo;
    private Stage popupStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItems = Arrays.asList(
                menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio
        );

        inicializarDisciplinas();
        configurarGridResponsivo();
        carregarDisciplinas();
    }

    private void inicializarDisciplinas() {
        disciplinasInfo = Arrays.asList(
                new DisciplinaInfo("Matemática", "MAT001", 3, "Prof. Silva"),
                new DisciplinaInfo("Português", "POR001", 2, "Prof. Santos"),
                new DisciplinaInfo("História", "HIS001", 1, "Prof. Oliveira"),
                new DisciplinaInfo("Geografia", "GEO001", 0, "Prof. Costa"),
                new DisciplinaInfo("Física", "FIS001", 1, "Prof. Lima"),
                new DisciplinaInfo("Química", "QUI001", 0, "Prof. Almeida"),
                new DisciplinaInfo("Biologia", "BIO001", 1, "Prof. Ferreira"),
                new DisciplinaInfo("Inglês", "ING001", 0, "Prof. Pereira"),
                new DisciplinaInfo("Artes", "ART001", 0, "Prof. Carvalho")
        );
    }

    private void configurarGridResponsivo() {
        disciplinasGrid.getColumnConstraints().clear();
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(200);
            col.setMaxWidth(400);
            disciplinasGrid.getColumnConstraints().add(col);
        }

        disciplinasGrid.getRowConstraints().clear();
        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setMinHeight(140);
            row.setPrefHeight(160);
            row.setMaxHeight(200);
            disciplinasGrid.getRowConstraints().add(row);
        }
    }



    @FXML
    private void handleMenuDisciplinas(MouseEvent event) {
        selecionarMenu(menuDisciplinas);
        topbarTitle.setText("Disciplinas: Minhas Disciplinas");
        carregarDisciplinas();
    }

    // ═══════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO PARA TELA DE BUSCAR
    // ═══════════════════════════════════════════════════════════════════
    @FXML
    private void handleMenuBuscar(MouseEvent event) {
        try {
            System.out.println("🔍 Abrindo tela de buscar...");

            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml", "/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css", "Gerador de Provas - Buscar");

            System.out.println(" Tela de buscar aberta com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de buscar: " + e.getMessage());
        }
    }

    @FXML
    private void handleMenuGerarProva(MouseEvent event) {
        try {
            System.out.println(" Abrindo tela de gerar prova...");
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaGerarProvaView.fxml", "/br/edu/ufersa/aplicativo/css/TelaGerarProvaStyle.css", "Gerador de Provas - Gerar Prova");

            System.out.println("Tela de gerar prova aberta com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de gerar prova: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO PARA TELA DE RELATÓRIO
    // ═══════════════════════════════════════════════════════════════════
    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaRelatorioView.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaRelatorioStyle.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) menuRelatorio.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Relatório");

            if (isFullScreen) stage.setFullScreen(true);
            if (isMaximized) stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela de relatório: " + e.getMessage());
        }
    }




    // ═══════════════════════════════════════════════════════════════════
    // FAB - ABRE POPUP COM OPÇÕES
    // ═══════════════════════════════════════════════════════════════════
    @FXML
    private void handleAddDisciplina() {
        if (popupStage != null && popupStage.isShowing()) {
            popupStage.close();
            return;
        }
        mostrarPopupOpcoes();
    }

    private void mostrarPopupOpcoes() {
        popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.setAlwaysOnTop(true);

        VBox popupContent = new VBox();
        popupContent.setSpacing(8);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setStyle(
                "-fx-background-color: #0A4174;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 16 20 16 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );

        Label tituloLabel = new Label("Adicionar");
        tituloLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        HBox btnDisciplina = criarOpcao("📚 Adicionar Disciplina", () -> {
            popupStage.close();
            abrirTelaAdicionarDisciplina();
        });

        HBox btnQuestao = criarOpcao("📝 Adicionar Questão", () -> {
            popupStage.close();
            abrirTelaAdicionarQuestao();
        });

        popupContent.getChildren().addAll(tituloLabel, btnDisciplina, btnQuestao);

        Scene popupScene = new Scene(popupContent);
        popupScene.setFill(Color.TRANSPARENT);
        popupStage.setScene(popupScene);

        popupStage.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && popupStage.isShowing()) {
                popupStage.close();
            }
        });

        posicionarPopupAoTab();
        popupStage.show();

        Scene mainScene = fabButton.getScene();
        if (mainScene != null) {
            mainScene.setOnMouseClicked(event -> {
                if (popupStage != null && popupStage.isShowing()) {
                    popupStage.close();
                    mainScene.setOnMouseClicked(null);
                }
            });
        }
    }

    private HBox criarOpcao(String texto, Runnable acao) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(10);
        item.setStyle(
                "-fx-background-color: rgba(189, 216, 233, 0.15);" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 16 10 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 180;"
        );

        item.setOnMouseEntered(e -> {
            item.setStyle(
                    "-fx-background-color: rgba(189, 216, 233, 0.35);" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 180;"
            );
        });

        item.setOnMouseExited(e -> {
            item.setStyle(
                    "-fx-background-color: rgba(189, 216, 233, 0.15);" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 180;"
            );
        });

        Label label = new Label(texto);
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        item.getChildren().add(label);
        item.setOnMouseClicked(e -> acao.run());

        return item;
    }

    private void posicionarPopupAoTab() {
        if (popupStage == null || fabButton == null) return;

        javafx.application.Platform.runLater(() -> {
            double x = fabButton.localToScene(fabButton.getBoundsInLocal()).getMinX() - 220;
            double y = fabButton.localToScene(fabButton.getBoundsInLocal()).getMinY() - 120;

            Scene scene = fabButton.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                x += stage.getX() + scene.getX();
                y += stage.getY() + scene.getY();
            }

            popupStage.setX(x);
            popupStage.setY(y);
        });
    }

    private void abrirTelaAdicionarDisciplina() {
        try {
            System.out.println(" Abrindo tela para adicionar disciplina...");

            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaAdicionarDiscView.fxml", "/br/edu/ufersa/aplicativo/css/TelaAdicionarDiscStyle.css", "Gerador de Provas - Adicionar Disciplina");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de adicionar disciplina: " + e.getMessage());
        }
    }

    private void abrirTelaAdicionarQuestao() {
        try {
            System.out.println(" Abrindo tela para adicionar questão...");

            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaAdicionarQuestView.fxml", "/br/edu/ufersa/aplicativo/css/TelaAdicionarQuestStyle.css", "Gerador de Provas - Adicionar Questão");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Erro ao abrir tela de adicionar questão: " + e.getMessage());
        }
    }

    private void showAlert(String titulo, String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void selecionarMenu(StackPane item) {
        for (StackPane m : menuItems) {
            m.getStyleClass().remove("menu-item-selected");
        }
        if (!item.getStyleClass().contains("menu-item-selected")) {
            item.getStyleClass().add("menu-item-selected");
        }
    }

    private void carregarDisciplinas() {
        disciplinasGrid.getChildren().clear();

        int col = 0, row = 0;
        for (DisciplinaInfo info : disciplinasInfo) {
            StackPane card = new StackPane();
            card.getStyleClass().add("disciplina-card");

            VBox cardContent = new VBox();
            cardContent.setAlignment(Pos.CENTER);
            cardContent.setSpacing(8);

            Label nomeLabel = new Label(info.getNome());
            nomeLabel.getStyleClass().add("disciplina-label");

            Label qtdLabel = new Label(info.getQuantidade() + " questões");
            qtdLabel.getStyleClass().add("disciplina-qtd-label");
            qtdLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #0A4174;");

            cardContent.getChildren().addAll(nomeLabel, qtdLabel);
            card.getChildren().add(cardContent);

            card.setUserData(info);
            card.setOnMouseClicked(e -> handleCardDisciplina(info));

            GridPane.setColumnIndex(card, col);
            GridPane.setRowIndex(card, row);
            GridPane.setFillWidth(card, true);
            GridPane.setFillHeight(card, true);

            disciplinasGrid.getChildren().add(card);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void handleCardDisciplina(DisciplinaInfo info) {
        try {
            System.out.println("Disciplina selecionada: " + info.getNome());
            Contexto.setDisciplinaSelecionada(info);
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/QuestoesView.fxml", "/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css", "Gerador de Provas - " + info.getNome());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir tela de questões: " + e.getMessage());
        }
    }

    public static class DisciplinaInfo {
        private String nome;
        private String codigo;
        private int quantidade;
        private String professor;

        public DisciplinaInfo(String nome, String codigo, int quantidade, String professor) {
            this.nome = nome;
            this.codigo = codigo;
            this.quantidade = quantidade;
            this.professor = professor;
        }

        public String getNome() { return nome; }
        public String getCodigo() { return codigo; }
        public int getQuantidade() { return quantidade; }
        public String getProfessor() { return professor; }

        public void setNome(String nome) { this.nome = nome; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
        public void setProfessor(String professor) { this.professor = professor; }
    }
}