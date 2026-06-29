package model.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.entities.Exam;
import model.entities.Question;
import model.entities.Subject;
import model.entities.Teacher;
import model.entities.User;

public class ExamDAO {

    public void insert(Exam exam) {
        String sql = "INSERT INTO exams (creation_date, semester, subject_id, teacher_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(stmt, exam);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    exam.setExamId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir prova no banco: " + e.getMessage(), e);
        }
    }

    public void update(Exam exam) {
        String sql = "UPDATE exams SET creation_date = ?, semester = ?, subject_id = ?, teacher_id = ? WHERE exam_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, exam);
            stmt.setInt(5, exam.getExamId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar prova: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM exams WHERE exam_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir prova: " + e.getMessage(), e);
        }
    }

    public List<Exam> findAll() {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.*, s.name as subject_name FROM exams e INNER JOIN subjects s ON e.subject_id = s.id_subject";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Instancia o QuestionDAO para buscar as questões de cada prova
            QuestionDAO questionDAO = new QuestionDAO();

            while (rs.next()) {
                Exam exam = new Exam();
                exam.setExamId(rs.getInt("exam_id"));

                // ==========================================
                // Lendo o semestre direto da coluna
                exam.setSemester(rs.getString("semester"));

                // Lendo a data de criação e convertendo de SQL Date para LocalDate
                if (rs.getDate("creation_date") != null) {
                    exam.setCreationDate(rs.getDate("creation_date").toLocalDate());
                }

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                subject.setName(rs.getString("subject_name"));
                exam.setSubject(subject);

                // Busca as questões no banco e salva na lista do Exam
                List<Question> questoesDaProva = questionDAO.findByExam(exam.getExamId());
                exam.setQuestions(questoesDaProva);

                list.add(exam);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar provas: " + e.getMessage(), e);
        }

        return list;
    }

    public Exam findById(int id) {
        // e.* traz TUDO de exams (exam_id, creation_date, semester, subject_id, teacher_id)
        // s.name e s.code trazem os extras da tabela subjects
        String sql = "SELECT e.*, s.name as subject_name, s.code as subject_code " +
                "FROM exams e " +
                "INNER JOIN subjects s ON e.subject_id = s.id_subject " +
                "WHERE e.exam_id = ?";

        Exam exam = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exam = new Exam();

                    // 1. Mapeando TODOS os campos da tabela 'exams'
                    exam.setExamId(rs.getInt("exam_id"));
                    exam.setSemester(rs.getString("semester"));

                    if (rs.getDate("creation_date") != null) {
                        exam.setCreationDate(rs.getDate("creation_date").toLocalDate());
                    }

                    // Criando o professor e setando apenas o ID (já que ele está na tabela exams)
                    User user = new User();
                    Teacher teacher = new Teacher();
                    user.setIdUser(rs.getInt("teacher_id"));
                    teacher.setUser(user);
                    exam.setTeacher(teacher);

                    // 2. Mapeando a tabela 'subjects' a partir do INNER JOIN
                    Subject subject = new Subject();
                    subject.setIdSubject(rs.getInt("subject_id"));
                    subject.setName(rs.getString("subject_name"));
                    subject.setCode(rs.getString("subject_code"));

                    exam.setSubject(subject);

                    // 3. Buscando a lista de questões vinculadas a esta prova
                    QuestionDAO questionDAO = new QuestionDAO();
                    exam.setQuestions(questionDAO.findByExam(exam.getExamId()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar prova por ID: " + e.getMessage(), e);
        }

        return exam;
    }

    public List<Exam> findBySemester(String semester) {
        String sql = "SELECT * FROM exams WHERE semester = ?";
        List<Exam> exams = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, semester);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exams.add(instantiateExam(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar provas por semestre: " + e.getMessage(), e);
        }
        return exams;
    }

    public List<Exam> findBySubject(int subjectId) {
        String sql = "SELECT * FROM exams WHERE subject_id = ?";
        List<Exam> exams = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, subjectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    exams.add(instantiateExam(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar provas por disciplina: " + e.getMessage(), e);
        }
        return exams;
    }
    public List<Exam> findByTeacher(int teacherId) {
        String sql = "SELECT * FROM exams WHERE teacher_id = ?";
        List<Exam> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
             
            st.setInt(1, teacherId);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateExam(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar as provas do professor: " + e.getMessage(), e);
        }
        return list;
    }
    
    public void linkQuestionsBatch(int examId, List<Question> questions) {
        if (questions == null || questions.isEmpty()) return;

        String sql = "INSERT INTO exam_question (exam_id, question_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Question q : questions) {
                stmt.setInt(1, examId);
                stmt.setInt(2, q.getQuestionId());
                stmt.addBatch(); 
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular lote de questões à prova: " + e.getMessage(), e);
        }
    }

    public void linkSingleQuestion(int examId, int questionId) {
        String sql = "INSERT INTO exam_question (exam_id, question_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular a questão à prova: " + e.getMessage(), e);
        }
    }

    public void unlinkQuestion(int examId, int questionId) {
        String sql = "DELETE FROM exam_question WHERE exam_id = ? AND question_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desvincular a questão da prova: " + e.getMessage(), e);
        }
    }


    private void setStatementParams(PreparedStatement stmt, Exam exam) throws SQLException {
        if (exam.getCreationDate() != null) {
            stmt.setDate(1, Date.valueOf(exam.getCreationDate()));
        } else {
            stmt.setNull(1, Types.DATE);
        }
        stmt.setString(2, exam.getSemester());
        stmt.setInt(3, exam.getSubject().getIdSubject());
        stmt.setInt(4, exam.getTeacher().getUser().getIdUser());
    }

    private Exam instantiateExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));

        Date data = rs.getDate("creation_date");
        if (data != null) {
            exam.setCreationDate(data.toLocalDate());
        }

        exam.setSemester(rs.getString("semester"));

        Subject subject = new Subject();
        subject.setIdSubject(rs.getInt("subject_id"));
        exam.setSubject(subject);

        Teacher teacher = new Teacher();
        User user = new User();
        user.setIdUser(rs.getInt("teacher_id"));
        teacher.setUser(user);
        exam.setTeacher(teacher);

        return exam;
    }
}