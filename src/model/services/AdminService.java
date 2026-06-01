package src.model.services;

import src.model.DAO.AdminDAO;
import src.model.entities.Admin;
import src.model.entities.User;

import java.util.List;

public class AdminService {

    private AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    public void inserir(User user, Admin admin) {
        validarDadosUsuario(user);
        
        // Se estiver tudo correto, chama o DAO
        adminDAO.inserir(user, admin);
    }

    public List<User> listar() {
        return adminDAO.listar();
    }

    public void atualizar(User user) {
        if (user.getIdUser() <= 0) {
            throw new IllegalArgumentException("Erro: ID de administrador inválido para atualização.");
        }
        
        validarDadosUsuario(user);
        
        adminDAO.atualizar(user);
    }

    public void deletar(int idUser) {
        if (idUser <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        adminDAO.deletar(idUser);
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