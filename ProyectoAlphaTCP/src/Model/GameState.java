package Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// ==========================================
// CLASE GAMESTATE
// Esta clase representa el estado global del juego.
//
// Aquí se guarda toda la información importante de la partida:
// - los jugadores registrados
// - el monstruo actual
// - si la ronda actual sigue activa
// - quién ganó la partida
// To handle dynamic players and persistent scores, we must utilize the GameState class
// MonsterHitServerTCP handles the incoming hits and updates the gamestate obj
// ==========================================

public class GameState {

    // ==========================================
    // CONSTANTES DEL JUEGO
    // ==========================================

    private static final int PUNTOS_NECESARIOS_PARA_GANAR = 5; // cantidad de puntos para ganar la partida

    // ==========================================
    // ATRIBUTOS PRINCIPALES DEL JUEGO
    // ==========================================

    private final Map<String, Player> jugadoresRegistrados; // guarda a todos los jugadores usando su nombre como llave
    private final Monster monstruoActual;                   // representa al monstruo/topo actual del juego
    private boolean rondaEnCurso;                           // indica si la ronda actual sigue abierta para recibir golpes
    private String nombreGanador;                           // guarda el nombre del ganador de la partida si ya existe uno


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    // Este método se ejecuta cuando se crea un nuevo GameState.
    // Inicializa todas las estructuras del juego.

    public GameState() {

        jugadoresRegistrados = new HashMap<>(); // crea el mapa vacío donde se guardarán los jugadores
        monstruoActual = new Monster();         // crea el monstruo que se usará en la partida
        rondaEnCurso = false;                   // al inicio todavía no hay una ronda activa
        nombreGanador = null;                   // al inicio no existe ganador
    }


    // ==========================================
    // MÉTODOS DE JUGADORES
    // ==========================================

    // Este método agrega un jugador nuevo al juego
    // solamente si todavía no existe en el mapa
    //
    // Regresa:
    // true  -> si sí se agregó
    // false -> si ya existía
    public boolean agregarJugador(String nombreJugador) {

        if (jugadoresRegistrados.containsKey(nombreJugador)) { // revisa si el jugador ya existe
            return false; // no lo vuelve a agregar
        }

        jugadoresRegistrados.put(nombreJugador, new Player(nombreJugador)); // crea y guarda al nuevo jugador
        return true; // indica que sí se agregó correctamente
    }


    // Este método regresa el objeto Player de un jugador específico
    public Player obtenerJugador(String nombreJugador) {

        return jugadoresRegistrados.get(nombreJugador); // devuelve el jugador buscado o null si no existe
    }


    // Este método regresa una vista de solo lectura
    // del mapa de jugadores registrados
    public Map<String, Player> obtenerJugadoresRegistrados() {

        return Collections.unmodifiableMap(jugadoresRegistrados);
    }


    // ==========================================
    // MÉTODOS DE LA RONDA Y DEL MONSTRUO
    // ==========================================

    // Este método inicia una nueva ronda.
    // Hace que el monstruo aparezca en una nueva posición aleatoria
    // y marca la ronda como activa.
    public int iniciarNuevaRonda() {

        int nuevaPosicionDelMonstruo = monstruoActual.generarNuevaPosicion(); // genera posición aleatoria entre 0 y 8
        rondaEnCurso = true; // marca que ahora sí hay una ronda activa

        return nuevaPosicionDelMonstruo; // regresa la posición donde apareció el monstruo
    }


    // Este método regresa la posición actual del monstruo
    public int obtenerPosicionActualDelMonstruo() {

        return monstruoActual.getPosicion();
    }


    // Este método regresa si la ronda sigue activa
    public boolean isRondaEnCurso() {

        return rondaEnCurso;
    }


    // Este método regresa el monstruo actual
    public Monster obtenerMonstruoActual() {

        return monstruoActual;
    }


    // ==========================================
    // MÉTODO PRIVADO PARA VALIDAR UN GOLPE
    // ==========================================
    // Este método concentra toda la validación previa
    // antes de aceptar un golpe como correcto

