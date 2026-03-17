package TestTime;

import Client.MonsterHitClientTCP;
import java.util.ArrayList;
import java.util.List;

public class WorkerThread extends Thread {
    private final String botId;
    private final int hitsPorHacer;
    private final String serverIp = "localhost";
    private final int serverPort = 49152;
    private final List<WorkerResult> resultados;

    public WorkerThread(int id, int hitsPorHacer) {
        this.botId = "Bot_" + id;
        this.hitsPorHacer = hitsPorHacer;
        this.resultados = new ArrayList<>(); // Memoria local del hilo
    }

    @Override
    public void run() {
        MonsterHitClientTCP tcpClient = new MonsterHitClientTCP(serverIp, serverPort);
        long t0Login = System.nanoTime(); // EVALUAR EL REGISTRO (LOGIN)
        String respLogin = tcpClient.sendLogin(botId);
        long t1Login = System.nanoTime();

        boolean loginOk = respLogin != null && respLogin.startsWith("LOGIN_OK"); // Verificamos si la respuesta indica éxito según tu protocolo
        resultados.add(new WorkerResult(botId, "LOGIN", (t1Login - t0Login), loginOk));

        if (!loginOk) return;// Si el servidor rechazó el login (ej. por saturación), este hilo termina prematuramente

        for (int i = 0; i < hitsPorHacer; i++) { // EVALUAR EL JUEGO (HITS MASIVOS)
            int randomPos = (int) (Math.random() * 9);// Simulamos un golpe en una casilla aleatoria (0 al 8)

            long t0Hit = System.nanoTime();
            String respHit = tcpClient.sendHit(botId, randomPos);
            long t1Hit = System.nanoTime();

            boolean hitOk = respHit != null && !respHit.startsWith("ERROR");  // un "ERROR" significa fallo.
            resultados.add(new WorkerResult(botId, "HIT", (t1Hit - t0Hit), hitOk)); // En tu protocolo, tanto "HIT_OK" como "MISS" significan que el servidor procesó la solicitud correctamente

            try { Thread.sleep(5); } catch (InterruptedException ignored) {} // Pausa minúscula para no agotar los puertos efímeros del SO instantáneamente
        }

        tcpClient.sendDisconnect(botId); // CIERRE DE CONEXIÓN: Libera el socket en el servidor para evitar que colapse bajo carga máxima
    }

    public List<WorkerResult> getResultados() {  // Método para que el orquestador recolecte la información al finalizar
        return resultados;
    }
}