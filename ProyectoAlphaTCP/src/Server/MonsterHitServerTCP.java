package Server;

import Model.GameState;
import Model.Player;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MonsterHitServerTCP {

    private static final int SERVER_PORT = 49152; // puerto donde escuchará el servidor TCP
    private static GameState gameState; // estado global compartido
    private static MonsterPublisherActiveMQ publisher; // publicador de eventos por ActiveMQ

    public static void main(String[] args) {
        gameState = new GameState(); // crea el estado compartido del juego
        publisher = new MonsterPublisherActiveMQ(gameState); // crea el publisher con el mismo estado compartido

        Thread publisherThread = new Thread(() -> publisher.startPublishing()); // crea un hilo para el publisher
        publisherThread.start(); // inicia el hilo del publisher

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("=========================================="); // imprime separador visual
            System.out.println("MonsterHit TCP server started"); // mensaje principal del servidor
            System.out.println("Port: " + SERVER_PORT); // imprime el puerto del servidor
            System.out.println("Waiting for clients..."); // indica que el servidor está listo
            System.out.println("=========================================="); // imprime separador visual

            while (true) {
                Socket clientSocket = serverSocket.accept(); // espera una nueva conexión
                System.out.println("Client connected from: " + clientSocket.getInetAddress()); // imprime la IP del cliente

                ConnectionHandler handler = new ConnectionHandler(clientSocket, gameState); // crea un hilo por cliente
                handler.start(); // inicia el manejo del cliente
            }

        } catch (IOException e) {
            System.out.println("Server start error: " + e.getMessage()); // muestra error del servidor
        }
    }
}

class ConnectionHandler extends Thread {

    private final Socket clientSocket; // socket de este cliente
    private final DataInputStream input; // flujo para leer mensajes
    private final DataOutputStream output; // flujo para responder mensajes
    private final GameState gameState; // estado compartido del juego

    public ConnectionHandler(Socket clientSocket, GameState gameState) throws IOException {
        this.clientSocket = clientSocket; // guarda el socket del cliente
        this.gameState = gameState; // guarda el estado compartido
        this.input = new DataInputStream(clientSocket.getInputStream()); // abre flujo de entrada
        this.output = new DataOutputStream(clientSocket.getOutputStream()); // abre flujo de salida
    }

    @Override
    public void run() {
        try {
            String request = input.readUTF(); // lee el mensaje enviado por el cliente
            System.out.println("TCP request: " + request); // imprime el mensaje recibido

            String response = processRequest(request); // procesa el mensaje recibido
            output.writeUTF(response); // manda la respuesta al cliente
            output.flush(); // fuerza el envío inmediato

        } catch (IOException e) {
            System.out.println("Client handling error: " + e.getMessage()); // muestra error durante la atención del cliente
        } finally {
            closeConnection(); // cierra recursos al terminar
        }
    }

    private String processRequest(String request) {
        if (request == null || request.trim().isEmpty()) return "ERROR|Empty message"; // evita procesar mensajes vacíos

        String[] parts = request.split("\\|"); // separa el mensaje por el símbolo |
        String command = getCommand(parts); // obtiene el comando principal

        switch (command) {
            case "LOGIN":
                return processLogin(parts); // procesa el login

            case "HIT":
                return processHit(parts); // procesa el golpe

            case "START_GAME":
                return processStartGame(parts); // procesa el inicio manual de la partida

            case "DISCONNECT":
                return processDisconnect(parts); // procesa la desconexión

            default:
                return "ERROR|Unknown command"; // responde error si el comando no existe
        }
    }

    private String getCommand(String[] parts) {
        if (parts.length == 0 || parts[0] == null) return ""; // evita errores si el formato viene mal
        return parts[0].trim().toUpperCase(); // normaliza el comando
    }

    private String processLogin(String[] parts) {
        if (parts.length < 2) return "ERROR|Invalid LOGIN format"; // valida estructura mínima

        String playerName = parts[1].trim(); // obtiene el nombre del jugador
        if (playerName.isEmpty()) return "ERROR|Empty player name"; // evita nombres vacíos

        Player player = gameState.addOrReconnectPlayer(playerName); // registra o reconecta al jugador
        if (player == null) return "ERROR|Could not register player"; // valida que sí se haya creado o reconectado

        System.out.println("Player logged in: " + playerName); // imprime el login exitoso
        return "LOGIN_OK|" + player.getName() + "|" + player.getScore(); // responde login correcto y score actual
    }

    private String processHit(String[] parts) {
        if (parts.length < 3) return "ERROR|Invalid HIT format"; // valida estructura mínima

        String playerName = parts[1].trim(); // obtiene el nombre del jugador
        int hitPosition; // guarda la posición golpeada

        try {
            hitPosition = Integer.parseInt(parts[2].trim()); // convierte la posición a entero
        } catch (NumberFormatException e) {
            return "ERROR|Invalid position"; // responde error si la posición no es numérica
        }

        boolean validHit = gameState.processHit(playerName, hitPosition); // procesa el golpe en el estado compartido

        if (!validHit) {
            System.out.println("Invalid hit from " + playerName + " at " + hitPosition); // imprime golpe fallido
            return "MISS|" + playerName; // responde que el golpe falló
        }

        int score = gameState.getPlayerScore(playerName); // consulta el score actualizado del jugador
        System.out.println("Valid hit from " + playerName + " at " + hitPosition); // imprime golpe válido

        if (gameState.hasWinner()) {
            return "HIT_OK|WINNER|" + gameState.getWinner() + "|" + score; // responde que hubo golpe válido y ganador
        }

        return "HIT_OK|" + playerName + "|" + score; // responde golpe correcto con score actualizado
    }

    private String processStartGame(String[] parts) {
        if (parts.length < 2) return "ERROR|Invalid START_GAME format"; // valida estructura mínima

        String playerName = parts[1].trim(); // obtiene el nombre del jugador que quiere iniciar
        Player player = gameState.getPlayer(playerName); // busca al jugador en el estado compartido

        if (player == null || !player.isConnected()) return "ERROR|Player not connected"; // valida que el jugador exista y esté conectado
        if (gameState.isGameStarted()) return "GAME_ALREADY_STARTED"; // evita iniciar dos veces la misma partida

        gameState.startGame(); // marca la partida como iniciada
        System.out.println("Game started by: " + playerName); // imprime quién inició la partida

        return "START_OK|" + playerName; // confirma que la partida fue iniciada
    }

    private String processDisconnect(String[] parts) {
        if (parts.length < 2) return "ERROR|Invalid DISCONNECT format"; // valida estructura mínima

        String playerName = parts[1].trim(); // obtiene el nombre del jugador
        Player player = gameState.getPlayer(playerName); // busca al jugador en el estado compartido

        if (player != null) player.setConnected(false); // si existe lo marca como desconectado

        System.out.println("Player disconnected: " + playerName); // imprime desconexión
        return "BYE|" + playerName; // responde despedida
    }

    private void closeConnection() {
        try {
            input.close(); // cierra el flujo de entrada
            output.close(); // cierra el flujo de salida
            clientSocket.close(); // cierra el socket del cliente
        } catch (IOException e) {
            System.out.println("Connection close error: " + e.getMessage()); // muestra error al cerrar recursos
        }
    }
}