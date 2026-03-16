package Model;

import java.util.Random;

public class Monster {

    private int position; // posición actual del monstruo en el tablero
    private boolean visible; // indica si el monstruo está visible
    private final Random random; // generador de posiciones aleatorias

    public Monster() {
        this.random = new Random(); // inicializa el generador aleatorio
        this.position = -1; // -1 significa que no hay monstruo visible
        this.visible = false; // al inicio no está visible
    }

    public int getPosition() {
        return position; // regresa la posición actual
    }

    public boolean isVisible() {
        return visible; // regresa si el monstruo está visible
    }

    public int generateNewPosition() {
        position = random.nextInt(9); // genera una posición aleatoria entre 0 y 8
        visible = true; // marca al monstruo como visible
        return position; // devuelve la posición generada
    }

    public void hide() {
        position = -1; // elimina la posición visible
        visible = false; // oculta el monstruo
    }

    @Override
    public String toString() {
        return "Monster position: " + position + " | Visible: " + visible; // regresa texto descriptivo
    }
}