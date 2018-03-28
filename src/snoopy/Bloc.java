package snoopy;

/**
 * Base commumne aux blocs, simplifie les tests de collision
 *
 * @author julien
 */
public abstract class Bloc extends Objet {
    // Constructeur
    /**
     * Construit le bloc
     *
     * @param x position dans la carte
     * @param y position dans la carte
     */
    public Bloc(int x, int y) {
        super(x, y, 2);
    }
}
