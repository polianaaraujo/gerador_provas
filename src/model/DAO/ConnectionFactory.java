package src.model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String url = "jdbc:mysql://localhost:3306/gerador_provas";
    private static final String usuario = "root";
    private static final String senha = "12345678";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(url, usuario, senha);

        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados", e);
        }
    }
}