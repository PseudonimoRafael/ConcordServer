package models;

//essa classe possui o modelo de usuario com seus atributos 


public class User {
    private String nickname;
    private Sting name;
    private String email;
    private String password;
    private String lastSeen;
    private boolean online;

    
    public User(String nickname,String name, String email, String password) {
        this.nickname = nickname;
        this.name= name;
        this.password = password;
        this.email = email;
        this.lastSeen = null;
        this.online = false;    
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Sting getName() {
        return name;
    }

    public void setName(Sting name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}