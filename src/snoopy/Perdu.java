package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Menu affiché en cas de défaite
 */
public class Perdu extends PanneauSol {
    // Constantes
    private static final float OISEAUX_AY = 0.2f;

    // Attributs
    private ArrayList<OiseauMouv> oiseaux = new ArrayList<>();
    private ScheduledExecutorService scheduler;

    private JButton btnRecommencer = new JButton("Recommencer");
    private JButton btnMenu = new JButton("Retourner au Menu");
    private JLabel lblMdp = new JLabel();

    // Constructeur
    /**
     * Prépare les boutons et les oiseaux
     *
     * @param theme thème à utiliser
     */
    public Perdu(Theme theme) {
        super(theme);

        // Ajout des boutons
        setLayout(null);
        add(btnRecommencer);
        add(btnMenu);
        add(lblMdp);
        positionBoutons();

        for (int i = 0; i < 5; ++i) {
            oiseaux.add(new OiseauMouv());
        }
    }

    // Méthodes
    /**
     * Positionne les boutons en fonction de la taille de la fenêtre
     */
    public void positionBoutons() {
        // Boutons
        Insets insets = getInsets();
        Dimension taille;

        // - recommencer
        taille = btnRecommencer.getPreferredSize();
        btnRecommencer.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );

        // - retourner au menu
        taille = btnMenu.getPreferredSize();
        btnMenu.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 75 + insets.top,
                taille.width, taille.height
        );

        // - mot de passe
        taille = lblMdp.getPreferredSize();
        lblMdp.setBounds(
                getWidth() - 15 - taille.width + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Perdu !!!!
        g2d.setColor(Color.black);
        g2d.setFont(new Font ("Plain", Font.BOLD,50));

        g2d.drawString("PERDU", getWidth()/2-100, 70);

        // Oiseaux
        for (int i = 0; i < oiseaux.size(); ++i) {
            OiseauMouv oiseau = oiseaux.get(i);
            if (oiseau.enAttente()) {
                continue;
            }

            BufferedImage img = theme.getOiseauImg((-oiseau.getY() / 5) % theme.getNbImgOiseau());
            g2d.drawImage(img,
                    getWidth() / 2 - 175 - i * 75, oiseau.getY() + getSol(),
                    50, 50,
                    null
            );

            g2d.drawImage(theme.symetriqueX(img),
                    getWidth() / 2 + 125 + i * 75, oiseau.getY() + getSol(),
                    50, 50,
                    null
            );
        }

        // Boutons
        positionBoutons();

        // On force la synchronisation de l'écran
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Fait bouger les oiseaux !
     */
    private void animer() {
        // Mouvement des oiseaux
        for (OiseauMouv oiseau : oiseaux) {
            oiseau.mouv();
        }

        repaint();
    }

    /**
     * Active, et initialise l'animation.
     */
    public void lancer() {
        // Animation !
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/30, TimeUnit.MILLISECONDS);

        // Initialisation oiseaux
        for (int i = 0; i < oiseaux.size(); ++i) {
            OiseauMouv oiseau = oiseaux.get(i);
            oiseau.init(-200, i*20);
        }
    }

    /**
     * Arrête l'animation
     */
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    /**
     * Indique le mot de passe à afficher
     *
     * @param mdp mote de passe
     */
    public void setMdp(String mdp) {
        lblMdp.setText("mdp : " + mdp);
    }

    // Accesseurs
    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnRecommencer() {
        return btnRecommencer;
    }

    // Sous classes
    /**
     * Représente 1 oiseau rebondissant
     */
    private class OiseauMouv {
        // Attributs
        private float y;
        private float dy;
        private int attente = 0;

        // Méthodes
        /**
         * Initialise le mouvement
         *
         * @param y coordonnée de départ
         * @param attente attente avant le debut de l'animation
         */
        public void init(int y, int attente) {
            this.y = y;
            this.dy = 0;
            this.attente = attente;
        }

        /**
         * Gère le mouvement de l'oiseau
         */
        public void mouv() {
            // Attente
            if (attente != 0) {
                attente--;
                return;
            }

            // Evolution
            dy += OISEAUX_AY;
            y += dy;

            // Rebonds
            if (y + dy > -50) {
                dy = -dy-OISEAUX_AY;
            }
        }

        // - accesseurs
        public int getY() {
            return (int) y;
        }

        public boolean enAttente() {
            return attente != 0;
        }
    }
}
