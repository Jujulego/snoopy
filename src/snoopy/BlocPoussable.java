package snoopy;

import java.awt.*;

/**
 * Représente un bloc sur la carte
 */
public class BlocPoussable extends Bloc implements Animation, Poussable {
    // Attributs
    private boolean poussable = true;

    // - animation
    private int ox; // position précédante
    private int oy;

    private double etat = 1.0; // varie de 0 -> 1, représente l'avancement dans l'animation
                               // Passe de 0 à 1, en 200ms de seconde

    // Constructeur
    public BlocPoussable(int x, int y) {
        super(x, y);

        // On initialise la position précédante à la position de départ
        ox = x;
        oy = y;
    }

    // Méthodes
    @Override
    public String afficher() {
        // Pas d'animation en console
        etat = 1.0;

        return "Po";
    }

    @Override
    public synchronized void animer(Carte carte, Theme theme) {
        if (etat < 1.0) {
            etat += 5.0/Aire.FPS;

            if (etat >= 1.0) {
                etat = 1.0;
            }
        }
    }

    @Override
    public synchronized boolean animation() {
        return etat < 1.0;
    }

    private void dessiner(Graphics2D g2d, int x, int y, Theme theme) {
        g2d.drawImage(theme.getBlocImg(1),
                x, y,
                Aire.LARG_IMG, Aire.LONG_IMG,
                null
        );
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        // Variations en x
        double x = ox;
        if (getX() > ox) {
            x += etat;
        } else if (getX() < ox) {
            x -= etat;
        }

        // Variations en y
        double y = oy;
        if (getY() > oy) {
            y += etat;
        } else if (getY() < oy) {
            y -= etat;
        }

        // Affichage !
        dessiner(g2d, bx + (int) (x * Aire.LARG_IMG), by + (int) (y * Aire.LONG_IMG), theme);

    }

    @Override
    public boolean pousser(Carte carte, int dx, int dy) {
        // une seule fois !
        if (!poussable) {
            return false;
        }
        poussable = false;

        // Calcul des nouvelles coordonnees
        int nx = getX() + dx;
        int ny = getY() + dy;

        // Récupération de la case cible
        Case case_ = carte.getCase(nx, ny);
        if (case_ == null || !case_.accessible()) {
            // La case n'est pas accessible, voire n'existe même pas !
            return false;
        }

        // Animation
        ox = getX();
        oy = getY();
        etat = 0.0;

        // Déplacement
        carte.enlever(this);

        setX(nx); setY(ny);
        carte.ajouter(this);

        return true;
    }
}
