package snoopy;

import java.awt.*;

/**
 * Représente un oiseau sur la carte
 *
 * @author julien benjamin
 */
public class Oiseau extends Objet implements Animation {
    // Constructeur
    public Oiseau(int x, int y) {
        super(x, y);
    }
    public int etat=1;

    // Méthodes
    @Override
    public void animer(Carte carte, Theme theme) {
        etat++;
        etat%=60;
    }

    @Override
    public boolean animation() {
        return true;
    }

    @Override
    public String afficher() {
        return "Oi";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        float a=(etat*theme.getNbImgOiseau()/Aire.FPS);
        int num_anim = (int) Math.floor(a) % theme.getNbImgOiseau();

        g2d.drawImage(theme.getOiseauImg(num_anim),
                bx + getX() * Moteur.LARG_IMG, by + getY() * Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }

    @Override
    public boolean estBloquant() {
        return false;
    }
}
