import java.sql.Connection;

import src.model.DAO.ConnectionFactory;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conexao = ConnectionFactory.getConnection();
            System.out.println("Ligação ao SisArq estabelecida com sucesso!");
            conexao.close();
        } catch (Exception e) {
            System.out.println("Ups, algo correu mal: " + e.getMessage());
        }
    }
    
}
