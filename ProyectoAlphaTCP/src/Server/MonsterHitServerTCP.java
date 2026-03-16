package Server;

<<<<<<< HEAD
import Model.GameState;
import Model.Player;
=======
import Model.GameState; // importar de la clase GameState
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

<<<<<<< HEAD
public class MonsterHitServerTCP {
    private static final int PUERTO_SERVIDOR_TCP = 49152; // puerto donde escucha el servidor
    private static GameState estadoActualDelJuego; // estado global compartido
    private static MonsterPublisherActiveMQ publicadorDeEventos; // publisher de ActiveMQ
=======
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

// clase principal para iniciar el servidor, abrir el puerto, esperar clientes y crear el ConnectionHandler
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
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf

    public static void main(String[] args) {
        try {
            estadoActualDelJuego = new GameState(); // crea el estado global compartido
            publicadorDeEventos = new MonsterPublisherActiveMQ(estadoActualDelJuego); // usa el mismo GameState

            // inicia el publisher en segundo plano
            Thread hiloPublisher = new Thread(() -> publicadorDeEventos.startPublishing());
            hiloPublisher.start();

            ServerSocket socketServidor = new ServerSocket(PUERTO_SERVIDOR_TCP); // abre el puerto TCP

            System.out.println("==========================================");
            System.out.println("Servidor TCP del juego iniciado");
            System.out.println("Puerto: " + PUERTO_SERVIDOR_TCP);
            System.out.println("Esperando conexiones de clientes...");
            System.out.println("==========================================");

            while (true) {
                Socket socketDelCliente = socketServidor.accept(); // espera hasta que un cliente se conecte
                System.out.println("Nuevo cliente conectado desde: " + socketDelCliente.getInetAddress());

                ConnectionHandler manejadorDeConexion =
                        new ConnectionHandler(socketDelCliente, estadoActualDelJuego); // crea un hilo para atender a ese cliente

                manejadorDeConexion.start(); // inicia el hilo del cliente
            }

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor TCP: " + e.getMessage());
        }
    }
}

class ConnectionHandler extends Thread {
    private final Socket socketDelCliente; // socket de este cliente
    private final DataInputStream flujoDeEntrada; // flujo para leer mensajes
    private final DataOutputStream flujoDeSalida; // flujo para responder
    private final GameState estadoActualDelJuego; // referencia al estado compartido

    public ConnectionHandler(Socket socketCliente, GameState gameStateCompartido) throws IOException {
        socketDelCliente = socketCliente; // guarda el socket del cliente
        estadoActualDelJuego = gameStateCompartido; // guarda el estado compartido

        flujoDeEntrada = new DataInputStream(socketDelCliente.getInputStream()); // flujo de entrada
        flujoDeSalida = new DataOutputStream(socketDelCliente.getOutputStream()); // flujo de salida
    }

    @Override
    public void run() {
        try {
<<<<<<< HEAD
            String mensajeRecibido = flujoDeEntrada.readUTF(); // lee el mensaje enviado por el cliente
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            String respuestaParaElCliente = procesarMensajeDelCliente(mensajeRecibido); // procesa el mensaje
            flujoDeSalida.writeUTF(respuestaParaElCliente); // responde al cliente
=======
            // lee el mensaje enviado por el cliente
            String mensajeRecibido = flujoDeEntrada.readUTF(); // readUTF() leer los strings
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            // procesa el mensaje y construye una respuesta
            String respuestaParaElCliente = procesarMensajeDelCliente(mensajeRecibido);

            // manda la respuesta al cliente
            flujoDeSalida.writeUTF(respuestaParaElCliente); // writeUTF para responderle al cliente con strings
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf
            flujoDeSalida.flush();

        } catch (IOException e) {
            System.out.println("Error al atender al cliente: " + e.getMessage());
        } finally {
            cerrarConexion(); // cierra la conexión al terminar
        }
    }

