package src.model.services;

import src.model.DAO.TeacherDAO;
import src.model.entities.Teacher;
import src.model.entities.User;

import java.util.List;

public class TeacherService {

    private TeacherDAO teacherDAO;

    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
    }

    public void inserir(User user, Teacher teacher) {
        validarDadosUsuario(user);
        
        if (teacher.getResgistration_number() == null || teacher.getResgistration_number().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A matrícula do professor é obrigatória.");
        }

        teacherDAO.inserir(user, teacher);
    }

    public List<Teacher> listar() {
        return teacherDAO.listar();
    }

    public void atualizar(User user, Teacher teacher) {
        if ( user.getIdUser() <= 0) {
            throw new IllegalArgumentException("Erro: ID de professor inválido para atualização.");
        }
        
        validarDadosUsuario(user);
        
        if (teacher.getResgistration_number() == null || teacher.getResgistration_number().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A matrícula do professor não pode ser apagada.");
        }

        teacherDAO.atualizar(user, teacher);
    }

    public void deletar(int idUser) {
        if (idUser <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        teacherDAO.deletar(idUser);
    }

    private void validarDadosUsuario(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome não pode estar vazio.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O email não pode estar vazio.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A palavra-passe não pode estar vazia.");
        }
    }
}