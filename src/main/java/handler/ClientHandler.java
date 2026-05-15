package handler;
// Atende cada cliente em uma thread separada, lendo e enviando pacotes em formato JSON

import com.google.gson.Gson;
import protocol.Packet;
import protocol.PacketType;
import server.Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;
    private String nickNameCliente;
    private Gson gson = new Gson();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void logout() {
        if (nickNameCliente != null) {
            Server.clientesOnline.remove(nickNameCliente);
            System.out.println(nickNameCliente + " saiu do servidor.");
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("erro ao fechar socket" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            this.saida = new PrintWriter(socket.getOutputStream(), true);
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String json;
            while ((json = entrada.readLine()) != null) {
                Packet pacote = gson.fromJson(json, Packet.class);
                System.out.println("Pacote recebido: " + pacote.getType());

                if (pacote.getType() == PacketType.LOGOUT) {
                    logout();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + nickNameCliente);
        } finally {
            logout();
        }
    }

    public void enviar(Packet pacote) {
        String json = gson.toJson(pacote);
        saida.println(json);
    }

    public String getNickNameCliente() {
        return nickNameCliente;
    }

    public void setNickNameCliente(String nickNameCliente) {
        this.nickNameCliente = nickNameCliente;
    }
}