package snoopy;

/**
 * Classe de base d'un bonus
 *
 * @author julien
 */
public abstract class Bonus extends Objet {
    // Constructeur
    public Bonus(int x, int y) {
        super(x, y, 1);
    }

    // Méthodes abstraites
    /**
     * Activation du bonus : applique ses effets au personnage
     *
     * @param perso personnage ayant activé le bonus
     */
    public abstract void activer(Perso perso);

    // Methodes
    @Override
    public boolean estBloquant() {
        return false;
    }
}
