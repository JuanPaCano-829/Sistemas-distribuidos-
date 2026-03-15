package Game;

import UI.GameWindow;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        System.out.println("Iniciando el Nodo del Juego...");

        SwingUtilities.invokeLater(() -> {
            GameWindow ventana = new GameWindow();
            ventana.setVisible(true);

            // ¡Borramos la línea de agregarNotificacion que estaba aquí!
        });
    }
}