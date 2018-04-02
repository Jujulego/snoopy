package snoopy;

/**
 * Rend un objet poussable
 */
public interface Poussable {
    /**
     * Pousse l'objet (si possible)
     *
     * @param carte carte actuelle
     * @param dx déplacement en x
     * @param dy déplacement en y
     * @return true si l'objet à pu être poussé
     */
    boolean pousser(Carte carte, int dx, int dy);

    /**
     * Indique si l'objet est poussable
     *
     * @return true si l'objet est poussable
     */
    boolean poussable();
}
