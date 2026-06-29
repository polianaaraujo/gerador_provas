package presenter.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.entities.Exam;
import model.services.ExamService;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CardExamController {

    @FXML private Label lblTituloProva;
    @FXML private Label lblNomeDisciplina;
    @FXML private Label lblQtdQuestoes;
    @FXML private Label lblDataCriacao;

    @FXML private Button btnExcluir;
    @FXML private Button btnDetalhes;

    private final ExamController examControllerPai;
    private final ExamService examService;
    private Exam examAtual;

    public CardExamController(ExamController examControllerPai) {
        this.examControllerPai = examControllerPai;
        this.examService = new ExamService(new ExamDAO(), new QuestionDAO());
    }

    public void setDados(Exam exam) {
        this.examAtual = exam;

        // 1. Seta o título se houver no objeto, senão adota um padrão de identificação
        if (lblTituloProva != null) {
            lblTituloProva.setText("Prova #" + exam.getExamId());
        }

        // 2. Preenche Disciplina e Semestre
        if (lblNomeDisciplina != null) {
            String disciplina = (exam.getSubject() != null) ? exam.getSubject().getName() : "Geral";
            String semestre = (exam.getSemester() != null) ? exam.getSemester() : "Semestre N/A";
            lblNomeDisciplina.setText(disciplina + " • " + semestre);
        }

        // 3. Preenche Quantidade de Questões vinculadas
        if (lblQtdQuestoes != null) {
            int total = (exam.getQuestions() != null) ? exam.getQuestions().size() : 0;
            lblQtdQuestoes.setText(total + (total == 1 ? " questão" : " questões"));
        }

        // 4. Formata e exibe a data de criação
        if (lblDataCriacao != null) {
            if (exam.getCreationDate() != null) {
                lblDataCriacao.setText(exam.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                lblDataCriacao.setText("--/--/----");
            }
        }

        // 5. Configuração dos Gatilhos dos botões do Card
        btnExcluir.setOnAction(e -> deletarProva());
        btnDetalhes.setOnAction(e -> verDetalhesProva());
    }

    private void deletarProva() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir esta prova permanentemente?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                examService.delete(examAtual.getExamId());
                // Força o controller da página principal a recarregar a lista atualizada do banco
                examControllerPai.carregarEFiltrarProvas();
            } catch (Exception e) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro ao Excluir");
                erro.setHeaderText(null);
                erro.setContentText(e.getMessage());
                erro.showAndWait();
            }
        }
    }

    private void verDetalhesProva() {
        try {
            // Carrega o FXML do modal de detalhes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/details-exam.fxml"));
            Parent root = loader.load();

            // 🎯 O SEGREDO: Busca a prova completa com todas as questões vinculadas do banco de dados!
            Exam provaCompleta = examService.findFullExamById(examAtual.getExamId());

            // Pega o controlador do modal e injeta a prova com as questões carregadas
            DetailsExamController controller = loader.getController();
            controller.inicializarDados(provaCompleta);

            // Cria e exibe a nova janela modal
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes da Prova");
            stage.initModality(Modality.APPLICATION_MODAL);

            if (btnDetalhes != null && btnDetalhes.getScene() != null) {
                stage.initOwner(btnDetalhes.getScene().getWindow());
            }

            stage.showAndWait();

        } catch (Exception e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro ao Abrir Detalhes");
            erro.setHeaderText(null);
            erro.setContentText("Não foi possível carregar a tela de detalhes: " + e.getMessage());
            erro.showAndWait();
            e.printStackTrace();
        }
    }
}