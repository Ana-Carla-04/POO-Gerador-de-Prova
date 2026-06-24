package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.model.entities.MultiplaEscolha;
import br.edu.ufersa.aplicativo.model.entities.Nivel;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class TelaGerarProvaManualController implements Initializable {

    // ── FXML ────────────────────────────────────────────────────────
    @FXML private StackPane centerRoot;
    @FXML private ComboBox<String> nivelCombo;

    // Lista
    @FXML private VBox listaContainer;

    // Card de detalhe
    @FXML private VBox    detalheCard;
    @FXML private Label   lblCodigo;
    @FXML private Label   lblNivel;
    @FXML private Label   lblTipo;
    @FXML private Label   lblEnunciado;
    @FXML private VBox    boxGabarito;
    @FXML private HBox    linhaAlt1;
    @FXML private HBox    linhaAlt2;
    @FXML private Label   alt1a;
    @FXML private Label   alt1b;
    @FXML private Label   alt2a;
    @FXML private Label   alt2b;
    @FXML private Label   lblAssunto;
    @FXML private Label   lblDisciplina;
    @FXML private Label   lblStatusIcone;

    @FXML private StackPane btnSelecionar;
    @FXML private StackPane btnDeselecionar;
    @FXML private StackPane btnGerar;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;


    // ── Estado ──────────────────────────────────────────────────────

    /** Quantidade máxima de questões definida na tela anterior (TelaGerarProva). */
    private int quantidadePredefinida = 10;

    /** Filtro de nível atualmente escolhido (null = todos). */
    private Nivel filtroNivel = null;

    /** Questão atualmente exibida no card de detalhe. */
    private Questao questaoSelecionadaNaLista = null;

    /** Conjunto (ordenado) das questões já escolhidas para compor a prova. */
    private final Set<Questao> questoesEscolhidas = new LinkedHashSet<>();

    /** Linhas da lista mapeadas por questão, para atualizar o ícone sem re-renderizar tudo. */
    private final java.util.Map<Questao, Label> iconePorQuestao = new java.util.HashMap<>();

    // ── Dados ───────────────────────────────────────────────────────
    private final List<Questao> todasQuestoes = new ArrayList<>();

    /** Disciplina selecionada na tela anterior */
    private String disciplinaSelecionada = null;

    // ================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nivelCombo.setItems(FXCollections.observableArrayList(
                "Todos", "Nível 1 - Fácil", "Nível 2 - Médio", "Nível 3 - Difícil"
        ));
        nivelCombo.setPromptText("nível da questão");
        nivelCombo.getSelectionModel().selectFirst();

        nivelCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> aplicarFiltroNivel()
        );

        // Usa o mesmo banco de questões do sistema
        todasQuestoes.addAll(ProvaSessao.bancoDeQuestoes());

        // Carrega a quantidade e as questões já definidas na tela anterior
        ProvaSessao sessao = ProvaSessao.getInstance();
        if (sessao.getTotalQuestoes() > 0) {
            quantidadePredefinida = sessao.getTotalQuestoes();
        }
        questoesEscolhidas.addAll(sessao.getQuestoes());

        // Pega a disciplina da sessão se não foi definida diretamente
        if (disciplinaSelecionada == null || disciplinaSelecionada.isEmpty()) {
            disciplinaSelecionada = sessao.getDisciplina();
        }

        renderizarLista();
        atualizarCardDetalhe(null);
        aplicarEstilosPadrao();
        atualizarBotoesAcao();
    }

    /** Chamado pela tela anterior para definir quantas questões a prova deve ter. */
    public void setQuantidadePredefinida(int quantidade) {
        this.quantidadePredefinida = quantidade;
    }

    /** Chamado pela tela anterior para definir a disciplina selecionada. */
    public void setDisciplinaSelecionada(String disciplina) {
        this.disciplinaSelecionada = disciplina;
    }

    // ================================================================
    // ESTILOS PADRÃO
    // ================================================================

    private void aplicarEstilosPadrao() {
        lblCodigo.getStyleClass().addAll("caixinha-valor", "det-codigo");
        lblNivel.getStyleClass().add("caixinha-valor");
        lblTipo.getStyleClass().add("caixinha-valor");
        lblEnunciado.getStyleClass().add("caixinha-valor-enunciado");
        lblAssunto.getStyleClass().add("caixinha-valor");
        lblDisciplina.getStyleClass().add("caixinha-valor");
        lblStatusIcone.getStyleClass().add("status-caixinha");
        boxGabarito.getStyleClass().add("caixinha-gabarito");

        alt1a.getStyleClass().add("det-normal");
        alt1b.getStyleClass().add("det-normal");
        alt2a.getStyleClass().add("det-normal");
        alt2b.getStyleClass().add("det-normal");
    }

    // ================================================================
    // FILTRO DE NÍVEL
    // ================================================================

    private void aplicarFiltroNivel() {
        String selected = nivelCombo.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("Todos")) {
            filtroNivel = null;
        } else if (selected.startsWith("Nível 1")) {
            filtroNivel = Nivel.FACIL;
        } else if (selected.startsWith("Nível 2")) {
            filtroNivel = Nivel.MEDIO;
        } else if (selected.startsWith("Nível 3")) {
            filtroNivel = Nivel.DIFICIL;
        }
        renderizarLista();
    }

    // ================================================================
    // LISTA DE QUESTÕES
    // ================================================================

    private void renderizarLista() {
        listaContainer.getChildren().clear();
        iconePorQuestao.clear();

        List<Questao> filtradas = new ArrayList<>();
        for (Questao q : todasQuestoes) {
            // Filtra por nível se selecionado
            if (filtroNivel != null && q.getNivel() != filtroNivel) continue;

            // Filtra por disciplina se selecionada
            if (disciplinaSelecionada != null && !disciplinaSelecionada.isEmpty()) {
                if (q.getDisciplina() == null || !q.getDisciplina().getNome().equals(disciplinaSelecionada)) {
                    continue;
                }
            }

            filtradas.add(q);
        }

        if (filtradas.isEmpty()) {
            String mensagem = "Nenhuma questão encontrada";
            if (disciplinaSelecionada != null && !disciplinaSelecionada.isEmpty()) {
                mensagem += " para a disciplina \"" + disciplinaSelecionada + "\"";
            }
            mensagem += ".";

            Label vazio = new Label(mensagem);
            vazio.setStyle("-fx-text-fill: #4a6a7a; -fx-font-size: 14px; -fx-padding: 20;");
            listaContainer.getChildren().add(vazio);
            return;
        }

        for (Questao q : filtradas) {
            StackPane row = new StackPane();
            row.getStyleClass().add("lista-item");
            row.setAlignment(Pos.CENTER_LEFT);

            HBox conteudo = new HBox();
            conteudo.setAlignment(Pos.CENTER_LEFT);
            conteudo.setSpacing(8);
            conteudo.setMaxWidth(Double.MAX_VALUE);
            StackPane.setAlignment(conteudo, Pos.CENTER_LEFT);

            // Mostra disciplina e assunto na lista
            String disciplinaLabel = q.getDisciplina() != null ? q.getDisciplina().getNome() : "Sem disciplina";
            Label lblTexto = new Label(disciplinaLabel + " - " + q.getAssunto());
            lblTexto.getStyleClass().add("lista-item-label");
            HBox.setHgrow(lblTexto, javafx.scene.layout.Priority.ALWAYS);

            boolean escolhida = questoesEscolhidas.contains(q);
            Label icone = new Label(escolhida ? "\u2705" : "\u2297");
            icone.getStyleClass().add(escolhida ? "lista-item-icone-on" : "lista-item-icone-off");

            javafx.scene.layout.Region espaco = new javafx.scene.layout.Region();
            HBox.setHgrow(espaco, javafx.scene.layout.Priority.ALWAYS);

            conteudo.getChildren().addAll(lblTexto, espaco, icone);
            row.getChildren().add(conteudo);

            iconePorQuestao.put(q, icone);

            if (escolhida) {
                row.getStyleClass().add("lista-item-ativa");
            }

            row.setOnMouseClicked(e -> {
                selecionarNaLista(q, row);
                e.consume();
            });

            listaContainer.getChildren().add(row);
        }
    }

    private void selecionarNaLista(Questao q, StackPane row) {
        for (javafx.scene.Node n : listaContainer.getChildren()) {
            n.getStyleClass().remove("lista-item-ativa");
        }
        row.getStyleClass().add("lista-item-ativa");

        questaoSelecionadaNaLista = q;
        atualizarCardDetalhe(q);
        atualizarBotoesAcao();
    }

    // ================================================================
    // CARD DE DETALHE
    // ================================================================

    private void atualizarCardDetalhe(Questao q) {
        if (q == null) {
            lblCodigo.setText("codigo");
            lblNivel.setText("-");
            lblTipo.setText("multipla escolha");
            lblEnunciado.setText("Selecione uma questão na lista ao lado.");
            limparGabarito();
            lblAssunto.setText("-");
            lblDisciplina.setText("-");
            lblStatusIcone.setText("❌");
            return;
        }

        lblCodigo.setText(String.valueOf(q.getCodigo()));
        lblNivel.setText(String.valueOf(q.getNivel().getValor()));
        lblTipo.setText(q instanceof MultiplaEscolha ? "multipla escolha" : q.getClass().getSimpleName());
        lblEnunciado.setText(q.getEnunciado());
        preencherGabarito(q);
        lblAssunto.setText(q.getAssunto());
        lblDisciplina.setText(q.getDisciplina() != null ? q.getDisciplina().getNome() : "-");
        atualizarStatusIcone(questoesEscolhidas.contains(q));
    }

    private void limparGabarito() {
        alt1a.setText("");
        alt1b.setText("");
        alt2a.setText("");
        alt2b.setText("");

        for (Label l : Arrays.asList(alt1a, alt1b, alt2a, alt2b)) {
            l.getStyleClass().remove("det-gabarito");
            if (!l.getStyleClass().contains("det-normal")) l.getStyleClass().add("det-normal");
        }

        linhaAlt1.setVisible(false);
        linhaAlt1.setManaged(false);
        linhaAlt2.setVisible(false);
        linhaAlt2.setManaged(false);
    }

    private void preencherGabarito(Questao q) {
        limparGabarito();

        if (!(q instanceof MultiplaEscolha)) {
            return;
        }

        MultiplaEscolha me = (MultiplaEscolha) q;
        List<String> alternativas = me.getAlternativas();
        String respostaCorreta = me.getResposta();

        List<Label> alvos = Arrays.asList(alt1a, alt1b, alt2a, alt2b);

        for (int i = 0; i < alternativas.size() && i < alvos.size(); i++) {
            Label lbl = alvos.get(i);
            String texto = alternativas.get(i);
            lbl.setText(letraAlternativa(i) + ") " + texto);
            lbl.setVisible(true);
            lbl.setManaged(true);

            lbl.getStyleClass().remove("det-normal");
            lbl.getStyleClass().remove("det-gabarito");
            lbl.getStyleClass().add(texto.equals(respostaCorreta) ? "det-gabarito" : "det-normal");
        }

        boolean temAlt1 = !alt1a.getText().isEmpty() || !alt1b.getText().isEmpty();
        boolean temAlt2 = !alt2a.getText().isEmpty() || !alt2b.getText().isEmpty();

        linhaAlt1.setVisible(temAlt1);
        linhaAlt1.setManaged(temAlt1);
        linhaAlt2.setVisible(temAlt2);
        linhaAlt2.setManaged(temAlt2);
    }

    private String letraAlternativa(int index) {
        return String.valueOf((char) ('a' + index));
    }

    private void atualizarStatusIcone(boolean escolhida) {
        lblStatusIcone.setText(escolhida ? "\u2705" : "\u274C");
    }

    // ================================================================
    // CONTROLE DOS BOTÕES (Dinâmico)
    // ================================================================

    private void atualizarBotoesAcao() {
        boolean temQuestao = questaoSelecionadaNaLista != null;
        boolean escolhida = temQuestao && questoesEscolhidas.contains(questaoSelecionadaNaLista);

        boolean mostrarSelecionar = temQuestao && !escolhida;
        btnSelecionar.setVisible(mostrarSelecionar);
        btnSelecionar.setManaged(mostrarSelecionar);

        boolean mostrarDeselecionar = temQuestao && escolhida;
        btnDeselecionar.setVisible(mostrarDeselecionar);
        btnDeselecionar.setManaged(mostrarDeselecionar);
    }

    // ================================================================
    // SELECIONAR / DESELECIONAR
    // ================================================================

    @FXML
    private void handleSelecionar(MouseEvent event) {
        event.consume();
        if (questaoSelecionadaNaLista == null) return;
        if (questoesEscolhidas.contains(questaoSelecionadaNaLista)) return;

        if (questoesEscolhidas.size() >= quantidadePredefinida) {
            alerta("Limite atingido",
                    "Você já selecionou o número máximo de questões definido (" +
                            quantidadePredefinida + ").");
            return;
        }

        questoesEscolhidas.add(questaoSelecionadaNaLista);
        atualizarIconeLista(questaoSelecionadaNaLista, true);
        atualizarStatusIcone(true);
        atualizarBotoesAcao();
    }

    @FXML
    private void handleDeselecionar(MouseEvent event) {
        event.consume();
        if (questaoSelecionadaNaLista == null) return;
        if (!questoesEscolhidas.contains(questaoSelecionadaNaLista)) return;

        questoesEscolhidas.remove(questaoSelecionadaNaLista);
        atualizarIconeLista(questaoSelecionadaNaLista, false);
        atualizarStatusIcone(false);
        atualizarBotoesAcao();
    }

    private void atualizarIconeLista(Questao q, boolean escolhida) {
        Label icone = iconePorQuestao.get(q);
        if (icone == null) return;
        icone.setText(escolhida ? "\u2705" : "\u2297");
        icone.getStyleClass().remove("lista-item-icone-on");
        icone.getStyleClass().remove("lista-item-icone-off");
        icone.getStyleClass().add(escolhida ? "lista-item-icone-on" : "lista-item-icone-off");
    }

    // ================================================================
    // GERAR PROVA — envia as questões escolhidas para a TelaGerarAuto
    // ================================================================

    @FXML
    private void handleGerar(MouseEvent event) {
        event.consume();
        if (questoesEscolhidas.isEmpty()) {
            alerta("Gerar Prova", "Selecione ao menos uma questão antes de gerar a prova.");
            return;
        }

        ProvaSessao sessao = ProvaSessao.getInstance();
        sessao.setQuestoes(new ArrayList<>(questoesEscolhidas));
        sessao.setTotalQuestoes(quantidadePredefinida);

        navegarParaAuto();
    }

    private void navegarParaAuto() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaGerarAutoView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);
            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/TelaGerarAutoStyle.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) listaContainer.getScene().getWindow();
            boolean fs  = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas - Prova Gerada");

            if (fs)  stage.setFullScreen(true);
            if (max) stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            alerta("Erro", "Não foi possível abrir a tela de prova gerada.");
        }
    }

    // ================================================================
    // NAVEGAÇÃO SIDEBAR
    // ================================================================

    @FXML
    private void handleVoltar(MouseEvent e) {
        navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle");
    }

    @FXML
    private void handleMenuDisciplinas(MouseEvent e) {
        navegarPara("TelaInicialView", "TelaInicialStyle");
    }

    @FXML
    private void handleMenuBuscar(MouseEvent e) {
        navegarPara("TelaBuscarView", "TelaBuscarStyle");
    }

    @FXML
    private void handleMenuGerarProva(MouseEvent e) {
        navegarPara("TelaGerarProvaView", "TelaGerarProvaStyle");
    }

    @FXML
    private void handleMenuRelatorio(MouseEvent e) {
        navegarPara("TelaGerarRelatorioView", "TelaRelatorioStyle");
    }



    private void navegarPara(String nomeView, String nomeCSS) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/" + nomeView + ".fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 750);

            URL cssUrl = getClass().getResource("/br/edu/ufersa/aplicativo/css/" + nomeCSS + ".css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) listaContainer.getScene().getWindow();
            boolean fs  = stage.isFullScreen();
            boolean max = stage.isMaximized();

            stage.setScene(scene);
            stage.setTitle("Gerador de Provas");

            if (fs)  stage.setFullScreen(true);
            if (max) stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            alerta("Erro", "Não foi possível navegar para a tela solicitada.");
        }
    }

    private void alerta(String titulo, String msg) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}