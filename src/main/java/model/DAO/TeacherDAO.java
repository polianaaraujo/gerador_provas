package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Teacher;

public class TeacherDAO {

    public void insert(Teacher teacher) {
        String sql = "INSERT INTO teacher (teacher_id, registration_number) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacher.getUser().getIdUser());
            setStatementParams(stmt, teacher, 2);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular dados do professor: " + e.getMessage(), e);
        }
    }

    public void update(Teacher teacher) {
        String sql = "UPDATE teacher SET registration_number = ? WHERE teacher_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, teacher, 1);
            stmt.setInt(2, teacher.getUser().getIdUser());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar matrícula do professor: " + e.getMessage(), e);
        }
    }

    public List<Teacher> findAll() {
        String sql = "SELECT t.*, u.* FROM teacher t INNER JOIN user u ON t.teacher_id = u.id_user WHERE u.status = true";
        List<Teacher> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(instantiateTeacher(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar professores: " + e.getMessage(), e);
        }
        return list;
    }

    public Teacher findById(int userId) {
        String sql = "SELECT t.*, u.* FROM teacher t INNER JOIN user u ON t.teacher_id = u.id_user WHERE t.teacher_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instantiateTeacher(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar professor por ID: " + e.getMessage(), e);
        }
        return null;
    }

    private void setStatementParams(PreparedStatement stmt, Teacher teacher, int parameterIndex) throws SQLException {
        stmt.setString(parameterIndex, teacher.getRegistration_number());
    }

    private Teacher instantiateTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setRegistration_number(rs.getString("registration_number"));

        teacher.setUser(UserDAO.instantiateUser(rs));

        return teacher;
    }
}