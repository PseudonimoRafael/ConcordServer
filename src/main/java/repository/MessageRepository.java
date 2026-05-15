package repository;

import database.DatabaseManager;
import models.Message;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageRepository {
    private DatabaseManager dbmanager;

    public MessageRepository(DatabaseManager dbmanager) {
        this.dbmanager = dbmanager;
    }

    public void salvarOffline(Message msg) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, time, date, need_to_keep) VALUES (?, ?, ?, time('now'), date('now'), 1)";
        try {
            PreparedStatement stmt = dbmanager.getConnection().prepareStatement(sql);
            stmt.setString(1, msg.getSender());
            stmt.setString(2, msg.getReceiver());
            stmt.setString(3, msg.getContent());
            stmt.executeUpdate();
            System.out.println("Mensagem offline salva para: " + msg.getReceiver());
        } catch (SQLException e) {
            System.out.println("Erro ao salvar mensagem offline: " + e.getMessage());
        }
    }
}