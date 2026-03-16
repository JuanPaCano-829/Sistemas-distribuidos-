package Model;
// Código para la clase Monster
import java.util.Random;

public class Monster {

    private int posicion; // posición del monstruo en el tablero (0-8)
    private boolean visible; // indica si el monstruo está visible
    private Random random; // generador de números aleatorios

    public Monster() {
        random = new Random(); // inicializa el generador aleatorio
        posicion = -1; // -1 significa que no hay monstruo en el tablero
        visible = false; // al inicio no está visible
    }

    // =========================
    // MÉTODOS GET
    // =========================
    public int getPosicion() {
        return posicion; // regresa la posición actual
    }

    public boolean estaVisible() {
        return visible; // indica si el monstruo está visible
    }

    // =========================
    // MÉTODOS DEL JUEGO
    // =========================
    public int generarNuevaPosicion() {
        posicion = random.nextInt(9); // genera posición aleatoria entre 0 y 8
        visible = true; // el monstruo aparece en el tablero
        return posicion;
    }

    public void ocultar() {
        posicion = -1; // elimina el monstruo del tablero
        visible = false; // deja de estar visible
    }

    // =========================
    // INFORMACIÓN DEL OBJETO
    // =========================
    @Override
    public String toString() {
        return "Monstruo en posicion: " + posicion + " | Visible: " + visible;
    }
}