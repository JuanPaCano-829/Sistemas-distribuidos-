package Model;

import java.util.Random;

// ==========================================
// CLASE MONSTER
// Esta clase representa al monstruo/topo que aparece
// en el tablero del juego
// ==========================================

public class Monster {

    // ==========================================
    // ATRIBUTOS DEL MONSTRUO
    // ==========================================

    private int posicion;     // posición donde aparece el monstruo (0 - 8)
    private boolean visible;  // indica si el monstruo está visible o no

    // generador de números aleatorios
    private Random random;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    // Se ejecuta cuando se crea un monstruo nuevo

    public Monster() {

        random = new Random(); // inicializa el generador aleatorio
        posicion = -1;         // -1 significa que no hay monstruo visible
        visible = false;       // al inicio el monstruo no está visible
    }


    // ==========================================
    // MÉTODOS GET
    // ==========================================

    // regresa la posición actual del monstruo
    public int getPosicion() {
        return posicion;
    }

    // regresa si el monstruo está visible
    public boolean estaVisible() {
        return visible;
    }


    // ==========================================
    // MÉTODOS DEL JUEGO
    // ==========================================

    // Este método genera una nueva posición aleatoria
    // para el monstruo dentro del tablero (0 - 8)

    public int generarNuevaPosicion() {

        posicion = random.nextInt(9); // genera número entre 0 y 8
        visible = true;               // el monstruo ahora está visible

        return posicion; // regresa la posición generada
    }


    // Este método oculta el monstruo del tablero
    // se usa cuando un jugador le pega

    public void ocultar() {

        posicion = -1;  // ya no hay monstruo en el tablero
        visible = false;
    }


    // ==========================================
    // MÉTODO PARA MOSTRAR INFORMACIÓN
    // ==========================================

    @Override
    public String toString() {

        return "Monstruo en posicion: " + posicion +
                " | Visible: " + visible;
    }
}