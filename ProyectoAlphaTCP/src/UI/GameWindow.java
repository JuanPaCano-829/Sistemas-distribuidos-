package UI;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    // El administrador de las "páginas"
    private CardLayout cardLayout;
    private JPanel panelPrincipal;

    public GameWindow() {
        setTitle("Pegarle al Topo - Alpha");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializamos el CardLayout
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        // Creamos las diferentes "páginas" de nuestro juego
        panelPrincipal.add(crearPanelMenu(), "MENU");
        panelPrincipal.add(crearPanelSolitario(), "SOLITARIO");
        panelPrincipal.add(crearPanelLobby(), "LOBBY");
        panelPrincipal.add(crearPanelUnirse(), "UNIRSE");

        // Agregamos el panel principal a la ventana
        add(panelPrincipal);

        // Le decimos que la primera página que debe mostrar es el MENU
        cardLayout.show(panelPrincipal, "MENU");
    }

    // ==========================================
    // PÁGINA 1: MENÚ PRINCIPAL
    // ==========================================
    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));

        JLabel titulo = new JLabel("PÉGARLE AL TOPO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JButton btnJugar = new JButton("1. Jugar (Solitario)");
        JButton btnHost = new JButton("2. Multijugador (Crear Sala)");
        JButton btnUnirse = new JButton("3. Unirse a Partida");

        // Navegación de los botones
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

        // Cabecera: Nombre, Velocidad, Score y Botón Iniciar
        JPanel panelControles = new JPanel(new GridLayout(2, 4, 5, 5));
        panelControles.setBorder(BorderFactory.createTitledBorder("Configuración"));

        panelControles.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField("Jugador1");
        panelControles.add(txtNombre);

        panelControles.add(new JLabel("Velocidad (1-10):"));
        JSpinner spinVelocidad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        panelControles.add(spinVelocidad);

        panelControles.add(new JLabel("Score:"));
        JLabel lblScore = new JLabel("0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Arial", Font.BOLD, 16));
        panelControles.add(lblScore);

        JButton btnIniciar = new JButton("¡Iniciar Partida!");
        panelControles.add(btnIniciar);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU"));
        panelControles.add(btnVolver);

        // Centro: Matriz de Topos 3x3
        JPanel panelTopos = new JPanel(new GridLayout(3, 3, 10, 10));
        panelTopos.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        for (int i = 0; i < 9; i++) {
            JButton topo = new JButton();
            topo.setBackground(new Color(173, 216, 230));
            panelTopos.add(topo);
        }

        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(panelTopos, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // PÁGINA 3: LOBBY (SALA DE ESPERA)
    // ==========================================
    private JPanel crearPanelLobby() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Sala de Espera Multijugador", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titulo, BorderLayout.NORTH);

        JTextArea areaJugadores = new JTextArea("Jugadores conectados:\n1. Tú (Anfitrión)\n...\nEsperando a otros...");
        areaJugadores.setEditable(false);
        panel.add(new JScrollPane(areaJugadores), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnIniciar = new JButton("Iniciar Partida Multijugador");
        JButton btnVolver = new JButton("Cancelar y Volver");

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
        JTextField txtCodigo = new JTextField("localhost");
        panel.add(txtCodigo);

        JButton btnConectar = new JButton("Conectar a la Sala");
        JButton btnVolver = new JButton("Volver al Menú");

        // Al conectar, te manda a la sala de espera (LOBBY)
        btnConectar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Conectando a " + txtCodigo.getText() + "...");
            cardLayout.show(panelPrincipal, "LOBBY");
        });

        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU"));

        panel.add(btnConectar);
        panel.add(btnVolver);

        return panel;
    }
}