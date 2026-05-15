package repository;
// Responsável por salvar, buscar e deletar mensagens offline no banco de dados

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import models.Message;

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

    public List<Message> buscarPendentes(String nickname) {
        List<Message> mensagens = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND need_to_keep = 1";
        try {
            PreparedStatement stmt = dbmanager.getConnection().prepareStatement(sql);
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                    rs.getString("sender_id"),
                    rs.getString("receiver_id"),
                    rs.getString("content")
                );
                msg.setId(rs.getInt("message_id"));
                mensagens.add(msg);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar mensagens pendentes: " + e.getMessage());
        }
        return mensagens;
    }

    public void deletarEntregues(String nickname) {
        String sql = "DELETE FROM messages WHERE receiver_id = ? AND need_to_keep = 1";
        try {
            PreparedStatement stmt = dbmanager.getConnection().prepareStatement(sql);
            stmt.setString(1, nickname);
            stmt.executeUpdate();
            System.out.println("Mensagens entregues removidas para: " + nickname);
        } catch (SQLException e) {
            System.out.println("Erro ao deletar mensagens entregues: " + e.getMessage());
        }
    }
}