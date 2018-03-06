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
     * @param dx    déplacement en x
     * @param dy    déplacement en y
     * @return true si le mouvement à été réalisé, false sinon
     */
    boolean deplacer(Carte carte, int dx, int dy);
}
