package UI;
// Código para la clase GamePanel
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private JButton[] botones;
    private JLabel lblScore;
    private int topoVisible = -1;

    private ImageIcon topoGif;

    private GameWindow window;

    public GamePanel(GameWindow window){

        this.window = window;

        setLayout(new BorderLayout());

        topoGif = new ImageIcon(getClass().getResource("/Assets/topo_saliendo.gif"));

        JPanel panelTop = new JPanel();

        lblScore = new JLabel("Score: 0");

        JButton btnVolver = new JButton("Salir");

        btnVolver.addActionListener(e -> {
            window.mostrarPantalla("LOGIN");
        });

        panelTop.add(lblScore);
        panelTop.add(btnVolver);

        JPanel tablero = new JPanel(new GridLayout(3,3,10,10));
        tablero.setBorder(BorderFactory.createEmptyBorder(30,50,30,50));

        botones = new JButton[9];

        for(int i = 0; i < 9; i++){

            JButton b = new JButton();
            b.setBackground(Color.LIGHT_GRAY);

            int indice = i;

            b.addActionListener(e -> {
                window.procesarClickEnCasilla(indice); // solo le avisamos a la ventana qué casilla se presionó
            });

            botones[i] = b;
            tablero.add(b);
        }

        add(panelTop, BorderLayout.NORTH);
        add(tablero, BorderLayout.CENTER);
    }

    public void mostrarTopo(int indice){

        ocultarTopos();

        if(indice >= 0 && indice < botones.length){
            botones[indice].setIcon(topoGif);
            topoVisible = indice;
        }
    }

    public void ocultarTopos(){

        for(JButton b : botones){
            b.setIcon(null);
        }

        topoVisible = -1;
    }

    public int getTopoVisible(){
        return topoVisible;
    }

    public void actualizarScore(int score){
        lblScore.setText("Score: " + score);
    }
}