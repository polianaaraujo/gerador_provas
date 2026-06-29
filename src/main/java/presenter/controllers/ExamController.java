package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.Exam;
import model.entities.Subject;
import model.services.ExamService;
import model.services.SubjectService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ExamController {

    @FXML private StackPane rootStackPane;
    @FXML private Button btnNovaProva;

    // Filtros
    @FXML private TextField txtBuscaTitulo;
    @FXML private MenuButton menuDisciplina;
    @FXML private TextField txtBuscaSemestre;

    // Listas e Estado Vazio
    @FXML private ScrollPane scrollPaneProvas;
    @FXML private VBox containerProvas;
    @FXML private VBox lblEstadoVazio;

    private ExamService examService;
    private SubjectService subjectService;

    private Subject disciplinaSelecionada = null;

    @FXML
    public void initialize() {
        this.examService = new ExamService(new ExamDAO(), new QuestionDAO());
        this.subjectService = new SubjectService(new SubjectDAO());

        // Ação para o botão de mudar para a tela de geração automática
        btnNovaProva.setOnAction(e -> abrirNovaProva());

        // Configura e popula filtros visuais
        carregarFiltrosDisciplina();
        configurarGatilhosFiltros();

        // Faz o carregamento inicial das provas cadastradas
        carregarEFiltrarProvas();
    }

    private void carregarFiltrosDisciplina() {
        if (menuDisciplina == null) return;
        menuDisciplina.getItems().clear();

        MenuItem itemTodas = new MenuItem("Todas as disciplinas");
        itemTodas.setOnAction(e -> {
            disciplinaSelecionada = null;
            menuDisciplina.setText("Todas as disciplinas");
            carregarEFiltrarProvas();
        });
        menuDisciplina.getItems().add(itemTodas);

        try {
            List<Subject> disciplinas = subjectService.findAll();
            for (Subject subj : disciplinas) {
                MenuItem item = new MenuItem(subj.getName());
                item.setOnAction(e -> {
                    disciplinaSelecionada = subj;
                    menuDisciplina.setText(subj.getName());
                    carregarEFiltrarProvas();
                });
                menuDisciplina.getItems().add(item);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar disciplinas nos filtros: " + e.getMessage());
        }
    }

    private void configurarGatilhosFiltros() {
        if (txtBuscaTitulo != null) {
            txtBuscaTitulo.textProperty().addListener((obs, antigo, novo) -> carregarEFiltrarProvas());
        }
        if (txtBuscaSemestre != null) {
            txtBuscaSemestre.textProperty().addListener((obs, antigo, novo) -> carregarEFiltrarProvas());
        }
    }

    public void carregarEFiltrarProvas() {
        if (examService == null || containerProvas == null) return;

        try {
            List<Exam> listaFiltrada = examService.findAll();

            // 1. Filtro por Disciplina Selecionada
            if (disciplinaSelecionada != null) {
                listaFiltrada = listaFiltrada.stream()
                        .filter(p -> p.getSubject() != null && p.getSubject().getIdSubject() == disciplinaSelecionada.getIdSubject())
                        .collect(Collectors.toList());
            }

            // 2. Filtro por Título da Prova (Verificação segura anti-null)
            String buscaTitulo = txtBuscaTitulo != null ? txtBuscaTitulo.getText() : "";
            if (buscaTitulo != null && !buscaTitulo.trim().isEmpty()) {
                final String termo = buscaTitulo.toLowerCase().trim();
                // Como dependendo da entidade o título fica no Semester ou Subject, filtramos onde você aplicar
                listaFiltrada = listaFiltrada.stream()
                        .filter(p -> (p.getSemester() != null && p.getSemester().toLowerCase().contains(termo)) ||
                                (p.getSubject() != null && p.getSubject().getName().toLowerCase().contains(termo)))
                        .collect(Collectors.toList());
            }

            // 3. Filtro por Semestre
            String buscaSemestre = txtBuscaSemestre != null ? txtBuscaSemestre.getText() : "";
            if (buscaSemestre != null && !buscaSemestre.trim().isEmpty()) {
                final String termoSemestre = buscaSemestre.toLowerCase().trim();
                listaFiltrada = listaFiltrada.stream()
                        .filter(p -> p.getSemester() != null && p.getSemester().toLowerCase().contains(termoSemestre))
                        .collect(Collectors.toList());
            }

            renderizarCardsProvas(listaFiltrada);

        } catch (Exception e) {
            System.err.println("Erro ao filtrar provas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderizarCardsProvas(List<Exam> provas) {
        containerProvas.getChildren().clear();

        if (provas == null || provas.isEmpty()) {
            lblEstadoVazio.setVisible(true);
            lblEstadoVazio.setManaged(true);
            scrollPaneProvas.setVisible(false);
            scrollPaneProvas.setManaged(false);
            return;
        }

        lblEstadoVazio.setVisible(false);
        lblEstadoVazio.setManaged(false);
        scrollPaneProvas.setVisible(true);
        scrollPaneProvas.setManaged(true);

        for (Exam exam : provas) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/card-exam.fxml"));

                // Injeta programaticamente o controlador passando a referência do pai
                CardExamController cardController = new CardExamController(this);
                loader.setController(cardController);

                HBox card = loader.load();
                cardController.setDados(exam);

                containerProvas.getChildren().add(card);
            } catch (Exception e) {
                System.err.println("Erro ao carregar card da prova ID " + exam.getExamId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void abrirNovaProva() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/new-exam.fxml"));
            Scene scene = btnNovaProva.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("Erro ao alternar para a tela de geração: " + e.getMessage());
        }
    }
}