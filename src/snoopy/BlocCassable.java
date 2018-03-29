package snoopy;

import java.awt.*;

/**
 * Bloc que Snoopy peut casser
 *
 * @author julien
 */
public class BlocCassable extends Bloc implements Animation {
    // Attributs
    private boolean anim_casse =false;
    private int num_anim=0;
    private double prec_anim=0;

    // Constructeur
    /**
     * Construit le bloc
     *
     * @param x position dans la carte
     * @param y position dans la carte
     */
    public BlocCassable(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Ca";
    }

    @Override
    public void animer(Carte carte, Theme theme) {
        if (anim_casse) {
            prec_anim += 0.7;

            if (prec_anim >= 1) {
                num_anim++;
                prec_anim = 0.0;
            }

            // L'animation est finie, on enleve le bloc
            if (num_anim >= theme.getNbImageAnimBloc()) {
                anim_casse = false;
                carte.enlever(this);
            }
        }
    }

    @Override
    public boolean animation() {
        return anim_casse;
    }

    @Override
    public boolean estBloquant() {
        return !anim_casse;
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        if (anim_casse) {
            // Affiche l'animation de desaparition
            g2d.drawImage(theme.getAnimBlocImg(num_anim),
                    bx + getX() * Moteur.LARG_IMG , by + getY() * Moteur.LONG_IMG ,
                    Moteur.LARG_IMG, Moteur.LONG_IMG,
                    null
            );
        } else {
            // Affiche le bloc
            g2d.drawImage(theme.getBlocImg(0),
                    bx + getX() * Moteur.LARG_IMG , by + getY() * Moteur.LONG_IMG ,
                    Moteur.LARG_IMG, Moteur.LONG_IMG,
                    null
            );
        }
    }

    /**
     * Casse le bloc : lance l'animation sauf en console
     *
     * @param carte carte du jeu
     * @param console indique si on en est en console
     */
    public void casser(Carte carte, boolean console) {
        if (console) {
            carte.enlever(this);
        } else {
            anim_casse = true;
            num_anim = 0;
        }
    }
}
