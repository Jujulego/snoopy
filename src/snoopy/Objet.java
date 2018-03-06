package snoopy;

/** Classe Objet
 * Base pour les blocks et le personnage
 */
public abstract class Objet implements Affichable {
    // Attributs
    private int x;
    private int y;
    private int z = 0;

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
    public boolean estBloquant() {
        return true;
    }

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
    public void setZ(int z) {
        this.z = z;
    }
}
