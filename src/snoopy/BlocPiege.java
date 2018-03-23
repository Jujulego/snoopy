package snoopy;

import java.awt.*;

public class BlocPiege extends Bloc implements Animation {
    // Attributs
    private boolean explose=false;
    private boolean anim=false;
    private int num_anim=0;
    private double prec_anim=0;


    // Constructeur
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

    public void toucher(Snoopy snoopy) {
        if (!explose) {
            snoopy.tuer();
            explose = true;

            // On lance l'animation !
            anim=true;
            num_anim=0;
        }
    }
}
