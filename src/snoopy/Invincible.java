package snoopy;

import java.awt.*;

public class Invincible extends Bonus {
    // Constructeur
    public Invincible(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public void activer() {

    }

    @Override
    public String afficher() {
        return "BI";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        g2d.drawImage(theme.getInvincibleImg(),
                bx + getX() * Moteur.LARG_IMG,
                by + getY() * Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }
}
