package Server;

import Model.GameState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// ==========================================
// CLASE MONSTERHITSERVERTCP
// Esta clase representa el servidor TCP del juego.
//
// Su trabajo es:
// - abrir un puerto
// - esperar conexiones de clientes
// - aceptar mensajes TCP
// - crear un hilo por cada cliente
// Solamente recibe acciones de los jugadores y actualiza
// el estado del juego usando GameState
// ==========================================

public class MonsterHitServerTCP {

    // ==========================================
    // CONSTANTES DEL SERVIDOR
    // ==========================================

    private static final int PUERTO_SERVIDOR_TCP = 49152; // puerto donde el servidor escuchará conexiones

    // ==========================================
    // ATRIBUTOS COMPARTIDOS DEL SERVIDOR
    // ==========================================

    private static GameState estadoActualDelJuego;                 // estado global de la partida
    private static MonsterPublisherActiveMQ publicadorDeEventos;   // clase que publica eventos al tópico

    // ==========================================
    // MÉTODO MAIN
    // ==========================================
    // Aquí inicia el servidor.
    //
    // El servidor:
    // 1. crea el estado del juego
    // 2. crea el publicador de eventos
    // 3. abre el puerto TCP
    // 4. espera clientes en un ciclo infinito
    // 5. crea un ConnectionHandler por cada cliente

    public static void main(String[] args) {

        try {
            // crea el estado global del juego
            estadoActualDelJuego = new GameState();

            // crea el publicador que después enviará eventos por ActiveMQ
            publicadorDeEventos = new MonsterPublisherActiveMQ();

            // abre el puerto del servidor TCP
            ServerSocket socketServidor = new ServerSocket(PUERTO_SERVIDOR_TCP);

            System.out.println("==========================================");
            System.out.println("Servidor TCP del juego iniciado");
            System.out.println("Puerto: " + PUERTO_SERVIDOR_TCP);
            System.out.println("Esperando conexiones de clientes...");
            System.out.println("==========================================");

            // ciclo infinito para aceptar jugadores
            while (true) {
                Socket socketDelCliente = socketServidor.accept(); // espera hasta que un cliente se conecte
                System.out.println("Nuevo cliente conectado desde: " + socketDelCliente.getInetAddress());

                // crea un hilo que atenderá a ese cliente en particular
                ConnectionHandler manejadorDeConexion =
                        new ConnectionHandler(socketDelCliente, estadoActualDelJuego, publicadorDeEventos);

                manejadorDeConexion.start(); // inicia el hilo del cliente
            }

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor TCP: " + e.getMessage());
        }
    }
}


// ==========================================
// CLASE CONNECTIONHANDLER
// ==========================================
// Esta clase se encarga de atender a UN cliente.
//
// Cada vez que un cliente se conecta, se crea un
// ConnectionHandler diferente.
//
// Su trabajo es:
// - leer el mensaje del cliente
// - interpretar el comando
// - actualizar GameState
// - responder al cliente
//
// Se usa Thread para que varios clientes puedan
// conectarse al mismo tiempo sin bloquearse entre sí.

class ConnectionHandler extends Thread {

    // ==========================================
    // ATRIBUTOS DE LA CONEXIÓN
    // ==========================================

    private final Socket socketDelCliente;                      // socket específico de este cliente
    private final DataInputStream flujoDeEntrada;               // para leer mensajes del cliente
    private final DataOutputStream flujoDeSalida;               // para responder al cliente
    private final GameState estadoActualDelJuego;               // referencia compartida al estado global
    private final MonsterPublisherActiveMQ publicadorDeEventos; // referencia al publicador de ActiveMQ


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    // Prepara todo lo necesario para hablar con este cliente

    public ConnectionHandler(Socket socketCliente,
                             GameState gameStateCompartido,
                             MonsterPublisherActiveMQ publicadorCompartido) throws IOException {

        socketDelCliente = socketCliente;                   // guarda el socket del cliente
        estadoActualDelJuego = gameStateCompartido;         // guarda la referencia al estado del juego
        publicadorDeEventos = publicadorCompartido;         // guarda la referencia al publicador

        flujoDeEntrada = new DataInputStream(socketDelCliente.getInputStream());   // canal para leer
        flujoDeSalida = new DataOutputStream(socketDelCliente.getOutputStream());  // canal para responder
    }


    // ==========================================
    // MÉTODO RUN
    // ==========================================
    // Aquí vive la lógica de atención del cliente.
    //
    // El flujo general es:
    // 1. leer mensaje
    // 2. procesarlo
    // 3. responder
    // 4. cerrar conexión

