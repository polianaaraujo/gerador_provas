package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.DAO.SubjectDAO;
import model.entities.*;
import model.services.ExamService;
import model.services.SubjectService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewExamController {

    @FXML private ComboBox<Subject> cbDisciplina;
    @FXML private ComboBox<String> cbSemestre;
    @FXML private TextField txtTitulo;
    @FXML private Label lblTotalQuestoes;

    @FXML private TextField txtQtdFacil;
    @FXML private TextField txtQtdMedio;
    @FXML private TextField txtQtdDificil;

    @FXML private Button btnGerarProva;

    private ExamService examService;
    private SubjectService subjectService;
    private QuestionDAO questionDAO;

    @FXML
    public void initialize() {
        this.questionDAO = new QuestionDAO();
        this.examService = new ExamService(new ExamDAO(), this.questionDAO);
        this.subjectService = new SubjectService(new SubjectDAO());

        cbSemestre.setItems(FXCollections.observableArrayList("2026.1", "2026.2", "2025.1", "2025.2"));

        try {
            cbDisciplina.setItems(FXCollections.observableArrayList(subjectService.findAll()));
        } catch (Exception e) {
            System.err.println("Erro ao listar disciplinas: " + e.getMessage());
        }

        cbDisciplina.setCellFactory(p -> new ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cbDisciplina.setButtonCell(cbDisciplina.getCellFactory().call(null));

        // Listener dinâmico para somar o total de questões automaticamente na interface
        javafx.beans.value.ChangeListener<String> somaTotalListener = (obs, antigo, novo) -> {
            int total = lerQuantidade(txtQtdFacil) + lerQuantidade(txtQtdMedio) + lerQuantidade(txtQtdDificil);
            lblTotalQuestoes.setText(String.valueOf(total));
        };
        txtQtdFacil.textProperty().addListener(somaTotalListener);
        txtQtdMedio.textProperty().addListener(somaTotalListener);
        txtQtdDificil.textProperty().addListener(somaTotalListener);

        btnGerarProva.setOnAction(e -> gerarProvaAutomatica());
    }

    private void gerarProvaAutomatica() {
        try {
            Subject disciplina = cbDisciplina.getValue();
            String semestre = cbSemestre.getValue();
            String titulo = txtTitulo.getText();

            if (disciplina == null || semestre == null || titulo == null || titulo.trim().isEmpty()) {
                mostrarAlerta("Campos Obrigatórios", "Por favor, preencha o Título, Disciplina e Semestre.");
                return;
            }

            int qtdFacil = lerQuantidade(txtQtdFacil);
            int qtdMedio = lerQuantidade(txtQtdMedio);
            int qtdDificil = lerQuantidade(txtQtdDificil);

            if (qtdFacil + qtdMedio + qtdDificil <= 0) {
                mostrarAlerta("Quantidade Inválida", "Defina a quantidade de questões para pelo menos um nível.");
                return;
            }

            List<Question> todasDoBanco = questionDAO.findAll();
            List<Question> filtradas = new ArrayList<>();

            for (Question q : todasDoBanco) {
                if (q.getSubject() != null && q.getSubject().getIdSubject() == disciplina.getIdSubject()) {
                    filtradas.add(q);
                }
            }

            List<Question> faceis = new ArrayList<>();
            List<Question> medias = new ArrayList<>();
            List<Question> dificeis = new ArrayList<>();

            for (Question q : filtradas) {
                if (q.getDifficulty() == Difficulty.EASY) faceis.add(q);
                else if (q.getDifficulty() == Difficulty.MEDIUM) medias.add(q);
                else if (q.getDifficulty() == Difficulty.HARD) dificeis.add(q);
            }

            if (faceis.size() < qtdFacil || medias.size() < qtdMedio || dificeis.size() < qtdDificil) {
                mostrarAlerta("Banco Insuficiente", "Disponíveis nesta disciplina -> Fáceis: " + faceis.size() + ", Médias: " + medias.size() + ", Difíceis: " + dificeis.size());
                return;
            }

            Collections.shuffle(faceis);
            Collections.shuffle(medias);
            Collections.shuffle(dificeis);

            List<Question> questoesSelecionadas = new ArrayList<>();
            questoesSelecionadas.addAll(faceis.subList(0, qtdFacil));
            questoesSelecionadas.addAll(medias.subList(0, qtdMedio));
            questoesSelecionadas.addAll(dificeis.subList(0, qtdDificil));

            // Configura o objeto Exam
            Exam novaProva = new Exam();
            novaProva.setSubject(disciplina);
            novaProva.setSemester(semestre);
            novaProva.setCreationDate(LocalDate.now());

            // Tratando de passar o Título se sua entidade aceitar ou concatenando no padrão do sistema
            // Se o seu Exam.java tiver set_title() ou similar use: novaProva.setTitle(titulo);

            Teacher professorMock = new Teacher();
            User userMock = new User();
            userMock.setIdUser(1); // Professor Admin logado
            professorMock.setUser(userMock);
            novaProva.setTeacher(professorMock);

            examService.saveGeneratedExam(novaProva, questoesSelecionadas);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso!");
            alert.setHeaderText(null);
            alert.setContentText("Prova '" + titulo + "' gerada com sucesso contendo " + questoesSelecionadas.size() + " questões!");
            alert.showAndWait();

            // Limpa o formulário
            txtTitulo.clear();
            txtQtdFacil.clear();
            txtQtdMedio.clear();
            txtQtdDificil.clear();
            lblTotalQuestoes.setText("--");

        } catch (Exception e) {
            mostrarAlerta("Erro ao Gerar Prova", e.getMessage());
            e.printStackTrace();
        }
    }

    private int lerQuantidade(TextField field) {
        if (field == null || field.getText().trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}