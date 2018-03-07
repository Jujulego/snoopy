package snoopy;

import java.awt.*;
import java.util.LinkedList;

/**
 * Représente Snoopy !!!
 */
public class Snoopy extends Objet implements Deplacable, Animation {
    // Attributs
    private Direction direction = Direction.BAS;
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();

    // - animation
    private int ox; // position précédante
    private int oy;

    private double etat = 1.0; // varie de 0 -> 1, représente l'avancement dans l'animation
                               // Passe de 0 à 1, en 200ms de seconde

    // Constructeur
    public Snoopy(int x, int y) {
        super(x, y, 1);

        // On initialise la position précédante à la position de départ
        ox = x;
        oy = y;
    }
    
    // Méthodes
    @Override
    public String afficher() {
        // Pas d'animation en console
        etat = 1.0;

        switch (direction) {
            case HAUT:
                return "S^";

            case BAS:
                return "Sv";

            case GAUCHE:
                return "S<";

            case DROITE:
                return "S>";

            default:
                return "Sn";
        }
    }

    @Override
    public synchronized void animer() {
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

    private void afficher(Graphics2D g2d, int x, int y) {
        // Affiche snoopy centré sur la position donnée
        g2d.setColor(Color.red);
        g2d.fillOval(
            x + (Aire.LARG_IMG - 30)/2,
            y + (Aire.LONG_IMG - 30)/2,
            30, 30
        );

        g2d.setColor(Color.black);
        switch (direction) {
            case HAUT:
                g2d.fillOval(x + Aire.LARG_IMG/2 - 2, y + 6, 4, 4);
                break;

            case BAS:
                g2d.fillOval(x + Aire.LARG_IMG/2 - 2, y + Aire.LONG_IMG - 10, 4, 4);
                break;

            case GAUCHE:
                g2d.fillOval(x + 6, y + Aire.LARG_IMG/2 - 2, 4, 4);
                break;

            case DROITE:
                g2d.fillOval(x + Aire.LARG_IMG - 10, y + Aire.LARG_IMG/2 - 2, 4, 4);
                break;
        }
    }

    @Override
    public synchronized void afficher(Graphics2D g2d) {
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
        afficher(g2d, (int) (x * Aire.LARG_IMG), (int) (y * Aire.LONG_IMG));
    }

    @Override
    public boolean deplacer(Carte carte, int dx, int dy) {
        // Calcul des nouvelles coordonnees
        int nx = getX() + dx;
        int ny = getY() + dy;

        // Récupération de la case cible
        Case case_ = carte.getCase(nx, ny);
        if (case_ == null || !case_.accessible()) {
            // La case n'est pas accessible, voire n'existe même pas !
            return false;
        }

        // Récupération de l'oiseau
        for (Objet o : case_.listeObjets()) {
            if (o instanceof Oiseau) {
                case_.enlever(o);
                oiseaux.add((Oiseau) o);
            }
        }

        // Animation
        ox = getX();
        oy = getY();
        etat = 0.0;

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

        // Déplacement
        carte.enlever(this);

        setX(nx); setY(ny);
        carte.ajouter(this);

        return true;
    }
}