    @Override
    public void run() {

        try {
            // lee el mensaje enviado por el cliente
            String mensajeRecibido = flujoDeEntrada.readUTF();
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            // procesa el mensaje y construye una respuesta
            String respuestaParaElCliente = procesarMensajeDelCliente(mensajeRecibido);

            // manda la respuesta al cliente
            flujoDeSalida.writeUTF(respuestaParaElCliente);
            flujoDeSalida.flush();

        } catch (IOException e) {
            System.out.println("Error al atender al cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }


    // ==========================================
    // MÉTODO PARA PROCESAR MENSAJES
    // ==========================================
    // Este método interpreta el mensaje del cliente.
    //
    // Formatos esperados:
    // LOGIN|nombreJugador
    // HIT|nombreJugador|posicion
    // DISCONNECT|nombreJugador

    private String procesarMensajeDelCliente(String mensajeCompleto) {

        // si el mensaje viene vacío, se responde con error
        if (mensajeCompleto == null || mensajeCompleto.trim().isEmpty()) {
            return "ERROR|Mensaje vacío";
        }

        // separa el mensaje usando el carácter |
        String[] partesDelMensaje = mensajeCompleto.split("\\|");

        // obtiene el comando principal del mensaje
        String tipoDeComando = obtenerTipoDeComando(partesDelMensaje);

        switch (tipoDeComando) {

            case "LOGIN":
                return procesarLogin(partesDelMensaje);

            case "HIT":
                return procesarGolpe(partesDelMensaje);

            case "DISCONNECT":
                return procesarDesconexion(partesDelMensaje);

            default:
                return "ERROR|Comando no reconocido";
        }
    }


    // ==========================================
    // MÉTODO PARA OBTENER EL TIPO DE COMANDO
    // ==========================================
    // Este método toma la primera parte del mensaje
    // y la normaliza para poder usarla en el switch

    private String obtenerTipoDeComando(String[] partesDelMensaje) {

        if (partesDelMensaje.length == 0 || partesDelMensaje[0] == null) {
            return "";
        }

        return partesDelMensaje[0].trim().toUpperCase();
    }


    // ==========================================
    // MÉTODO PARA LOGIN
    // ==========================================
    // Si el jugador no existe, lo agrega.
    // Si ya existe, simplemente se mantiene en el estado del juego.

    private String procesarLogin(String[] partesDelMensaje) {

        // revisa que el mensaje tenga el formato correcto
        if (partesDelMensaje.length < 2) {
            return "ERROR|Formato LOGIN inválido";
        }

        String nombreDelJugador = partesDelMensaje[1].trim(); // nombre escrito por el jugador

        // agrega al jugador si todavía no existe
        estadoActualDelJuego.agregarJugador(nombreDelJugador);

        System.out.println("Jugador registrado o reconectado: " + nombreDelJugador);

        // opcionalmente podrías publicar la lista de jugadores actualizada
        // publicadorDeEventos.publicarJugadores(...);

        return "LOGIN_OK|" + nombreDelJugador;
    }


    // ==========================================
    // MÉTODO PARA PROCESAR GOLPES
    // ==========================================
    // Valida el golpe usando GameState.
    // Si el golpe fue válido:
    // - suma punto
    // - revisa si hay ganador
    // - publica eventos por ActiveMQ
    // Si no fue válido:
    // - responde con MISS

    private String procesarGolpe(String[] partesDelMensaje) {

        // revisa que el mensaje tenga el formato correcto
        if (partesDelMensaje.length < 3) {
            return "ERROR|Formato HIT inválido";
        }

        String nombreDelJugador = partesDelMensaje[1].trim(); // nombre del jugador que golpeó
        int posicionGolpeada;

        // intenta convertir la posición a número entero
        try {
            posicionGolpeada = Integer.parseInt(partesDelMensaje[2].trim());
        } catch (NumberFormatException e) {
            return "ERROR|Posición inválida";
        }

        // le pide a GameState que valide y procese el golpe
        boolean golpeValido =
                estadoActualDelJuego.procesarGolpeDelJugador(nombreDelJugador, posicionGolpeada);

        // si el golpe no fue válido, responde MISS
        if (!golpeValido) {
            System.out.println("Golpe inválido de " + nombreDelJugador + " en posición " + posicionGolpeada);
            return "MISS|" + nombreDelJugador;
        }

        // si el golpe sí fue válido, imprime información
        System.out.println("Golpe válido de " + nombreDelJugador + " en posición " + posicionGolpeada);

        // publica que alguien golpeó correctamente
        publicadorDeEventos.publicarGolpeValido(
                nombreDelJugador,
                estadoActualDelJuego.obtenerJugador(nombreDelJugador).getScore()
        );

        // revisa si ya hay ganador
        if (estadoActualDelJuego.hayGanadorEnLaPartida()) {
            String nombreDelGanador = estadoActualDelJuego.obtenerNombreGanador();

            System.out.println("Tenemos ganador: " + nombreDelGanador);

            // publica el ganador a todos los clientes
            publicadorDeEventos.publicarGanador(nombreDelGanador);

            // reinicia la partida para empezar otra
            estadoActualDelJuego.reiniciarPartidaCompleta();

            // publica el reinicio del juego
            publicadorDeEventos.publicarReinicio();

            return "HIT_OK|WINNER|" + nombreDelGanador;
        }

        return "HIT_OK|" + nombreDelJugador;
    }


    // ==========================================
    // MÉTODO PARA DESCONECTAR UN JUGADOR
    // ==========================================
    // Por ahora solo informa la desconexión.
    // Más adelante podrías marcar al jugador como desconectado.

    private String procesarDesconexion(String[] partesDelMensaje) {

        // revisa que el mensaje tenga el formato correcto
        if (partesDelMensaje.length < 2) {
            return "ERROR|Formato DISCONNECT inválido";
        }

        String nombreDelJugador = partesDelMensaje[1].trim();

        System.out.println("Jugador desconectado: " + nombreDelJugador);

        return "BYE|" + nombreDelJugador;
    }


    // ==========================================
    // MÉTODO PARA CERRAR LA CONEXIÓN
    // ==========================================
    // Este método cierra correctamente el socket del cliente

    private void cerrarConexion() {

        try {
            if (flujoDeEntrada != null) {
                flujoDeEntrada.close();
            }

            if (flujoDeSalida != null) {
                flujoDeSalida.close();
            }

            if (socketDelCliente != null) {
                socketDelCliente.close();
            }

        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}