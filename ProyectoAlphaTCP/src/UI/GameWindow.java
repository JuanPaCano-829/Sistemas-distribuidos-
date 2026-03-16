package UI;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    private LoginPanel loginPanel;
    private LobbyPanel lobbyPanel;
    private GamePanel gamePanel;

    public GameWindow() {

        setTitle("Pegarle al Topo");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        lobbyPanel = new LobbyPanel(this);
        gamePanel = new GamePanel(this);

        container.add(loginPanel, "LOGIN");
        container.add(lobbyPanel, "LOBBY");
        container.add(gamePanel, "GAME");

        add(container);

        mostrarPantalla("LOGIN");
    }

    public void mostrarPantalla(String nombre) {
        cardLayout.show(container, nombre);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public LobbyPanel getLobbyPanel() {
        return lobbyPanel;
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }
}