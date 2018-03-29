package snoopy;

import java.awt.*;

public class Pause extends Bonus {
    // Constructeur
    public Pause(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public void activer() {

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
