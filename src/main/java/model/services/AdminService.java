package model.services;

import java.util.List;

import model.DAO.AdminDAO;
import model.DAO.UserDAO;
import model.entities.Admin;
import model.entities.User;
import model.entities.UserRole;

public class AdminService {

    private AdminDAO adminDAO;
    private UserDAO userDAO;

    public AdminService(AdminDAO adminDAO, UserDAO userDAO) {
        this.adminDAO = adminDAO;
        this.userDAO = userDAO;
    }

    public void inserir(Admin admin) {
        if (admin.getUser() == null) {
            throw new IllegalArgumentException("Erro: Os dados do usuário não podem estar ausentes.");
        }

        admin.getUser().setRole(UserRole.ADMIN);

        validarDadosUsuario(admin.getUser());
        User usuarioSalvo = userDAO.inserir(admin.getUser());
        admin.setUser(usuarioSalvo);
        adminDAO.inserir(admin);
    }

    public List<Admin> listar() {
        return adminDAO.listar();
    }

    public void atualizar(Admin admin) {
        if (admin.getUser() == null || admin.getUser().getIdUser() <= 0) { 
            throw new IllegalArgumentException("Erro: ID de administrador inválido para atualização.");
        }
        
        validarDadosUsuario(admin.getUser());
        userDAO.atualizar(admin.getUser());
    }

    public void deletar(int idAdmin) {
        if (idAdmin <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        
        adminDAO.deletar(idAdmin);
        userDAO.deletar(idAdmin);
    }

    private void validarDadosUsuario(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Erro: Os dados do usuário não podem estar ausentes.");
        }
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