package snoopy;

import java.awt.*;

public class BlocPiege extends Bloc {
    // Constructeur
    public BlocPiege(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "T ";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        g2d.drawImage(theme.getBlocImg(0),
                bx + getX() * Moteur.LARG_IMG , by + getY() * Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }

    public void toucher(Carte carte, Snoopy snoopy) {
        snoopy.tuer();
        carte.enlever(this);
    }
}
