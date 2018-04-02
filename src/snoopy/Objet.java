package snoopy;

/**
 * Représente un objet sur la carte
 *
 * @author julien
 */
public abstract class Objet implements Affichable {
    // Attributs
    // - coordonées
    private int x;
    private int y;
    private int z = 0; // indice z, ordre d'affichage dans les cases
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
    /**
     * Coordonée X de l'objet dans la carte
     *
     * @return coordonnée x
     */
    public int getX() {
        return x;
    }

    /**
     * Coordonée X de l'objet dans la carte
     *
     * @param x nouvelle coordonnée
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Coordonée Y de l'objet dans la carte
     *
     * @return coordonnée y
     */
    public int getY() {
        return y;
    }

    /**
     * Coordonée Y de l'objet dans la carte
     *
     * @param y nouvelle coordonnée
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Indice Z, sur une même case, l'indice z le plus petit sera affiché et premier.
     *
     * @return indice z
     */
    public int getZ() {
        return z;
    }
}
