package model.services;

import java.util.ArrayList;

import model.DAO.ExamDAO;
import model.entities.Exam;
import model.entities.Question;

public class ExamService {

    private ExamDAO examDAO = new ExamDAO();

    public void cadastrar(Exam exam) {

        if (exam == null) {
            throw new IllegalArgumentException("A prova não pode ser nula.");
        }

        if (exam.getTitulo() == null || exam.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da prova é obrigatório.");
        }

        if (exam.getDisciplina() == null) {
            throw new IllegalArgumentException("A disciplina da prova é obrigatória.");
        }

        if (exam.getSemestre() == null || exam.getSemestre().trim().isEmpty()) {
            throw new IllegalArgumentException("O semestre da prova é obrigatório.");
        }

        if (exam.getProfessor() == null) {
            throw new IllegalArgumentException("O professor da prova é obrigatório.");
        }

        examDAO.inserir(exam);
    }

    public ArrayList<Exam> listar() {
        return examDAO.listar();
    }

    public void atualizar(Exam exam) {

        if (exam == null) {
            throw new IllegalArgumentException("A prova não pode ser nula.");
        }

        if (exam.getCodigo() <= 0) {
            throw new IllegalArgumentException("Código da prova inválido.");
        }

        if (exam.getTitulo() == null || exam.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título da prova é obrigatório.");
        }

        if (exam.getDisciplina() == null) {
            throw new IllegalArgumentException("A disciplina da prova é obrigatória.");
        }

        if (exam.getSemestre() == null || exam.getSemestre().trim().isEmpty()) {
            throw new IllegalArgumentException("O semestre da prova é obrigatório.");
        }

        if (exam.getProfessor() == null) {
            throw new IllegalArgumentException("O professor da prova é obrigatório.");
        }

        examDAO.atualizar(exam);
    }

    public void excluir(int codigo) {

        if (codigo <= 0) {
            throw new IllegalArgumentException("Código da prova inválido.");
        }

        examDAO.excluir(codigo);
    }

    public void adicionarQuestao(Exam exam, Question question) {

        if (exam == null) {
            throw new IllegalArgumentException("Prova inválida.");
        }

        if (question == null) {
            throw new IllegalArgumentException("Questão inválida.");
        }

        exam.addQuestao(question);
    }

    public void removerQuestao(Exam exam, Question question) {

        if (exam == null) {
            throw new IllegalArgumentException("Prova inválida.");
        }

        if (question == null) {
            throw new IllegalArgumentException("Questão inválida.");
        }

        exam.removerQuestao(question);
    }
}