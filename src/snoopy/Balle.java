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
    public boolean estAuBordX(int marge) {
        return (x + marge) / Moteur.LARG_IMG != (x - marge) / Moteur.LARG_IMG;
    }
    public boolean estAuBordY(int marge) {
        return (y + marge) / Moteur.LONG_IMG != (y - marge) / Moteur.LONG_IMG;
    }
    public boolean estAuBord(int marge) {
        return estAuBordX(marge) || estAuBordY(marge);
    }

    @Override
    public String afficher() {
        return "Ba";
    }

    @Override
    public void animer(Carte carte, Theme theme) {
        // Déplacement
        x += dx;
        y += dy;

        // Rebonds sur les bords
        if (x <= RAYON) {
            x = RAYON;
            dx = -dx;
        } else if (x + RAYON >= carte.getTx() * Moteur.LARG_IMG) {
            x = carte.getTx() * Moteur.LARG_IMG - RAYON;
            dx = -dx;
        }

        if (y <= RAYON) {
            y = RAYON;
            dy = -dy;
        } else if (y + RAYON >= carte.getTy() * Moteur.LONG_IMG) {
            y = carte.getTy() * Moteur.LONG_IMG - RAYON;
            dy = -dy;
        }

        // Rebond sur les blocs
        boolean rebond_x = false, rebond_y = false;
        Case case_suiv = carte.getCase(
                ((x + (dx * RAYON)/Math.abs(dx)) / Moteur.LARG_IMG),
                y / Moteur.LONG_IMG
        );
        if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
            rebond_x = true;
        }

        case_suiv = carte.getCase(
                x / Moteur.LARG_IMG,
                ((y + (dy * RAYON)/Math.abs(dy)) / Moteur.LONG_IMG)
        );
        if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
            rebond_y = true;
        }

        case_suiv = carte.getCase(
                ((x + (dx * RAYON)/Math.abs(dx)) / Moteur.LARG_IMG),
                ((y + (dy * RAYON)/Math.abs(dy)) / Moteur.LONG_IMG)
        );
        if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
            rebond_x = true;
            rebond_y = true;
        }

        if (rebond_x && x / Moteur.LARG_IMG != (x + dx) / Moteur.LARG_IMG) {
            dx *= -1;
        }

        if (rebond_y && y / Moteur.LONG_IMG != (y + dy) / Moteur.LONG_IMG) {
            dy *= -1;
        }
    }

    @Override
    public boolean animation() {
        return true;
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
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
