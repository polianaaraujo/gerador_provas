package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.entities.Alternative;
import model.entities.Question;
import model.entities.QuestionType;

public class QuestionDAO {

    private Connection conexao;

    public QuestionDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public Question inserir(Question question) {
        String sql = "INSERT INTO question (tipo, enunciado, gabarito, assunto, nivel_dificuldade) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, question.getTipo().name());
            stmt.setString(2, question.getEnunciado());
            stmt.setString(3, question.getGabarito());
            stmt.setString(4, question.getAssunto());
            stmt.setInt(5, question.getNivelDificuldade());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        question.setCodigo(rs.getInt(1));
                    }
                }
            }
            return question;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir questão no banco: " + e.getMessage(), e);
        }
    }

    public List<Question> listar() {
        String sql = "SELECT q.*, a.id AS alt_id, a.texto AS alt_texto, a.correta " +
                     "FROM question q " +
                     "LEFT JOIN alternativas a ON q.codigo = a.question_id";

        Map<Integer, Question> mapQuestoes = new LinkedHashMap<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int codigoQuestao = rs.getInt("codigo");
                Question question = mapQuestoes.get(codigoQuestao);

                if (question == null) {
                    question = new Question();
                    question.setCodigo(codigoQuestao);
                    
                    String tipoDoBanco = rs.getString("tipo");
                    if (tipoDoBanco != null) {
                        question.setTipo(QuestionType.valueOf(tipoDoBanco));
                    }
                    
                    question.setEnunciado(rs.getString("enunciado"));
                    question.setGabarito(rs.getString("gabarito"));
                    question.setAssunto(rs.getString("assunto"));
                    question.setNivelDificuldade(rs.getInt("nivel_dificuldade"));
                    
                    mapQuestoes.put(codigoQuestao, question);
                }

                int idAlternativa = rs.getInt("alt_id");
                if (idAlternativa > 0) {
                    Alternative alt = new Alternative();
                    alt.setId(idAlternativa);
                    alt.setTexto(rs.getString("alt_texto"));
                    alt.setCorreta(rs.getBoolean("correta"));
                    
                    question.getAlternativas().add(alt);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar questões: " + e.getMessage(), e);
        }

        // Converte os valores do Map de volta para uma List padrão
        return new ArrayList<>(mapQuestoes.values());
    }

    public void atualizar(Question question) {
        String sql = "UPDATE question SET tipo = ?, enunciado = ?, gabarito = ?, assunto = ?, nivel_dificuldade = ? WHERE codigo = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, question.getTipo().name());
            stmt.setString(2, question.getEnunciado());
            stmt.setString(3, question.getGabarito());
            stmt.setString(4, question.getAssunto());
            stmt.setInt(5, question.getNivelDificuldade());
            stmt.setInt(6, question.getCodigo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar questão: " + e.getMessage(), e);
        }
    }

    public void excluir(int codigo) {
        String sql = "DELETE FROM question WHERE codigo = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir questão: " + e.getMessage(), e);
        }
    }
}