    // =========================
    // PROCESAMIENTO DE MENSAJES
    // =========================
    private String procesarMensajeDelCliente(String mensajeCompleto) {
        if (mensajeCompleto == null || mensajeCompleto.trim().isEmpty()) {
            return "ERROR|Mensaje vacío"; // evita procesar mensajes vacíos
        }

        String[] partesDelMensaje = mensajeCompleto.split("\\|"); // separa el mensaje por |
        String tipoDeComando = obtenerTipoDeComando(partesDelMensaje); // obtiene el comando principal

        switch (tipoDeComando) {
            case "LOGIN":
                return procesarLogin(partesDelMensaje); // procesa registro del jugador

            case "HIT":
                return procesarGolpe(partesDelMensaje); // procesa golpe al monstruo

            case "DISCONNECT":
                return procesarDesconexion(partesDelMensaje); // procesa desconexión

            default:
                return "ERROR|Comando no reconocido";
        }
    }

    private String obtenerTipoDeComando(String[] partesDelMensaje) {
        if (partesDelMensaje.length == 0 || partesDelMensaje[0] == null) {
            return ""; // evita errores si el mensaje viene mal formado
        }

        return partesDelMensaje[0].trim().toUpperCase(); // normaliza el comando
    }

    // =========================
    // LOGIN
    // =========================
    private String procesarLogin(String[] partesDelMensaje) {
        if (partesDelMensaje.length < 2) {
            return "ERROR|Formato LOGIN inválido"; // valida formato mínimo
        }

        String nombreDelJugador = partesDelMensaje[1].trim(); // obtiene el nombre del jugador
        estadoActualDelJuego.agregarJugador(nombreDelJugador); // agrega al jugador si no existe

        Player jugador = estadoActualDelJuego.obtenerJugador(nombreDelJugador); // obtiene el jugador
        if (jugador != null) {
            jugador.setConectado(true); // marca al jugador como conectado
        }

        System.out.println("Jugador registrado o reconectado: " + nombreDelJugador);

        return "LOGIN_OK|" + nombreDelJugador; // responde login exitoso
    }


    // =========================
    // GOLPES
    // =========================
    private String procesarGolpe(String[] partesDelMensaje) {
        if (partesDelMensaje.length < 3) {
            return "ERROR|Formato HIT inválido"; // valida formato mínimo
        }

        String nombreDelJugador = partesDelMensaje[1].trim(); // nombre del jugador que golpeó
        int posicionGolpeada;

        try {
            posicionGolpeada = Integer.parseInt(partesDelMensaje[2].trim()); // convierte la posición a entero
        } catch (NumberFormatException e) {
            return "ERROR|Posición inválida";
        }

        boolean golpeValido = estadoActualDelJuego.procesarGolpe(nombreDelJugador, posicionGolpeada); // valida y procesa el golpe

        if (!golpeValido) {
            System.out.println("Golpe inválido de " + nombreDelJugador + " en posición " + posicionGolpeada);
            return "MISS|" + nombreDelJugador; // responde que el golpe no fue válido
        }

        System.out.println("Golpe válido de " + nombreDelJugador + " en posición " + posicionGolpeada);

        // aquí ya no publicamos manualmente WINNER o RESET
        // eso lo hace MonsterPublisherActiveMQ usando el mismo GameState compartido

        if (estadoActualDelJuego.hayGanador()) {
            return "HIT_OK|WINNER|" + estadoActualDelJuego.obtenerGanador(); // respuesta inmediata al cliente que ganó
        }

        return "HIT_OK|" + nombreDelJugador; // responde golpe correcto
    }

    // =========================
    // DESCONEXIÓN
    // =========================
    private String procesarDesconexion(String[] partesDelMensaje) {
        if (partesDelMensaje.length < 2) {
            return "ERROR|Formato DISCONNECT inválido"; // valida formato mínimo
        }

        String nombreDelJugador = partesDelMensaje[1].trim(); // obtiene el nombre del jugador
        Player jugador = estadoActualDelJuego.obtenerJugador(nombreDelJugador); // busca al jugador

        if (jugador != null) {
            jugador.setConectado(false); // lo marca como desconectado
        }

        System.out.println("Jugador desconectado: " + nombreDelJugador);

        return "BYE|" + nombreDelJugador; // responde despedida
    }

    // =========================
    // CIERRE DE CONEXIÓN
    // =========================
    private void cerrarConexion() {
        try {
            if (flujoDeEntrada != null) flujoDeEntrada.close(); // cierra entrada
            if (flujoDeSalida != null) flujoDeSalida.close(); // cierra salida
            if (socketDelCliente != null) socketDelCliente.close(); // cierra socket

        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}