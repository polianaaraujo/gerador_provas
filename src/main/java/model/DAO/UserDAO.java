package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.entities.User;
import model.entities.UserRole;

public class UserDAO {

    private Connection conexao;

    public UserDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public User inserir(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole().name()); 

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setIdUser(rs.getInt(1)); 
                    }
                }
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário no banco: " + e.getMessage(), e);
        }
    }

    public List<User> listar() {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                
                user.setPassword(rs.getString("password"));

                String roleDoBanco = rs.getString("role");
                if (roleDoBanco != null) {
                    user.setRole(UserRole.valueOf(roleDoBanco)); 
                }
                
                usuarios.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return usuarios;
    }

    public void atualizar(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getIdUser());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void deletar(int idUser) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário: " + e.getMessage(), e);
        }
    }

    public User buscarPorEmailESenha(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        User user = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setIdUser(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    
                    String roleDoBanco = rs.getString("role");
                    if (roleDoBanco != null) {
                        user.setRole(UserRole.valueOf(roleDoBanco)); 
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar login: " + e.getMessage(), e);
        }
        
        return user;
    }
}