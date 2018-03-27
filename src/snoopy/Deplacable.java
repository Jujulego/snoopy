package snoopy;

/**
 * Rend un objet dÃ©placable
 */
public interface Deplacable {
    /**
     * Gestion du déplacement de l'objet.
     * Ne gère pas la mise à jour de l'affichage
     *
     * @param carte carte sur laquelle appliquer le dÃ©placement
     * @param theme
     * @param dx    déplacement en x
     * @param dy    déplacement en y
     * @return true si le mouvement a été réalisé, false sinon
     */
    boolean deplacer(Carte carte, Theme theme, int dx, int dy);
}
