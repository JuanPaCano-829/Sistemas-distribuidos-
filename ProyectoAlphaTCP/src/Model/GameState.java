package Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public synchronized Player addOrReconnectPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) return null; // evita nombres vacíos

        String cleanName = playerName.trim(); // limpia espacios laterales
        Player player = players.get(cleanName); // busca al jugador por nombre

        if (player == null) {
            player = new Player(cleanName); // crea un jugador nuevo si no existía
            players.put(cleanName, player); // guarda al nuevo jugador en el mapa
        } else {
            player.setConnected(true); // si ya existía lo marca como reconectado
        }

        return player; // regresa el jugador creado o reconectado
    }

    public synchronized Player getPlayer(String playerName) {
        return players.get(playerName); // regresa el jugador buscado
    }

    public synchronized Map<String, Player> getPlayers() {
        return Collections.unmodifiableMap(new HashMap<>(players)); // regresa una copia de solo lectura
    }

    public synchronized void startGame() {
        gameStarted = true; // marca que la partida ya fue iniciada desde el lobby
    }

    public synchronized boolean isGameStarted() {
        return gameStarted; // regresa si la partida ya fue iniciada
    }

    public synchronized int startNewRound() {
        int position = monster.generateNewPosition(); // genera una nueva posición del monstruo
        roundActive = true; // activa la ronda actual
        return position; // regresa la nueva posición
    }

    public synchronized int getMonsterPosition() {
        return monster.getPosition(); // regresa la posición actual del monstruo
    }

    public synchronized boolean isRoundActive() {
        return roundActive; // regresa si hay una ronda activa
    }

    public synchronized boolean hasWinner() {
        return winner != null; // indica si ya hay ganador
    }

    public synchronized String getWinner() {
        return winner; // regresa el nombre del ganador
    }

    public synchronized int getPlayerScore(String playerName) {
        Player player = players.get(playerName); // busca al jugador por nombre
        return player == null ? 0 : player.getScore(); // devuelve su score o cero si no existe
    }

    public synchronized String getConnectedPlayersMessage() {
        StringBuilder builder = new StringBuilder(); // construye el texto de jugadores conectados

        for (Player player : players.values()) {
            if (player.isConnected()) {
                if (builder.length() > 0) builder.append(","); // separa nombres con coma
                builder.append(player.getName()); // agrega el nombre del jugador conectado
            }
        }

        return builder.toString(); // regresa la lista final
    }

    private boolean isValidHit(String playerName, int hitPosition) {
        Player player = players.get(playerName); // obtiene al jugador que golpeó
        if (player == null) return false; // invalida si no existe el jugador
        if (!player.isConnected()) return false; // invalida si está desconectado
        if (!gameStarted) return false; // invalida si la partida todavía no inicia
        if (!roundActive) return false; // invalida si no hay ronda activa
        if (!monster.isVisible()) return false; // invalida si el monstruo ya no está visible
        return hitPosition == monster.getPosition(); // valida que la casilla coincida
    }

    public synchronized boolean processHit(String playerName, int hitPosition) {
        if (!isValidHit(playerName, hitPosition)) return false; // corta si el golpe es inválido

        Player player = players.get(playerName); // obtiene al jugador que acertó
        player.addPoint(); // suma un punto al jugador

        monster.hide(); // oculta el monstruo después de un acierto
        roundActive = false; // cierra la ronda actual

        if (player.getScore() >= POINTS_TO_WIN) winner = player.getName(); // revisa si ya ganó la partida

        return true; // confirma que el golpe fue válido
    }

    public synchronized void resetMatch() {
        for (Player player : players.values()) player.resetScore(); // reinicia el score de todos los jugadores

        monster.hide(); // oculta al monstruo
        roundActive = false; // deja la ronda inactiva
        gameStarted = false; // regresa al lobby para que vuelvan a iniciar manualmente
        winner = null; // elimina el ganador actual
    }

    @Override
    public synchronized String toString() {
        StringBuilder text = new StringBuilder("===== GAME STATE =====\n"); // crea encabezado de depuración

        for (Player player : players.values()) text.append(player).append("\n"); // agrega cada jugador al texto

        text.append(monster).append("\n"); // agrega el estado del monstruo
        text.append("Round active: ").append(roundActive).append("\n"); // agrega estado de la ronda
        text.append("Game started: ").append(gameStarted).append("\n"); // agrega estado de inicio de juego
        text.append("Winner: ").append(winner).append("\n"); // agrega ganador actual

        return text.toString(); // regresa el texto completo
    }
}