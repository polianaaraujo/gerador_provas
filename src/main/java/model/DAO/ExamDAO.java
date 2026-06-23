package model.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.entities.Exam;
import model.entities.Subject;
import model.entities.Teacher;
import model.entities.User;

public class ExamDAO {

    public void inserir(Exam exam) {
        String sql = "INSERT INTO exams (title, creation_date, semester, subject_id, teacher_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, exam.getTitulo());

            if (exam.getDataDeCriacao() != null) {
                stmt.setDate(2, Date.valueOf(exam.getDataDeCriacao()));
            } else {
                stmt.setDate(2, null);
            }

            stmt.setString(3, exam.getSemestre());
            stmt.setInt(4, exam.getDisciplina().getIdSubject());
            stmt.setInt(5, exam.getProfessor().getUser().getIdUser());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir prova no banco: " + e.getMessage(), e);
        }
    }

    public ArrayList<Exam> listar() {
        String sql = "SELECT exam_id, title, creation_date, semester, subject_id, teacher_id FROM exams";

        ArrayList<Exam> exams = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Exam exam = new Exam();

                exam.setCodigo(rs.getInt("exam_id"));
                exam.setTitulo(rs.getString("title"));

                Date data = rs.getDate("creation_date");
                if (data != null) {
                    exam.setDataDeCriacao(data.toLocalDate());
                }

                exam.setSemestre(rs.getString("semester"));

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                exam.setDisciplina(subject);

                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                exam.setProfessor(teacher);

                exams.add(exam);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar provas: " + e.getMessage(), e);
        }

        return exams;
    }

    public void atualizar(Exam exam) {
        String sql = "UPDATE exams SET title = ?, creation_date = ?, semester = ?, subject_id = ?, teacher_id = ? WHERE exam_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, exam.getTitulo());

            if (exam.getDataDeCriacao() != null) {
                stmt.setDate(2, Date.valueOf(exam.getDataDeCriacao()));
            } else {
                stmt.setDate(2, null);
            }

            stmt.setString(3, exam.getSemestre());
            stmt.setInt(4, exam.getDisciplina().getIdSubject());
            stmt.setInt(5, exam.getProfessor().getUser().getIdUser());
            stmt.setInt(6, exam.getCodigo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar prova: " + e.getMessage(), e);
        }
    }

    public void excluir(int codigo) {
        String sql = "DELETE FROM exams WHERE exam_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir prova: " + e.getMessage(), e);
        }
    }
}