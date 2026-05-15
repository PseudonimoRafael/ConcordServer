package handler;
// Atende cada cliente em uma thread separada, processa pacotes de login, registro e logout em JSON

import com.google.gson.Gson;
import models.User;
import protocol.Packet;
import protocol.PacketType;
import server.Server;
import service.AuthService;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;
    private String nickNameCliente;
    private Gson gson = new Gson();
    private AuthService authService;

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
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
                processarPacote(pacote);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + nickNameCliente);
        } finally {
            logout();
        }
    }

    private void processarPacote(Packet pacote) {
        switch (pacote.getType()) {
            case REGISTER:
                processarRegistro(pacote);
                break;
            case LOGIN:
                processarLogin(pacote);
                break;
            case LOGOUT:
                logout();
                break;
            default:
                System.out.println("Pacote desconhecido: " + pacote.getType());
        }
    }

    private void processarRegistro(Packet pacote) {
        User novoUser = new User(pacote.getSender(), pacote.getSender(), "", pacote.getContent());
        boolean sucesso = authService.registrar(novoUser);
        if (sucesso) {
            enviar(new Packet(PacketType.REGISTER_OK));
        } else {
            enviar(new Packet(PacketType.REGISTER_FAIL));
        }
    }

    private void processarLogin(Packet pacote) {
        User user = authService.autenticar(pacote.getSender(), pacote.getContent());
        if (user != null) {
            nickNameCliente = pacote.getSender();
            Server.clientesOnline.put(nickNameCliente, this);
            System.out.println(nickNameCliente + " está online.");
            enviar(new Packet(PacketType.LOGIN_OK));
        } else {
            enviar(new Packet(PacketType.LOGIN_FAIL));
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