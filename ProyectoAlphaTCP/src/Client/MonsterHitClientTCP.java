package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// ==========================================
// CLASE MONSTERHITCLIENTTCP
// Esta clase representa el cliente TCP del juego.
//
// Su trabajo es:
// - conectarse al servidor TCP
// - enviar mensajes al servidor
// - recibir respuestas del servidor
// ==========================================

public class MonsterHitClientTCP {

<<<<<<< HEAD
    private final String ipDelServidor; // IP del servidor TCP
    private final int puertoDelServidor; // puerto donde escucha el servidor

    public MonsterHitClientTCP(String ipServidor, int puertoServidor) {
        ipDelServidor = ipServidor; // guarda IP del servidor
        puertoDelServidor = puertoServidor; // guarda puerto del servidor
    }

    // =========================
    // MENSAJES DEL CLIENTE
    // =========================
    public String enviarLogin(String nombreJugador) {
        String mensaje = "LOGIN|" + nombreJugador; // mensaje de registro
        return enviarMensajeAlServidor(mensaje);
    }

    public String enviarGolpe(String nombreJugador, int posicionGolpeada) {
        String mensaje = "HIT|" + nombreJugador + "|" + posicionGolpeada; // mensaje de golpe
        return enviarMensajeAlServidor(mensaje);
    }

    public String enviarDesconexion(String nombreJugador) {
        String mensaje = "DISCONNECT|" + nombreJugador; // mensaje de salida
        return enviarMensajeAlServidor(mensaje);
    }


    // =========================
    // ENVÍO GENERAL DE MENSAJES TCP
    // =========================
    private String enviarMensajeAlServidor(String mensaje) {

        try (
                Socket socket = new Socket(ipDelServidor, puertoDelServidor); // abre conexión TCP
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream()); // flujo de salida
                DataInputStream entrada = new DataInputStream(socket.getInputStream()) // flujo de entrada
        ) {

            salida.writeUTF(mensaje); // envía mensaje al servidor
            salida.flush();

            System.out.println("Mensaje enviado: " + mensaje);

            String respuesta = entrada.readUTF(); // espera respuesta del servidor

            System.out.println("Respuesta recibida: " + respuesta);

            return respuesta; // devuelve la respuesta al programa

        } catch (IOException e) {

            System.out.println("Error en conexión TCP: " + e.getMessage());
=======
    // ==========================================
    // ATRIBUTOS DEL CLIENTE
    // ==========================================

    private final String ipDelServidor; // dirección IP del servidor al que se conectará el cliente
    private final int puertoDelServidor; // puerto TCP donde está escuchando el servidor


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    // Este método se ejecuta cuando se crea un nuevo cliente TCP.
    //
    // Aquí solo guardamos:
    // - la IP del servidor
    // - el puerto del servidor

    public MonsterHitClientTCP(String ipServidor, int puertoServidor) {

        ipDelServidor = ipServidor; // guarda la IP del servidor
        puertoDelServidor = puertoServidor; // guarda el puerto del servidor
    }


    // ==========================================
    // MÉTODO PARA ENVIAR LOGIN
    // ==========================================
    // Este método se usa cuando un jugador quiere registrarse
    // o entrar a la partida.

    public String enviarLogin(String nombreDelJugador) {

        String mensajeLogin = "LOGIN|" + nombreDelJugador; // construye el mensaje de login
        return enviarMensajeAlServidor(mensajeLogin); // manda el mensaje y regresa la respuesta
    }


    // ==========================================
    // MÉTODO PARA ENVIAR GOLPE
    // ==========================================
    // Este método se usa cuando el jugador hace clic
    // en una casilla del tablero.

    public String enviarGolpe(String nombreDelJugador, int posicionGolpeada) {

        String mensajeGolpe = "HIT|" + nombreDelJugador + "|" + posicionGolpeada; // construye el mensaje del golpe
        return enviarMensajeAlServidor(mensajeGolpe); // manda el mensaje y regresa la respuesta
    }


    // ==========================================
    // MÉTODO PARA ENVIAR DESCONEXIÓN
    // ==========================================
    // Este método se usa cuando el jugador sale del juego
    // o quiere cerrar su conexión con el servidor.

    public String enviarDesconexion(String nombreDelJugador) {

        String mensajeDesconexion = "DISCONNECT|" + nombreDelJugador; // construye el mensaje de desconexión
        return enviarMensajeAlServidor(mensajeDesconexion); // manda el mensaje y regresa la respuesta
    }


    // ==========================================
    // MÉTODO GENERAL PARA ENVIAR MENSAJES
    // Este es el método más importante de la clase.
    //
    // Aquí ocurre todo el flujo TCP:
    // 1. abrir un socket
    // 2. conectarse al servidor
    // 3. crear flujo de salida
    // 4. crear flujo de entrada
    // 5. enviar mensaje
    // 6. leer respuesta
    // 7. cerrar conexión automáticamente
    //
    // Este método recibe un String con el mensaje completo
    // y regresa otro String con la respuesta del servidor.
    // ==========================================

    private String enviarMensajeAlServidor(String mensajeAEnviar) {

        try (
                Socket socketDelCliente = new Socket(ipDelServidor, puertoDelServidor);
                DataOutputStream flujoDeSalida = new DataOutputStream(socketDelCliente.getOutputStream());
                DataInputStream flujoDeEntrada = new DataInputStream(socketDelCliente.getInputStream())
        ) {
            // manda el mensaje al servidor
            flujoDeSalida.writeUTF(mensajeAEnviar);
            flujoDeSalida.flush();

            System.out.println("Mensaje enviado al servidor: " + mensajeAEnviar);

            // espera la respuesta del servidor
            String respuestaDelServidor = flujoDeEntrada.readUTF();

            System.out.println("Respuesta recibida del servidor: " + respuestaDelServidor);

            // regresa la respuesta para que la use quien llamó este método
            return respuestaDelServidor;

        } catch (IOException e) {
            System.out.println("Error en la conexión TCP del cliente: " + e.getMessage());
>>>>>>> 520fbdab6b513d72dedfebe94605c49b3b937dbf
            return "ERROR|No se pudo conectar con el servidor";
        }
    }
}