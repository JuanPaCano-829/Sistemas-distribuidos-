package UI;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    // Administrador de vistas
    private CardLayout cardLayout; // manejar pantallas dentro de la misma ventan
    private JPanel panelPrincipal; // contenedor de pantallas

    // Componentes que necesitamos controlar desde fuera
    private JButton[] botonesTopos; // arreglo de 9 botones
    private JLabel lblScore; // txt para el score
    private JTextArea areaJugadores; // donde aparecen los jugadores conectados
    private JTextField txtNombre; // donde el jugador escribe su nombre
    private JSpinner spinVelocidad; // valor de la velocidad del juego
    private JTextField txtCodigo; // para escribir el IP del host para unirte a la partida

    // Para recordar qué topo está visible
    private int topoVisible = -1; // boton que tiene el topo

    public GameWindow() { // para construir la ventana
        setTitle("Pegarle al Topo - Proyecto Alpha - Juan Pablo y Giuseppe"); // nombre de la ventana
        setSize(500, 600); // ancho y alto de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // cuando cierras la ventana el programa termina
        setLocationRelativeTo(null); // ventana en el centro de la pantalla

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        // pantallas dentro del menu
        panelPrincipal.add(crearPanelMenu(), "MENU");
        panelPrincipal.add(crearPanelSolitario(), "SOLITARIO");
        panelPrincipal.add(crearPanelLobby(), "LOBBY");
        panelPrincipal.add(crearPanelUnirse(), "UNIRSE");

        // se agrega el panel dentro de la ventana
        add(panelPrincipal);

        // se muestra el menu principal
        cardLayout.show(panelPrincipal, "MENU");
    }

    // ==========================================
    // PÁGINA 1: MENÚ PRINCIPAL
    // ==========================================
    private JPanel crearPanelMenu() { // construye la pantalla Menu
        JPanel panel = new JPanel(new GridLayout(4, 1, 20, 20)); // para crear las ociones son 4 renglones y 1 columna
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        JLabel titulo = new JLabel("¡Pégale al monstruo!", SwingConstants.CENTER);
        titulo.setFont(new Font("Comic Sans", Font.BOLD, 24));

        // Botones del menu principal
        JButton btnJugar = new JButton("1) Modo un jugador");
        JButton btnHost = new JButton("2) Modo Multijugado - Crear Sala");
        JButton btnUnirse = new JButton("3) Unirse a Partida");

        // cuando presiones el boton a donde va
        btnJugar.addActionListener(e -> cardLayout.show(panelPrincipal, "SOLITARIO"));
        btnHost.addActionListener(e -> cardLayout.show(panelPrincipal, "LOBBY"));
        btnUnirse.addActionListener(e -> cardLayout.show(panelPrincipal, "UNIRSE"));


        panel.add(titulo);
        panel.add(btnJugar);
        panel.add(btnHost);
        panel.add(btnUnirse);

        return panel;
    }

    // ==========================================
    // PÁGINA 2: JUGAR (MODO SOLITARIO)
    // ==========================================
    private JPanel crearPanelSolitario() {
        JPanel panel = new JPanel(new BorderLayout());

        // panel para los controles
        JPanel panelControles = new JPanel(new GridLayout(2, 4, 5, 5));
        panelControles.setBorder(BorderFactory.createTitledBorder("Configuración"));

        panelControles.add(new JLabel("Nombre:"));
        txtNombre = new JTextField("Escribe tu nombre...");
        panelControles.add(txtNombre);

        panelControles.add(new JLabel("Velocidad (1-10):"));
        spinVelocidad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        panelControles.add(spinVelocidad);

        panelControles.add(new JLabel("Score:"));
        lblScore = new JLabel("0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Arial", Font.BOLD, 16));
        panelControles.add(lblScore);

        JButton btnIniciar = new JButton("¡Iniciar Partida!");

        // Cuando presionas pasa lo siguiente
        btnIniciar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Partida iniciada para: " + obtenerNombreJugador());
            actualizarScore(0);
            ocultarTopos();
        });
        panelControles.add(btnIniciar);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU"));
        panelControles.add(btnVolver);

        JPanel panelTopos = new JPanel(new GridLayout(3, 3, 10, 10));
        panelTopos.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // crea el arreglo de 9 botones
        botonesTopos = new JButton[9];

        // crea los 9 botones
        for (int i = 0; i < 9; i++) {
            JButton topo = new JButton();
            topo.setBackground(new Color(173, 216, 230));
            topo.setFocusPainted(false);

            final int indice = i;
            topo.addActionListener(e -> {
                System.out.println("Se hizo clic en la casilla: " + indice);

                if (indice == topoVisible) {
                    System.out.println("¡Le pegaste al topo!");
                    actualizarScore(Integer.parseInt(lblScore.getText()) + 1);
                    ocultarTopos();

                    // Aquí después puedes conectar tu cliente TCP:
                    // Client.enviarMensaje(ipDestino, puertoDestino, "HIT|" + indice);
                } else {
                    System.out.println("Casilla incorrecta.");
                }
            });

            botonesTopos[i] = topo;
            panelTopos.add(topo);
        }

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(panelTopos, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // PÁGINA 3: LOBBY
    // ==========================================
    private JPanel crearPanelLobby() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Sala de Espera Multijugador", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titulo, BorderLayout.NORTH);

        areaJugadores = new JTextArea("Jugadores conectados:\n1. Tú (Anfitrión)\n...\nEsperando a otros...");
        areaJugadores.setEditable(false);
        panel.add(new JScrollPane(areaJugadores), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnIniciar = new JButton("Iniciar Partida Multijugador");
        JButton btnVolver = new JButton("Cancelar y Volver");

        btnIniciar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Aquí después arrancas la partida multijugador.");
            mostrarPantalla("SOLITARIO");
        });

        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU"));

        panelBotones.add(btnIniciar);
        panelBotones.add(btnVolver);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // PÁGINA 4: UNIRSE A PARTIDA
    // ==========================================
    private JPanel crearPanelUnirse() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        panel.add(new JLabel("Ingresa la IP del Anfitrión:", SwingConstants.CENTER));

        txtCodigo = new JTextField("localhost");
        panel.add(txtCodigo);

        JButton btnConectar = new JButton("Conectar a la Sala");
        JButton btnVolver = new JButton("Volver al Menú");

        btnConectar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Conectando a " + txtCodigo.getText() + "...");
            cardLayout.show(panelPrincipal, "LOBBY");
        });

        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU"));

        panel.add(btnConectar);
        panel.add(btnVolver);

        return panel;
    }

    // ==========================================
    // MÉTODOS PÚBLICOS PARA CONTROLAR LA UI
    // ==========================================

    public void mostrarPantalla(String nombrePantalla) {
        cardLayout.show(panelPrincipal, nombrePantalla);
    }

    public void actualizarScore(int score) {
        lblScore.setText(String.valueOf(score));
    }

    public void actualizarJugadores(String texto) {
        areaJugadores.setText(texto);
    }

    public String obtenerNombreJugador() {
        return txtNombre.getText();
    }

    public int obtenerVelocidad() {
        return (int) spinVelocidad.getValue();
    }

    public String obtenerIPHost() {
        return txtCodigo.getText();
    }

    public void mostrarTopo(int indice) {
        ocultarTopos();

        if (indice >= 0 && indice < botonesTopos.length) {
            botonesTopos[indice].setText("🟢");
            botonesTopos[indice].setBackground(Color.GREEN);
            topoVisible = indice;
        }
    }

    public void ocultarTopos() {
        if (botonesTopos == null) return;

        for (JButton boton : botonesTopos) {
            boton.setText("");
            boton.setBackground(new Color(173, 216, 230));
        }
        topoVisible = -1;
    }

    public int getTopoVisible() {
        return topoVisible;
    }
}