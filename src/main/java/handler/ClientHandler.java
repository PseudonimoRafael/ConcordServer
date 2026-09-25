package handler;
// Atende cada cliente em uma thread separada, processa pacotes de login, registro e logout em JSON
import security.DHAbstraction;
import security.DHFirstRequest;

import com.google.gson.Gson;
import models.Message;
import models.User;
import protocol.Packet;
import protocol.PacketType;
import repository.MessageRepository;
import server.Server;
import service.AuthService;
import service.PresenceService;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter saida;
    private BufferedReader entrada;
    private String nickNameCliente;
    private Gson gson = new Gson();
    private DHAbstraction DH_key;
    
    private AuthService authService;
    private PresenceService presenceService;
    private MessageRepository messageRepository;
    public DHFirstRequest dhreq;
    public boolean keyRenoval;
    

    public ClientHandler(Socket socket, AuthService authService, PresenceService presenceService, MessageRepository messageRepository) {
        this.socket = socket;
        this.authService = authService;
        this.presenceService = presenceService;
        this.messageRepository = messageRepository;

        // Parte de criptografia
        keyRenoval = true;
        try {
            DH_key = new DHAbstraction();
            dhreq = new DHFirstRequest(DH_key);
        }
        catch (Exception e) {
            }
    }
    public void keyNegociation(){
        if (keyRenoval) {
            try {
                dhreq.makeRequest();
            keyRenoval = false;
            } catch (Exception e) {
            // TODO
            }
        }
    }

    public void logout() {
        if (nickNameCliente != null) {
            Server.clientesOnline.remove(nickNameCliente);
            presenceService.usuarioDesconectou(nickNameCliente);
            System.out.println(nickNameCliente + " saiu do servidor.");
        }
        try { socket.close(); } catch (IOException e) {}
    }
    @Override
    public void run() {
        try {
            this.saida = new PrintWriter(socket.getOutputStream(), true);
            this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String json;
            while ((json = entrada.readLine()) != null) {
                keyNegociation();
                Packet pacote = gson.fromJson(json, Packet.class);
                processarPacote(pacote);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado ou erro: " + nickNameCliente);
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
            case DH_SER_INIT: InicioDHServer(pacote); break;
            case DH_SER_RESPONSE: RespostaDHServer(pacote); break;
            default: break;
        }
    }

    private void processarMensagem(Packet pacote) {
        String destinatario = pacote.getReceiver();
        if (Server.clientesOnline.containsKey(destinatario)) {
            // Roteamento em Tempo Real
            ClientHandler handlerDestino = Server.clientesOnline.get(destinatario);
            handlerDestino.enviar(pacote);
        } else {
            // Salvar Offline
            Message msgOffline = new Message(pacote.getSender(), destinatario, pacote.getContent());
            messageRepository.salvarOffline(msgOffline);
        }
    }

    // ... (Mantenha seus métodos processarRegistro e processarLogin EXATAMENTE como estavam)
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
            presenceService.usuarioConectou(nickNameCliente, this);
        } else {
            enviar(new Packet(PacketType.LOGIN_FAIL));
        }
    }

    private void InicioDHServer(Packet pacote) {
    }
    
    private void RespostaDHServer(Packet pacote) {
    }
    public void enviar(Packet pacote) {
        String json = gson.toJson(pacote);
        saida.println(json);
        System.out.println(json); // Para observar os pacotes que são criados
    }
    

    public DHAbstraction getDHChannel() {
          return DH_key;
    }

    public void setDHChannel(DHAbstraction SecureChannel) {
        this.DH_key = DH_key;
    }
}
