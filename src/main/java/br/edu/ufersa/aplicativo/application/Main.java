package br.edu.ufersa.aplicativo.application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GerenteDeCena.setStage(primaryStage);
        GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/LoginView.fxml", "/br/edu/ufersa/aplicativo/css/LoginStyle.css", "Gerente de Provas - Login");

        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}