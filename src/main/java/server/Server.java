package server;
// Abre o servidor TCP, inicializa o banco e os serviços, e cria uma thread para cada cliente

import database.DatabaseManager;
import handler.ClientHandler;
import repository.UserRepository;
import service.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import service.PresenceService;

public class Server {

    private static final int PORTA = 8080;
    public static Map<String, ClientHandler> clientesOnline = new ConcurrentHashMap<>();

    public void iniciar() {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.conectar();

        UserRepository userRepository = new UserRepository(dbManager);
        AuthService authService = new AuthService(userRepository);
        PresenceService presenceService = new PresenceService(userRepository);
        
        System.out.println("Servidor iniciado na porta " + PORTA);

        try {
            ServerSocket serverSocket = new ServerSocket(PORTA);
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                ClientHandler handler = new ClientHandler(socketCliente, authService, presenceService);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}