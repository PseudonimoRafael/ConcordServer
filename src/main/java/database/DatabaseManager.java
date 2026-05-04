package database;

// essa classe vai criar e atualizar as tabelas do banco de dados(sqlite), alem de fornecer a conexao para as outras classes

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private Connection connection;


    public void conectar() {
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:concord.db");
            System.out.println("Conectado ao banco de dados");
            criarTabelas();
        }
        catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }


        private void criarTabelas() {

            String sqlTabelaUsers = "CREATE TABLE IF NOT EXISTS users (" +
                                    "nickname VARCHAR PRIMARY KEY," +
                                    "name VARCHAR NOT NULL,"+
                                    "email VARCHAR NOT NULL,"+
                                    "password VARCHAR NOT NULL," +
                                    "last_seen DATE)";
            String sqlTabelaMessages = "CREATE TABLE IF NOT EXISTS messages (" +
                                        "message_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "sender_id VARCHAR NOT NULL," +
                                        "receiver_id VARCHAR NOT NULL," +
                                        "content TEXT NOT NULL, " +
                                        "time TIME NOT NULL, " +
                                        "date DATE NOT NULL, " +
                                        "need_to_keep INTEGER DEFAULT 0,"+
                                        "FOREIGN KEY (receiver_id) REFERENCES users(nickname), "+
                                        "FOREIGN KEY (sender_id) REFERENCES users(nickname))";
            try {
                Statement stmt = connection.createStatement();
                stmt.execute(sqlTabelaUsers);  
                stmt.execute(sqlTabelaMessages);
                System.out.println("Tabelas prontas");
            } catch (SQLException e) {
                System.out.println("Erro ao criar tabelas: " + e.getMessage());
            }                               
    }

    public Connection getConnection() {
        return connection;
    }

}
