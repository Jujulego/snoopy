package snoopy;

import java.awt.*;
import java.util.LinkedList;

public class Snoopy extends Objet implements Deplacable, Animation {
    // Attributs
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();
    private double etat = 1.0;
    private int ox;
    private int oy;

    // Constructeur
    public Snoopy(int x, int y) {
        super(x, y, 1);
        ox = x;
        oy = y;
    }
    
    // Méthodes
    @Override
    public String afficher() {
        etat = 1.0;
        return "Sn";
    }

    @Override
    public void animer() {
        if (etat < 1.0) {
            etat += 0.17;

            if (etat >= 1.0) {
                etat = 1.0;
            }
        }
    }

    @Override
    public boolean animation() {
        return etat < 1.0;
    }

    private void afficher(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.red);
        g2d.fillOval(
            x + (Aire.LARG_IMG - 30)/2,
            y + (Aire.LONG_IMG - 30)/2,
            30, 30
        );
    }

    @Override
    public void afficher(Graphics2D g2d) {
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

        afficher(g2d, (int) (x * Aire.LARG_IMG), (int) (y * Aire.LONG_IMG));
    }

    @Override
    public boolean deplacer(Carte carte, int dx, int dy) {
        // Calcul des nouvelles coordonnees
        int nx = getX() + dx;
        int ny = getY() + dy;

        // Récupération de la case
        Case case_ = carte.getCase(nx, ny);
        if (case_ == null || !case_.vide()) {
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

        // Déplacement
        carte.enlever(this);

        setX(nx); setY(ny);
        carte.ajouter(this);

        return true;
    }
}
