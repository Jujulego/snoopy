package snoopy;

import java.awt.*;

/**
 * Bonus de pause !!!
 * Arrête tout (y compris le timer !) sauf les mouvements du personnage qui l'a récupéré
 */
public class Pause extends Bonus {
    // Constantes
    public static final int DUREE = 128;

    // Constructeur
    public Pause(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public void activer(Perso perso) {
        perso.activerPause();
    }

    @Override
    public String afficher() {
        return "BP";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        g2d.drawImage(theme.getPauseImg(),
                bx + getX() * Moteur.LARG_IMG,
                by + getY() * Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }
}
