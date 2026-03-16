package Model;

public class Player {

    private String nombre; // nombre del jugador
    private int score; // puntos acumulados en la partida
    private boolean conectado; // indica si el jugador sigue conectado

    public Player(String nombre) {
        this.nombre = nombre; // guarda el nombre del jugador
        this.score = 0; // score inicial
        this.conectado = true; // al crearse se asume conectado
    }

    // =========================
    // MÉTODOS GET
    // =========================
    public String getNombre() {
        return nombre; // regresa el nombre
    }

    public int getScore() {
        return score; // regresa el score actual
    }

    public boolean estaConectado() {
        return conectado; // indica si el jugador sigue conectado
    }

    // =========================
    // MÉTODOS SET
    // =========================
    public void setConectado(boolean estado) {
        conectado = estado; // cambia el estado de conexión
    }


    // =========================
    // LÓGICA DEL JUEGO
    // =========================
    public void sumarPunto() {
        score++; // incrementa el score en 1
    }

    public void reiniciarScore() {
        score = 0; // reinicia el score del jugador
    }

    // =========================
    // INFORMACIÓN DEL OBJETO
    // =========================
    @Override
    public String toString() {
        return "Jugador: " + nombre + " | Score: " + score + " | Conectado: " + conectado;
    }
}