package UI;
// Código para la clase LobbyPanel
import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {

    private JTextArea areaJugadores;
    private GameWindow window;

    public LobbyPanel(GameWindow window){

        this.window = window;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titulo = new JLabel("Lobby - Jugadores conectados", SwingConstants.CENTER);

        areaJugadores = new JTextArea();
        areaJugadores.setEditable(false);

        JButton btnIniciar = new JButton("Iniciar partida");

        btnIniciar.addActionListener(e -> {
            window.mostrarPantalla("GAME");
        });

        add(titulo, BorderLayout.NORTH);
        add(new JScrollPane(areaJugadores), BorderLayout.CENTER);
        add(btnIniciar, BorderLayout.SOUTH);
    }

    public void actualizarJugadores(String texto){
        areaJugadores.setText(texto);
    }
}