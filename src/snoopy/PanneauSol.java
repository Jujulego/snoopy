package snoopy;

import javax.swing.*;
import java.awt.*;

/**
 * Base des menu graphiques
 * Affiche un environnement, avec le ciel et le sol
 */
public class PanneauSol extends JPanel {
    // Constantes
    private static final float FRONTIERE = 3/5.0f;

    // Attributs
    protected Theme theme;

    // Constructeur
    /**
     * Prépare le JPanel
     *
     * @param theme thème à utiliser
     */
    public PanneauSol(Theme theme) {
        this.theme = theme;

        // Paramètres
        setDoubleBuffered(true);
        setMinimumSize(new Dimension(1000, 550));
        setSize(getMinimumSize());
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Ciel
        g2d.setBackground(new Color(108, 213, 231));
        g2d.clearRect(0, 0, getWidth(), getHeight());

        // Sol
        int x = 0;
        int y = getSol();
        while (y < getHeight()) {
            g2d.drawImage(theme.getCaseImg(0),
                    x, y,
                    Moteur.LARG_IMG, Moteur.LONG_IMG,
                    null
            );
            x += Moteur.LARG_IMG;

            if (x > getWidth()) {
                x = 0;
                y += Moteur.LONG_IMG;
            }
        }
    }

    /**
     * Indique l'ordonnée frontière entre le ciel et le sol
     *
     * @return la frontière entre ciel et sol
     */
    public int getSol() {
        return (int) (getHeight() * FRONTIERE);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
