package UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

public class LobbyPanel extends JPanel {

    private final JTextArea playersArea; // área donde se muestran los jugadores
    private final JButton startButton; // botón para iniciar la partida
    private final GameWindow window; // referencia a la ventana principal

    public LobbyPanel(GameWindow window) {
        this.window = window; // guarda la referencia a la ventana

        setLayout(new BorderLayout()); // usa distribución por regiones
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // agrega márgenes internos

        JLabel title = new JLabel("Lobby - Connected players", JLabel.CENTER); // crea el título del lobby
        playersArea = new JTextArea(); // crea el área de texto para la lista
        playersArea.setEditable(false); // evita que el usuario escriba en la lista

        startButton = new JButton("Start Game"); // crea el botón real para iniciar la partida
        startButton.addActionListener(e -> window.startGameFromLobby()); // delega el clic a la ventana principal

        add(title, BorderLayout.NORTH); // agrega el título arriba
        add(new JScrollPane(playersArea), BorderLayout.CENTER); // agrega la lista al centro
        add(startButton, BorderLayout.SOUTH); // agrega el botón abajo
    }

    public void updatePlayers(String text) {
        playersArea.setText(text); // reemplaza el texto del lobby con la nueva lista
    }

    public void setStartButtonEnabled(boolean enabled) {
        startButton.setEnabled(enabled); // habilita o deshabilita el botón según el estado del juego
    }
}