package UI;
// Código para la clase LoginPanel
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField txtNombre;
    private GameWindow window;

    public LoginPanel(GameWindow window) {

        this.window = window;

        setLayout(new GridLayout(4,1,10,10));
        setBorder(BorderFactory.createEmptyBorder(100,100,100,100));

        JLabel titulo = new JLabel("Ingresar nombre", SwingConstants.CENTER);

        txtNombre = new JTextField();

        JButton btnEntrar = new JButton("Entrar");

        btnEntrar.addActionListener(e -> {
            if(txtNombre.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this,"Escribe tu nombre");
                return;
            }

            window.mostrarPantalla("LOBBY");
        });

        add(titulo);
        add(txtNombre);
        add(btnEntrar);
    }

    public String getNombreJugador(){
        return txtNombre.getText();
    }
}