package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonsterHitClientTCP {

    private final String serverIp;
    private final int serverPort;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;

    public MonsterHitClientTCP(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    private void connectIfNeeded() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(serverIp, serverPort);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
        }
    }

    public String sendLogin(String playerName) {
        return sendMessage("LOGIN|" + playerName);
    }

    public String sendHit(String playerName, int position) {
        return sendMessage("HIT|" + playerName + "|" + position);
    }

    public String sendStartGame(String playerName) {
        return sendMessage("START_GAME|" + playerName);
    }


    public void sendDisconnect(String playerName) {
        if (playerName == null || playerName.isBlank()) return;

        sendMessage("DISCONNECT|" + playerName);
        closeConnection();
    }



    private String sendMessage(String message) {
        try {
            connectIfNeeded();
            output.writeUTF(message);

            System.out.println("TCP sent: " + message);
            String response = input.readUTF();
            System.out.println("TCP received: " + response);

            return response;

        } catch (IOException e) {
            System.out.println("TCP connection error: " + e.getMessage());
            closeConnection();
            return "ERROR|Could not connect to server";
        }
    }

    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing client TCP: " + e.getMessage());
        }
    }
}