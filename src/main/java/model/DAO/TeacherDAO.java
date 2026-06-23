package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Teacher;
import model.entities.User;
import model.entities.UserRole;

public class TeacherDAO {

    private Connection conexao;
    public TeacherDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Teacher teacher) {
        String sql = "INSERT INTO teachers (user_id, registration_number) VALUES (?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUser().getIdUser()); 
            stmt.setString(2, teacher.getResgistration_number());
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir professor no banco: " + e.getMessage(), e);
        }
    }

    public List<Teacher> listar() {
        String sql = "SELECT u.user_id, u.name, u.email, u.password, u.role, t.registration_number " +
                     "FROM users u INNER JOIN teachers t ON u.user_id = t.user_id";
        
        List<Teacher> listaTeachers = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                
                // CORREÇÃO: Recuperando o papel (role) do banco e convertendo para Enum
                String roleDoBanco = rs.getString("role");
                if (roleDoBanco != null) {
                    user.setRole(UserRole.valueOf(roleDoBanco)); 
                }
                
                Teacher teacher = new Teacher();
                teacher.setResgistration_number(rs.getString("registration_number"));
                teacher.setUser(user); 
                
                listaTeachers.add(teacher);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar professores: " + e.getMessage(), e);
        }
        
        return listaTeachers;
    }

    public void atualizar(Teacher teacher) {
        String sql = "UPDATE teachers SET registration_number = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, teacher.getResgistration_number());
            stmt.setInt(2, teacher.getUser().getIdUser()); 
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar professor: " + e.getMessage(), e);
        }
    }

    public void deletar(int idUser) {
        String sql = "DELETE FROM teachers WHERE user_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar professor: " + e.getMessage(), e);
        }
    }
}