package UI;
// Código para la clase GameWindow
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;
    private LoginPanel loginPanel;
    private LobbyPanel lobbyPanel;
    private GamePanel gamePanel;
    private String nombreJugadorActual; // guarda el nombre del jugador actual

    public GameWindow() {
        setTitle("Pegarle al Topo");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nombreJugadorActual = ""; // al inicio no hay jugador registrado

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

    public void iniciarSesion(String nombreJugador) { // este método se llama cuando el jugador inicia sesión
        nombreJugadorActual = nombreJugador; // guarda el nombre del jugador
        lobbyPanel.actualizarJugadores("Jugadores conectados:\n- " + nombreJugadorActual); // actualiza el texto del lobby con el nombre del jugador
        mostrarPantalla("LOBBY");// cambia a la pantalla del lobby
    }

    public void procesarClickEnCasilla(int indice) {

        System.out.println("Click en casilla: " + indice);
        int topoActual = gamePanel.getTopoVisible(); // obtener la posición actual del topo
        if(indice == topoActual){
            System.out.println("Intento de golpe correcto");
        } else {
            System.out.println("Casilla incorrecta");
        }
    }

    public String getNombreJugadorActual() {
        return nombreJugadorActual;
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