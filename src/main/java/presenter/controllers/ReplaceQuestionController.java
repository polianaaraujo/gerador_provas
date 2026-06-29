package presenter.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.entities.Exam;
import model.entities.Question;
import model.services.ExamService;

import java.util.List;
import java.util.stream.Collectors;

public class ReplaceQuestionController {

    @FXML private Button btnFecharX;
    @FXML private ComboBox<Question> cbNovaQuestao;
    @FXML private Button btnCancelar;
    @FXML private Button btnSubstituir;

    private Exam examAtual;
    private Question questaoAntiga;
    private DetailsExamController controllerPai;

    private ExamService examService;
    private QuestionDAO questionDAO;

    @FXML
    public void initialize() {
        this.questionDAO = new QuestionDAO();
        this.examService = new ExamService(new ExamDAO(), this.questionDAO);

        btnFecharX.setOnAction(e -> fecharModal());
        btnCancelar.setOnAction(e -> fecharModal());
        btnSubstituir.setOnAction(e -> processarSubstituicao());

        // Customiza como a questão vai aparecer escrita no ComboBox
        cbNovaQuestao.setCellFactory(p -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String diff = item.getDifficulty() != null ? item.getDifficulty().name() : "N/A";
                    String textoCurto = item.getStatement().length() > 50
                            ? item.getStatement().substring(0, 47) + "..."
                            : item.getStatement();
                    setText("[Q-" + item.getQuestionId() + " | " + diff + "] " + textoCurto);
                }
            }
        });
        cbNovaQuestao.setButtonCell(cbNovaQuestao.getCellFactory().call(null));
    }

    public void inicializarDados(Exam exam, Question questao, DetailsExamController parent) {
        this.examAtual = exam;
        this.questaoAntiga = questao;
        this.controllerPai = parent;

        carregarQuestoesDisponiveis();
    }

    private void carregarQuestoesDisponiveis() {
        try {
            // Busca todas as questões do banco
            List<Question> todasQuestoes = questionDAO.findAll();

            // Filtra: Tem que ser da mesma disciplina E não pode já estar dentro dessa prova
            List<Question> disponiveis = todasQuestoes.stream()
                    .filter(q -> q.getSubject() != null && examAtual.getSubject() != null
                            && q.getSubject().getIdSubject() == examAtual.getSubject().getIdSubject())
                    .filter(q -> examAtual.getQuestions().stream().noneMatch(eq -> eq.getQuestionId() == q.getQuestionId()))
                    .collect(Collectors.toList());

            cbNovaQuestao.setItems(FXCollections.observableArrayList(disponiveis));

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao carregar as questões disponíveis.");
        }
    }

    private void processarSubstituicao() {
        Question novaQuestao = cbNovaQuestao.getValue();

        if (novaQuestao == null) {
            mostrarAlerta("Aviso", "Selecione uma nova questão para substituir.");
            return;
        }

        try {
            // 1. O serviço vai no banco E também atualiza a lista em memória (add/remove)
            examService.replaceQuestion(examAtual, questaoAntiga, novaQuestao);

            // 2. Manda a tela pai renderizar novamente com a lista já atualizada pelo serviço
            if (controllerPai != null) {
                controllerPai.inicializarDados(examAtual);
            }

            fecharModal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao substituir a questão: " + e.getMessage());
        }
    }

    private void fecharModal() {
        Stage stage = (Stage) btnFecharX.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}