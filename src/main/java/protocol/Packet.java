
//Essa classe vai fazer a conversão do pacote para bytes


import java.io.Serializable;

public class Packet implements Serializable{
    //manter a versão de serialização do cliente e srevidor igual
    private static final long serialVersionUID = 1;
    
    
    private PacketType type;
    private String sender;
    private String receiver;
    private String content;
    private long timestamp;

    // type = tipo de operação do pacote, sender = remetente, receiver = destinatario
    //content = texto enviado, timestamp = data

    public Packet(PacketType type){
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

