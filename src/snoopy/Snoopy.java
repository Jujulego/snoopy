package snoopy;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Représente Snoopy !!!
 */
public class Snoopy extends Perso {
    // Constantes
    public static final int MAX_VIES = 3;

    // Attributs
    private int vies;
    private int pause = 0; // Mode console
    private LinkedList<Oiseau> oiseaux = new LinkedList<>();

    // Constructeur
    /**
     * Construit snoopy
     *
     * @param x coordonnées de départ
     * @param y coordonnées de départ
     * @param vies nombre de vies
     */
    public Snoopy(int x, int y, int vies) {
        super(x, y, 3);
        this.vies = vies;
    }
    
    // Méthodes
    @Override
    public boolean estBloquant() {
        return false;
    }

    @Override
    protected String getReprConsole() {
        if (pause != 0) {
            pause--;
        }

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
    public boolean deplacer(Carte carte, Theme theme, int dx, int dy) {
        if (theme.getNumTheme() == Theme.CONSOLE) {
            pause = 2;
        }

        return super.deplacer(carte, theme, dx, dy);
    }

    @Override
    public boolean deplacable() {
        if (etat == 1) {
            return vies > 0 && pause == 0; // Sauf si il est mort
        }

        return false;
    }

    /**
     * Enlève une vie à Snoopy, sauf si Snoopy est invincible
     *
     * @return true si snoopy est mort
     */
    public boolean tuer() {
        if (!estInvicible() && vies > 0) {
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
