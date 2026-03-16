package Server;

import Model.GameState; // importar la clase GameState

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MonsterHitServerTCP {

    private static final int PUERTO_SERVIDOR_TCP = 49152; // puerto donde escucha el servidor

    private static GameState estadoActualDelJuego; // estado global de la partida
    private static MonsterPublisherActiveMQ publicadorDeEventos; // publicador de eventos por ActiveMQ

    public static void main(String[] args) {
        try {
            estadoActualDelJuego = new GameState(); // crea el estado global del juego
            publicadorDeEventos = new MonsterPublisherActiveMQ(); // crea el publicador de eventos
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
                        new ConnectionHandler(socketDelCliente, estadoActualDelJuego, publicadorDeEventos); // crea un hilo para atender a ese cliente

                manejadorDeConexion.start(); // inicia el hilo del cliente
            }

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor TCP: " + e.getMessage());
        }
    }
}

class ConnectionHandler extends Thread {
    private final Socket socketDelCliente; // socket específico de este cliente
    private final DataInputStream flujoDeEntrada; // para leer mensajes del cliente
    private final DataOutputStream flujoDeSalida; // para responder al cliente
    private final GameState estadoActualDelJuego; // referencia al estado global del juego
    private final MonsterPublisherActiveMQ publicadorDeEventos; // referencia al publicador de ActiveMQ

    public ConnectionHandler(Socket socketCliente,
                             GameState gameStateCompartido,
                             MonsterPublisherActiveMQ publicadorCompartido) throws IOException {

        socketDelCliente = socketCliente; // guarda el socket del cliente
        estadoActualDelJuego = gameStateCompartido; // guarda la referencia al estado del juego
        publicadorDeEventos = publicadorCompartido; // guarda la referencia al publicador

        flujoDeEntrada = new DataInputStream(socketDelCliente.getInputStream()); // flujo para leer
        flujoDeSalida = new DataOutputStream(socketDelCliente.getOutputStream()); // flujo para responder
    }

    @Override
    public void run() {
        try {
            String mensajeRecibido = flujoDeEntrada.readUTF(); // lee el mensaje enviado por el cliente
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            String respuestaParaElCliente = procesarMensajeDelCliente(mensajeRecibido); // procesa el mensaje
            flujoDeSalida.writeUTF(respuestaParaElCliente); // responde al cliente
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

        publicadorDeEventos.publicarGolpeValido(
                nombreDelJugador,
                estadoActualDelJuego.obtenerJugador(nombreDelJugador).getScore()
        ); // publica el golpe válido a los demás clientes

        if (estadoActualDelJuego.hayGanador()) {
            String nombreDelGanador = estadoActualDelJuego.obtenerGanador(); // obtiene el ganador actual

            System.out.println("Tenemos ganador: " + nombreDelGanador);

            publicadorDeEventos.publicarGanador(nombreDelGanador); // publica ganador
            estadoActualDelJuego.reiniciarPartida(); // reinicia la partida
            publicadorDeEventos.publicarReinicio(); // publica reinicio del juego

            return "HIT_OK|WINNER|" + nombreDelGanador; // responde que hubo ganador
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