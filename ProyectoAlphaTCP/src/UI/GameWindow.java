package UI;

import Client.MonsterHitClientTCP;
import Client.MonsterSubscriberActiveMQ;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class GameWindow extends JFrame {

    private static final String SERVER_IP = "localhost"; // IP del servidor TCP
    private static final int SERVER_PORT = 49152; // puerto del servidor TCP

    private final CardLayout cardLayout; // layout para cambiar entre pantallas
    private final JPanel container; // contenedor principal de pantallas
    private final LoginPanel loginPanel; // pantalla de login
    private final LobbyPanel lobbyPanel; // pantalla de lobby
    private final GamePanel gamePanel; // pantalla del juego
    private final MonsterHitClientTCP tcpClient; // cliente TCP para enviar comandos
    private final MonsterSubscriberActiveMQ subscriber; // subscriber JMS para escuchar eventos
    private boolean subscriberStarted; // evita iniciar el subscriber más de una vez
    private String currentPlayerName; // guarda el nombre del jugador actual

    public GameWindow() {
        setTitle("Whack-a-Mole Multiplayer"); // título de la ventana
        setSize(500, 600); // tamaño inicial de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // define cierre del programa
        setLocationRelativeTo(null); // centra la ventana en pantalla

        currentPlayerName = ""; // al inicio no hay jugador activo
        subscriberStarted = false; // al inicio el subscriber no está escuchando

        cardLayout = new CardLayout(); // crea el CardLayout
        container = new JPanel(cardLayout); // crea el contenedor con CardLayout

        tcpClient = new MonsterHitClientTCP(SERVER_IP, SERVER_PORT); // crea el cliente TCP
        subscriber = new MonsterSubscriberActiveMQ(this); // crea el subscriber JMS

        loginPanel = new LoginPanel(this); // crea el panel de login
        lobbyPanel = new LobbyPanel(this); // crea el panel de lobby
        gamePanel = new GamePanel(this); // crea el panel de juego

        container.add(loginPanel, "LOGIN"); // registra la pantalla de login
        container.add(lobbyPanel, "LOBBY"); // registra la pantalla de lobby
        container.add(gamePanel, "GAME"); // registra la pantalla del juego

        add(container); // agrega el contenedor a la ventana
        showScreen("LOGIN"); // muestra la pantalla inicial
    }

    public void showScreen(String screenName) {
        cardLayout.show(container, screenName); // cambia a la pantalla indicada
    }

    public void login(String playerName) {
        String response = tcpClient.sendLogin(playerName); // manda el login al servidor

        if (response.startsWith("LOGIN_OK|")) {
            String[] parts = response.split("\\|"); // separa la respuesta del servidor
            currentPlayerName = parts[1]; // guarda el nombre confirmado por el servidor

            int score = 0; // score por defecto
            if (parts.length >= 3) score = Integer.parseInt(parts[2]); // extrae el score si viene incluido

            if (!subscriberStarted) {
                subscriber.startListening(); // inicia la escucha de eventos JMS
                subscriberStarted = true; // marca que ya fue iniciado
            }

            gamePanel.updateScore(score); // actualiza el score visual
            lobbyPanel.updatePlayers("Waiting for player list..."); // deja un mensaje temporal en el lobby
            lobbyPanel.setStartButtonEnabled(true); // habilita el botón para iniciar la partida
            showScreen("LOBBY"); // cambia a la pantalla del lobby
            return; // termina si el login fue exitoso
        }

        showError(response); // muestra error si el login falló
    }

    public void startGameFromLobby() {
        if (currentPlayerName == null || currentPlayerName.isBlank()) return; // evita iniciar si no hay sesión activa

        String response = tcpClient.sendStartGame(currentPlayerName); // manda la petición para iniciar la partida

        if (response.startsWith("START_OK|")) {
            lobbyPanel.setStartButtonEnabled(false); // deshabilita el botón para evitar varios clics
            return; // espera a que el cambio real llegue por JMS con GAME_START
        }

        if (response.equals("GAME_ALREADY_STARTED")) {
            lobbyPanel.setStartButtonEnabled(false); // si ya inició, solo deshabilita el botón
            return; // evita mostrar error en este caso
        }

        if (response.startsWith("ERROR|")) {
            showError(response); // muestra error si algo falló al iniciar la partida
        }
    }

    public void processCellClick(int index) {
        if (currentPlayerName == null || currentPlayerName.isBlank()) return; // evita enviar golpes si no hay sesión activa

        String response = tcpClient.sendHit(currentPlayerName, index); // manda el golpe al servidor

        if (response.startsWith("HIT_OK|WINNER|")) {
            String[] parts = response.split("\\|"); // separa la respuesta del servidor
            if (parts.length >= 4) {
                int score = Integer.parseInt(parts[3]); // obtiene el score actualizado
                gamePanel.updateScore(score); // actualiza el score visual
            }
            return; // no hace más porque el winner oficial llegará también por JMS
        }

        if (response.startsWith("HIT_OK|")) {
            String[] parts = response.split("\\|"); // separa la respuesta del servidor
            if (parts.length >= 3) {
                int score = Integer.parseInt(parts[2]); // obtiene el score actualizado
                gamePanel.updateScore(score); // actualiza el score visual
            }
            return; // termina si fue golpe correcto
        }

        if (response.startsWith("MISS|")) {
            System.out.println("Miss at position: " + index); // imprime golpe fallido en consola
            return; // no hace más si el golpe falló
        }

        if (response.startsWith("ERROR|")) {
            showError(response); // muestra error si hubo problema con la solicitud
        }
    }

    public void disconnectAndReturnToLogin() {
        if (currentPlayerName != null && !currentPlayerName.isBlank()) tcpClient.sendDisconnect(currentPlayerName); // avisa al servidor que el jugador sale

        currentPlayerName = ""; // limpia el nombre del jugador actual
        gamePanel.resetBoard(); // limpia el tablero y score
        lobbyPanel.updatePlayers(""); // limpia la lista del lobby
        lobbyPanel.setStartButtonEnabled(true); // deja listo el botón por si se vuelve a entrar
        loginPanel.clearFields(); // limpia el campo del login
        showScreen("LOGIN"); // vuelve a la pantalla de login
    }

    public void handleMonsterPosition(int position) {
        if (currentPlayerName == null || currentPlayerName.isBlank()) return; // ignora mensajes si no hay sesión activa

        showScreen("GAME"); // asegura que el jugador vea la pantalla del juego
        gamePanel.showMonster(position); // muestra el monstruo en la posición recibida
    }

    public void handleSystemMessage(String message) {
        if (currentPlayerName == null || currentPlayerName.isBlank()) return; // ignora mensajes si no hay sesión activa

        if (message.equals("GAME_START")) {
            gamePanel.resetBoard(); // limpia tablero y score visual al iniciar nueva partida
            lobbyPanel.setStartButtonEnabled(false); // deshabilita el botón porque la partida ya comenzó
            showScreen("GAME"); // entra automáticamente al juego
            return; // termina el manejo del mensaje
        }

        if (message.startsWith("WINNER:")) {
            String winnerName = message.substring("WINNER:".length()).trim(); // extrae el nombre del ganador
            JOptionPane.showMessageDialog(this, "Winner: " + winnerName); // muestra el ganador en una ventana
            lobbyPanel.setStartButtonEnabled(true); // vuelve a habilitar el botón para la siguiente partida
            showScreen("LOBBY"); // regresa al lobby después de terminar la partida
            return; // termina el manejo del mensaje
        }

        if (message.startsWith("PLAYERS:")) {
            String players = message.substring("PLAYERS:".length()).trim(); // obtiene la cadena con jugadores
            lobbyPanel.updatePlayers(formatPlayersText(players)); // actualiza el lobby con formato legible
        }
    }

    private String formatPlayersText(String players) {
        if (players.isBlank()) return "Connected players:\n- No connected players"; // mensaje por defecto si no hay nadie conectado

        String[] names = players.split(","); // separa los nombres por coma
        StringBuilder text = new StringBuilder("Connected players:\n"); // crea el encabezado del texto

        for (String name : names) {
            if (!name.trim().isEmpty()) text.append("- ").append(name.trim()).append("\n"); // agrega cada nombre en una línea
        }

        return text.toString(); // regresa el texto final para el lobby
    }

    private void showError(String response) {
        String message = response; // toma la respuesta completa por defecto

        if (response != null && response.startsWith("ERROR|")) {
            String[] parts = response.split("\\|", 2); // divide el mensaje solo una vez
            if (parts.length == 2) message = parts[1]; // toma solo la parte del texto de error
        }

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); // muestra el error en pantalla
    }
}