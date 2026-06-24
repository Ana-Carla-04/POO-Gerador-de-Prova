package br.edu.ufersa.aplicativo.controlles;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.model.entities.Professor;
import br.edu.ufersa.aplicativo.model.dto.TentarLoginDTO;
import br.edu.ufersa.aplicativo.model.service.AutenticacaoService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;

import br.edu.ufersa.aplicativo.application.GerenteDeCena;

public class LoginController {
    private final AutenticacaoService autenticacaoService;

    public LoginController() {
        autenticacaoService = ServiceFactory.criarAutenticacaoService();
    }

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Hyperlink     registerLink;

    // ── LOGIN ──────────────────────────────────────────────────────────────
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Campos vazios", "Atenção",
                    "Por favor, preencha todos os campos.");
            return;
        }

        TentarLoginDTO dto = new TentarLoginDTO(email, password);

        Optional<Professor> professorLogado = autenticacaoService.tentarLogin(dto);
        if (professorLogado.isEmpty()) {
            showAlert(AlertType.ERROR, "Falha no login", "Erro", "Email ou senha incorreto.");
        } else {
            Contexto.setProfessorLogado(professorLogado.get());
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml", "/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css", "Gerador de Provas - Tela inicial");
        }
    }

    // ── IR PARA CADASTRO ───────────────────────────────────────────────────
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Carregar a nova tela
            GerenteDeCena.carregarCena("/br/edu/ufersa/aplicativo/views/CadastroView.fxml", "/br/edu/ufersa/aplicativo/css/LoginStyle.css","Gerador de Provas - Cadastro");
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