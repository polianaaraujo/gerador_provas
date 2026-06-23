package model.services;

import java.util.List;

import model.DAO.TeacherDAO;
import model.entities.Teacher;
import model.entities.User;
import model.entities.UserRole;

public class TeacherService {

    private TeacherDAO teacherDAO;
    private UserService userService; 

    public TeacherService(TeacherDAO teacherDAO, UserService userService) {
        this.teacherDAO = teacherDAO;
        this.userService = userService;
    }

    public void inserir(Teacher teacher) {
        if (teacher.getResgistration_number() == null || teacher.getResgistration_number().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A matrícula do professor é obrigatória.");
        }

        teacher.getUser().setRole(UserRole.TEACHER);

        User usuarioSalvo = userService.inserir(teacher.getUser());

        teacher.setUser(usuarioSalvo);
        teacherDAO.inserir(teacher);
    }

    public List<Teacher> listar() {
        return teacherDAO.listar();
    }

    public void atualizar(Teacher teacher) {
        if (teacher.getUser() == null || teacher.getUser().getIdUser() <= 0) { 
            throw new IllegalArgumentException("Erro: ID de professor inválido para atualização.");
        }
        
        if (teacher.getResgistration_number() == null || teacher.getResgistration_number().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A matrícula do professor não pode ser apagada.");
        }
        userService.atualizar(teacher.getUser());
        teacherDAO.atualizar(teacher);
    }

    public void deletar(int idTeacher) {
        if (idTeacher <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        
        teacherDAO.deletar(idTeacher);
        userService.deletar(idTeacher);
    }
}