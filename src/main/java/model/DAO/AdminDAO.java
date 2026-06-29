package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Admin;

public class AdminDAO {

    public void insert(Admin admin) {
        String sql = "INSERT INTO admin (admin_id) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, admin);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conceder privilégios de administrador: " + e.getMessage(), e);
        }
    }

    public void delete(int userId) {
        String sql = "DELETE FROM admin WHERE admin_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover privilégio de administrador: " + e.getMessage(), e);
        }
    }

    public List<Admin> findAll() {
        String sql = "SELECT a.*, u.* FROM admin a INNER JOIN user u ON a.admin_id = u.id_user WHERE u.status = true";
        List<Admin> list = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(instantiateAdmin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar administradores: " + e.getMessage(), e);
        }
        return list;
    }

    public Admin findById(int userId) {
        String sql = "SELECT a.*, u.* FROM admin a INNER JOIN user u ON a.admin_id = u.id_user WHERE a.admin_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instantiateAdmin(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar administrador por ID: " + e.getMessage(), e);
        }
        return null;
    }

    private void setStatementParams(PreparedStatement stmt, Admin admin) throws SQLException {
        stmt.setInt(1, admin.getUser().getIdUser());
    }

    private Admin instantiateAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setUser(UserDAO.instantiateUser(rs));
        return admin;
    }
}