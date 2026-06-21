package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class TelaGerarProvaController implements Initializable {

    @FXML private Label topbarTitle;

    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;
    @FXML private StackPane menuProvas;

    @FXML private StackPane cardGerarAleatorio;
    @FXML private StackPane cardGerarManual;

    @FXML private TextField numeroQuestoesField;
    @FXML private TextField professorField;
    @FXML private TextField instituicaoField;

    @FXML private RadioButton nivel1Op1, nivel1Op2, nivel1Op3, nivel1Op4, nivel1Op5;
    @FXML private RadioButton nivel2Op1, nivel2Op2, nivel2Op3, nivel2Op4, nivel2Op5;
    @FXML private RadioButton nivel3Op1, nivel3Op2, nivel3Op3, nivel3Op4, nivel3Op5;

    @FXML private ComboBox<String> disciplinaComboBox;

    private List<StackPane> menuItems;
    private int totalQuestoes = 0;
    private boolean isUpdating = false;

    /** niveisRadioButtons[0] = Fácil, [1] = Médio, [2] = Difícil. */
    private RadioButton[][] niveisRadioButtons;
    private static final Nivel[] ORDEM_NIVEIS = { Nivel.FACIL, Nivel.MEDIO, Nivel.DIFICIL };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItems = Arrays.asList(
                menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio, menuProvas
        );
        selecionarMenu(menuGerarProva);

        niveisRadioButtons = new RadioButton[][]{
                {nivel1Op1, nivel1Op2, nivel1Op3, nivel1Op4, nivel1Op5},
                {nivel2Op1, nivel2Op2, nivel2Op3, nivel2Op4, nivel2Op5},
                {nivel3Op1, nivel3Op2, nivel3Op3, nivel3Op4, nivel3Op5}
        };

        desabilitarTodosNiveis();

        numeroQuestoesField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleNumeroQuestoesChanged();
        });

        adicionarListenersNiveis();

        // Carregar disciplinas no ComboBox
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        List<Questao> questoes = ProvaSessao.bancoDeQuestoes();
        Set<String> disciplinasSet = new HashSet<>();
        for (Questao q : questoes) {
            if (q.getDisciplina() != null) {
                disciplinasSet.add(q.getDisciplina().getNome());
            }
        }

        ObservableList<String> itens = FXCollections.observableArrayList(disciplinasSet);
        FXCollections.sort(itens);
        disciplinaComboBox.setItems(itens);
    }

    // ═══════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO DO MENU LATERAL
    // ═══════════════════════════════════════════════════════════════════
    @FXML
    private void handleMenuDisciplinas(MouseEvent event) {
        navegarPara(
                "/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml",
                "/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css",
                "Gerador de Provas - Disciplinas",
                menuDisciplinas
        );
    }

    @FXML
    private void handleMenuBuscar(MouseEvent event) {
        navegarPara(
                "/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml",
                "/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css",
                "Gerador de Provas - Buscar",
                menuBuscar
        );
    }

    @FXML
    private void handleMenuGerarProva(MouseEvent event) {
        // já estou aqui
    }

    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
        selecionarMenu(menuRelatorio);
        topbarTitle.setText("Relatório");
    }

    @FXML
    private void handleMenuProvas(MouseEvent event) {
        selecionarMenu(menuProvas);
        topbarTitle.setText("Provas");
    }

    private void navegarPara(String fxmlPath, String cssPath, String tituloJanela, StackPane origem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) origem.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle(tituloJanela);

            if (isFullScreen) stage.setFullScreen(true);
            if (isMaximized) stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao navegar para " + fxmlPath + ": " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CARDS DE OPÇÃO (GERAR ALEATÓRIO / GERAR MANUAL)
    // ═══════════════════════════════════════════════════════════════════

    @FXML
    private void handleGerarAleatorio(MouseEvent event) {
        DadosFormulario dados = validarFormulario();
        if (dados == null) return;

        // Filtra questões pela disciplina selecionada
        List<Questao> questoesDisciplina = new ArrayList<>();
        for (Questao q : ProvaSessao.bancoDeQuestoes()) {
            if (q.getDisciplina() != null && q.getDisciplina().getNome().equals(dados.disciplina)) {
                questoesDisciplina.add(q);
            }
        }

        List<Questao> sorteadas = sortearQuestoes(questoesDisciplina, dados.qtdNivel1, dados.qtdNivel2, dados.qtdNivel3);
        if (sorteadas == null) return;

        ProvaSessao sessao = ProvaSessao.getInstance();
        sessao.setProfessor(dados.professor);
        sessao.setInstituicao(dados.instituicao);
        sessao.setDisciplina(dados.disciplina);
        sessao.setTotalQuestoes(dados.totalQuestoes);
        sessao.setQuestoes(sorteadas);

        abrirTelaGerarAuto();
    }

    @FXML
    private void handleGerarManual(MouseEvent event) {
        DadosFormulario dados = validarFormulario();
        if (dados == null) return;

        ProvaSessao sessao = ProvaSessao.getInstance();
        sessao.setProfessor(dados.professor);
        sessao.setInstituicao(dados.instituicao);
        sessao.setDisciplina(dados.disciplina);
        sessao.setTotalQuestoes(dados.totalQuestoes);
        sessao.setQuestoes(Collections.emptyList());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaGerarProvaManualView.fxml"));
            Parent root = loader.load();

            TelaGerarProvaManualController controller = loader.getController();
            controller.setDisciplinaSelecionada(dados.disciplina);
            controller.setQuantidadePredefinida(dados.totalQuestoes);

            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaGerarProvaManualStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) cardGerarManual.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Manual");

            if (isFullScreen) stage.setFullScreen(true);
            if (isMaximized) stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao navegar para TelaGerarProvaManualView: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a tela de geração manual.");
        }
    }

    private void abrirTelaGerarAuto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaGerarAutoView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaGerarAutoStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) cardGerarAleatorio.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Prova Gerada");

            if (isFullScreen) stage.setFullScreen(true);
            if (isMaximized) stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao navegar para TelaGerarAutoView: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a tela de prova gerada.");
        }
    }

    private List<Questao> sortearQuestoes(List<Questao> questoesDisciplina, int qtdNivel1, int qtdNivel2, int qtdNivel3) {
        List<Questao> resultado = new ArrayList<>();
        int[] quantidades = {qtdNivel1, qtdNivel2, qtdNivel3};

        for (int i = 0; i < ORDEM_NIVEIS.length; i++) {
            int qtd = quantidades[i];
            if (qtd == 0) continue;
            Nivel nivel = ORDEM_NIVEIS[i];

            List<Questao> doNivel = new ArrayList<>();
            for (Questao q : questoesDisciplina) {
                if (q.getNivel() == nivel) doNivel.add(q);
            }
            Collections.shuffle(doNivel);

            if (doNivel.size() < qtd) {
                showAlert("Questões insuficientes",
                        "Não há questões suficientes cadastradas no nível " + nivel.getValor() +
                                " para a disciplina " + disciplinaComboBox.getSelectionModel().getSelectedItem() +
                                ".");
                return null;
            }
            resultado.addAll(doNivel.subList(0, qtd));
        }

        Collections.shuffle(resultado);
        return resultado;
    }

    // ═══════════════════════════════════════════════════════════════════
    // GERENCIAMENTO DE QUANTIDADE DE QUESTÕES
    // ═══════════════════════════════════════════════════════════════════
    @FXML
    private void handleNumeroQuestoesChanged() {
        try {
            String text = numeroQuestoesField.getText();
            if (text == null || text.isEmpty()) {
                totalQuestoes = 0;
                desabilitarTodosNiveis();
                return;
            }

            totalQuestoes = Integer.parseInt(text);
            if (totalQuestoes < 1 || totalQuestoes > 10) {
                totalQuestoes = 0;
                numeroQuestoesField.setText("");
                showAlert("Valor inválido", "Digite um número entre 1 e 10.");
                desabilitarTodosNiveis();
                return;
            }

            isUpdating = true;
            limparTodasSelecoes();
            atualizarOpcoesNiveis();
            isUpdating = false;

        } catch (NumberFormatException e) {
            totalQuestoes = 0;
            desabilitarTodosNiveis();
        }
    }

    private void limparTodasSelecoes() {
        for (RadioButton[] nivel : niveisRadioButtons) {
            for (RadioButton rb : nivel) {
                rb.setSelected(false);
            }
        }
    }

    private void desabilitarTodosNiveis() {
        isUpdating = true;
        for (RadioButton[] nivel : niveisRadioButtons) {
            for (RadioButton rb : nivel) {
                rb.setDisable(true);
                rb.setSelected(false);
            }
        }
        isUpdating = false;
    }

    private void atualizarOpcoesNiveis() {
        if (totalQuestoes == 0) {
            desabilitarTodosNiveis();
            return;
        }

        isUpdating = true;
        for (RadioButton[] nivel : niveisRadioButtons) {
            for (RadioButton rb : nivel) {
                rb.setDisable(false);
            }
        }
        isUpdating = false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // LISTENERS PARA RADIO BUTTONS
    // ═══════════════════════════════════════════════════════════════════
    private void adicionarListenersNiveis() {
        for (RadioButton[] nivel : niveisRadioButtons) {
            for (RadioButton rb : nivel) {
                rb.setOnAction(e -> handleNivelChanged());
            }
        }
    }

    @FXML
    private void handleNivelChanged() {
        if (isUpdating || totalQuestoes == 0) return;

        int[] quantidades = obterQuantidadesAtuais();
        int somaAtual = quantidades[0] + quantidades[1] + quantidades[2];

        if (somaAtual > totalQuestoes) {
            isUpdating = true;
            desmarcarUltimaSelecao();
            isUpdating = false;
        }
    }

    private void desmarcarUltimaSelecao() {
        for (int nivel = 2; nivel >= 0; nivel--) {
            RadioButton[] options = niveisRadioButtons[nivel];
            for (int i = options.length - 1; i >= 0; i--) {
                if (options[i].isSelected()) {
                    options[i].setSelected(false);
                    showAlert("Aviso", "A distribuição ultrapassou o total de " + totalQuestoes + " questões.\n" +
                            "A última seleção foi desmarcada automaticamente.");
                    return;
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ═══════════════════════════════════════════════════════════════════
    private int obterQuantidadeSelecionada(RadioButton[] options) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                return i + 1;
            }
        }
        return 0;
    }

    private int[] obterQuantidadesAtuais() {
        return new int[]{
                obterQuantidadeSelecionada(niveisRadioButtons[0]),
                obterQuantidadeSelecionada(niveisRadioButtons[1]),
                obterQuantidadeSelecionada(niveisRadioButtons[2])
        };
    }

    private DadosFormulario validarFormulario() {
        if (totalQuestoes == 0) {
            showAlert("Campo obrigatório", "Defina o número de questões (1 a 10).");
            return null;
        }

        String disciplina = disciplinaComboBox.getSelectionModel().getSelectedItem();
        String professor = professorField.getText();
        String instituicao = instituicaoField.getText();

        if (professor == null || professor.isBlank() || instituicao == null || instituicao.isBlank()) {
            showAlert("Campos obrigatórios", "Preencha o Professor e a Instituição.");
            return null;
        }

        if (disciplina == null || disciplina.isEmpty()) {
            showAlert("Campo obrigatório", "Selecione uma disciplina.");
            return null;
        }

        int qtdNivel1 = obterQuantidadeSelecionada(niveisRadioButtons[0]);
        int qtdNivel2 = obterQuantidadeSelecionada(niveisRadioButtons[1]);
        int qtdNivel3 = obterQuantidadeSelecionada(niveisRadioButtons[2]);

        int somaTotal = qtdNivel1 + qtdNivel2 + qtdNivel3;
        if (somaTotal != totalQuestoes) {
            showAlert("Distribuição incompleta",
                    String.format("A distribuição de questões (%d) não corresponde ao total definido (%d).\nAjuste a distribuição antes de gerar.",
                            somaTotal, totalQuestoes));
            return null;
        }

        if (somaTotal == 0) {
            showAlert("Nenhuma questão", "Selecione pelo menos uma questão para gerar a prova.");
            return null;
        }

        return new DadosFormulario(totalQuestoes, disciplina, professor, instituicao, qtdNivel1, qtdNivel2, qtdNivel3);
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    private static class DadosFormulario {
        final int totalQuestoes;
        final String disciplina;
        final String professor;
        final String instituicao;
        final int qtdNivel1;
        final int qtdNivel2;
        final int qtdNivel3;

        DadosFormulario(int totalQuestoes, String disciplina, String professor, String instituicao,
                        int qtdNivel1, int qtdNivel2, int qtdNivel3) {
            this.totalQuestoes = totalQuestoes;
            this.disciplina = disciplina == null ? "" : disciplina;
            this.professor = professor;
            this.instituicao = instituicao;
            this.qtdNivel1 = qtdNivel1;
            this.qtdNivel2 = qtdNivel2;
            this.qtdNivel3 = qtdNivel3;
        }
    }
}