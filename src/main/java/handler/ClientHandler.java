package handler;
// Atende cada cliente em uma thread separada, processa pacotes de login, registro, mensagens e logout em JSON

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import models.Message;
import models.User;
import protocol.Packet;
import protocol.PacketType;
import repository.MessageRepository;
import server.Server;
import service.AuthService;
import service.PresenceService;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;
    private String nickNameCliente;
    private Gson gson = new Gson();
    private AuthService authService;
    private PresenceService presenceService;
    private MessageRepository messageRepository;

    public ClientHandler(Socket socket, AuthService authService, PresenceService presenceService, MessageRepository messageRepository) {
        this.socket = socket;
        this.authService = authService;
        this.presenceService = presenceService;
        this.messageRepository = messageRepository;
    }

    public void logout() {
        if (nickNameCliente != null) {
            Server.clientesOnline.remove(nickNameCliente);
            presenceService.usuarioDesconectou(nickNameCliente);
            System.out.println(nickNameCliente + " saiu do servidor.");
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar socket: " + e.getMessage());
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
            case REGISTER: processarRegistro(pacote); break;
            case LOGIN: processarLogin(pacote); break;
            case LOGOUT: logout(); break;
            case MESSAGE: processarMensagem(pacote); break;
            case TYPING_START: encaminharDigitando(pacote); break;
            case TYPING_STOP: encaminharDigitando(pacote); break;
            default: break;
        }
    }

    private void processarRegistro(Packet pacote) {
        User novoUser = new User(pacote.getSender(), pacote.getSender(), "", pacote.getContent());
        if (authService.registrar(novoUser)) {
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
            enviar(new Packet(PacketType.LOGIN_OK));
            presenceService.usuarioConectou(nickNameCliente, this, messageRepository);
        } else {
            enviar(new Packet(PacketType.LOGIN_FAIL));
        }
    }

    private void processarMensagem(Packet pacote) {
        String destinatario = pacote.getReceiver();
        if (Server.clientesOnline.containsKey(destinatario)) {
            ClientHandler handlerDestino = Server.clientesOnline.get(destinatario);
            handlerDestino.enviar(pacote);
        } else {
            Message msgOffline = new Message(pacote.getSender(), destinatario, pacote.getContent());
            messageRepository.salvarOffline(msgOffline);
        }
    }

    private void encaminharDigitando(Packet pacote) {
        String destinatario = pacote.getReceiver();
        if (Server.clientesOnline.containsKey(destinatario)) {
            ClientHandler handlerDestino = Server.clientesOnline.get(destinatario);
            handlerDestino.enviar(pacote);
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