    private boolean esGolpeValido(String nombreJugador, int posicionGolpeadaPorElJugador) {

        // revisa si el jugador existe en la partida
        if (!jugadoresRegistrados.containsKey(nombreJugador)) {
            return false;
        }

        // revisa si la ronda sigue activa
        if (!rondaEnCurso) {
            return false;
        }

        // revisa si el monstruo todavía está visible
        if (!monstruoActual.estaVisible()) {
            return false;
        }

        // revisa si la posición golpeada coincide con la posición real del monstruo
        return posicionGolpeadaPorElJugador == monstruoActual.getPosicion();
    }


    // ==========================================
    // MÉTODO PARA VALIDAR Y PROCESAR UN GOLPE
    // ==========================================
    // Este método es de los más importantes del juego.
    //
    // Aquí se revisa:
    // - si el jugador existe
    // - si la ronda sigue activa
    // - si el monstruo sigue visible
    // - si la posición golpeada es la correcta
    //
    // Si el golpe es válido:
    // - se suma un punto al jugador
    // - se oculta el monstruo
    // - se cierra la ronda
    // - se revisa si ya existe un ganador
    //
    // Se usa synchronized para evitar que dos jugadores
    // ganen la misma ronda al mismo tiempo.

    public synchronized boolean procesarGolpeDelJugador(String nombreJugador, int posicionGolpeadaPorElJugador) {

        // valida si el golpe cumple todas las condiciones necesarias
        if (!esGolpeValido(nombreJugador, posicionGolpeadaPorElJugador)) {
            return false; // si no es válido, termina aquí
        }

        // si llega hasta aquí, el golpe sí fue correcto
        Player jugadorQueGolpeo = jugadoresRegistrados.get(nombreJugador); // obtiene al jugador que hizo el golpe
        jugadorQueGolpeo.sumarPunto(); // suma un punto a su score

        monstruoActual.ocultar(); // oculta el monstruo porque ya lo golpearon
        rondaEnCurso = false;     // cierra la ronda actual para que nadie más gane ese mismo monstruo

        // revisa si el jugador ya alcanzó los puntos para ganar
        if (jugadorQueGolpeo.getScore() >= PUNTOS_NECESARIOS_PARA_GANAR) {
            nombreGanador = jugadorQueGolpeo.getNombre(); // guarda el nombre del ganador
        }

        return true; // indica que el golpe fue válido
    }


    // ==========================================
    // MÉTODOS DE GANADOR
    // ==========================================

    // Este método indica si ya existe un ganador
    public boolean hayGanadorEnLaPartida() {

        return nombreGanador != null;
    }


    // Este método regresa el nombre del ganador actual
    public String obtenerNombreGanador() {

        return nombreGanador;
    }


    // ==========================================
    // MÉTODO PARA REINICIAR TODA LA PARTIDA
    // ==========================================
    // Este método se usa cuando alguien gana
    // y queremos volver a empezar desde cero.
    //
    // Reinicia:
    // - score de todos los jugadores
    // - monstruo actual
    // - ronda activa
    // - ganador

    public void reiniciarPartidaCompleta() {

        // recorre todos los jugadores y les pone el score en 0
        for (Player jugadorActual : jugadoresRegistrados.values()) {
            jugadorActual.reiniciarScore();
        }

        monstruoActual.ocultar(); // oculta el monstruo del tablero
        rondaEnCurso = false;     // deja la ronda cerrada
        nombreGanador = null;     // elimina al ganador actual
    }


    // ==========================================
    // MÉTODO PARA MOSTRAR EL ESTADO DEL JUEGO
    // ==========================================
    // Este método sirve para imprimir en consola
    // toda la información del juego de forma legible

    @Override
    public String toString() {

        String textoEstadoDelJuego = "===== ESTADO ACTUAL DEL JUEGO =====\n";

        // agrega la información de cada jugador
        for (Player jugadorActual : jugadoresRegistrados.values()) {
            textoEstadoDelJuego += jugadorActual.toString() + "\n";
        }

        // agrega la información del monstruo actual
        textoEstadoDelJuego += monstruoActual.toString() + "\n";

        // agrega si la ronda sigue activa o no
        textoEstadoDelJuego += "Ronda en curso: " + rondaEnCurso + "\n";

        // agrega el nombre del ganador si existe
        textoEstadoDelJuego += "Ganador actual: " + nombreGanador + "\n";

        return textoEstadoDelJuego;
    }
}