package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.application.GerenteDeCena;
import br.edu.ufersa.aplicativo.controlles.TelaInicialController.DisciplinaInfo;
import br.edu.ufersa.aplicativo.model.entities.Discursiva;
import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.VerdadeiroFalso;
import br.edu.ufersa.aplicativo.model.service.QuestaoService;
import br.edu.ufersa.aplicativo.model.service.ServiceFactory;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class QuestoesController implements Initializable {

    private QuestaoService questaoService;

    /* ── FXML ─────────────────────────────────────────────────── */
    @FXML private Label topbarTitle;
    @FXML private ComboBox<String> nivelCombo;
    @FXML private VBox listaContainer;
    @FXML private AnchorPane detalhePanel;

    // Detalhe
    @FXML private Label questaoCodigoLabel;
    @FXML private Label questaoNivelLabel;
    @FXML private Label questaoTipoLabel;
    @FXML private Label enunciadoLabel;
    @FXML private Label assuntoLabel;
    @FXML private Label disciplinaLabel;
    @FXML private GridPane gabaritoGrid;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;

    private List<StackPane> menuItems;
    private HBox itemSelecionado;

    private DisciplinaInfo disciplinaInfo;
    private Map<String, List<QuestaoInfo>> questoesPorDisciplina;

    // ================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questaoService = ServiceFactory.criarQuestaoService();

        // Tenta pegar a disciplina do Contexto
        disciplinaInfo = Contexto.getDisciplinaSelecionada();

        // Verifica se veio uma questão específica do Contexto
        Questao questaoEspecifica = Contexto.getQuestaoSelecionada();

        // Limpa a questão do Contexto após recuperar
        Contexto.limparQuestaoSelecionada();

        menuItems = Arrays.asList(menuDisciplinas, menuBuscar, menuGerarProva, menuRelatorio);

        inicializarQuestoesPorDisciplina();

        nivelCombo.setItems(FXCollections.observableArrayList(
                "Todos", "Nível 1", "Nível 2", "Nível 3"
        ));
        nivelCombo.setPromptText("nível da questão");
        nivelCombo.getSelectionModel().selectFirst();

        // Atualiza o título da topbar
        if (disciplinaInfo != null) {
            String nomeDisc = Contexto.getNomeDisciplinaSelecionada();
            if (nomeDisc != null && !nomeDisc.isEmpty()) {
                disciplinaInfo = new DisciplinaInfo(nomeDisc, "", 0, "");
            }

        }

        carregarQuestoes();

        // Se veio uma questão específica, seleciona ela automaticamente
        if (questaoEspecifica != null) {
            selecionarQuestaoEspecifica(questaoEspecifica);
        }
    }

    /**
     * Seleciona uma questão específica na lista
     */
    private void selecionarQuestaoEspecifica(Questao questao) {
        String codigoQuestao = String.valueOf(questao.getCodigo());
        String nomeDisciplina = disciplinaInfo != null ? disciplinaInfo.getNome() :
                (questao.getDisciplina() != null ? questao.getDisciplina().getNome() : "Geral");

        List<QuestaoInfo> listaQuestoes = questoesPorDisciplina.get(nomeDisciplina);

        if (listaQuestoes != null) {
            for (int i = 0; i < listaQuestoes.size(); i++) {
                QuestaoInfo info = listaQuestoes.get(i);
                if (info.getCodigo().equals(codigoQuestao)) {
                    // Encontrou a questão, seleciona na lista
                    if (i < listaContainer.getChildren().size()) {
                        HBox item = (HBox) listaContainer.getChildren().get(i);

                        // Remove seleção anterior
                        if (itemSelecionado != null) {
                            itemSelecionado.getStyleClass().remove("questao-item-selected");
                        }

                        // Seleciona o item
                        item.getStyleClass().add("questao-item-selected");
                        itemSelecionado = item;

                        // Atualiza o detalhe
                        atualizarDetalhe(info);

                        System.out.println("Questão selecionada automaticamente: " + info.getCodigo());
                    }
                    break;
                }
            }
        }
    }

    // ================================================================
    // CARREGAMENTO DOS DADOS
    // ================================================================

    private void inicializarQuestoesPorDisciplina() {
        questoesPorDisciplina = new HashMap<>();

        try {
            List<Questao> todasAsQuestoesDoBanco = questaoService.listarQuestoes();

            for (Questao q : todasAsQuestoesDoBanco) {
                String nomeDisc = q.getDisciplina() != null ? q.getDisciplina().getNome() : "Geral";
                questoesPorDisciplina.putIfAbsent(nomeDisc, new ArrayList<>());

                String tipo;
                String gabarito = "";
                String[] alternativas = new String[]{};

                if (q instanceof MultiplaEscolha) {
                    MultiplaEscolha me = (MultiplaEscolha) q;
                    tipo = "Múltipla Escolha";
                    if (me.getAlternativas() != null) {
                        alternativas = me.getAlternativas().toArray(new String[0]);
                    }
                    gabarito = me.getResposta() != null ? me.getResposta().trim() : "";

                } else if (q instanceof VerdadeiroFalso) {
                    VerdadeiroFalso vf = (VerdadeiroFalso) q;
                    tipo = "Verdadeiro ou Falso";

                    if (vf.getAlternativas() != null && !vf.getAlternativas().isEmpty()) {
                        alternativas = vf.getAlternativas().toArray(new String[0]);
                    } else {
                        alternativas = new String[]{"Verdadeiro", "Falso"};
                    }

                    String gabVF = "";
                    List<String> altsVF = vf.getAlternativas();
                    List<Boolean> respsVF = vf.getRespostas();
                    if (respsVF != null && !respsVF.isEmpty() && altsVF != null) {
                        for (int idx = 0; idx < respsVF.size(); idx++) {
                            if (respsVF.get(idx) && idx < altsVF.size()) {
                                gabVF = altsVF.get(idx);
                                break;
                            }
                        }
                    }
                    if (gabVF.isEmpty() && vf.getResposta() != null) {
                        gabVF = vf.getResposta();
                    }
                    gabarito = gabVF;

                } else if (q instanceof Discursiva) {
                    tipo = "Discursiva";
                    gabarito = q.getResposta() != null ? q.getResposta() : "";
                    alternativas = new String[]{};

                } else {
                    tipo = "Desconhecido";
                }

                QuestaoInfo info = new QuestaoInfo(
                        String.valueOf(q.getCodigo()),
                        q.getAssunto() != null ? q.getAssunto() : "Sem Assunto",
                        "Nível " + q.getNivel().getValor(),
                        tipo,
                        q.getEnunciado(),
                        gabarito,
                        alternativas
                );

                questoesPorDisciplina.get(nomeDisc).add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao inicializar dados do banco: " + e.getMessage());
        }
    }

    // ================================================================
    // LISTA DE QUESTÕES
    // ================================================================

    private void carregarQuestoes() {
        listaContainer.getChildren().clear();

        if (disciplinaInfo == null) {
            Label vazio = new Label("Selecione uma disciplina primeiro.");
            vazio.setStyle("-fx-text-fill: #001D39; -fx-font-size: 14px;");
            listaContainer.getChildren().add(vazio);
            return;
        }

        String nivelSelecionado          = nivelCombo.getSelectionModel().getSelectedItem();
        String nomeDisciplinaSelecionada = disciplinaInfo.getNome();
        List<QuestaoInfo> todasQuestoes  = questoesPorDisciplina.get(nomeDisciplinaSelecionada);

        if (todasQuestoes == null || todasQuestoes.isEmpty()) {
            Label vazio = new Label("Nenhuma questão encontrada para '" + nomeDisciplinaSelecionada + "'.");
            vazio.setStyle("-fx-text-fill: #001D39; -fx-font-size: 14px;");
            listaContainer.getChildren().add(vazio);
            return;
        }

        boolean primeiro = true;
        for (QuestaoInfo q : todasQuestoes) {
            if (!nivelSelecionado.equals("Todos") && !q.getNivel().equals(nivelSelecionado)) {
                continue;
            }

            HBox item = new HBox();
            item.getStyleClass().add("questao-item");
            item.setUserData(q);

            Label lbl = new Label(q.getAssunto());
            lbl.getStyleClass().add("questao-item-label");
            item.getChildren().add(lbl);

            item.setOnMouseClicked(this::handleSelecionarQuestao);

            if (primeiro) {
                item.getStyleClass().add("questao-item-selected");
                itemSelecionado = item;
                atualizarDetalhe(q);
                primeiro = false;
            }

            listaContainer.getChildren().add(item);
        }
    }

    // ================================================================
    // DETALHE DA QUESTÃO
    // ================================================================

    private void atualizarDetalhe(QuestaoInfo q) {
        questaoCodigoLabel.setText(q.getCodigo());
        questaoNivelLabel.setText(q.getNivel());
        questaoTipoLabel.setText(q.getTipo());
        enunciadoLabel.setText(q.getEnunciado());
        assuntoLabel.setText(q.getAssunto());
        if (disciplinaInfo != null) {
            disciplinaLabel.setText(disciplinaInfo.getNome());
        }
        preencherGabarito(q);
    }

    private void preencherGabarito(QuestaoInfo q) {
        gabaritoGrid.getChildren().clear();

        String[] alternativas = q.getAlternativas();
        String gabarito = q.getGabarito() != null ? q.getGabarito().trim() : "";
        String tipo = q.getTipo();

        if ("Discursiva".equals(tipo)) {
            Label msg = new Label(!gabarito.isEmpty() ? gabarito : "Questão discursiva — sem gabarito.");
            msg.getStyleClass().add("det-normal");
            msg.setWrapText(true);
            msg.setMaxWidth(Double.MAX_VALUE);
            GridPane.setColumnIndex(msg, 0);
            GridPane.setRowIndex(msg, 0);
            GridPane.setColumnSpan(msg, 2);
            gabaritoGrid.getChildren().add(msg);
            return;
        }

        if (alternativas == null || alternativas.length == 0) {
            Label msg = new Label("Sem alternativas cadastradas.");
            msg.getStyleClass().add("det-normal");
            GridPane.setColumnIndex(msg, 0);
            GridPane.setRowIndex(msg, 0);
            gabaritoGrid.getChildren().add(msg);
            return;
        }

        boolean isMultipla = "Múltipla Escolha".equals(tipo);
        int metade = (int) Math.ceil(alternativas.length / 2.0);

        for (int i = 0; i < alternativas.length; i++) {
            int col = i < metade ? 0 : 1;
            int row = i < metade ? i : i - metade;

            String prefixo = isMultipla ? (char) ('A' + i) + ") " : "";
            String textoAlternativa = alternativas[i].trim();
            Label altLabel = new Label(prefixo + textoAlternativa);
            altLabel.setWrapText(true);
            altLabel.setMaxWidth(Double.MAX_VALUE);

            boolean eGabarito = textoAlternativa.equalsIgnoreCase(gabarito);

            if (eGabarito) {
                altLabel.getStyleClass().add("det-gabarito");
            } else {
                altLabel.getStyleClass().add("det-normal");
            }

            GridPane.setColumnIndex(altLabel, col);
            GridPane.setRowIndex(altLabel, row);
            GridPane.setFillWidth(altLabel, true);
            gabaritoGrid.getChildren().add(altLabel);
        }
    }

    // ================================================================
    // AÇÕES
    // ================================================================

    @FXML
    private void handleNivelFiltro() {
        carregarQuestoes();
    }

    @FXML
    private void handleSelecionarQuestao(MouseEvent event) {
        HBox clicado = (HBox) event.getSource();
        if (itemSelecionado != null) {
            itemSelecionado.getStyleClass().remove("questao-item-selected");
        }
        clicado.getStyleClass().add("questao-item-selected");
        itemSelecionado = clicado;

        QuestaoInfo questao = (QuestaoInfo) clicado.getUserData();
        if (questao != null) {
            atualizarDetalhe(questao);
        }
    }

    @FXML
    private void handleExcluir() {
        if (itemSelecionado == null) {
            showAlert("Erro", "Selecione uma questão para excluir.");
            return;
        }

        QuestaoInfo info = (QuestaoInfo) itemSelecionado.getUserData();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir questão");
        alert.setHeaderText("Confirmar exclusão");
        alert.setContentText("Deseja realmente excluir esta questão?");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == javafx.scene.control.ButtonType.OK) {
                try {
                    Questao questaoParaDeletar = null;
                    for (Questao q : questaoService.listarQuestoes()) {
                        if (String.valueOf(q.getCodigo()).equals(info.getCodigo())) {
                            questaoParaDeletar = q;
                            break;
                        }
                    }

                    if (questaoParaDeletar != null) {
                        questaoService.deletar(questaoParaDeletar);
                        List<QuestaoInfo> lista = questoesPorDisciplina.get(disciplinaInfo.getNome());
                        if (lista != null) lista.remove(info);
                        itemSelecionado = null;
                        carregarQuestoes();
                        showAlert("Sucesso", "Questão excluída com sucesso.");
                    } else {
                        showAlert("Erro", "Questão não encontrada no banco de dados.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erro", "Erro ao excluir questão: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditar() {
        if (itemSelecionado == null) {
            showAlert("Erro", "Selecione uma questão para editar.");
            return;
        }

        QuestaoInfo info = (QuestaoInfo) itemSelecionado.getUserData();
        if (info == null) return;

        // Busca a Questao real no banco pelo código
        try {
            Questao questaoParaEditar = null;
            for (Questao q : questaoService.listarQuestoes()) {
                if (String.valueOf(q.getCodigo()).equals(info.getCodigo())) {
                    questaoParaEditar = q;
                    break;
                }
            }

            if (questaoParaEditar == null) {
                showAlert("Erro", "Questão não encontrada no banco de dados.");
                return;
            }

            // Salva no Contexto para a próxima tela ler
            Contexto.setQuestaoParaEditar(questaoParaEditar);

            GerenteDeCena.carregarCena(
                    "/br/edu/ufersa/aplicativo/views/TelaAdicionarQuestView.fxml",
                    "/br/edu/ufersa/aplicativo/css/TelaAdicionarQuestStyle.css",
                    "Gerador de Provas - Editar Questão");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao abrir edição: " + e.getMessage());
        }
    }
    // ================================================================
    // NAVEGAÇÃO
    // ================================================================

    @FXML
    private void handleVoltar() {
        try {
            GerenteDeCena.carregarCena(
                    "/br/edu/ufersa/aplicativo/views/TelaInicialView.fxml",
                    "/br/edu/ufersa/aplicativo/css/TelaInicialStyle.css",
                    "Gerador de Provas - Disciplinas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleMenuDisciplinas(MouseEvent e) {  handleVoltar(); }
    @FXML private void handleMenuBuscar(MouseEvent e)      { abrirTelaBuscar(); }
    @FXML private void handleMenuGerarProva(MouseEvent e)  { abrirTelaGerarProva(); }

    private void abrirTelaGerarProva() {
        try {
            GerenteDeCena.carregarCena(
                    "/br/edu/ufersa/aplicativo/views/TelaGerarProvaView.fxml",
                    "/br/edu/ufersa/aplicativo/css/TelaGerarProvaStyle.css",
                    "Gerador de Provas - Gerar Prova");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaBuscar() {
        try {
            GerenteDeCena.carregarCena(
                    "/br/edu/ufersa/aplicativo/views/TelaBuscarView.fxml",
                    "/br/edu/ufersa/aplicativo/css/TelaBuscarStyle.css",
                    "Gerador de Provas - Buscar");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMenuRelatorio(MouseEvent event) {
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

    private void selecionarMenu(StackPane item) {
        for (StackPane m : menuItems) {
            m.getStyleClass().remove("menu-item-selected");
        }
        if (!item.getStyleClass().contains("menu-item-selected")) {
            item.getStyleClass().add("menu-item-selected");
        }
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // ================================================================
    // CLASSE INTERNA
    // ================================================================

    public static class QuestaoInfo {
        private final String codigo;
        private final String assunto;
        private final String nivel;
        private final String tipo;
        private final String enunciado;
        private final String gabarito;
        private final String[] alternativas;

        public QuestaoInfo(String codigo, String assunto, String nivel, String tipo,
                           String enunciado, String gabarito, String[] alternativas) {
            this.codigo       = codigo;
            this.assunto      = assunto;
            this.nivel        = nivel;
            this.tipo         = tipo;
            this.enunciado    = enunciado;
            this.gabarito     = gabarito;
            this.alternativas = alternativas;
        }

        public String   getCodigo()       { return codigo; }
        public String   getAssunto()      { return assunto; }
        public String   getNivel()        { return nivel; }
        public String   getTipo()         { return tipo; }
        public String   getEnunciado()    { return enunciado; }
        public String   getGabarito()     { return gabarito; }
        public String[] getAlternativas() { return alternativas; }
    }
}