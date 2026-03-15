package ClientServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Heredamos de Thread para que escuche en el fondo sin congelar tu ventana
public class Server extends Thread {

    private int puerto;
    private boolean escuchando;

    public Server(int puerto) {
        this.puerto = puerto;
        this.escuchando = true;
    }

    @Override
    public void run() {
        // Abrimos el ServerSocket en el puerto indicado (ej. 5000)
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("✅ NODO INICIADO: Escuchando en el puerto " + puerto + "...");

            while (escuchando) {
                // El programa se "pausa" en esta línea hasta que alguien se conecta
                Socket socketCliente = serverSocket.accept();

                // Alguien se conectó, preparamos el canal para leer su mensaje
                DataInputStream in = new DataInputStream(socketCliente.getInputStream());

                // Leemos el texto que nos mandaron
                String mensajeRecibido = in.readUTF();
                System.out.println("📩 MENSAJE ENTRANTE: " + mensajeRecibido);

                // En P2P, recibimos el golpe/mensaje y cerramos rápido la conexión
                socketCliente.close();
            }
        } catch (IOException e) {
            System.out.println("❌ Error en el servidor TCP: " + e.getMessage());
        }
    }
}