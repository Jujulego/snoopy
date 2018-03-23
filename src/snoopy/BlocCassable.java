package snoopy;

import java.awt.*;

public class BlocCassable extends Bloc implements Animation {
    // Constructeur
    public BlocCassable(int x, int y) {
        super(x, y);
    }
    private boolean anim_casse =false;
    private int num_anim=0;
    private double prec_anim=0;

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
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        if (anim_casse) {
            // Affiche l'animation de desaparition
            g2d.drawImage(theme.getAnimBlocImg(num_anim),
                    bx + getX() * Aire.LARG_IMG , by + getY() * Aire.LONG_IMG ,
                    Aire.LARG_IMG, Aire.LONG_IMG,
                    null
            );
        } else {
            // Affiche le bloc
            g2d.drawImage(theme.getBlocImg(0),
                    bx + getX() * Aire.LARG_IMG , by + getY() * Aire.LONG_IMG ,
                    Aire.LARG_IMG, Aire.LONG_IMG,
                    null
            );
        }
    }

    public void casser() {
        anim_casse = true;
        num_anim = 0;
    }
}
