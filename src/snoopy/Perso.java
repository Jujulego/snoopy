package snoopy;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Perso extends Objet implements Deplacable, Animation, Teleportable {
    // Constantes
    public static final int DUREE_DEPL = 5;

    // Attributs
    private Direction direction = Direction.BAS;
    private Teleporteur dernier_teleporteur = null;

    // - animation
    private int ox; // position précédante
    private int oy;
    protected double etat = 1.0; // varie de 0 -> 1, représente l'avancement dans l'animation
                               // Passe de 0 à 1, en 400ms de seconde

    // Constructeur
    public Perso(int x, int y, int z) {
        super(x, y, z);

        // On initialise la position précédante à la position de départ
        ox = x;
        oy = y;
    }

    // Méthodes abstraites
    protected abstract String getReprConsole();
    protected abstract BufferedImage getReprGraphique(Theme theme, Direction dir);
    protected abstract boolean interactions(Case case_, Carte carte, Theme theme, int dx, int dy);

    // Méthodes
    @Override
    public String afficher() {
        // Pas d'animation en console
        etat = 1.0;

        // Affichage !
        switch (direction) {
            case HAUT:
                return getReprConsole() + "^";

            case BAS:
                return getReprConsole() + "v";

            case GAUCHE:
                return getReprConsole() + "<";

            case DROITE:
                return getReprConsole() + ">";

            default:
                return getReprConsole() + " ";
        }
    }

    @Override
    public synchronized void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
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
        dessiner(g2d,theme, bx + (int) (x * Moteur.LARG_IMG), by + (int) (y * Moteur.LONG_IMG));
    }

    private void dessiner(Graphics2D g2d, Theme theme, int x, int y) {
        g2d.drawImage(getReprGraphique(theme, direction),
                x, y, Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }

    @Override
    public synchronized void animer(Carte carte, Theme theme) {
        if (etat < 1.0) {
            etat += ((double) DUREE_DEPL) / Aire.FPS;

            // Fin de l'animation
            if (etat >= 1.0) {
                etat = 1.0;
            }
        }
    }

    @Override
    public synchronized boolean animation() {
        return etat < 1.0;
    }

    @Override
    public boolean deplacer(Carte carte, Theme theme, int dx, int dy) {
        // Calcul des nouvelles coordonnees
        int nx = getX() + dx;
        int ny = getY() + dy;

        // Direction
        if (dx < 0) {
            direction = Direction.GAUCHE;
        } else if (dx > 0) {
            direction = Direction.DROITE;
        } else if (dy < 0) {
            direction = Direction.HAUT;
        } else if (dy > 0) {
            direction = Direction.BAS;
        }

        // Récupération de la case cible
        Case case_ = carte.getCase(nx, ny);
        if (case_ == null) { // La case n'existe pas !
            return false;
        }

        // Interactions
        if (!interactions(case_, carte, theme, dx, dy)) {
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

        // Téléportation
        dernier_teleporteur = null;

        return true;
    }

    @Override
    public boolean teleportable() {
        return dernier_teleporteur == null;
    }

    @Override
    public synchronized void teleportation(Teleporteur teleporteur) {
        dernier_teleporteur = teleporteur;
        setX(teleporteur.getX());
        setY(teleporteur.getY());
        ox = teleporteur.getX();
        oy = teleporteur.getY();
    }

    public Direction getDirection() {
        return direction;
    }
}
