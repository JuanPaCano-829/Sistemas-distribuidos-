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
            return "ERROR|No se pudo conectar con el servidor";
        }
    }
}