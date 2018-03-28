package snoopy;

/**
 * Rend un objet déplacable
 */
public interface Deplacable {
    /**
     * Gestion du déplacement de l'objet.
     * Ne gère pas la mise à jour de l'affichage
     *
     * @param carte carte sur laquelle appliquer le déplacement
     * @param theme
     * @param dx    déplacement en x
     * @param dy    déplacement en y
     * @return true si le mouvement a été réalisé, false sinon
     */
    boolean deplacer(Carte carte, Theme theme, int dx, int dy);

    /**
     * Indique s'il est possible de déplacer l'objet
     *
     * @return true si l'objet peut se déplacer
     */
    boolean deplacable();
}
