package Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    private static final int PUNTOS_PARA_GANAR = 5; // puntos necesarios para ganar la partida

    private final Map<String, Player> jugadores; // jugadores registrados
    private final Monster monstruo; // monstruo actual del juego
    private boolean rondaActiva; // indica si la ronda está abierta
    private String ganador; // nombre del jugador ganador

    public GameState() {
        jugadores = new HashMap<>(); // mapa donde se guardan los jugadores
        monstruo = new Monster(); // crea el monstruo del juego
        rondaActiva = false; // al inicio no hay ronda activa
        ganador = null; // al inicio no hay ganador
    }
    // =========================
    // MÉTODOS DE JUGADORES
    // =========================
    public boolean agregarJugador(String nombreJugador) {
        if (jugadores.containsKey(nombreJugador)) return false; // evita duplicados

        jugadores.put(nombreJugador, new Player(nombreJugador)); // crea y guarda el jugador
        return true;
    }

    public Player obtenerJugador(String nombreJugador) {
        return jugadores.get(nombreJugador); // regresa el jugador o null
    }

    public Map<String, Player> obtenerJugadores() {
        return Collections.unmodifiableMap(jugadores); // vista de solo lectura
    }

    // =========================
    // MÉTODOS DE RONDA
    // =========================
    public int iniciarNuevaRonda() {
        int posicion = monstruo.generarNuevaPosicion(); // genera posición aleatoria del monstruo
        rondaActiva = true; // abre la ronda
        return posicion;
    }

    public int obtenerPosicionMonstruo() {
        return monstruo.getPosicion(); // regresa la posición actual
    }

    public boolean isRondaActiva() {
        return rondaActiva; // indica si la ronda sigue activa
    }

    public Monster obtenerMonstruo() {
        return monstruo; // regresa el monstruo actual
    }

    // =========================
    // VALIDACIÓN DE GOLPES
    // =========================
    private boolean esGolpeValido(String nombreJugador, int posicionGolpeada) {
        if (!jugadores.containsKey(nombreJugador)) return false; // jugador inexistente
        if (!rondaActiva) return false; // ronda cerrada
        if (!monstruo.estaVisible()) return false; // monstruo ya oculto
        return posicionGolpeada == monstruo.getPosicion(); // valida posición correcta
    }

    public synchronized boolean procesarGolpe(String nombreJugador, int posicionGolpeada) {
        if (!esGolpeValido(nombreJugador, posicionGolpeada)) return false; // golpe inválido

        Player jugador = jugadores.get(nombreJugador); // obtiene el jugador
        jugador.sumarPunto(); // suma punto al jugador

        monstruo.ocultar(); // oculta el monstruo
        rondaActiva = false; // cierra la ronda

        if (jugador.getScore() >= PUNTOS_PARA_GANAR) ganador = jugador.getNombre(); // revisa si ya ganó

        return true;
    }

    // =========================
    // GANADOR
    // =========================
    public boolean hayGanador() {
        return ganador != null; // verifica si ya hay ganador
    }

    public String obtenerGanador() {
        return ganador; // regresa el nombre del ganador
    }

    // =========================
    // REINICIO DEL JUEGO
    // =========================
    public void reiniciarPartida() {
        for (Player jugador : jugadores.values()) jugador.reiniciarScore(); // reinicia score de todos

        monstruo.ocultar(); // oculta el monstruo
        rondaActiva = false; // cierra la ronda
        ganador = null; // elimina ganador
    }

    // =========================
    // ESTADO DEL JUEGO
    // =========================
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("===== ESTADO DEL JUEGO =====\n");
        for (Player jugador : jugadores.values())
            texto.append(jugador).append("\n"); // agrega info de jugadores

        texto.append(monstruo).append("\n"); // agrega info del monstruo
        texto.append("Ronda activa: ").append(rondaActiva).append("\n"); // estado de la ronda
        texto.append("Ganador: ").append(ganador).append("\n"); // ganador actual

        return texto.toString();
    }
}