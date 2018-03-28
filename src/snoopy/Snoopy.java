package snoopy;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Représente Snoopy !!!
 */
public class Snoopy extends Perso {
    // Constantes
    public static final int MAX_VIES = 3;

    // Attributs
    private int vies = MAX_VIES;
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();

    // Constructeur
    public Snoopy(int x, int y) {
        super(x, y, 3);
    }
    
    // Méthodes
    @Override
    public boolean estBloquant() {
        return false;
    }

    @Override
    protected String getReprConsole() {
        return "S";
    }

    @Override
    protected BufferedImage getReprGraphique(Theme theme, Direction dir) {
        int num_anim=0;
        if (animation()) {
            int nb = theme.getNbImgPerso(dir);
            num_anim = (int) (etat / (1.0 / (2 * nb))) % nb;
        }

        return theme.getPersoImg(dir, num_anim);
    }

    @Override
    protected boolean interactions(Case case_, Carte carte, Theme theme, int dx, int dy) {
        Objet obj = case_.getObjet();

        if (obj instanceof Poussable) { // On pousse !
            if (!((Poussable) obj).pousser(carte, dx, dy)) {
                return false;
            }
        } else if (!case_.accessible()) { // La case n'est pas accessible !
            if (obj instanceof BlocPiege) { // Bouum !
                ((BlocPiege) obj).toucher(this, carte, theme.getNumTheme() == Theme.CONSOLE);
            }

            return false;
        }

        // Récupération de l'oiseau
        for (Objet o : case_.listeObjets()) {
            if (o instanceof Oiseau) {
                case_.enlever(o);
                oiseaux.add((Oiseau) o);
            }
        }

        return true;
    }

    @Override
    public boolean deplacable() {
        return vies > 0; // Sauf si il est mort
    }

    public boolean tuer() {
        if (vies > 0) {
            vies--;
        }

        return vies == 0;
    }

    // - accesseurs
    public int getVies() {
        return vies;
    }
    public int getOiseaux() {
        return oiseaux.size();
    }
}
