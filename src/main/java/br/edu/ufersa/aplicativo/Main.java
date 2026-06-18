package br.edu.ufersa.aplicativo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

public class Main extends Application {

    private static Stage stage;
    private static Scene currentScene;
    private static boolean isFullScreen = false;
    private static boolean isMaximized = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Adicionar listener para capturar mudanças de tela cheia
        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            isFullScreen = newValue;
            System.out.println("FullScreen mudou para: " + newValue);
        });

        // Adicionar listener para capturar mudanças de maximizado
        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            isMaximized = newValue;
            System.out.println("Maximized mudou para: " + newValue);
        });

        // Carregar a tela inicial
        carregarTela("/br/edu/ufersa/aplicativo/views/LoginView.fxml", "Gerador de Provas - Login");

        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.show();
    }

    // ════════════════════════════════════════════════════════════════════
    // MÉTODO PARA CARREGAR TELA MANTENDO O ESTADO DE TELA CHEIA
    // ════════════════════════════════════════════════════════════════════
    public static void carregarTela(String fxmlPath, String titulo) throws Exception {
        URL fxmlUrl = Main.class.getResource(fxmlPath);

        if (fxmlUrl == null) {
            throw new RuntimeException("FXML não encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 750);

        URL cssUrl = Main.class.getResource("/br/edu/ufersa/aplicativo/css/LoginStyle.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // SALVAR O ESTADO ATUAL ANTES DE TROCAR
        if (stage != null) {
            isFullScreen = stage.isFullScreen();
            isMaximized = stage.isMaximized();

            System.out.println("Estado antes da troca - FullScreen: " + isFullScreen + ", Maximized: " + isMaximized);
        }

        stage.setTitle(titulo);
        stage.setScene(scene);
        currentScene = scene;

        // RESTAURAR O ESTADO APÓS TROCAR A SCENE
        // Usar Platform.runLater para garantir que a troca foi concluída
        javafx.application.Platform.runLater(() -> {
            if (stage != null) {
                if (isFullScreen) {
                    stage.setFullScreen(true);
                    System.out.println("Restaurando FullScreen: true");
                }
                if (isMaximized) {
                    stage.setMaximized(true);
                    System.out.println("Restaurando Maximized: true");
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════
    // MÉTODOS DE CONTROLE DE TELA CHEIA
    // ════════════════════════════════════════════════════════════════════
    public static void toggleFullScreen() {
        if (stage != null) {
            isFullScreen = !stage.isFullScreen();
            stage.setFullScreen(isFullScreen);
        }
    }

    public static void enterFullScreen() {
        if (stage != null) {
            isFullScreen = true;
            stage.setFullScreen(true);
        }
    }

    public static void exitFullScreen() {
        if (stage != null) {
            isFullScreen = false;
            stage.setFullScreen(false);
        }
    }

    public static void toggleMaximized() {
        if (stage != null) {
            isMaximized = !stage.isMaximized();
            stage.setMaximized(isMaximized);
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static boolean isFullScreen() {
        return isFullScreen;
    }

    public static boolean isMaximized() {
        return isMaximized;
    }

    public static void main(String[] args) {
        launch(args);
    }
}