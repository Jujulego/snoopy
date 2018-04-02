package snoopy;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Représente un téléporteur
 */
public class Teleporteur extends Objet implements Animation, Affichable {
    // Constantes
    public static final int RAYON = 20;
    private double angle_rot=0.2;

    // Attributs
    private Teleporteur paire = null;

    // Constructeur
    /**
     * Positionne le téléporteur
     *
     * @param x coordonnées dans la carte
     * @param y coordonnées dans la carte
     */
    public Teleporteur(int x, int y) {
        super(x, y, 1);
    }

    /**
     * Positionne le téléporteur et le paire avec un autre
     *
     * @param x coordonnées dans la carte
     * @param y coordonnées dans la carte
     * @param paire téléporteur associé
     */
    public Teleporteur(int x, int y, Teleporteur paire) {
        this(x, y);
        this.paire = paire;
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Te";
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        BufferedImage img = theme.getPortailImg();

        if(theme.getNumTheme() != Theme.SNOOPY)
        {
            angle_rot = (angle_rot + 0.3) % 24;
            AffineTransform ma_rotation = AffineTransform.getRotateInstance(angle_rot*Math.PI/12, theme.getPortailImg().getWidth() / 2, theme.getPortailImg().getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(ma_rotation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            img = op.filter(theme.getPortailImg(), null);
        }
        g2d.drawImage(img,
                bx +  (getX()) * Moteur.LARG_IMG ,
                by +  (getY() ) * Moteur.LONG_IMG,
                //Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );
    }

    @Override
    public boolean estBloquant() {
        return false;
    }

    /**
     * Renvoie le téléporteur associé
     *
     * @return téléporteur associé
     */
    public Teleporteur getPaire() {
        return paire;
    }

    /**
     * Associe le téléporteur avec un autre
     *
     * @param paire
     */
    public void setPaire(Teleporteur paire) {
        this.paire = paire;
    }

    @Override
    public void animer(Carte carte, Theme theme) {

    }

    @Override
    public boolean animation() {
        return true;
    }
}
