package snoopy;

import java.awt.*;

public class Teleporteur extends Objet {
    // Constantes
    public static final int RAYON = 20;

    // Attributs
    private Teleporteur paire = null;

    // Constructeur
    public Teleporteur(int x, int y) {
        super(x, y, 1);
    }

    public Teleporteur(int x, int y, Teleporteur paire) {
        this(x, y);
        this.paire = paire;
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Te";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        /*g2d.setColor(Color.black);
        g2d.fillOval(
                bx + (int) ((getX() + 0.5) * Moteur.LARG_IMG) - RAYON,
                by + (int) ((getY() + 0.5) * Moteur.LONG_IMG) - RAYON,
                2*RAYON, 2*RAYON
        );*/
        g2d.drawImage(theme.getPortailImg(),
                bx + (int) ((getX() + 0.5) * Moteur.LARG_IMG) - RAYON,
                by + (int) ((getY() + 0.5) * Moteur.LONG_IMG) - RAYON,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }

    @Override
    public boolean estBloquant() {
        return false;
    }

    public Teleporteur getPaire() {
        return paire;
    }

    public void setPaire(Teleporteur paire) {
        this.paire = paire;
    }
}
