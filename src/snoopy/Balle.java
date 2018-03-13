package snoopy;

import java.awt.*;

public class Balle implements Animation, Affichable {
    // Constantes
    public static final int RAYON = 10;

    // Attributs
    private int x;
    private int y;
    private int dx;
    private int dy;

    private boolean touche = false;

    // Constructeur
    public Balle(int x, int y, int dx, int dy) {
        // Attributs
        this.dx = dx;
        this.dy = dy;

        this.x = x;
        this.y = y;
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Ba";
    }

    @Override
    public void animer(Carte carte) {
        x += dx;
        y += dy;

        // Rebonds sur les bords
        if (x <= 0 || x >= carte.getTx() * Aire.LARG_IMG) {
            dx = -dx;
        }

        if (y <= 0 || y >= carte.getTy() * Aire.LONG_IMG) {
            dy = -dy;
        }
    }

    @Override
    public boolean animation() {
        return true;
    }

    @Override
    public void afficher(Graphics2D g2d, int bx, int by) {
        g2d.setColor(Color.yellow);
        g2d.fillOval(
                bx + x - RAYON,
                by + y - RAYON,
                RAYON*2, RAYON*2
        );
    }

    // - accesseurs
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getTouche() {
        return touche;
    }

    public void setTouche(boolean touche) {
        this.touche = touche;
    }
}
