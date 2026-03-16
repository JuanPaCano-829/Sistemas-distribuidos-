package Game;

import UI.GameWindow;
import javax.swing.SwingUtilities;

// AQUÍ IMPLEMENTAMOS UN REGISTRO LOG IN COMO RESPUESTA AL REGISTRO DEL TCP
public class Main {

    public static void main(String[] args) {

        System.out.println("Iniciando el Nodo del Juego...");

        SwingUtilities.invokeLater(() -> {
            GameWindow ventana = new GameWindow();
            ventana.setVisible(true);

        });
    }
}