package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonsterHitClientTCP {

    private final String serverIp; // guarda la IP del servidor
    private final int serverPort; // guarda el puerto del servidor

    public MonsterHitClientTCP(String serverIp, int serverPort) {
        this.serverIp = serverIp; // asigna la IP recibida
        this.serverPort = serverPort; // asigna el puerto recibido
    }

    public String sendLogin(String playerName) {
        String message = "LOGIN|" + playerName; // construye el mensaje de login
        return sendMessage(message); // envía el mensaje al servidor
    }

    public String sendHit(String playerName, int position) {
        String message = "HIT|" + playerName + "|" + position; // construye el mensaje de golpe
        return sendMessage(message); // envía el mensaje al servidor
    }

    public String sendStartGame(String playerName) {
        String message = "START_GAME|" + playerName; // construye el mensaje para iniciar la partida
        return sendMessage(message); // envía el mensaje al servidor
    }

    public String sendDisconnect(String playerName) {
        String message = "DISCONNECT|" + playerName; // construye el mensaje de desconexión
        return sendMessage(message); // envía el mensaje al servidor
    }

    private String sendMessage(String message) {
        try (
                Socket socket = new Socket(serverIp, serverPort); // abre una conexión TCP temporal
                DataOutputStream output = new DataOutputStream(socket.getOutputStream()); // flujo para enviar datos
                DataInputStream input = new DataInputStream(socket.getInputStream()) // flujo para leer respuesta
        ) {
            output.writeUTF(message); // manda el mensaje al servidor
            output.flush(); // fuerza el envío inmediato

            System.out.println("TCP sent: " + message); // imprime el mensaje enviado
            String response = input.readUTF(); // espera la respuesta del servidor
            System.out.println("TCP received: " + response); // imprime la respuesta recibida

            return response; // regresa la respuesta al programa

        } catch (IOException e) {
            System.out.println("TCP connection error: " + e.getMessage()); // muestra error de conexión
            return "ERROR|Could not connect to server"; // regresa un error estándar
        }
    }
}