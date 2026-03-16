package Game;

import UI.GameWindow;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting game client...");

        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}