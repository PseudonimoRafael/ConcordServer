package service;
// Gerencia o status online/offline dos usuários e notifica os clientes conectados

import com.google.gson.Gson;
import handler.ClientHandler;
import protocol.Packet;
import protocol.PacketType;
import repository.UserRepository;
import server.Server;

import java.util.ArrayList;
import java.util.List;

public class PresenceService {

    private UserRepository userRepository;
    private Gson gson = new Gson();

    public PresenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void usuarioConectou(String nickname, ClientHandler handler) {
        notificarTodos(nickname, "online", handler);
        enviarListaContatos(handler);
    }

    public void usuarioDesconectou(String nickname) {
        notificarTodos(nickname, "offline", null);
    }

    private void notificarTodos(String nickname, String status, ClientHandler excluir) {
        Packet pacote = new Packet(PacketType.STATUS_UPDATE);
        pacote.setSender(nickname);
        pacote.setContent(status);

        for (ClientHandler handler : Server.clientesOnline.values()) {
            if (handler != excluir) {
                handler.enviar(pacote);
            }
        }
    }

    private void enviarListaContatos(ClientHandler handler) {
        List<String> contatos = new ArrayList<>();

        for (String nickname : userRepository.buscarTodos()) {
            String status = Server.clientesOnline.containsKey(nickname) ? "online" : "offline";
            contatos.add(nickname + ":" + status);
        }

        Packet pacote = new Packet(PacketType.CONTACT_LIST);
        pacote.setMessageList(contatos);
        handler.enviar(pacote);
    }
}