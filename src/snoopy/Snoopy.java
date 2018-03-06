package snoopy;

import java.util.LinkedList;

public class Snoopy extends Objet implements Deplacable {
    // Attributs
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();

    // Constructeur
    public Snoopy(int x, int y) {
        super(x, y, 1);
    }
    
    //Ca marche
    
    // Méthodes
    @Override
    public String afficher() {
        return "Sn";
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
