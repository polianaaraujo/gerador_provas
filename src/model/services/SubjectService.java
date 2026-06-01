package src.model.services;

import src.model.DAO.SubjectDAO;
import src.model.entities.Subject;

import java.util.List;

public class SubjectService {
    private SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
    }

    public void inserir(Subject subject) {
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da disciplina não pode estar vazio.");
        }
        
        if (subject.getTeacherId() == null || subject.getTeacherId() <= 0) {
            throw new IllegalArgumentException("Erro: É obrigatório associar um Professor válido à disciplina.");
        }
        subjectDAO.inserir(subject);
    }

    public List<Subject> listar() {
        return subjectDAO.listar();
    }

    public void atualizar(Subject subject) {
        if (subject.getIdSubject() == null || subject.getIdSubject() <= 0) {
            throw new IllegalArgumentException("Erro: ID da disciplina inválido para atualização.");
        }

        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da disciplina não pode ficar vazio.");
        }

        subjectDAO.atualizar(subject);
    }

    public void deletar(int idSubject) {
        if (idSubject <= 0) {
            throw new IllegalArgumentException("Erro: O ID da disciplina fornecido é inválido.");
        }

        subjectDAO.deletar(idSubject);
    }
}