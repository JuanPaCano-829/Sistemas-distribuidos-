package TestTime;

public class WorkerResult {
    public final String idHilo;
    public final String tipoOperacion;
    public final long tiempoRespuestaNs; // <-- ¡ESTO DEBE SER long, NO String!
    public final boolean exitoso;

    public WorkerResult(String idHilo, String tipoOperacion, long tiempoRespuestaNs, boolean exitoso) {
        this.idHilo = idHilo;
        this.tipoOperacion = tipoOperacion;
        this.tiempoRespuestaNs = tiempoRespuestaNs; // <-- Aquí también debe ser long
        this.exitoso = exitoso;
    }
}