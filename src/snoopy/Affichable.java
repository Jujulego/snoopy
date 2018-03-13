package snoopy;

import java.awt.*;

/**
 * Base de tout objet affiché aussi bien en console qu'en graphique
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
     * @param theme
     * @param bx
     * @param by
     */
    void afficher(Graphics2D g2d, Theme theme, int bx, int by);
}
