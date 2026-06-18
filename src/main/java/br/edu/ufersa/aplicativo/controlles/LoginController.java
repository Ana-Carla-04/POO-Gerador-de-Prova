package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Hyperlink     registerLink;

    // ── LOGIN ──────────────────────────────────────────────────────────────
    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Campos vazios", "Atenção",
                    "Por favor, preencha todos os campos.");
            return;
        }

        if (email.equals("admin@email.com") && password.equals("123456")) {
            showAlert(AlertType.INFORMATION, "Login realizado!", "Bem-vindo",
                    "Bem-vindo ao Gerador de Provas!");
        } else {
            showAlert(AlertType.ERROR, "Falha no login", "Erro",
                    "Email ou senha incorretos.");
            passwordField.clear();
        }
    }

    // ── IR PARA CADASTRO ───────────────────────────────────────────────────
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            System.out.println("Abrindo Cadastro - FullScreen atual: " + Main.isFullScreen());

            // Salvar o estado atual
            boolean currentFullScreen = Main.isFullScreen();
            boolean currentMaximized = Main.isMaximized();

            // Carregar a nova tela
            Main.carregarTela("/br/edu/ufersa/aplicativo/views/CadastroView.fxml",
                    "Gerador de Provas - Cadastro");

            // Forçar a restauração do estado (redundante, mas seguro)
            javafx.application.Platform.runLater(() -> {
                if (currentFullScreen) {
                    Main.enterFullScreen();
                }
                if (currentMaximized) {
                    Main.toggleMaximized();
                }
            });

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erro", "Erro de navegação",
                    "Não foi possível carregar a tela de cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── UTILITÁRIO ─────────────────────────────────────────────────────────
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}