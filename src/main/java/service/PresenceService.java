package service;
// Gerencia o status online/offline dos usuários, notifica clientes e entrega mensagens offline

import java.util.ArrayList;
import java.util.List;

import handler.ClientHandler;
import models.Message;
import protocol.Packet;
import protocol.PacketType;
import repository.MessageRepository;
import repository.UserRepository;
import server.Server;

public class PresenceService {

    private UserRepository userRepository;

    public PresenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void usuarioConectou(String nickname, ClientHandler handler, MessageRepository messageRepository) {
        notificarTodos(nickname, "online", handler);
        entregarMensagensOffline(nickname, handler, messageRepository);
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

    private void entregarMensagensOffline(String nickname, ClientHandler handler, MessageRepository messageRepository) {
        List<Message> pendentes = messageRepository.buscarPendentes(nickname);
        if (pendentes.isEmpty()) return;

        for (Message msg : pendentes) {
            Packet pacote = new Packet(PacketType.MESSAGE);
            pacote.setSender(msg.getSender());
            pacote.setReceiver(msg.getReceiver());
            pacote.setContent(msg.getContent());
            handler.enviar(pacote);
        }

        messageRepository.deletarEntregues(nickname);
        System.out.println(pendentes.size() + " mensagens offline entregues para: " + nickname);
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