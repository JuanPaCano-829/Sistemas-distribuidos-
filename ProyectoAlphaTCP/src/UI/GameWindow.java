package UI;

import Client.MonsterHitClientTCP;
import Client.MonsterSubscriberActiveMQ;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class GameWindow extends JFrame {

    private static final String SERVER_IP = "localhost"; 
    private static final int SERVER_PORT = 49152; 

    private final CardLayout cardLayout; 
    private final JPanel container; 
    private final LoginPanel loginPanel; 
    private final LobbyPanel lobbyPanel; 
    private final GamePanel gamePanel; 
    private final MonsterHitClientTCP tcpClient; 
    private final MonsterSubscriberActiveMQ subscriber; 
    private boolean subscriberStarted; 
    private String currentPlayerName;

    public GameWindow() {
        setTitle("AlphaTCP Golpea al topo"); 
        setSize(500, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 

        currentPlayerName = ""; 
        subscriberStarted = false; 

        cardLayout = new CardLayout(); 
        container = new JPanel(cardLayout); 

        tcpClient = new MonsterHitClientTCP(SERVER_IP, SERVER_PORT); 
        subscriber = new MonsterSubscriberActiveMQ(this); 

        loginPanel = new LoginPanel(this); 
        lobbyPanel = new LobbyPanel(this); 
        gamePanel = new GamePanel(this); 

        container.add(loginPanel, "LOGIN"); 
        container.add(lobbyPanel, "LOBBY"); 
        container.add(gamePanel, "GAME"); 

        add(container); 
        showScreen("LOGIN"); 
    }

    public void showScreen(String screenName) {
        cardLayout.show(container, screenName); 
    }

    public void login(String playerName) {
        String response = tcpClient.sendLogin(playerName); 

        if (response.startsWith("LOGIN_OK|")) {
            String[] parts = response.split("\\|"); 
            currentPlayerName = parts[1]; 

            int score = 0; 
            if (parts.length >= 3) score = Integer.parseInt(parts[2]); 

            if (!subscriberStarted) {
                subscriber.startListening(); 
                subscriberStarted = true; 
            }

             
            lobbyPanel.updatePlayers("Waiting for player list..."); 
            lobbyPanel.setStartButtonEnabled(true); 
            showScreen("LOBBY"); 
            return; 
        }

        showError(response); 
    }

    public void requestGameStart() {
        String response = tcpClient.sendStartGame(currentPlayerName);

        if (response.startsWith("ERROR|")) {
            showError(response);
        }
    }

    public void processCellClick(int index) {
        String response = tcpClient.sendHit(currentPlayerName, index);

        if (response.startsWith("HIT_OK|WINNER|")) {
            String[] parts = response.split("\\|"); 
            if (parts.length >= 4) {
                int score = Integer.parseInt(parts[3]); 
                 
            }
            return; 
        }

        if (response.startsWith("HIT_OK|")) {
            String[] parts = response.split("\\|"); 
            if (parts.length >= 3) {
                int score = Integer.parseInt(parts[2]); 
                 
            }
            return; 
        }

        if (response.startsWith("MISS|")) {
            System.out.println("Miss at position: " + index); 
            return; 
        }

        if (response.startsWith("ERROR|")) {
            showError(response); 
        }
    }

    public void disconnectAndReturnToLogin() {
        tcpClient.sendDisconnect(currentPlayerName);

        subscriber.stopListening();
        subscriberStarted = false;

        currentPlayerName = "";
        gamePanel.resetBoard();
        lobbyPanel.updatePlayers("");
        lobbyPanel.setStartButtonEnabled(true);
        loginPanel.clearFields();
        showScreen("LOGIN");
    }

    public void handleMonsterPosition(int position) {

        showScreen("GAME"); 
        gamePanel.showMonster(position); 
    }

    public void handleSystemMessage(String message) {

        if (message.equals("GAME_START")) {
            gamePanel.resetBoard();
            showScreen("GAME"); 
            return; 
        }

        if (message.startsWith("WINNER:")) {
            String winnerName = message.substring("WINNER:".length()).trim(); 
            JOptionPane.showMessageDialog(this, "Winner: " + winnerName);
            showScreen("LOBBY"); 
            return; 
        }

        if (message.startsWith("PLAYERS:")) {
            String players = message.substring("PLAYERS:".length()).trim();
            String formatted = formatPlayersText(players);
            lobbyPanel.updatePlayers("Connected players:\n" + formatted);
            gamePanel.updateAllScores(formatted);
        }
    }

    private String formatPlayersText(String players) {
        if (players.isBlank()) return "- No connected players";

        String[] entries = players.split(",");
        StringBuilder text = new StringBuilder();

        for (String entry : entries) {
            if (!entry.trim().isEmpty()) {
                String[] parts = entry.split(":");
                String name = parts[0];
                String score = parts.length > 1 ? parts[1] : "0";
                text.append("- ").append(name).append(": ").append(score).append("\n");
            }
        }
        return text.toString();
    }

    private void showError(String response) {
        String message = response; 

        if (response != null && response.startsWith("ERROR|")) {
            String[] parts = response.split("\\|", 2); 
            if (parts.length == 2) message = parts[1]; 
        }

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); 
    }
}