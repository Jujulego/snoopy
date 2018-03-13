package snoopy;


import java.awt.*;
import java.util.LinkedList;

/**
 * Représente Snoopy !!!
 */
public class Snoopy extends Objet implements Deplacable, Animation {
    // Constantes
    public static final int MAX_VIES = 3;

    // Attributs
    private int vies = MAX_VIES;
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
    public boolean tuer() {
        if (vies > 0) {
            vies--;
        }

        return vies == 0;
    }

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
    public synchronized void animer(Carte carte) {
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

    private void dessiner(Graphics2D g2d,Theme theme, int x, int y) {
        switch (direction) {
            case HAUT:
                g2d.drawImage(theme.get_truc(0), x, y,Aire.LARG_IMG,Aire.LONG_IMG, null);
                break;

            case BAS:
                g2d.drawImage(theme.get_truc(1), x, y,Aire.LARG_IMG,Aire.LONG_IMG, null);

                break;

            case GAUCHE:
                g2d.drawImage(theme.get_truc(2), x, y,Aire.LARG_IMG,Aire.LONG_IMG, null);

                break;

            case DROITE:
                g2d.drawImage(theme.get_truc(3), x, y,Aire.LARG_IMG,Aire.LONG_IMG, null);

                break;
        }

        /*
        // Affiche snoopy centré sur la position donnée
        g2d.setColor(vies == 0 ? Color.gray : Color.red);
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
        }*/
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
        dessiner(g2d,theme, bx + (int) (x * Aire.LARG_IMG), by + (int) (y * Aire.LONG_IMG));
    }

    @Override
    public boolean deplacer(Carte carte, int dx, int dy) {
        // Il est mort !!!
        if (vies == 0) {
            return false;
        }

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

        Objet obj = case_.getObjet();
        if (obj instanceof Poussable) {
            if (!((Poussable) obj).pousser(carte, dx, dy)) {
                return false;
            }
        } else if (!case_.accessible()) { // La case n'est pas accessible !
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

    // - accesseurs
    public int getVies() {
        return vies;
    }

    public int getOiseaux() {
        return oiseaux.size();
    }
}
