package Game;

import UI.GameWindow;
import javax.swing.SwingUtilities;

public class Main {

    //Inicia la ejecución y despliega la interfaz gráfica principal.
    public static void main(String[] args) {
        System.out.println("Starting game client...");
        //SwingUtilities.invokeLater coloca la tarea de creación de la interfaz en una cola de eventos
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}