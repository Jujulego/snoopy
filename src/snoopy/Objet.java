package snoopy;

/**
 * Représente un objet sur la carte
 */
public abstract class Objet implements Affichable {
    // Attributs
    // - coordonées
    private int x;
    private int y;
    private int z = 0; // indice z, sur une même case seul l'objet avec l'indice le plus grand sera affiché
                       // Ne pas changer !

    // Constructeur
    public Objet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Objet(int x, int y, int z) {
        this(x, y);
        this.z = z;
    }

    // Méthodes
    /**
     * Indique si l'objet empèche le passage (est bloquant) d'autres objets
     * @return true si bloquant, false sinon
     */
    public boolean estBloquant() {
        return true;
    }

    // Accesseurs
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }
}
