package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MonsterHitClientTCP {

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
            return "ERROR|No se pudo conectar con el servidor";
        }
    }
}