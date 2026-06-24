package br.edu.ufersa.aplicativo.application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GerenteDeCena {
    private static Stage stage;

    public static void setStage(Stage stage) {
        if (stage != null) GerenteDeCena.stage = stage;
        else throw new IllegalArgumentException("Stage invalido");
    }

    public static void carregarCena(String fxmlPath, String cssPath, String titulo) {
        FXMLLoader fxmlLoader = new FXMLLoader(GerenteDeCena.class.getResource(fxmlPath));
        try {
            Scene cena = new Scene(fxmlLoader.load());
            cena.getStylesheets().add(GerenteDeCena.class.getResource(cssPath).toExternalForm());
            stage.setTitle(titulo);
            stage.setScene(cena);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
