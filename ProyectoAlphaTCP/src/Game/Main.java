package Game;

import UI.GameWindow;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting game client..."); // mensaje de arranque del cliente

        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow(); // crea la ventana principal
            window.setVisible(true); // muestra la interfaz
        });
    }
}