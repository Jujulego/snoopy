package snoopy;

import java.awt.*;

/**
 * Base de tout objet affiché aussi bien en console qu'en graphique
 *
 * @author julien benjamin
 */
public interface Affichable {
    /**
     * Affichage en console
     * @return texte à afficher
     */
    String afficher();

    /**
     * Affichage en graphique
     * @param g2d
     * @param theme theme à utiliser
     * @param bx coordonnées de base
     * @param by coordonnées de base
     */
    void afficher(Graphics2D g2d, Theme theme, int bx, int by);
}
