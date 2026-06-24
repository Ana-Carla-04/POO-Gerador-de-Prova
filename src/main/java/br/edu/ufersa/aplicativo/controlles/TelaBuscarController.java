package br.edu.ufersa.aplicativo.controlles;

import br.edu.ufersa.aplicativo.application.Contexto;
import br.edu.ufersa.aplicativo.model.entities.Questao;
import br.edu.ufersa.aplicativo.model.entities.Prova;
import br.edu.ufersa.aplicativo.model.DAO.QuestaoDAO;
import br.edu.ufersa.aplicativo.model.entities.Disciplina;
import br.edu.ufersa.aplicativo.model.DAO.DisciplinaDAO;
import br.edu.ufersa.aplicativo.model.DAO.ProvaDAO;
import br.edu.ufersa.aplicativo.util.Conexao;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TelaBuscarController implements Initializable {
    private Connection conexao;
    private QuestaoDAO questaoDAO;
    private DisciplinaDAO disciplinaDAO;
    private ProvaDAO provaDAO;


    // ── FXML ────────────────────────────────────────────────────────
    @FXML private StackPane centerRoot;

    // Lista
    @FXML private VBox listaContainer;

    // Filtro principal
    @FXML private StackPane selectorTipo;
    @FXML private Label     selectorTipoLabel;
    @FXML private HBox      boxTipoBotoes;
    @FXML private StackPane btnProva;
    @FXML private StackPane btnQuestoes;

    // Sub-filtros
    @FXML private VBox subfiltroProva;
    @FXML private VBox subfiltroQuestoes;

    // Chips Prova
    @FXML private StackPane chipDisciplinaProva;
    @FXML private StackPane chipSemestre;

    // Chips Questões
    @FXML private StackPane chipDisciplinaQuest;
    @FXML private StackPane chipAssunto;
    @FXML private StackPane chipDificuldade;

    // Popups flutuantes
    @FXML private VBox popupTipo;
    @FXML private VBox popupDisciplinaProva;
    @FXML private VBox popupSemestre;
    @FXML private VBox popupDisciplinaQuest;
    @FXML private VBox popupAssunto;
    @FXML private VBox popupDificuldade;

    // Menu sidebar
    @FXML private StackPane menuDisciplinas;
    @FXML private StackPane menuBuscar;
    @FXML private StackPane menuGerarProva;
    @FXML private StackPane menuRelatorio;


    // ── Estado ──────────────────────────────────────────────────────
    private enum ModoBusca { NENHUM, PROVA, QUESTOES }
    private ModoBusca modoAtual = ModoBusca.NENHUM;

    // Filtros ativos
    private String filtroTipo        = null; // "Prova" | "Questões"
    private String filtroDisciplina  = null;
    private String filtroSemestre    = null;
    private String filtroAssunto     = null;
    private String filtroDificuldade = null;

    // Chips com estado ativo
    private final List<StackPane> chipsAtivos = new ArrayList<>();

    // Popup visível no momento
    private VBox popupAtual = null;

    // ── Dados ──────────────────────────────────────────────────
    private final List<String> semestres = Arrays.asList(
            "2024.1", "2024.2", "2025.1", "2025.2"
    );

    // Itens da lista
    private final List<ItemBusca> todosItens = new ArrayList<>();
    private final List<ItemBusca> itensFiltrados = new ArrayList<>();

    // ================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.conexao = Conexao.abrirConexao();
            this.questaoDAO = new QuestaoDAO(this.conexao);
            this.disciplinaDAO = new DisciplinaDAO(this.conexao);
            this.provaDAO = new ProvaDAO(this.conexao);

            carregarDadosDoBanco();
            preencherPopupsDisciplina();
            preencherPopupAssunto();
            preencherPopupDificuldade();
            atualizarLista();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================================================================
    // CARREGAR DADOS DO BANCO
    // ================================================================

    private void carregarDadosDoBanco() {
        todosItens.clear();

        try {
            // 1. Carregar Questões
            List<Questao> questoesDoBanco = questaoDAO.listar();
            for (Questao q : questoesDoBanco) {
                todosItens.add(new ItemBusca(q));
            }

            // 2. Carregar Provas Salvas
            List<Prova> provasDoBanco = provaDAO.listar();
            for (Prova p : provasDoBanco) {
                todosItens.add(new ItemBusca(p));
            }

            System.out.println("Carregados " + questoesDoBanco.size() + " questões e " +
                    provasDoBanco.size() + " provas");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar os dados na tela de busca.");
        }

        itensFiltrados.addAll(todosItens);
    }

    // ================================================================
    // PREENCHIMENTO DOS POPUPS COM DADOS
    // ================================================================

    private void preencherPopupsDisciplina() {
        try {
            List<Disciplina> disciplinasDoBanco = disciplinaDAO.listar();

            List<String> nomesDisciplinas = new ArrayList<>();
            for (Disciplina d : disciplinasDoBanco) {
                nomesDisciplinas.add(d.getNome());
            }

            preencherPopupLista(popupDisciplinaProva, nomesDisciplinas, opcao -> {
                filtroDisciplina = opcao;
                atualizarLabelChip(chipDisciplinaProva, "disciplina", opcao);
                fecharPopupAtual();
                atualizarLista();
            });

            preencherPopupLista(popupDisciplinaQuest, nomesDisciplinas, opcao -> {
                filtroDisciplina = opcao;
                atualizarLabelChip(chipDisciplinaQuest, "disciplina", opcao);
                fecharPopupAtual();
                atualizarLista();
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar disciplinas no filtro.");
        }
    }

    private void preencherPopupAssunto() {
        try {
            List<String> assuntosUnicos = new ArrayList<>();

            if (this.questaoDAO != null) {
                List<Questao> questoesDoBanco = this.questaoDAO.listar();

                for (Questao q : questoesDoBanco) {
                    String assunto = q.getAssunto();

                    if (assunto != null && !assunto.trim().isEmpty()) {
                        assunto = assunto.trim();
                        if (!assuntosUnicos.contains(assunto)) {
                            assuntosUnicos.add(assunto);
                        }
                    }
                }
            }

            preencherPopupLista(popupAssunto, assuntosUnicos, opcao -> {
                filtroAssunto = opcao;
                atualizarLabelChip(chipAssunto, "assuntos", opcao);
                fecharPopupAtual();
                atualizarLista();
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar assuntos dinâmicos no filtro.");
        }
    }

    private void preencherPopupDificuldade() {
        for (javafx.scene.Node n : popupDificuldade.getChildren()) {
            if (n instanceof Label l && l.getStyleClass().contains("popup-opcao")) {
                l.setOnMouseClicked(e -> {
                    filtroDificuldade = l.getText();
                    atualizarLabelChip(chipDificuldade, "dificuldade", l.getText());
                    fecharPopupAtual();
                    atualizarLista();
                    e.consume();
                });
            }
        }
    }

    /** Popula um popup com uma lista de strings e uma ação por item */
    private void preencherPopupLista(VBox popup, List<String> itens, java.util.function.Consumer<String> acao) {
        popup.getChildren().clear();
        // Opção "Todos" para limpar filtro
        Label todos = new Label("— Todos —");
        todos.getStyleClass().add("popup-opcao");
        todos.setOnMouseClicked(e -> { acao.accept(null); e.consume(); });
        popup.getChildren().add(todos);

        for (String item : itens) {
            Label l = new Label(item);
            l.getStyleClass().add("popup-opcao");
            l.setOnMouseClicked(e -> { acao.accept(item); e.consume(); });
            popup.getChildren().add(l);
        }
    }

    // ================================================================
    // POPUP PRINCIPAL (Prova / Questões)
    // ================================================================

    @FXML
    private void handleAbrirPopupTipo(MouseEvent event) {
        event.consume();
        togglePopup(popupTipo, selectorTipo);
    }

    @FXML
    private void handlePopupEscolherProva(MouseEvent event) {
        event.consume();
        selecionarModo(ModoBusca.PROVA, "Prova");
        fecharPopupAtual();
    }

    @FXML
    private void handlePopupEscolherQuestoes(MouseEvent event) {
        event.consume();
        selecionarModo(ModoBusca.QUESTOES, "Questões");
        fecharPopupAtual();
    }

    // ================================================================
    // BOTÕES PROVA / QUESTÕES (mostrados após selecionar no popup)
    // ================================================================

    @FXML
    private void handleSelecionarProva(MouseEvent event) {
        event.consume();
        selecionarModo(ModoBusca.PROVA, "Prova");
    }

    @FXML
    private void handleSelecionarQuestoes(MouseEvent event) {
        event.consume();
        selecionarModo(ModoBusca.QUESTOES, "Questões");
    }

    /** Aplica modo e atualiza visibilidade de sub-filtros e botões */
    private void selecionarModo(ModoBusca modo, String labelTexto) {
        modoAtual = modo;
        filtroTipo        = labelTexto;
        filtroDisciplina  = null;
        filtroSemestre    = null;
        filtroAssunto     = null;
        filtroDificuldade = null;

        // Label do selector
        selectorTipoLabel.setText(labelTexto);
        selectorTipoLabel.getStyleClass().remove("filtro-selector-label");
        if (!selectorTipoLabel.getStyleClass().contains("filtro-selector-label-active"))
            selectorTipoLabel.getStyleClass().add("filtro-selector-label-active");

        // Mostra botões Prova/Questões
        boxTipoBotoes.setVisible(true);
        boxTipoBotoes.setManaged(true);

        // Destaque no botão ativo
        resetEstiloBotoes();
        if (modo == ModoBusca.PROVA) {
            ativarBotaoTipo(btnProva);
            subfiltroProva.setVisible(true);
            subfiltroProva.setManaged(true);
            subfiltroQuestoes.setVisible(false);
            subfiltroQuestoes.setManaged(false);
        } else {
            ativarBotaoTipo(btnQuestoes);
            subfiltroQuestoes.setVisible(true);
            subfiltroQuestoes.setManaged(true);
            subfiltroProva.setVisible(false);
            subfiltroProva.setManaged(false);
        }

        // Reseta chips
        resetarChips();
        atualizarLista();
    }

    // ================================================================
    // CHIPS DE SUB-FILTRO
    // ================================================================

    @FXML
    private void handleToggleChipDisciplinaProva(MouseEvent event) {
        event.consume();
        if (chipAtivo(chipDisciplinaProva)) {
            desativarChip(chipDisciplinaProva, "disciplina");
            filtroDisciplina = null;
            fecharPopupAtual();
            atualizarLista();
        } else {
            ativarChip(chipDisciplinaProva);
            togglePopup(popupDisciplinaProva, chipDisciplinaProva);
        }
    }

    @FXML
    private void handleToggleChipSemestre(MouseEvent event) {
        event.consume();
        if (chipAtivo(chipSemestre)) {
            desativarChip(chipSemestre, "semestre");
            filtroSemestre = null;
            fecharPopupAtual();
            atualizarLista();
        } else {
            ativarChip(chipSemestre);
            togglePopup(popupSemestre, chipSemestre);
        }
    }

    @FXML
    private void handleToggleChipDisciplinaQuest(MouseEvent event) {
        event.consume();
        if (chipAtivo(chipDisciplinaQuest)) {
            desativarChip(chipDisciplinaQuest, "disciplina");
            filtroDisciplina = null;
            fecharPopupAtual();
            atualizarLista();
        } else {
            ativarChip(chipDisciplinaQuest);
            togglePopup(popupDisciplinaQuest, chipDisciplinaQuest);
        }
    }

    @FXML
    private void handleToggleChipAssunto(MouseEvent event) {
        event.consume();
        if (chipAtivo(chipAssunto)) {
            desativarChip(chipAssunto, "assuntos");
            filtroAssunto = null;
            fecharPopupAtual();
            atualizarLista();
        } else {
            ativarChip(chipAssunto);
            togglePopup(popupAssunto, chipAssunto);
        }
    }

    @FXML
    private void handleToggleChipDificuldade(MouseEvent event) {
        event.consume();
        if (chipAtivo(chipDificuldade)) {
            desativarChip(chipDificuldade, "dificuldade");
            filtroDificuldade = null;
            fecharPopupAtual();
            atualizarLista();
        } else {
            ativarChip(chipDificuldade);
            togglePopup(popupDificuldade, chipDificuldade);
        }
    }

    @FXML
    private void handleEscolherDificuldade(MouseEvent event) {
        // tratado via preencherPopupDificuldade()
    }

    // ================================================================
    // FECHAR POPUPS AO CLICAR NO FUNDO
    // ================================================================

    @FXML
    private void handleFecharTodosPopups(MouseEvent event) {
        fecharPopupAtual();
    }

    /** Consome o clique dentro do popup para que não propague ao fundo */
    @FXML
    private void handleConsumir(MouseEvent event) {
        event.consume();
    }

    // ================================================================
    // LISTA / FILTRO
    // ================================================================

    private void atualizarLista() {
        itensFiltrados.clear();

        for (ItemBusca item : todosItens) {
            if (filtroTipo != null && !item.tipo.equals(filtroTipo)) continue;
            if (filtroDisciplina != null && !item.disciplina.equals(filtroDisciplina)) continue;
            if (filtroSemestre != null && !item.semestre.equals(filtroSemestre)) continue;
            if (filtroAssunto != null && !item.assunto.equals(filtroAssunto)) continue;
            if (filtroDificuldade != null && !item.dificuldade.equals(filtroDificuldade)) continue;
            itensFiltrados.add(item);
        }

        renderizarLista();
    }

    private void renderizarLista() {
        listaContainer.getChildren().clear();

        if (itensFiltrados.isEmpty()) {
            Label vazio = new Label("Nenhum resultado encontrado.");
            vazio.setStyle("-fx-text-fill: #4a6a7a; -fx-font-size: 14px; -fx-padding: 20;");
            listaContainer.getChildren().add(vazio);
            return;
        }

        for (ItemBusca item : itensFiltrados) {
            StackPane row = new StackPane();
            row.getStyleClass().add("lista-item");
            row.setAlignment(Pos.CENTER_LEFT);

            // Ícone indicando o tipo
            String icone = item.tipo.equals("Prova") ? "📄 " : "❓ ";
            Label lbl = new Label(icone + item.descricao);
            lbl.getStyleClass().add("lista-item-label");

            // Adiciona badge com o tipo
            Label badge = new Label(item.tipo);
            badge.getStyleClass().add("lista-item-badge");
            badge.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-padding: 2 8;");

            HBox content = new HBox(10);
            content.setAlignment(Pos.CENTER_LEFT);
            content.getChildren().addAll(lbl, badge);

            row.getChildren().add(content);

            // ═══════════════════════════════════════════════════════════════
            // Clique no item - navega para visualizar
            // ═══════════════════════════════════════════════════════════════
            row.setOnMouseClicked(e -> {
                // Remove seleção anterior
                for (javafx.scene.Node n : listaContainer.getChildren()) {
                    n.getStyleClass().remove("lista-item-selected");
                }

                // Marca o item atual como selecionado
                row.getStyleClass().add("lista-item-selected");

                // Navega para visualizar o item
                if (item.tipo.equals("Prova")) {
                    // Navega para visualizar a prova
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/br/edu/ufersa/aplicativo/views/TelaRelatorioProvaView.fxml"));
                        Parent root = loader.load();
                        TelaRelatorioProvaController controller = loader.getController();
                        controller.setProva(item.prova);

                        Stage stage = (Stage) row.getScene().getWindow();
                        stage.setScene(new Scene(root, 1280, 750));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Para questões, navega para a tela de questões com a questão selecionada
                    try {
                        // Carrega a questão completa do banco
                        Questao questaoCompleta = null;
                        List<Questao> todasQuestoes = questaoDAO.listar();
                        for (Questao q : todasQuestoes) {
                            if (String.valueOf(q.getCodigo()).equals(item.codigoQuestao)) {
                                questaoCompleta = q;
                                break;
                            }
                        }

                        if (questaoCompleta != null) {
                            // Salva a questão selecionada no Contexto
                            Contexto.setQuestaoSelecionada(questaoCompleta);

                            // Também salva a disciplina no Contexto para a tela de questões
                            // Busca a disciplina pelo nome
                            List<Disciplina> disciplinas = disciplinaDAO.listar();
                            for (Disciplina d : disciplinas) {
                                if (d.getNome().equals(item.disciplina)) {
                                    TelaInicialController.DisciplinaInfo discInfo = new TelaInicialController.DisciplinaInfo(
                                            d.getNome(),      // nome
                                            d.getCodigo(),    // codigo
                                            0,                // quantidade (0 pois é só para contexto)
                                            ""                // professor (vazio)
                                    );
                                    Contexto.setDisciplinaSelecionada(discInfo);
                                    break;
                                }
                            }

                            FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource("/br/edu/ufersa/aplicativo/views/QuestoesView.fxml"));
                            Parent root = loader.load();

                            Stage stage = (Stage) row.getScene().getWindow();
                            stage.setScene(new Scene(root, 1280, 750));
                        } else {
                            alerta("Erro", "Questão não encontrada no banco de dados.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        alerta("Erro", "Erro ao abrir a questão: " + ex.getMessage());
                    }
                }

                e.consume();
            });

            listaContainer.getChildren().add(row);
        }
    }

    // ================================================================
    // GERENCIAMENTO DE POPUPS
    // ================================================================

    private void togglePopup(VBox popup, javafx.scene.Node ancora) {
        if (popupAtual != null && popupAtual != popup) {
            fecharPopup(popupAtual);
        }

        boolean estaAberto = popup.isVisible();
        if (estaAberto) {
            fecharPopup(popup);
        } else {
            abrirPopup(popup, ancora);
        }
    }

    private void abrirPopup(VBox popup, javafx.scene.Node ancora) {
        popup.setVisible(true);
        popup.setManaged(true);
        popupAtual = popup;

        javafx.application.Platform.runLater(() -> {
            javafx.geometry.Bounds bounds = ancora.localToScene(ancora.getBoundsInLocal());
            javafx.geometry.Bounds rootB  = centerRoot.localToScene(centerRoot.getBoundsInLocal());

            double tx = bounds.getMinX() - rootB.getMinX();
            double ty = bounds.getMaxY() - rootB.getMinY() + 4;

            double popupW = popup.prefWidth(-1);
            if (popupW <= 0) popupW = 200;
            double maxX = centerRoot.getWidth() - popupW - 10;
            if (tx > maxX) tx = maxX;

            StackPane.setAlignment(popup, javafx.geometry.Pos.TOP_LEFT);
            popup.setTranslateX(tx);
            popup.setTranslateY(ty);
        });
    }

    private void fecharPopup(VBox popup) {
        popup.setVisible(false);
        popup.setManaged(false);
        if (popupAtual == popup) popupAtual = null;
    }

    private void fecharPopupAtual() {
        if (popupAtual != null) fecharPopup(popupAtual);
    }

    // ================================================================
    // HELPERS DE ESTILO
    // ================================================================

    private void resetEstiloBotoes() {
        for (StackPane btn : List.of(btnProva, btnQuestoes)) {
            btn.getStyleClass().remove("tipo-btn-selected");
            if (btn.getChildren().get(0) instanceof Label l) {
                l.getStyleClass().remove("tipo-btn-label-selected");
                if (!l.getStyleClass().contains("tipo-btn-label"))
                    l.getStyleClass().add("tipo-btn-label");
            }
        }
    }

    private void ativarBotaoTipo(StackPane btn) {
        btn.getStyleClass().add("tipo-btn-selected");
        if (btn.getChildren().get(0) instanceof Label l) {
            l.getStyleClass().remove("tipo-btn-label");
            l.getStyleClass().add("tipo-btn-label-selected");
        }
    }

    private boolean chipAtivo(StackPane chip) {
        return chipsAtivos.contains(chip);
    }

    private void ativarChip(StackPane chip) {
        if (!chipsAtivos.contains(chip)) chipsAtivos.add(chip);
        chip.getStyleClass().add("chip-filtro-ativo");
        if (chip.getChildren().get(0) instanceof Label l) {
            l.getStyleClass().add("chip-label-ativo");
        }
    }

    private void desativarChip(StackPane chip, String labelOriginal) {
        chipsAtivos.remove(chip);
        chip.getStyleClass().remove("chip-filtro-ativo");
        if (chip.getChildren().get(0) instanceof Label l) {
            l.getStyleClass().remove("chip-label-ativo");
            l.setText(labelOriginal);
        }
    }

    private void atualizarLabelChip(StackPane chip, String prefixo, String valor) {
        if (chip.getChildren().get(0) instanceof Label l) {
            l.setText(valor != null ? valor : prefixo);
        }
    }

    private void resetarChips() {
        for (StackPane chip : List.of(chipDisciplinaProva, chipSemestre,
                chipDisciplinaQuest, chipAssunto, chipDificuldade)) {
            chip.getStyleClass().remove("chip-filtro-ativo");
            if (chip.getChildren().get(0) instanceof Label l) {
                l.getStyleClass().remove("chip-label-ativo");
            }
        }
        chipsAtivos.clear();
        setChipLabel(chipDisciplinaProva, "disciplina");
        setChipLabel(chipSemestre,        "semestre");
        setChipLabel(chipDisciplinaQuest, "disciplina");
        setChipLabel(chipAssunto,         "assuntos");
        setChipLabel(chipDificuldade,     "dificuldade");
    }

    private void setChipLabel(StackPane chip, String texto) {
        if (chip.getChildren().get(0) instanceof Label l) l.setText(texto);
    }

    // ================================================================
    // NAVEGAÇÃO SIDEBAR
    // ================================================================

    @FXML private void handleVoltar(MouseEvent e)           { navegarPara("TelaInicialView"); }
    @FXML private void handleMenuDisciplinas(MouseEvent e)  { navegarPara("TelaInicialView"); }
    @FXML private void handleMenuBuscar(MouseEvent e)       { /* já estou aqui */ }
    @FXML private void handleMenuGerarProva(MouseEvent e)   { navegarPara("TelaGerarProvaView"); }
    @FXML private void handleMenuRelatorio(MouseEvent e)    { navegarPara("TelaRelatorioView"); }

    private void navegarPara(String nomeView) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/aplicativo/views/" + nomeView + ".fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listaContainer.getScene().getWindow();
            boolean fs  = stage.isFullScreen();
            boolean max = stage.isMaximized();
            stage.setScene(new Scene(root, 1280, 750));
            if (fs)  stage.setFullScreen(true);
            if (max) stage.setMaximized(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // ================================================================
    // MODELO DE DADOS
    // ================================================================

    public static class ItemBusca {
        final String descricao;
        final String tipo;        // "Prova" ou "Questões"
        final String disciplina;
        final String semestre;
        final String assunto;
        final String dificuldade;
        final Prova prova;        // Referência à prova, se for uma
        final String codigoQuestao; // Código da questão, se for uma questão

        // Construtor para Questão
        public ItemBusca(Questao q) {
            String nomeDisc = (q.getDisciplina() != null) ? q.getDisciplina().getNome() : "Geral";
            this.descricao   = "Questão " + q.getCodigo() + " — " + nomeDisc;
            this.tipo        = "Questões";
            this.disciplina  = nomeDisc;
            this.semestre    = "";
            this.assunto     = q.getAssunto() != null ? q.getAssunto() : "Geral";
            this.dificuldade = (q.getNivel() != null) ? q.getNivel().getDescricaoTela() : "Fácil";
            this.prova       = null;
            this.codigoQuestao = String.valueOf(q.getCodigo());
        }

        // Construtor para Prova
        public ItemBusca(Prova p) {
            String nomeDisc = (p.getDisciplina() != null) ? p.getDisciplina().getNome() : "Geral";
            String info = p.getQuestoes() != null ? p.getQuestoes().size() + " questões" : "0 questões";
            this.descricao   = p.getCodigo() + " — " + nomeDisc + " (" + info + ")";
            this.tipo        = "Prova";
            this.disciplina  = nomeDisc;
            this.semestre    = "";
            this.assunto     = "Geral";
            this.dificuldade = "Geral";
            this.prova       = p;
            this.codigoQuestao = null;
        }
    }
}