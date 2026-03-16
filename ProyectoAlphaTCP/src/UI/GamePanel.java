package UI;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

public class GamePanel extends JPanel {

    private final JButton[] buttons; // arreglo de botones del tablero
    private final JTextArea scoreArea;
    private final ImageIcon monsterGif; // imagen animada del monstruo
    private final GameWindow window; // referencia a la ventana principal

    public GamePanel(GameWindow window) {
        this.window = window; // guarda la referencia de la ventana

        setLayout(new BorderLayout()); // usa BorderLayout para distribuir componentes
        monsterGif = new ImageIcon(getClass().getResource("/Assets/topo_saliendo.gif")); // carga el gif desde recursos

        JPanel topPanel = new JPanel(); // panel superior para score y salida
        scoreArea = new JTextArea("Scores:\n");
        scoreArea.setEditable(false);
        scoreArea.setBackground(topPanel.getBackground());

        JButton exitButton = new JButton("Exit"); // botón de salida

        exitButton.addActionListener(e -> window.disconnectAndReturnToLogin()); // desconecta al jugador y vuelve al login

        topPanel.add(scoreArea);
        topPanel.add(exitButton); // agrega el botón de salida al panel superior

        JPanel board = new JPanel(new GridLayout(3, 3, 10, 10)); // crea el tablero 3x3
        board.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // agrega márgenes al tablero

        buttons = new JButton[9]; // crea el arreglo para los 9 botones

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton(); // crea un botón nuevo
            button.setBackground(Color.LIGHT_GRAY); // le pone color gris claro

            int index = i; // guarda el índice para usarlo dentro del lambda

            button.addActionListener(e -> window.processCellClick(index)); // delega el clic a la ventana principal

            buttons[i] = button; // guarda el botón en el arreglo
            board.add(button); // agrega el botón al tablero
        }

        add(topPanel, BorderLayout.NORTH); // agrega el panel superior
        add(board, BorderLayout.CENTER); // agrega el tablero al centro
    }

    public void showMonster(int index) {
        hideMonsters(); // limpia cualquier monstruo anterior

        if (index >= 0 && index < buttons.length) {
            buttons[index].setIcon(monsterGif); // coloca el gif en la casilla indicada
        }
    }

    public void hideMonsters() {
        for (JButton button : buttons) button.setIcon(null); // quita cualquier icono de todas las casillas
    }

    public void updateAllScores(String text) {
        scoreArea.setText("Scores:\n" + text);
    }

    public void resetBoard() {
        hideMonsters(); // limpia el tablero

    }
}