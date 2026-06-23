package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.service.DisciplinaService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;
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

    private DisciplinaService disciplinaService;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disciplinaService = ServiceFactory.criarDisciplinaService();
    }

    /* ── VOLTAR (SETA) ───────────────────────────────────────── */
    @FXML
    private void handleVoltar(MouseEvent event) {
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
        abrirTelaGerarProva();
    }

    /* ── Menu: Relatório ─────────────────────────────────────── */
    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
        abrirRelatorio() ;
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

        try {
            Disciplina novaDisciplina = new Disciplina(nome, codigo, Contexto.getProfessorLogado(), null);
            disciplinaService.inserir(novaDisciplina);
            
            showAlert("Sucesso", "Disciplina '" + nome + "' adicionada com sucesso!");
            voltarParaTelaInicial();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao adicionar disciplina: " + e.getMessage());
        }
    }


    // NAVEGAÇÃO PARA TELA DE RELATÓRIO

    private void abrirRelatorio() {
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

    /* ── Abrir Tela Gerar Prova ────────────────────────────────────── */
    private void abrirTelaGerarProva(){
        try{
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