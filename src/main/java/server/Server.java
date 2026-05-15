package server;
//Essa classe vai abrir o servidor e escutar

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import handler.ClientHandler;


public class Server {
    private static final int PORTA = 8080;

    public static Map<String, ClientHandler> clientesOnline = new ConcurrentHashMap<>();
    public void iniciar(){
        System.out.println("servidor aberto na porta: " + PORTA);
        try{
            ServerSocket serverSocket = new ServerSocket(PORTA);
            while (true) { 
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                ClientHandler handler = new ClientHandler(socketCliente);
                new Thread(handler).start();
            }
        }
        catch (IOException e){
            System.out.println("erro ao abrir servidor: "+ e.getMessage());
        }
    }
}
