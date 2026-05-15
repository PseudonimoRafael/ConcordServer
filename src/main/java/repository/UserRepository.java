package repository;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import models.User;
public class UserRepository {
    private DatabaseManager dbmanager;
    
    public UserRepository(DatabaseManager dbmanager){
        this.dbmanager = dbmanager;
    }

        public boolean salvar(User user){
            String sql = "INSERT INTO users(nickname, name, email, password) VALUES (?,?,?,?)";
            try {
                            PreparedStatement stmt  = dbmanager.getConnection().prepareStatement(sql);
                            stmt.setString(1,user.getNickname());
                            stmt.setString(2,user.getName());
                            stmt.setString(3,user.getEmail());
                            stmt.setString(4,user.getPassword());
                            stmt.executeUpdate();

                            return true;
            } catch (SQLException e) {
                System.out.println("erro ao salvar usuairio: " + e.getMessage());
                return false;
            }

        }
         public User buscarPorNickname(String nickname){
            String sql="SELECT * FROM users WHERE nickname = ?";
            try {
                PreparedStatement stmt  = dbmanager.getConnection().prepareStatement(sql);
                stmt.setString(1, nickname);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    String nome = rs.getString("name");
                    String email = rs.getString("email");
                    String senha = rs.getString("password");
                    String lastSeen = rs.getString("last_seen");
                    User user = new User (nickname, nome, email, senha);
                    user.setLastSeen(lastSeen);
                    return user;
                }
                return null;
            } catch (SQLException e) {
                System.out.println("erro ao buscar usuario:" + e.getMessage());
                return null;
            }
        }
        public List<String> buscarTodos() {
            List<String> nicknames = new ArrayList<>();
            String sql = "SELECT nickname FROM users";
            try {
                PreparedStatement stmt = dbmanager.getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    nicknames.add(rs.getString("nickname"));
                }
                } catch (SQLException e) {
                    System.out.println("Erro ao buscar contatos: " + e.getMessage());
                }
                return nicknames;
        }   
}