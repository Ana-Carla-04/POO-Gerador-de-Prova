package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TelaAdicionarDiscController implements Initializable {

    /* ── FXML ─────────────────────────────────────────────────── */
    @FXML private TextField fieldNome;
    @FXML private TextField fieldCodigo;
    @FXML private TextField fieldAssunto1;
    @FXML private TextField fieldAssunto2;
    @FXML private TextField fieldAssunto3;
    @FXML private TextField fieldAssunto4;
    @FXML private Button btnAdicionar;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;
    @FXML private StackPane menuProvas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialização vazia
    }

    /* ── VOLTAR (SETA) ───────────────────────────────────────── */
    @FXML
    private void handleVoltar(MouseEvent event) {
        System.out.println("🔙 Voltar para tela inicial...");
        voltarParaTelaInicial();
    }

    /* ── Menu: Disciplinas ───────────────────────────────────── */
    @FXML
    private void handleMenuDisciplinas(MouseEvent event) {
        voltarParaTelaInicial();
    }

    /* ── Menu: Buscar ────────────────────────────────────────── */
    @FXML
    private void handleMenuBuscar(MouseEvent event) {
        abrirTelaBuscar();
    }

    /* ── Menu: Gerar Prova ───────────────────────────────────── */
    @FXML
    private void handleMenuGerarProva(MouseEvent event) {
        showAlert("Gerar Prova", "Tela de geração de prova em breve!");
    }

    /* ── Menu: Relatório ─────────────────────────────────────── */
    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
        showAlert("Relatório", "Tela de relatórios em breve!");
    }

    /* ── Menu: Provas ────────────────────────────────────────── */
    @FXML
    private void handleMenuProvas(MouseEvent event) {
        showAlert("Provas", "Tela de provas em breve!");
    }

    /* ── Adicionar Disciplina ────────────────────────────────── */
    @FXML
    private void handleAdicionar() {
        String nome = fieldNome.getText().trim();
        String codigo = fieldCodigo.getText().trim();

        if (nome.isEmpty() || codigo.isEmpty()) {
            showAlert("Erro", "Preencha todos os campos obrigatórios!");
            return;
        }

        System.out.println("📚 Adicionando disciplina:");
        System.out.println("   Nome: " + nome);
        System.out.println("   Código: " + codigo);
        System.out.println("   Assunto 1: " + fieldAssunto1.getText());
        System.out.println("   Assunto 2: " + fieldAssunto2.getText());
        System.out.println("   Assunto 3: " + fieldAssunto3.getText());
        System.out.println("   Assunto 4: " + fieldAssunto4.getText());

        showAlert("Sucesso", "Disciplina '" + nome + "' adicionada com sucesso!");
        voltarParaTelaInicial();
    }

    /* ── Abrir Tela Buscar ────────────────────────────────────── */
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

            Stage stage = (Stage) fieldNome.getScene().getWindow();
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

            System.out.println(" Tela de buscar aberta com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível abrir a tela de buscar: " + e.getMessage());
        }
    }

    /* ── Voltar para Tela Inicial ────────────────────────────── */
    private void voltarParaTelaInicial() {
        try {
            System.out.println("🔄 Voltando para tela inicial...");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) fieldNome.getScene().getWindow();
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

            System.out.println("✅ Volta realizada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível voltar para a tela inicial: " + e.getMessage());
        }
    }

    /* ── Alertas ──────────────────────────────────────────────── */
    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}