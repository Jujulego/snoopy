package snoopy;

import java.awt.*;

public class BlocCassable extends Bloc implements Animation {
    // Constructeur
    public BlocCassable(int x, int y) {
        super(x, y);
    }
    private boolean anim=false;
    private int num_anim=0;
    private int prec_anim=0;
    private double etat=1.0;

    // Méthodes
    @Override
    public String afficher() {
        return "Ca";
    }


    @Override
    public void animer(Carte carte, Theme theme) {
        if(num_anim==theme.getNbImageAnimBloc())anim=false;
        else
        {
            //prec_anim=
           // num_anim = (int)
        }

    }

    @Override
    public boolean animation() {
        return anim;
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        /*
        g2d.setColor(new Color(0xFF991D));
        g2d.fillRect(
                bx + getX() * Aire.LARG_IMG + MARGE, by + getY() * Aire.LONG_IMG + MARGE,
                Aire.LARG_IMG - 2 * MARGE, Aire.LONG_IMG - 2 * MARGE
        );*/

        if(animation())
        {
            g2d.drawImage(theme.getAnimBlocImg(num_anim),bx + getX() * Aire.LARG_IMG , by + getY() * Aire.LONG_IMG , Aire.LARG_IMG, Aire.LONG_IMG, null);
        }
        else
        {
            g2d.drawImage(theme.getBlocImg(0),bx + getX() * Aire.LARG_IMG , by + getY() * Aire.LONG_IMG , Aire.LARG_IMG, Aire.LONG_IMG, null);
        }

    }

    public void casser(Carte carte) {
        carte.enlever(this);
        anim=true;

    }
}
