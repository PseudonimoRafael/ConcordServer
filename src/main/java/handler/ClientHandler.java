package handler;
//Essa classe vai ser usada para processos com relação a atender e lidar com os clientes


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protocol.Packet;

public class ClientHandler implements Runnable{
        private Socket socket;
            private ObjectOutputStream saida;
            private ObjectInputStream entrada;
            private String nickNameCliente;
            
            public ClientHandler(Socket socket){
                this.socket = socket;
                
            }

            @Override
            public void run(){
                try {
                    this.saida = new ObjectOutputStream(socket.getOutputStream());
                    this.entrada = new ObjectInputStream(socket.getInputStream());
                    Packet pacote;
                    while ((pacote = (Packet) entrada.readObject()) != null) {
                        System.out.println("Pacote recebido: " + pacote.getType());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Cliente desconectado: " + nickNameCliente);
                }
            }
}
