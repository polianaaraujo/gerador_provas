package src.model.DAO;

import src.model.entities.Teacher;
import src.model.entities.User;
import src.model.DAO.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    
    public void inserir(User user, Teacher teacher) {
        String sqlUser = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String sqlTeacher = "INSERT INTO teachers (user_id, registration_number) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 
            
            PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, user.getName());
            stmtUser.setString(2, user.getEmail());
            stmtUser.setString(3, user.getPassword());
            stmtUser.setString(4, "teacher"); // Define o papel como professor
            
            stmtUser.executeUpdate();

            ResultSet rs = stmtUser.getGeneratedKeys();
            int idGerado = 0;
            if (rs.next()) {
                idGerado = rs.getInt(1);
                user.setIdUser(idGerado);
                teacher.setId(idGerado); 
            }

            PreparedStatement stmtTeacher = conn.prepareStatement(sqlTeacher);
            stmtTeacher.setInt(1, idGerado);
            stmtTeacher.setString(2, teacher.getResgistration_number()); 
            stmtTeacher.executeUpdate();
            
            conn.commit(); 

            rs.close();
            stmtUser.close();
            stmtTeacher.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 


    public List<Teacher> listar() {
        String sql = "SELECT u.user_id, u.name, u.email, u.password, u.role, t.registration_number " +
                     "FROM users u INNER JOIN teachers t ON u.user_id = t.user_id";
        
        List<Teacher> listaTeachers = new ArrayList<>();

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Teacher teacher = new Teacher();
                User user = new User();
                
                user.setIdUser(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                teacher.setResgistration_number(rs.getString("registration_number")); // Puxa a matrícula
                
                listaTeachers.add(teacher);
            }

            // Fechar os recursos
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaTeachers;
    }


    public void atualizar(User user, Teacher teacher) {
        String sqlUser = "UPDATE users SET name = ?, email = ?, password = ? WHERE user_id = ?";
        String sqlTeacher = "UPDATE teachers SET registration_number = ? WHERE user_id = ?";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Transação, pois atualizamos duas tabelas

            // Atualiza os dados base do utilizador
            PreparedStatement stmtUser = conn.prepareStatement(sqlUser);
            stmtUser.setString(1, user.getName());
            stmtUser.setString(2, user.getEmail());
            stmtUser.setString(3, user.getPassword());
            stmtUser.setInt(4, user.getIdUser());
            stmtUser.executeUpdate();

            // Atualiza a matrícula na tabela de professores
            PreparedStatement stmtTeacher = conn.prepareStatement(sqlTeacher);
            stmtTeacher.setString(1, teacher.getResgistration_number());
            stmtTeacher.setInt(2, user.getIdUser()); // Usa o mesmo ID
            stmtTeacher.executeUpdate();

            conn.commit();

            // Fechar os recursos
            stmtUser.close();
            stmtTeacher.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    
    public void deletar(int idUser) {
        // Como o ON DELETE CASCADE está ativo, basta apagar de 'users'
        String sql = "DELETE FROM users WHERE user_id = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idUser);
            stmt.executeUpdate();

            // Fechar os recursos
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}