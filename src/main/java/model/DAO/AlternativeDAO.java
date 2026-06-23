package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.entities.Alternative;
import model.entities.Question;

public class AlternativeDAO {

    private Connection conexao;

    public AlternativeDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public Alternative inserir(Alternative alternativa) {
        String sql = "INSERT INTO alternativas (texto, correta, question_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, alternativa.getTexto());
            stmt.setBoolean(2, alternativa.isCorreta());
            // Usamos o 'codigo' da Questão como Chave Estrangeira
            stmt.setInt(3, alternativa.getQuestao().getCodigo()); 

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        alternativa.setId(rs.getInt(1));
                    }
                }
            }
            return alternativa;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir alternativa no banco: " + e.getMessage(), e);
        }
    }

    // Método crucial: Busca apenas as alternativas de uma questão específica
    public List<Alternative> listarPorQuestao(int idQuestao) {
        String sql = "SELECT * FROM alternativas WHERE question_id = ?";
        List<Alternative> alternativas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idQuestao);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alternative alt = new Alternative();
                    alt.setId(rs.getInt("id"));
                    alt.setTexto(rs.getString("texto"));
                    alt.setCorreta(rs.getBoolean("correta"));
                    
                    // Recria a referência básica da questão
                    Question q = new Question();
                    q.setCodigo(rs.getInt("question_id"));
                    alt.setQuestao(q);
                    
                    alternativas.add(alt);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alternativas: " + e.getMessage(), e);
        }
        
        return alternativas;
    }

    public void atualizar(Alternative alternativa) {
        String sql = "UPDATE alternativas SET texto = ?, correta = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, alternativa.getTexto());
            stmt.setBoolean(2, alternativa.isCorreta());
            stmt.setInt(3, alternativa.getId());
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar alternativa: " + e.getMessage(), e);
        }
    }

    public void deletar(int idAlternativa) {
        String sql = "DELETE FROM alternativas WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idAlternativa);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar alternativa: " + e.getMessage(), e);
        }
    }
}