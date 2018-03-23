package snoopy;

import java.awt.*;

public class BlocPiege extends Bloc implements Animation {
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

            if (num_anim >= theme.getNbImageAnimBloc()) {
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
        g2d.drawImage(theme.getBlocImg(0),
                bx + getX() * Aire.LARG_IMG , by + getY() * Aire.LONG_IMG,
                Aire.LARG_IMG, Aire.LONG_IMG,
                null
        );
    }

    public void toucher(Carte carte, Snoopy snoopy) {
        snoopy.tuer();
        carte.enlever(this);
        anim=true;
    }

}
