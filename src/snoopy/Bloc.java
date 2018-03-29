package snoopy;

import java.awt.*;

/**
 * Base commumne aux blocs, simplifie les tests de collision
 *
 * @author julien
 */
public class Bloc extends Objet {
    // Constructeur
    /**
     * Construit le bloc
     *
     * @param x position dans la carte
     * @param y position dans la carte
     */
    public Bloc(int x, int y) {
        super(x, y, 2);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "XX";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        g2d.drawImage(theme.getBlocImg(1),
                bx + getX() * Moteur.LARG_IMG, by + getY() * Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }
}
