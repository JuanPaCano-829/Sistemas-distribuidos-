package Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//Inicializa componentes y variables de estado del juego.
public class GameState {

    private static final int POINTS_TO_WIN = 5; // define los puntos necesarios para ganar

    private final Map<String, Player> players; // guarda todos los jugadores registrados
    private final Monster monster; // guarda el monstruo compartido
    private boolean roundActive; // indica si hay una ronda activa
    private boolean gameStarted; // indica si la partida ya fue iniciada desde el lobby
    private String winner; // guarda el nombre del ganador actual

    public GameState() {
        players = new HashMap<>(); // crea el mapa de jugadores
        monster = new Monster(); // crea el monstruo del juego
        roundActive = false; // al inicio no hay ronda activa
        gameStarted = false; // al inicio el juego no ha comenzado
        winner = null; // al inicio no hay ganador
    }

    //Registra un nuevo jugador o marca su reconexión.
    public synchronized Player addOrReconnectPlayer(String playerName) {
        String cleanName = playerName.trim(); // limpia espacios laterales
        Player player = players.get(cleanName); // busca al jugador por nombre

        if (player == null) {
            player = new Player(cleanName);
            players.put(cleanName, player); // guarda al nuevo jugador en el mapa
        } else {
            player.setConnected(true); // si ya existía lo marca como reconectado
        }

        return player; // regresa el jugador creado o reconectado
    }

    //Busca y devuelve un objeto jugador mediante su nombre.
    public synchronized Player getPlayer(String playerName) {
        return players.get(playerName); // regresa el jugador buscado
    }
    //Activa el inicio oficial de la partida.
    public synchronized void startGame() {
        gameStarted = true;
    }
    //Verifica si la partida se encuentra actualmente activa
    public synchronized boolean isGameStarted() {
        return gameStarted; // regresa si la partida ya fue iniciada
    }

    //Genera posición del monstruo e inicia nueva ronda.
    public synchronized int startNewRound() {
        int position = monster.generateNewPosition(); // genera una nueva posición del monstruo
        roundActive = true; // activa la ronda actual
        return position; // regresa la nueva posición
    }
    //Indica si un jugador ya ganó la partida.
    public synchronized boolean hasWinner() {
        return winner != null; // indica si ya hay ganador
    }

    public synchronized String getWinner() {
        return winner; // regresa el nombre del ganador
    }

    //Obtiene el puntaje actual de un jugador determinado.
    public synchronized int getPlayerScore(String playerName) {
        Player player = players.get(playerName); // busca al jugador por nombre
        return player == null ? 0 : player.getScore(); // devuelve su score o cero si no existe
    }

    //Genera cadena con nombres y puntos de jugadores activos.
    public synchronized String getConnectedPlayersMessage() {
        StringBuilder builder = new StringBuilder();
        for (Player player : players.values()) {
            if (player.isConnected()) {
                if (!builder.isEmpty()) builder.append(",");
                builder.append(player.getName()).append(":").append(player.getScore());
            }
        }
        return builder.toString();
    }

    //Evalúa si un golpe es válido
    private boolean isValidHit(String playerName, int hitPosition) {
        Player player = players.get(playerName); // obtiene al jugador que golpeó
        if (player == null) return false; // invalida si no existe el jugador
        if (!player.isConnected()) return false; // invalida si está desconectado
        if (!gameStarted) return false; // invalida si la partida todavía no inicia
        if (!roundActive) return false; // invalida si no hay ronda activa
        if (!monster.isVisible()) return false; // invalida si el monstruo ya no está visible
        return hitPosition == monster.getPosition(); // valida que la casilla coincida
    }

    //Registra aciertos, actualiza puntajes y comprueba condiciones finales.
    public synchronized boolean processHit(String playerName, int hitPosition) {
        if (!isValidHit(playerName, hitPosition)) return false; // corta si el golpe es inválido

        Player player = players.get(playerName); // obtiene al jugador que acertó
        player.addPoint(); // suma un punto al jugador

        monster.hide(); // oculta el monstruo después de un acierto
        roundActive = false; // cierra la ronda actual

        if (player.getScore() >= POINTS_TO_WIN) winner = player.getName(); // revisa si ya ganó la partida

        return true; // confirma que el golpe fue válido
    }

    //Limpia puntajes y estados para iniciar otra partida.
    public synchronized void resetMatch() {
        for (Player player : players.values()) player.resetScore(); // reinicia el score de todos los jugadores

        monster.hide(); // oculta al monstruo
        roundActive = false; // deja la ronda inactiva
        winner = null; // elimina el ganador actual
    }


}