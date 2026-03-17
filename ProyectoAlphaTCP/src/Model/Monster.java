package Model;

import java.util.Random;

//Inicializa el estado base y el generador aleatorio.
public class Monster {

    private int position; // posición actual del monstruo en el tablero
    private boolean visible; // indica si el monstruo está visible
    private final Random random; // generador de posiciones aleatorias

    public Monster() {
        this.random = new Random(); // inicializa el generador aleatorio
        this.position = -1; // -1 significa que no hay monstruo visible
        this.visible = false; // al inicio no está visible
    }

    //Retorna el índice de ubicación actual del monstruo
    public int getPosition() {
        return position; // regresa la posición actual
    }

    //Indica si el monstruo está presente en el tablero.
    public boolean isVisible() {
        return visible; // regresa si el monstruo está visible
    }

    //Genera ubicación aleatoria y activa visibilidad del monstruo.
    public int generateNewPosition() {
        position = random.nextInt(9); // genera una posición aleatoria entre 0 y 8
        visible = true; // marca al monstruo como visible
        return position; // devuelve la posición generada
    }

    //Oculta al monstruo y resetea su posición actual.
    public void hide() {
        position = -1; // elimina la posición visible
        visible = false; // oculta el monstruo
    }
}