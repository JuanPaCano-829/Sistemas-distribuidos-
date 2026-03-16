package UI;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    // Administrador de vistas
    private CardLayout cardLayout; // manejar pantallas dentro de la misma ventana
    private JPanel panelPrincipal; // contenedor de pantallas

    // Componentes que necesitamos controlar desde fuera
    private JButton[] botonesTopos; // arreglo de 9 botones
    private JLabel lblScore; // texto donde se muestra el score
    private JTextArea areaJugadores; // donde aparecen los jugadores conectados
    private JTextField txtNombre; // donde el jugador escribe su nombre
    private JSpinner spinVelocidad; // valor de la velocidad del juego
    private JTextField txtCodigo; // para escribir la IP del host para unirte a la partida
    private ImageIcon topoGif;

    // Para recordar qué topo está visible
    private int topoVisible = -1; // botón que tiene el topo visible, -1 significa que no hay ninguno

    public GameWindow() { // para construir la ventana
        topoGif = new ImageIcon("C:\\Sistemas Distribuidos\\Sistemas-distribuidos-\\Extras_proyecto\\topo_saliendo.gif");
        setTitle("Pegarle al Topo - Proyecto Alpha - Juan Pablo y Giuseppe"); // nombre de la ventana
        setSize(500, 600); // ancho y alto de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // cuando cierras la ventana el programa termina
        setLocationRelativeTo(null); // ventana en el centro de la pantalla

        cardLayout = new CardLayout(); // crea el administrador de pantallas
        panelPrincipal = new JPanel(cardLayout); // panel principal que usará el CardLayout

        // pantallas dentro del menú
        panelPrincipal.add(crearPanelMenu(), "MENU"); // agrega la pantalla del menú principal
        panelPrincipal.add(crearPanelSolitario(), "SOLITARIO"); // agrega la pantalla de juego solitario
        panelPrincipal.add(crearPanelLobby(), "LOBBY"); // agrega la pantalla de sala de espera
        panelPrincipal.add(crearPanelUnirse(), "UNIRSE"); // agrega la pantalla para unirse a una partida

        // se agrega el panel dentro de la ventana
        add(panelPrincipal);

        // se muestra el menú principal
        cardLayout.show(panelPrincipal, "MENU");
    }

    // ==========================================
    // PÁGINA 1: MENÚ PRINCIPAL
    // ==========================================
    private JPanel crearPanelMenu() { // construye la pantalla Menu
        JPanel panel = new JPanel(new GridLayout(4, 1, 20, 20)); // para crear las opciones son 4 renglones y 1 columna
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100)); // margen interno para que no quede pegado a la orilla

        JLabel titulo = new JLabel("¡Pégale al monstruo!", SwingConstants.CENTER); // texto del título centrado
        titulo.setFont(new Font("Comic Sans", Font.BOLD, 24)); // fuente del título

        // Botones del menú principal
        JButton btnJugar = new JButton("1) Modo un jugador"); // botón para ir al modo solitario
        JButton btnHost = new JButton("2) Modo Multijugado - Crear Sala"); // botón para crear sala
        JButton btnUnirse = new JButton("3) Unirse a Partida"); // botón para unirse a una partida existente

        // cuando presiones el botón a donde va
        btnJugar.addActionListener(e -> cardLayout.show(panelPrincipal, "SOLITARIO")); // cambia a la pantalla solitario
        btnHost.addActionListener(e -> cardLayout.show(panelPrincipal, "LOBBY")); // cambia a la pantalla lobby
        btnUnirse.addActionListener(e -> cardLayout.show(panelPrincipal, "UNIRSE")); // cambia a la pantalla unirse

        panel.add(titulo); // agrega el título al panel
        panel.add(btnJugar); // agrega el botón de jugar
        panel.add(btnHost); // agrega el botón de crear sala
        panel.add(btnUnirse); // agrega el botón de unirse

        return panel; // regresa el panel ya construido
    }

    // ==========================================
    // PÁGINA 2: JUGAR (MODO SOLITARIO)
    // ==========================================
    private JPanel crearPanelSolitario() { // construye la pantalla del modo solitario
        JPanel panel = new JPanel(new BorderLayout()); // panel principal usando BorderLayout

        // panel para los controles
        JPanel panelControles = new JPanel(new GridLayout(2, 4, 5, 5)); // 2 filas, 4 columnas para acomodar controles
        panelControles.setBorder(BorderFactory.createTitledBorder("Configuración")); // borde con título

        panelControles.add(new JLabel("Nombre:")); // etiqueta para el nombre
        txtNombre = new JTextField("Escribe tu nombre..."); // campo de texto para el nombre
        panelControles.add(txtNombre); // agrega el campo de nombre

        panelControles.add(new JLabel("Velocidad (1-10):")); // etiqueta para la velocidad
        spinVelocidad = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1)); // spinner con valor inicial 5, mínimo 1, máximo 10 y paso 1
        panelControles.add(spinVelocidad); // agrega el spinner de velocidad

        panelControles.add(new JLabel("Score:")); // etiqueta del score
        lblScore = new JLabel("0", SwingConstants.CENTER); // inicia el score en 0
        lblScore.setFont(new Font("Arial", Font.BOLD, 16)); // fuente del score
        panelControles.add(lblScore); // agrega la etiqueta del score

        JButton btnIniciar = new JButton("¡Iniciar Partida!"); // botón para iniciar partida

        // Cuando presionas pasa lo siguiente
        btnIniciar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Partida iniciada para: " + obtenerNombreJugador()); // muestra mensaje con el nombre del jugador
            actualizarScore(0); // reinicia el score a 0
            ocultarTopos(); // oculta todos los topos al iniciar
        });
        panelControles.add(btnIniciar); // agrega el botón iniciar

        JButton btnVolver = new JButton("Volver al Menú"); // botón para regresar al menú
        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU")); // cambia a la pantalla del menú
        panelControles.add(btnVolver); // agrega el botón volver

        JPanel panelTopos = new JPanel(new GridLayout(3, 3, 10, 10)); // tablero de 3x3 para los topos
        panelTopos.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // margen alrededor del tablero

        // crea el arreglo de 9 botones
        botonesTopos = new JButton[9];

        // crea los 9 botones
        for (int i = 0; i < 9; i++) {
            JButton topo = new JButton(); // crea un botón vacío
            topo.setBackground(new Color(173, 216, 230)); // color de fondo azul claro
            topo.setFocusPainted(false); // quita el borde de enfoque al hacer clic

            final int indice = i; // guarda el índice del botón actual
            topo.addActionListener(e -> { // acción cuando haces clic en una casilla
                System.out.println("Se hizo clic en la casilla: " + indice); // imprime qué casilla se presionó

                if (indice == topoVisible) { // si la casilla clickeada es donde está el topo
                    System.out.println("¡Le pegaste al topo!"); // mensaje en consola
                    actualizarScore(Integer.parseInt(lblScore.getText()) + 1); // suma 1 al score actual
                    ocultarTopos(); // oculta todos los topos después de pegarle

                    // Aquí después puedes conectar tu cliente TCP:
                    // Client.enviarMensaje(ipDestino, puertoDestino, "HIT|" + indice);
                } else { // si le pegó a una casilla incorrecta
                    System.out.println("Casilla incorrecta."); // mensaje en consola
                }
            });

            botonesTopos[i] = topo; // guarda el botón en el arreglo
            panelTopos.add(topo); // agrega el botón al panel del tablero
        }

        panel.add(panelControles, BorderLayout.NORTH); // coloca los controles arriba
        panel.add(panelTopos, BorderLayout.CENTER); // coloca el tablero en el centro

        return panel; // regresa la pantalla ya construida
    }

    // ==========================================
    // PÁGINA 3: LOBBY
    // ==========================================
    private JPanel crearPanelLobby() { // construye la pantalla de sala de espera
        JPanel panel = new JPanel(new BorderLayout()); // panel principal del lobby
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // margen interno

        JLabel titulo = new JLabel("Sala de Espera Multijugador", SwingConstants.CENTER); // título del lobby
        titulo.setFont(new Font("Arial", Font.BOLD, 18)); // fuente del título
        panel.add(titulo, BorderLayout.NORTH); // coloca el título arriba

        areaJugadores = new JTextArea("Jugadores conectados:\n1. Tú (Anfitrión)\n...\nEsperando a otros..."); // texto inicial de jugadores
        areaJugadores.setEditable(false); // impide que el usuario edite el área de texto
        panel.add(new JScrollPane(areaJugadores), BorderLayout.CENTER); // agrega área con scroll al centro

        JPanel panelBotones = new JPanel(new FlowLayout()); // panel para los botones del lobby
        JButton btnIniciar = new JButton("Iniciar Partida Multijugador"); // botón para iniciar partida multi
        JButton btnVolver = new JButton("Cancelar y Volver"); // botón para volver al menú

        btnIniciar.addActionListener(e -> { // acción al iniciar partida multijugador
            JOptionPane.showMessageDialog(this, "Aquí después arrancas la partida multijugador."); // mensaje temporal
            mostrarPantalla("SOLITARIO"); // por ahora cambia al panel de juego
        });

        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU")); // regresa al menú

        panelBotones.add(btnIniciar); // agrega botón iniciar
        panelBotones.add(btnVolver); // agrega botón volver
        panel.add(panelBotones, BorderLayout.SOUTH); // coloca los botones abajo

        return panel; // regresa el panel lobby
    }

    // ==========================================
    // PÁGINA 4: UNIRSE A PARTIDA
    // ==========================================
    private JPanel crearPanelUnirse() { // construye la pantalla para unirse a una partida
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 filas, 1 columna
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100)); // margen interno

        panel.add(new JLabel("Ingresa la IP del Anfitrión:", SwingConstants.CENTER)); // instrucción para escribir la IP

        txtCodigo = new JTextField("localhost"); // campo de texto con localhost por defecto
        panel.add(txtCodigo); // agrega el campo al panel

        JButton btnConectar = new JButton("Conectar a la Sala"); // botón para conectarse
        JButton btnVolver = new JButton("Volver al Menú"); // botón para volver

        // boton para conectar
        btnConectar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Conectando a " + txtCodigo.getText() + "..."); // muestra mensaje con la IP
            cardLayout.show(panelPrincipal, "LOBBY"); // cambia al lobby
        });

        btnVolver.addActionListener(e -> cardLayout.show(panelPrincipal, "MENU")); // vuelve al menú principal

        panel.add(btnConectar); // agrega botón conectar
        panel.add(btnVolver); // agrega botón volver

        return panel; // regresa el panel de unirse
    }

    // ==========================================
    // MÉTODOS PÚBLICOS PARA CONTROLAR LA UI
    // ==========================================

    public void mostrarPantalla(String nombrePantalla) { // cambia a una pantalla específica usando su nombre
        cardLayout.show(panelPrincipal, nombrePantalla);
    }

    public void actualizarScore(int score) { // actualiza el número del score en la interfaz
        lblScore.setText(String.valueOf(score));
    }

    public void actualizarJugadores(String texto) { // actualiza el texto del área de jugadores conectados
        areaJugadores.setText(texto);
    }

    public String obtenerNombreJugador() { // regresa el nombre que escribió el jugador
        return txtNombre.getText();
    }

    public int obtenerVelocidad() { // regresa la velocidad seleccionada en el spinner
        return (int) spinVelocidad.getValue();
    }

    public String obtenerIPHost() { // regresa la IP escrita para conectarse a una partida
        return txtCodigo.getText();
    }

    public void mostrarTopo(int indice) { // muestra un topo en la casilla indicada
        ocultarTopos(); // primero oculta cualquier topo anterior

        if (indice >= 0 && indice < botonesTopos.length) {

            botonesTopos[indice].setIcon(topoGif); // pone el GIF del topo
            botonesTopos[indice].setBackground(Color.WHITE); // opcional: fondo blanco para que se vea mejor

            topoVisible = indice; // guarda qué topo está visible
        }
    }

    public void ocultarTopos() { // oculta todos los topos del tablero
        if (botonesTopos == null) return;

        for (JButton boton : botonesTopos) {
            boton.setIcon(null); // quita el gif
            boton.setText("");
            boton.setBackground(new Color(173, 216, 230));
        }

        topoVisible = -1;
    }

    public int getTopoVisible() { // regresa el índice del topo visible actual
        return topoVisible;
    }
}