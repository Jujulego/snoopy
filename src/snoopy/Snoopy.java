package snoopy;

import java.awt.*;
import java.util.LinkedList;

public class Snoopy extends Objet implements Deplacable {
    // Attributs
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();

    // Constructeur
    public Snoopy(int x, int y) {
        super(x, y, 1);
    }
    
    // Méthodes
    @Override
    public String afficher() {
        return "Sn";
    }

    @Override
    public void afficher(Graphics2D g2d) {
        g2d.setColor(Color.red);
        g2d.fillOval(
                getX() * Aire.LARG_IMG + (Aire.LARG_IMG - 30)/2,
                getY() * Aire.LONG_IMG + (Aire.LONG_IMG - 30)/2,
                30, 30
        );
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

        // Déplacement
        carte.enlever(this);

        setX(nx); setY(ny);
        carte.ajouter(this);

        return true;
    }
}
