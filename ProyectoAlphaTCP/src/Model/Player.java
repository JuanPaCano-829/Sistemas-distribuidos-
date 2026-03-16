package Model;

// ==========================================
// CLASE PLAYER
// Esta clase representa a un jugador dentro del juego
// Aquí guardamos toda la información de cada jugador
// ==========================================
public class Player {

    // ==========================================
    // ATRIBUTOS DEL JUGADOR
    // ==========================================

    private String nombre;     // nombre del jugador
    private int score;         // puntos que ha ganado en la partida
    private boolean conectado; // indica si el jugador está conectado actualmente


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    // Este método se ejecuta cuando se crea un jugador nuevo.
    // Inicializa el nombre y comienza con score = 0

    public Player(String nombre) {

        this.nombre = nombre;   // guarda el nombre del jugador
        this.score = 0;         // el score inicia en 0
        this.conectado = true;  // cuando se crea asumimos que está conectado
    }


    // ==========================================
    // MÉTODOS GET (OBTENER INFORMACIÓN)
    // ==========================================

    // Regresa el nombre del jugador
    public String getNombre() {
        return nombre;
    }

    // Regresa el score actual del jugador
    public int getScore() {
        return score;
    }

    // Regresa si el jugador está conectado o no
    public boolean estaConectado() {
        return conectado;
    }


    // ==========================================
    // MÉTODOS SET (MODIFICAR INFORMACIÓN)
    // ==========================================

    // Cambia el estado de conexión del jugador
    public void setConectado(boolean estado) {
        conectado = estado;
    }


    // ==========================================
    // MÉTODOS DE LÓGICA DEL JUEGO
    // ==========================================

    // Suma un punto al score del jugador
    public void sumarPunto() {

        score++; // incrementa el score en 1
    }


    // Reinicia el score del jugador
    // Esto se usa cuando termina una partida
    public void reiniciarScore() {

        score = 0;
    }


    // ==========================================
    // MÉTODO PARA MOSTRAR INFORMACIÓN
    // ==========================================
    // Este método regresa un texto con la información
    // del jugador, útil para imprimir en consola

    @Override
    public String toString() {

        return "Jugador: " + nombre +
                " | Score: " + score +
                " | Conectado: " + conectado;
    }
}