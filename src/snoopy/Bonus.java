package snoopy;

public abstract class Bonus extends Objet {
    // Constructeur
    public Bonus(int x, int y) {
        super(x, y, 1);
    }

    // Méthodes abstraites
    public abstract void activer();

    // Methodes
    @Override
    public boolean estBloquant() {
        return false;
    }
}
