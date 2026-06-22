package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.GerenteDeCena;
import br.edu.ufersa.aplicativo.application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroController {

    @FXML private TextField     nomeField;
    @FXML private TextField     emailField;
    @FXML private PasswordField senhaField;
    @FXML private PasswordField confirmarSenhaField;
    @FXML private Button        cadastrarButton;
    @FXML private Hyperlink     loginLink;

    // ── CADASTRAR ──────────────────────────────────────────────────────────
    @FXML
    private void handleCadastrar(ActionEvent event) {
        String nome           = nomeField.getText().trim();
        String email          = emailField.getText().trim();
        String senha          = senhaField.getText().trim();
        String confirmarSenha = confirmarSenhaField.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            showAlert(AlertType.WARNING, "Campos vazios", "Atenção",
                    "Por favor, preencha todos os campos.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            showAlert(AlertType.ERROR, "Senhas diferentes", "Erro",
                    "As senhas não coincidem. Tente novamente.");
            senhaField.clear();
            confirmarSenhaField.clear();
            return;
        }

        if (senha.length() < 6) {
            showAlert(AlertType.WARNING, "Senha fraca", "Atenção",
                    "A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        showAlert(AlertType.INFORMATION, "Cadastro realizado!", "Sucesso",
                "Conta criada com sucesso! Faça login para continuar.");

        irParaLogin(event);
    }

    // ── VOLTAR PARA LOGIN ──────────────────────────────────────────────────
    @FXML
    private void handleFazerLogin(ActionEvent event) {
        irParaLogin(event);
    }

    private void irParaLogin(ActionEvent event) {
        try {
            // Carregar a nova tela
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/LoginView.fxml", "/br/edu/ufersa/aplicativo/css/LoginStyle.css", "Gerente de provas - Login");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erro", "Erro de navegação",
                    "Não foi possível carregar a tela de login: " + e.getMessage());
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