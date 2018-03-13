package snoopy;

import java.awt.*;

public class BlocCassable extends Bloc {
    // Constructeur
    public BlocCassable(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Ca";
    }

    @Override
    public void afficher(Graphics2D g2d, int bx, int by) {
        g2d.setColor(new Color(0xFF991D));
        g2d.fillRect(
                bx + getX() * Aire.LARG_IMG + MARGE, by + getY() * Aire.LONG_IMG + MARGE,
                Aire.LARG_IMG - 2 * MARGE, Aire.LONG_IMG - 2 * MARGE
        );
    }

    public void casser(Carte carte) {
        carte.enlever(this);
    }
}
