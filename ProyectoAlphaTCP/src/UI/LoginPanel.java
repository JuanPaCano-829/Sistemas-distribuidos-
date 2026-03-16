package UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

public class LoginPanel extends JPanel {

    private final JTextField nameField; // campo para escribir el nombre
    private final GameWindow window; // referencia a la ventana principal

    public LoginPanel(GameWindow window) {
        this.window = window; // guarda la referencia de la ventana

        setLayout(new GridLayout(4, 1, 10, 10)); // usa un layout simple en columna
        setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100)); // agrega márgenes internos

        JLabel title = new JLabel("Escribe tu nombre aqui", JLabel.CENTER); // crea el título del panel
        nameField = new JTextField(); // crea el campo de texto
        JButton loginButton = new JButton("Login"); // crea el botón de entrada

        loginButton.addActionListener(e -> {
            String playerName = nameField.getText().trim(); // obtiene el texto escrito y le quita espacios
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tu nombre no puede ser vacio"); // muestra error si está vacío
                return; // detiene la ejecución si no hay nombre
            }

            window.login(playerName); // manda el login real a la ventana principal
        });

        add(title); // agrega el título al panel
        add(nameField); // agrega el campo de texto
        add(loginButton); // agrega el botón
    }

    public void clearFields() {
        nameField.setText(""); // limpia el campo de nombre
    }
}