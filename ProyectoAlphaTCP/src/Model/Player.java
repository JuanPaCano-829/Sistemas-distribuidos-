package Model;

public class Player {

    private final String name; // guarda el nombre del jugador
    private int score; // guarda el puntaje actual
    private boolean connected; // indica si el jugador está conectado

    public Player(String name) {
        this.name = name; // asigna el nombre del jugador
        this.score = 0; // inicia score en cero
        this.connected = true; // al crearse se considera conectado
    }

    public String getName() {
        return name; // regresa el nombre del jugador
    }

    public int getScore() {
        return score; // regresa el puntaje actual
    }

    public boolean isConnected() {
        return connected; // regresa el estado de conexión
    }

    public void setConnected(boolean connected) {
        this.connected = connected; // cambia el estado de conexión
    }

    public void addPoint() {
        score++; // incrementa el score en uno
    }

    public void resetScore() {
        score = 0; // reinicia el score a cero
    }

    @Override
    public String toString() {
        return "Player: " + name + " | Score: " + score + " | Connected: " + connected; // regresa texto descriptivo
    }
}