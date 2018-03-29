package snoopy;

import java.awt.image.BufferedImage;

/**
 * Bad Snoopy est l'ennemis de Snoopy, il le chasse à travers la carte
 *
 * @author julien
 */
public class BadSnoopy extends Perso {
    // Attributs
    private int pause = 0;

    // Constructeur
    /**
     * Construit BadSnoopy
     *
     * @param x coordonnées de départ dans la matrice
     * @param y coordonnées de départ dans la matrice
     */
    public BadSnoopy(int x, int y) {
        super(x, y, 2);
    }

    // Méthodes
    @Override
    public boolean estBloquant() {
        return true; // Empeche d'autres objets de se mettre sur BadSnoopy
    }

    @Override
    protected String getReprConsole() {
        return "B";
    }

    @Override
    protected BufferedImage getReprGraphique(Theme theme, Direction dir) {
        int num_anim = 0;
        if (animation()) {
            int nb = theme.getNbImgBad(dir);
            num_anim = (int) (etat / (1.0 / (2 * nb))) % nb;
        }

        return theme.getBadImg(dir, num_anim);
    }

    @Override
    public synchronized boolean animation() {
        return pause != 0 || super.animation();
    }

    @Override
    public synchronized void animer(Carte carte, Theme theme) {
        if (super.animation() || pause == 0) {
            super.animer(carte, theme);
        } else {
            pause--;
        }
    }

    @Override
    public boolean deplacer(Carte carte, Theme theme, int dx, int dy) {
        boolean ret = super.deplacer(carte, theme, dx, dy);
        if (ret) {
            pause = 12;
        }

        return ret;
    }

    @Override
    protected boolean interactions(Case case_, Carte carte, Theme theme, int dx, int dy) {
        for (Objet o : case_.listeObjets()) {
            if (o instanceof Snoopy) {
                ((Snoopy) o).tuer();
                break;
            }
        }

        return true;
    }

    @Override
    public boolean deplacable() {
        return true;
    }
}
