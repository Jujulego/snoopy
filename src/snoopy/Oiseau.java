package snoopy;

import java.awt.*;

/**
 * Représente un oiseau sur la carte
 */
public class Oiseau extends Objet {
    // Constructeur
    public Oiseau(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Oi";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        g2d.setColor(Color.blue);
        g2d.fillOval(
                bx + getX() * Aire.LARG_IMG + (Aire.LARG_IMG - 30)/2,
                by + getY() * Aire.LONG_IMG + (Aire.LONG_IMG - 30)/2,
                30, 30
        );
    }

    @Override
    public boolean estBloquant() {
        return false;
    }
}
