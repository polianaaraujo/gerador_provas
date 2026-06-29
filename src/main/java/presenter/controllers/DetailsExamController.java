package presenter.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Exam;
import model.entities.Question;
import model.entities.MultipleChoiceQuestion;

import java.time.format.DateTimeFormatter;

public class DetailsExamController {

    @FXML private Label lblNomeProva;
    @FXML private Label lblDisciplina;
    @FXML private Label lblSemestre;
    @FXML private Label lblDataCriacao;
    @FXML private Label lblTotalQuestoes;

    @FXML private Button btnFechar;
    @FXML private VBox vboxQuestoesProva;

    @FXML
    public void initialize() {
        btnFechar.setOnAction(e -> {
            Stage stage = (Stage) btnFechar.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Injeta a prova selecionada, preenche os cabeçalhos e gera as questões reais na tela.
     */
    public void inicializarDados(Exam exam) {
        if (exam == null) return;

        // 1. Preenche os metadados básicos do cabeçalho do Modal
        lblNomeProva.setText("Prova #" + exam.getExamId());
        lblDisciplina.setText(exam.getSubject() != null ? exam.getSubject().getName() : "Geral");
        lblSemestre.setText(exam.getSemester() != null ? exam.getSemester() : "N/A");

        if (exam.getCreationDate() != null) {
            lblDataCriacao.setText(exam.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblDataCriacao.setText("--/--/----");
        }

        // 2. Limpa o container de questões para renderização limpa
        vboxQuestoesProva.getChildren().clear();

        if (exam.getQuestions() == null || exam.getQuestions().isEmpty()) {
            lblTotalQuestoes.setText("0"); // Zera o contador se não houver questões
            Label lblAviso = new Label("Esta prova não possui questões vinculadas.");
            lblAviso.setStyle("-fx-text-fill: #64748b; -fx-font-style: italic;");
            vboxQuestoesProva.getChildren().add(lblAviso);
            return;
        }

        int contadorQuestoesReais = 0;

        // Loop criando o layout de cada questão dinamicamente usando as classes do seu question.css
        for (Question q : exam.getQuestions()) {
            VBox cardQuestao = new VBox();
            cardQuestao.setSpacing(12.0);
            cardQuestao.setMaxWidth(Double.MAX_VALUE);
            cardQuestao.getStyleClass().add("cartao-questao");
            cardQuestao.getStylesheets().add(getClass().getResource("/styles/question.css").toExternalForm());
            cardQuestao.setPadding(new Insets(16, 20, 16, 20));

            // Linha superior: Badges e Botões de Ação
            HBox linhaTop = new HBox();
            linhaTop.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            linhaTop.setSpacing(8.0);

            Label badgeId = new Label("Q-" + q.getQuestionId());
            badgeId.getStyleClass().add("badge-padrao");

            // Mapeamento visual das cores do badge de dificuldade
            Label badgeDificuldade = new Label();
            badgeDificuldade.getStyleClass().removeAll("badge-facil", "badge-medio", "badge-dificil");
            if (q.getDifficulty() != null) {
                String diff = q.getDifficulty().name();
                if ("EASY".equalsIgnoreCase(diff)) {
                    badgeDificuldade.setText("FÁCIL");
                    badgeDificuldade.getStyleClass().add("badge-facil");
                } else if ("HARD".equalsIgnoreCase(diff)) {
                    badgeDificuldade.setText("DIFÍCIL");
                    badgeDificuldade.getStyleClass().add("badge-dificil");
                } else {
                    badgeDificuldade.setText("MÉDIO");
                    badgeDificuldade.getStyleClass().add("badge-medio");
                }
            } else {
                badgeDificuldade.setText("MÉDIO");
                badgeDificuldade.getStyleClass().add("badge-medio");
            }

            Label badgeTipo = new Label();
            badgeTipo.getStyleClass().add("badge-padrao");

            Region mola = new Region();
            HBox.setHgrow(mola, Priority.ALWAYS);

            // Botão de Substituir Questão (Ícone refrescar)
            Button btnSubstituir = new Button();
            btnSubstituir.getStyleClass().add("btn-acao-card");
            btnSubstituir.getStylesheets().add(getClass().getResource("/styles/exam.css").toExternalForm());
            try {
                ImageView imgRefrescar = new ImageView(new Image(getClass().getResourceAsStream("/icons/refrescar.png")));
                imgRefrescar.setFitHeight(16);
                imgRefrescar.setFitWidth(16);
                btnSubstituir.setGraphic(imgRefrescar);
            } catch (Exception ignored) {}

            // Botão de Remover Questão da Prova (Ícone lixo)
            Button btnRemover = new Button();
            btnRemover.getStyleClass().add("btn-acao-card");
            btnRemover.getStylesheets().add(getClass().getResource("/styles/exam.css").toExternalForm());
            try {
                ImageView imgLixo = new ImageView(new Image(getClass().getResourceAsStream("/icons/lixo.png")));
                imgLixo.setFitHeight(15);
                imgLixo.setFitWidth(15);
                btnRemover.setGraphic(imgLixo);
            } catch (Exception ignored) {}

            linhaTop.getChildren().addAll(badgeId, badgeDificuldade, badgeTipo, mola, btnSubstituir, btnRemover);

            // Caminho da disciplina
            String nomeMateria = exam.getSubject() != null ? exam.getSubject().getName() : "Geral";
            String assunto = q.getTopic() != null ? q.getTopic() : "Geral";
            Label lblCaminho = new Label(nomeMateria + " > " + assunto);
            lblCaminho.getStyleClass().add("txt-caminho");

            // Enunciado da questão
            Label lblEnunciado = new Label(q.getStatement());
            lblEnunciado.getStyleClass().add("txt-enunciado");
            lblEnunciado.setWrapText(true);

            // Bloco de conteúdo dinâmico (Alternativas ou Linhas de Resposta)
            VBox boxConteudoEspecifico = new VBox(6.0);
            VBox.setMargin(boxConteudoEspecifico, new Insets(5, 0, 5, 0));

            // Identifica a herança real da questão (Polimorfismo)
            if (q instanceof MultipleChoiceQuestion) {
                badgeTipo.setText("Múltipla Escolha");
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;

                if (mcq.getAlternativeA() != null) boxConteudoEspecifico.getChildren().add(criarLabelOpcao("A) " + mcq.getAlternativeA()));
                if (mcq.getAlternativeB() != null) boxConteudoEspecifico.getChildren().add(criarLabelOpcao("B) " + mcq.getAlternativeB()));
                if (mcq.getAlternativeC() != null) boxConteudoEspecifico.getChildren().add(criarLabelOpcao("C) " + mcq.getAlternativeC()));
                if (mcq.getAlternativeD() != null) boxConteudoEspecifico.getChildren().add(criarLabelOpcao("D) " + mcq.getAlternativeD()));

                Label lblGabarito = new Label("Gabarito: " + mcq.getAnswerKey());
                lblGabarito.getStyleClass().add("txt-gabarito");
                VBox.setMargin(lblGabarito, new Insets(5, 0, 0, 0));
                cardQuestao.getChildren().addAll(linhaTop, lblCaminho, lblEnunciado, boxConteudoEspecifico, lblGabarito);

            } else {
                badgeTipo.setText("Discursiva");

                Label lblDiscursivaInfo = new Label("📋 Resposta esperada direto na folha de avaliação.");
                lblDiscursivaInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #2563eb; -fx-font-style: italic;");
                boxConteudoEspecifico.getChildren().add(lblDiscursivaInfo);

                if (q.getAnswerKey() != null && !q.getAnswerKey().trim().isEmpty()) {
                    Label lblExpectativa = new Label("Expectativa de Resposta: " + q.getAnswerKey());
                    lblExpectativa.getStyleClass().add("txt-gabarito");
                    VBox.setMargin(lblExpectativa, new Insets(5, 0, 0, 0));
                    cardQuestao.getChildren().addAll(linhaTop, lblCaminho, lblEnunciado, boxConteudoEspecifico, lblExpectativa);
                } else {
                    cardQuestao.getChildren().addAll(linhaTop, lblCaminho, lblEnunciado, boxConteudoEspecifico);
                }
            }

            // Incrementa o contador local e adiciona o card gerado à tela
            contadorQuestoesReais++;
            vboxQuestoesProva.getChildren().add(cardQuestao);
        }


        lblTotalQuestoes.setText(String.valueOf(contadorQuestoesReais));
    }

    private Label criarLabelOpcao(String texto) {
        Label lbl = new Label(texto);
        lbl.getStyleClass().add("txt-opcao");
        lbl.setWrapText(true);
        return lbl;
    }
}