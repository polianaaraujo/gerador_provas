package model.services;

import java.util.List;

import model.DAO.ExamDAO;
import model.DAO.QuestionDAO;
import model.entities.Exam;
import model.entities.Question;
import model.exceptions.EntidadeNaoEncontradaException;
import model.exceptions.RegraNegocioException;

public class ExamService {

    private ExamDAO examDAO;
    private QuestionDAO questionDAO;

    public ExamService(ExamDAO examDAO, QuestionDAO questionDAO) {
        this.examDAO = examDAO;
        this.questionDAO = questionDAO;
    }

    public void insert(Exam exam) {
        validate(exam);
        examDAO.insert(exam);
    }

    public void update(Exam exam) {
        if (exam == null || exam.getExamId() <= 0) {
            throw new RegraNegocioException("Código da prova inválido para atualização.");
        }
        
        validate(exam);
        examDAO.update(exam);
    }

    public void delete(int id) {
        if (id <= 0) {
            throw new RegraNegocioException("Código da prova inválido para exclusão.");
        }
        examDAO.delete(id);
    }

    public List<Exam> findAll() {
        return examDAO.findAll();
    }

    public Exam findById(int id) {
        if (id <= 0) {
            throw new RegraNegocioException("ID da prova inválido. O ID deve ser maior que zero.");
        }
        
        Exam exam = examDAO.findById(id);
        
        if (exam == null) {
            throw new EntidadeNaoEncontradaException("Nenhuma prova encontrada com o ID informado.");
        }
        
        return exam;
    }
    public Exam findFullExamById(int id) {
        Exam exam = findById(id); 
        
        List<Question> questoesDaProva = questionDAO.findByExam(id);
        exam.setQuestions(questoesDaProva);

        return exam;
    }

    public List<Exam> findBySemester(String semester) {
        if (semester == null || semester.trim().isEmpty()) {
            throw new RegraNegocioException("O campo semestre é obrigatório e não pode ser vazio.");
        }

        if (!semester.matches("\\d{4}\\.\\d")) {
            throw new RegraNegocioException("O semestre deve seguir o formato padrão (Ex: 2026.1 ou 2026.2).");
        }

        return examDAO.findBySemester(semester);
    }

    public List<Exam> findBySubject(int subjectId) {
        if (subjectId <= 0) {
            throw new RegraNegocioException("ID da disciplina inválido. Selecione uma disciplina válida na lista.");
        }

        return examDAO.findBySubject(subjectId);
    }
    public List<Exam> findByTeacher(int teacherId) {
        if (teacherId <= 0) {
            throw new RegraNegocioException("ID do professor inválido para busca.");
        }
        return examDAO.findByTeacher(teacherId);
    }

    public void addQuestion(Exam exam, Question question) {
        if (exam == null || question == null) {
            throw new RegraNegocioException("Prova ou questão inválida para o vínculo.");
        }
        exam.insertQuestion(question); 
        
        if (exam.getExamId() > 0 && question.getQuestionId() > 0) {
            examDAO.linkSingleQuestion(exam.getExamId(), question.getQuestionId());
        }
    }

    public void removeQuestion(Exam exam, Question question) {
        if (exam == null || question == null) {
            throw new RegraNegocioException("Prova ou questão inválida para o desvínculo.");
        }
        exam.removeQuestion(question);

        if (exam.getExamId() > 0 && question.getQuestionId() > 0) {
            examDAO.unlinkQuestion(exam.getExamId(), question.getQuestionId());
        }
    }
    public void replaceQuestion(Exam exam, Question oldQuestion, Question newQuestion) {
        if (exam == null || oldQuestion == null || newQuestion == null) {
            throw new RegraNegocioException("Dados inválidos para substituição de questão.");
        }

        // Reutiliza a lógica robusta que você já construiu
        removeQuestion(exam, oldQuestion);
        addQuestion(exam, newQuestion);
    }

    public void saveGeneratedExam(Exam exam, List<Question> generatedQuestions) {
        if (generatedQuestions == null || generatedQuestions.isEmpty()) {
            throw new RegraNegocioException("Não é possível salvar uma prova sem questões.");
        }
        insert(exam);
        examDAO.linkQuestionsBatch(exam.getExamId(), generatedQuestions);
        exam.setQuestions(generatedQuestions);
    }

    private void validate(Exam exam) {
        if (exam == null) {
            throw new RegraNegocioException("A prova não pode ser nula.");
        }

        if (exam.getSubject() == null || exam.getSubject().getIdSubject() <= 0) {
            throw new RegraNegocioException("A disciplina da prova é obrigatória.");
        }

        if (exam.getSemester() == null || exam.getSemester().trim().isEmpty()) {
            throw new RegraNegocioException("O semestre da prova é obrigatório.");
        }

        if (exam.getTeacher() == null || exam.getTeacher().getUser() == null || exam.getTeacher().getUser().getIdUser() <= 0) {
            throw new RegraNegocioException("O professor responsável pela prova é obrigatório.");
        }
    }
}