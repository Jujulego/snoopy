package snoopy;

/**
 * Base de la gestion des animations
 *
 * @author julien benjamin
 */
public interface Animation {
    /**
     * Cette méthode doit ce limiter à l'évolution de l'animation (changement des coordonnées).
     * Ne permet pas de rafraichir l'affichage.
     * Appelée régulièrement.
     *
     * @param carte carte affichée
     * @param theme thème à utiliser
     */
    void animer(Carte carte, Theme theme);

    /**
     * Indique si une animation est en cours
     *
     * @return true si une animation est en cours, false sinon
     */
    boolean animation();
}
