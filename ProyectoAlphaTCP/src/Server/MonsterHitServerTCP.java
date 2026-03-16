package Server;

import java.net.*;
import java.io.*;

// Heredamos de Thread para que escuche en el fondo sin congelar tu ventana
public class MonsterHitServerTCP extends Thread {
    public static void main(String args[]) {
        try {
            int serverPort = 49152;
            // 1. EL SOCKET "ESCUCHA" (ServerSocket)
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("Esperando al golpe...");
                // 2. ACEPTAR LA CONEXIÓN (Bloqueante)
                Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                ConnectionHandler ch = new ConnectionHandler(clientSocket);
                ch.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

}

    class ConnectionHandler extends Thread {
        private DataInputStream in;
        private DataOutputStream out;
        private Socket clientSocket;

        // CONSTRUCTOR: Prepara las herramientas
        public ConnectionHandler(Socket aClientSocket) {
            try {
                clientSocket = aClientSocket;
                // Configura los "tubos" de comunicación para este cliente específico
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Connection:" + e.getMessage());
            }
        }
    }

    class UpdateGameState extends Thread {

    }

    class ConnectActiveMQ extends Thread {

    }
