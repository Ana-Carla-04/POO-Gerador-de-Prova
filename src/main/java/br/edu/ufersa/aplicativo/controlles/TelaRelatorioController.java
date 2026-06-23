package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.Prova;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TelaRelatorioController implements Initializable {

    // ── FXML ──────────────────────────────────────────────────────────────
    @FXML private Label topbarTitle;
    @FXML private GridPane provasGrid;
    @FXML private ComboBox<String> comboTipoFiltro;
    @FXML private ComboBox<String> comboValorFiltro;
    @FXML private Button btnLimparFiltro;
    @FXML private Label labelResultado;

    // Sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;

    // ── DADOS ─────────────────────────────────────────────────────────────
    private List<StackPane> menuItems;
    private List<ProvaInfo> todasAsProvas;
    private List<ProvaInfo> provasFiltradas;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Opções de tipo de filtro
    private static final String FILTRO_DISCIPLINA = "Disciplina";
    private static final String FILTRO_DATA       = "Data";
    private static final String FILTRO_PROFESSOR  = "Professor";

    // ── INICIALIZAÇÃO ──────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuItems = Arrays.asList(
                menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio
        );

        // CARREGAR DADOS REAIS DA SESSÃO
        carregarProvasDaSessao();

        configurarFiltros();
        renderizarProvas(todasAsProvas);
    }

    // ── CARREGAR PROVAS DA SESSÃO ──────────────────────────────────────────
    private void carregarProvasDaSessao() {
        todasAsProvas = new ArrayList<>();

        ProvaSessao sessao = ProvaSessao.getInstance();
        List<Prova> provasSalvas = sessao.getProvasSalvas();

        System.out.println("Carregando " + provasSalvas.size() + " provas da sessão");

        for (Prova prova : provasSalvas) {
            String disciplina = prova.getDisciplina() != null
                    ? prova.getDisciplina().getNome()
                    : "Sem disciplina";

            String data = prova.getDataDeCriacao() != null
                    ? prova.getDataDeCriacao().format(FORMATTER)
                    : "Data não definida";

            String professor = prova.getProfessor() != null
                    ? prova.getProfessor()
                    : "Sem professor";

            String codigo = prova.getCodigo();

            // Contar questões
            int qtdQuestoes = prova.getQuestoes() != null ? prova.getQuestoes().size() : 0;

            // Determinar tipo da prova (baseado nas questões)
            String tipo = determinarTipoProva(prova);

            todasAsProvas.add(new ProvaInfo(codigo, disciplina, data, professor, tipo, qtdQuestoes, prova));
        }

        // Se não houver provas na sessão, adicionar algumas de exemplo
        if (todasAsProvas.isEmpty()) {
            //adicionarProvasExemplo();
        }

        provasFiltradas = new ArrayList<>(todasAsProvas);
    }

    // ── DETERMINAR TIPO DA PROVA ──────────────────────────────────────────
    private String determinarTipoProva(Prova prova) {
        if (prova.getQuestoes() == null || prova.getQuestoes().isEmpty()) {
            return "Sem questões";
        }

        // Verificar o tipo da primeira questão (assumindo que todas são do mesmo tipo)
        // Isso pode ser melhorado
        return "Múltipla Escolha";
    }

    // ── ADICIONAR PROVAS DE EXEMPLO (CASO NÃO HAJA NA SESSÃO) ─────────────
    private void adicionarProvasExemplo() {
        // Prova 1
        ProvaInfo p1 = new ProvaInfo("PROVA-001", "Matemática", "15/03/2026", "Prof. Silva", "Múltipla Escolha", 10, null);
        todasAsProvas.add(p1);

        // Prova 2
        ProvaInfo p2 = new ProvaInfo("PROVA-002", "Português", "20/03/2026", "Prof. Santos", "Discursiva", 5, null);
        todasAsProvas.add(p2);

        // Prova 3
        ProvaInfo p3 = new ProvaInfo("PROVA-003", "História", "25/03/2026", "Prof. Oliveira", "Verdadeiro/Falso", 8, null);
        todasAsProvas.add(p3);
    }

    // ── FILTROS ───────────────────────────────────────────────────────────
    private void configurarFiltros() {
        comboTipoFiltro.getItems().setAll(FILTRO_DISCIPLINA, FILTRO_DATA, FILTRO_PROFESSOR);
    }

    @FXML
    private void handleTipoFiltroChanged() {
        String tipo = comboTipoFiltro.getValue();
        if (tipo == null) return;

        comboValorFiltro.getItems().clear();
        comboValorFiltro.setValue(null);
        comboValorFiltro.setPromptText("selecione...");

        switch (tipo) {
            case FILTRO_DISCIPLINA -> {
                List<String> discs = todasAsProvas.stream()
                        .map(ProvaInfo::getDisciplina)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                comboValorFiltro.getItems().setAll(discs);
                comboValorFiltro.setPromptText("Disciplina...");
            }
            case FILTRO_DATA -> {
                List<String> datas = todasAsProvas.stream()
                        .map(ProvaInfo::getData)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                comboValorFiltro.getItems().setAll(datas);
                comboValorFiltro.setPromptText("Data...");
            }
            case FILTRO_PROFESSOR -> {
                List<String> professores = todasAsProvas.stream()
                        .map(ProvaInfo::getProfessor)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                comboValorFiltro.getItems().setAll(professores);
                comboValorFiltro.setPromptText("Professor...");
            }
        }

        comboValorFiltro.setVisible(true);
        comboValorFiltro.setManaged(true);
        btnLimparFiltro.setVisible(true);
        btnLimparFiltro.setManaged(true);
    }

    @FXML
    private void handleValorFiltroChanged() {
        String tipo  = comboTipoFiltro.getValue();
        String valor = comboValorFiltro.getValue();
        if (tipo == null || valor == null) return;

        provasFiltradas = todasAsProvas.stream()
                .filter(p -> switch (tipo) {
                    case FILTRO_DISCIPLINA -> p.getDisciplina().equals(valor);
                    case FILTRO_DATA       -> p.getData().equals(valor);
                    case FILTRO_PROFESSOR  -> p.getProfessor().equals(valor);
                    default -> true;
                })
                .collect(Collectors.toList());

        int qtd = provasFiltradas.size();
        labelResultado.setText(qtd + (qtd == 1 ? " prova encontrada" : " provas encontradas"));
        renderizarProvas(provasFiltradas);
    }

    @FXML
    private void handleLimparFiltro() {
        comboTipoFiltro.setValue(null);
        comboValorFiltro.getItems().clear();
        comboValorFiltro.setValue(null);
        comboValorFiltro.setVisible(false);
        comboValorFiltro.setManaged(false);
        btnLimparFiltro.setVisible(false);
        btnLimparFiltro.setManaged(false);
        labelResultado.setText("");
        provasFiltradas = new ArrayList<>(todasAsProvas);
        renderizarProvas(provasFiltradas);
    }

    private void renderizarProvas(List<ProvaInfo> provas) {
        provasGrid.getChildren().clear();

        if (provas.isEmpty()) {
            Label vazio = new Label("Nenhuma prova encontrada.");
            vazio.getStyleClass().add("empty-state-label");
            provasGrid.add(vazio, 0, 0, 3, 1);
            GridPane.setHalignment(vazio, HPos.CENTER);
            return;
        }

        int col = 0;
        int row = 0;

        for (ProvaInfo prova : provas) {
            VBox card = criarCard(prova);
            provasGrid.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox criarCard(ProvaInfo prova) {
        VBox card = new VBox();
        card.getStyleClass().add("prova-card");
        card.setSpacing(0);

        // ── Cabeçalho: código da prova ──
        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblCodigo = new Label(prova.getCodigo());
        lblCodigo.getStyleClass().add("card-codigo-label");

        header.getChildren().addAll(lblCodigo, spacer);

        // ── Disciplina ──
        Label lblDisciplina = new Label(prova.getDisciplina());
        lblDisciplina.getStyleClass().add("card-disciplina-label");
        lblDisciplina.setWrapText(false);

        // ── Professor ──
        Label lblProfessor = new Label("Professor: " + prova.getProfessor());
        lblProfessor.getStyleClass().add("card-professor-label");

        // ── Linha divisória ──
        Region divider = new Region();
        divider.getStyleClass().add("card-divider");

        // ── Rodapé: data e questões ──
        HBox footer = new HBox();
        footer.getStyleClass().add("card-footer");
        footer.setAlignment(Pos.CENTER_LEFT);

        Label lblData = new Label("📅 " + prova.getData());
        lblData.getStyleClass().add("card-data-label");

        Label lblQuestoes = new Label("📝 " + prova.getQtdQuestoes() + " questões");
        lblQuestoes.getStyleClass().add("card-questoes-label");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        footer.getChildren().addAll(lblData, spacer2, lblQuestoes);

        card.getChildren().addAll(header, lblDisciplina, lblProfessor, divider, footer);

        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinHeight(150);

        // ── Ação ao clicar — navegar para detalhe da prova ──
        card.setOnMouseClicked(e -> {
            System.out.println("Clicou na prova: " + prova.getCodigo());

            // Buscar a prova completa na sessão
            Prova provaCompleta = buscarProvaPorCodigo(prova.getCodigo());

            if (provaCompleta != null) {
                abrirRelatorioProva(provaCompleta);
            } else {
                System.out.println("Prova não encontrada na sessão: " + prova.getCodigo());
                // Se não encontrar, usar os dados da provaInfo para criar uma prova temporária
                abrirRelatorioProvaInfo(prova);
            }
        });

        return card;
    }

    // ── BUSCAR PROVA POR CÓDIGO ────────────────────────────────────────────
    private Prova buscarProvaPorCodigo(String codigo) {
        ProvaSessao sessao = ProvaSessao.getInstance();
        for (Prova prova : sessao.getProvasSalvas()) {
            if (prova.getCodigo().equals(codigo)) {
                return prova;
            }
        }
        return null;
    }

    // ── ABRIR RELATÓRIO DA PROVA ──────────────────────────────────────────
    private void abrirRelatorioProva(Prova prova) {
        try {
            System.out.println("Abrindo relatório da prova: " + prova.getCodigo());

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaRelatorioProvaView.fxml")
            );
            Parent root = loader.load();

            TelaRelatorioProvaController controller = loader.getController();
            controller.setProva(prova);

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaRelatorioProvaStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) menuRelatorio.getScene().getWindow();
            boolean fs = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Relatório da Prova - " + prova.getCodigo());

            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir relatório da prova: " + e.getMessage());
        }
    }

    // ── ABRIR RELATÓRIO COM DADOS DA PROVAINFO (FALLBACK) ────────────────
    private void abrirRelatorioProvaInfo(ProvaInfo provaInfo) {
        try {
            System.out.println("Abrindo relatório da provaInfo: " + provaInfo.getCodigo());

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaRelatorioProvaView.fxml")
            );
            Parent root = loader.load();

            TelaRelatorioProvaController controller = loader.getController();

            // Criar uma prova temporária com os dados disponíveis
            // Nota: Isso só funcionará se o controller conseguir lidar com dados mínimos
            // Você pode precisar modificar o TelaRelatorioProvaController para aceitar ProvaInfo

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaRelatorioProvaStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) menuRelatorio.getScene().getWindow();
            boolean fs = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Relatório da Prova - " + provaInfo.getCodigo());

            if (fs) stage.setFullScreen(true);
            if (max) stage.setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir relatório da provaInfo: " + e.getMessage());
        }
    }

    // ── NAVEGAÇÃO SIDEBAR ──────────────────────────────────────────────────
    @FXML
    private void handleMenuDisciplinas(MouseEvent event) {
        navegarPara("TelaInicialView", "TelaInicialStyle", menuDisciplinas);
    }

    @FXML
    private void handleMenuBuscar(MouseEvent event) {
        navegarPara("TelaBuscarView", "TelaBuscarStyle", menuBuscar);
    }

    @FXML
    private void handleMenuGerarProva(MouseEvent event) {
        navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle", menuGerarProva);
    }

    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
        navegarPara("TelaRelatorioView", "TelaRelatorioStyle", menuRelatorio);
    }

    private void navegarPara(String nomeView, String nomeCSS, StackPane menuItem) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/" + nomeView + ".fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/" + nomeCSS + ".css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) menuRelatorio.getScene().getWindow();
            boolean isFullScreen = stage.isFullScreen();
            boolean isMaximized  = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas");

            if (isFullScreen) stage.setFullScreen(true);
            if (isMaximized)  stage.setMaximized(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao navegar para " + nomeView + ": " + e.getMessage());
        }
    }

    // ── MODEL INTERNO ──────────────────────────────────────────────────────
    public static class ProvaInfo {
        private final String codigo;
        private final String disciplina;
        private final String data;
        private final String professor;
        private final String tipo;
        private final int qtdQuestoes;
        private final Prova prova;

        public ProvaInfo(String codigo, String disciplina, String data, String professor, String tipo, int qtdQuestoes, Prova prova) {
            this.codigo = codigo;
            this.disciplina = disciplina;
            this.data = data;
            this.professor = professor;
            this.tipo = tipo;
            this.qtdQuestoes = qtdQuestoes;
            this.prova = prova;
        }

        public String getCodigo() { return codigo; }
        public String getDisciplina() { return disciplina; }
        public String getData() { return data; }
        public String getProfessor() { return professor; }
        public String getTipo() { return tipo; }
        public int getQtdQuestoes() { return qtdQuestoes; }
        public Prova getProva() { return prova; }
    }
}