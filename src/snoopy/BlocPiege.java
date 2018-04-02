package snoopy;

import java.awt.*;

/**
 * Bloc piégé : explose dès que Snoopy le touche
 *
 * @author julien benjamin
 */
public class BlocPiege extends Bloc implements Animation {
    // Attributs
    private boolean explose=false;
    private boolean anim=false;
    private int num_anim=0;
    private double prec_anim=0;

    // Constructeur
    /**
     * Construit le bloc
     *
     * @param x position dans la carte
     * @param y position dans la carte
     */
    public BlocPiege(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "T ";
    }

    @Override
    public void animer(Carte carte, Theme theme) {
        if (anim) {
            prec_anim += 0.7;

            if (prec_anim >= 1) {
                num_anim++;
                prec_anim = 0.0;
            }

            if (num_anim >= theme.getNbImageAnimBoom()) {
                anim = false;
                carte.enlever(this);
            }
        }

    }

    @Override
    public boolean animation() {
        return anim;
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        if(anim)
        {
            g2d.drawImage(theme.getBoomImg(num_anim),
                    bx + getX() * Moteur.LARG_IMG , by + getY() * Moteur.LONG_IMG,
                    Moteur.LARG_IMG, Moteur.LONG_IMG,
                    null
            );
        }
        else
        {
            g2d.drawImage(theme.getBlocImg(0),
                    bx + getX() * Moteur.LARG_IMG , by + getY() * Moteur.LONG_IMG,
                    Moteur.LARG_IMG, Moteur.LONG_IMG,
                    null
            );
        }

    }

    /**
     * Fait exploser le bloc : lance l'animation si on est pas en console
     *
     * @param snoopy le personnage touché
     * @param carte la carte
     * @param console indique si on est en console
     */
    public void toucher(Snoopy snoopy, Carte carte, boolean console) {
        if (!explose) {
            snoopy.tuer();
            explose = true;

            if (console) {
                carte.enlever(this);
            } else {
                // On lance l'animation !
                anim = true;
                num_anim = 0;
            }
        }
    }
}
