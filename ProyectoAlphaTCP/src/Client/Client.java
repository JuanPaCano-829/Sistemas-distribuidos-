package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    // Usamos un método estático para poder llamarlo desde cualquier parte sin instanciarlo
    public static void enviarMensaje(String ipDestino, int puertoDestino, String mensaje) {

        // Lo metemos en un Hilo (Thread) para que si el internet de tu amigo está lento,
        // tu ventana del juego no se quede congelada esperando.
        new Thread(() -> {
            try (Socket socket = new Socket(ipDestino, puertoDestino)) {

                // Preparamos el paquete de datos
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // Enviamos el texto
                out.writeUTF(mensaje);
                System.out.println("✅ MENSAJE ENVIADO a " + ipDestino + ":" + puertoDestino + " -> " + mensaje);

                // Al salir del bloque 'try', el socket se cierra automáticamente (¡Repartidor se retira!)

            } catch (IOException e) {
                System.out.println("❌ Error al enviar mensaje a " + ipDestino + ": " + e.getMessage());
            }
        }).start();
    }
}