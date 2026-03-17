package TestTime;

import Client.MonsterHitClientTCP;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String[] args) {
        int clientesConcurrentes = 50;
        int repeticiones = 10;
        int hitsPorCliente = 10;

        List<String> lineasCsv = new ArrayList<>(); // Almacena los registros en memoria
        lineasCsv.add("ConfiguracionClientes,Repeticion,ID_Hilo,TipoOperacion,TiempoRespuesta (Nano Sec),Exitoso");

        MonsterHitClientTCP adminClient = new MonsterHitClientTCP("localhost", 49152);
        adminClient.sendLogin("AdminTester");
        adminClient.sendStartGame("AdminTester"); // Inicia la partida para que el servidor acepte y valide los HITs

        for (int repeticion = 1; repeticion <= repeticiones; repeticion++) {
            List<WorkerThread> hilosActivos = new ArrayList<>();

            for (int i = 0; i < clientesConcurrentes; i++) {
                WorkerThread t = new WorkerThread(i, hitsPorCliente);
                hilosActivos.add(t);
                t.start();
            }

            for (WorkerThread t : hilosActivos) {
                try {
                    t.join(); // Bloquea el hilo principal hasta que este WorkerThread termine

                    for (WorkerResult res : t.getResultados()) { // Extrae los datos y los formatea para el CSV
                        String linea = clientesConcurrentes + "," + repeticion + "," + res.idHilo + "," + res.tipoOperacion + "," + res.tiempoRespuestaNs + "," + res.exitoso;
                        lineasCsv.add(linea);
                    }
                } catch (InterruptedException e) {
                    System.err.println("Hilo interrumpido: " + e.getMessage());
                }
            }
        }

        adminClient.sendDisconnect("AdminTester"); // Limpieza al terminar todas las repeticiones

        String nombreArchivo = "resultados_estresamiento_" + clientesConcurrentes + "_clientes.csv";
        guardarEnCSV(lineasCsv, nombreArchivo);
    }

    private static void guardarEnCSV(List<String> lineas, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (String linea : lineas) {
                writer.println(linea);
            }
            System.out.println("Exportación exitosa a " + nombreArchivo); // Confirmación final silenciada
        } catch (IOException e) {
            System.err.println("Error de I/O al guardar el CSV: " + e.getMessage());
        }
    }
}