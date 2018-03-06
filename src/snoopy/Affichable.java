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
     */
    void afficher(Graphics2D g2d);
}
