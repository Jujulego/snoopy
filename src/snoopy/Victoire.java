package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Menu en cas de victoire de l'utilisateur
 *
 * @author julien
 */
public class Victoire extends PanneauSol {
    // Attributs
    private int perso_x = -50;
    private int perso_dx = 2;
    private int oiseau_x = -130;
    private int oiseau_dx = 2;

    private int bad_x = 0;
    private int bad_dx = 2;

    private int balle_x = 0;
    private int balle_dx = 2;

    private float balle_y = -80;
    private float balle_dy = 0;
    private float balle_ay = 0.2f;

    private int numAnimPerso;
    private int numAnimBad;
    private int numAnimOiseau;
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private JButton btnContinuer = new JButton("Continuer");
    private JButton btnMenu = new JButton("Retourner au Menu");

    // Constructeur
    /**
     * Prépare les boutons et les animations
     *
     * @param theme thème à utiliser
     */
    public Victoire(Theme theme) {
        super(theme);

        // Ajout des boutons
        setLayout(null);
        add(btnContinuer);
        add(btnMenu);
        positionBoutons();
    }

    // Méthodes
    /**
     * Postionne les boutons en fonction de la fenêtre
     */
    private void positionBoutons() {
        // Boutons
        Insets insets = getInsets();
        Dimension taille;

        // - recommencer
        taille = btnContinuer.getPreferredSize();
        btnContinuer.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );

        // - retourner au menu
        taille = btnMenu.getPreferredSize();
        btnMenu.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 75 + insets.top,
                taille.width, taille.height
        );
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Piege de la balle
        int long_ = (int) (Moteur.LONG_IMG * 4/5.0);
        int larg  = (int) (Moteur.LARG_IMG * 4/5.0);
        int x = getWidth() - 8*larg, y = getSol()-long_;

        for (int i = 1; i < 4; ++i) {
            g2d.drawImage(theme.getBlocImg((i + ((x + 1) % 2)) % 2),
                    x, y - i*long_,
                    larg, long_,
                    null
            );

            g2d.drawImage(theme.getBlocImg((i + (x % 2)) % 2),
                    getWidth()-larg, y - i*long_,
                    larg, long_,
                    null
            );
        }

        while (x < getWidth()) {
            g2d.drawImage(theme.getBlocImg((x / larg) % 2),
                    x, y,
                    larg, long_,
                    null
            );

            x += larg;
        }

        g2d.drawImage(theme.getBalleImg(),
                getWidth()-7*larg+balle_x, (int) balle_y + getSol()-long_,
                (int) (Balle.RAYON * 8/5.0f), (int) (Balle.RAYON * 8/5.0f),
                null
        );

        g2d.drawImage(theme.getBadImg(bad_dx > 0 ? Direction.DROITE : Direction.GAUCHE, numAnimBad),
                getWidth()-7*larg+bad_x, getSol()-2*long_,
                larg, long_,
                null
        );

        // Perso et Oiseau
        BufferedImage img = theme.getOiseauImg(numAnimOiseau);
        if (oiseau_dx > 0) {
            img = theme.symetriqueX(img);
        }

        g2d.drawImage(img,
                getWidth() - oiseau_x, getSol()-50,
                50, 50,
                null
        );

        g2d.drawImage(theme.symetriqueX(theme.getPersoImg(perso_dx > 0 ? Direction.DROITE : Direction.GAUCHE, numAnimPerso)),
                getWidth() - perso_x, getSol()-50,
                50, 50,
                null
        );

        g2d.drawImage(theme.getCoeurPlein(),
                getWidth() - oiseau_x + 10, getSol() - 50 - 30,
                25, 25,
                null
        );

        // Gagné !!!!
        g2d.setColor(Color.black);
        g2d.setFont(new Font ("Plain", Font.BOLD,50));

        g2d.drawString("GAGNÉ", getWidth()/2-100, 70);

        // Boutons
        positionBoutons();

        // On force la synchronisation de l'écran
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Gestion des animations
     */
    private void animer() {
        int larg  = (int) (Moteur.LARG_IMG * 4/5.0);

        // Mouvement du personnage et de l'oiseau
        oiseau_x += oiseau_dx;
        if (oiseau_x + oiseau_dx > getWidth()) {
            oiseau_x = perso_x + 80;
            oiseau_dx = -oiseau_dx;

        } else if (oiseau_dx < 0 && oiseau_x + oiseau_dx < 8*larg+50) {
            oiseau_x = perso_x - 80;
            oiseau_dx = -oiseau_dx;
        }

        perso_x += perso_dx;
        if (perso_x + perso_dx > getWidth()) {
            perso_dx = -perso_dx;

        } else if (perso_dx < 0 && perso_x + perso_dx < 8*larg+50) {
            perso_dx = -perso_dx;
        }

        // Balle et BadSnoopy
        bad_x += bad_dx;
        if (bad_x + bad_dx + Moteur.LARG_IMG * 4/5.0 > Moteur.LARG_IMG * 24/5.0) {
            bad_dx = -bad_dx;

        } else if (bad_x + bad_dx < 0) {
            bad_dx = -bad_dx;
        }

        balle_x += balle_dx;
        if (balle_x + balle_dx + Balle.RAYON * 8/5.0 > Moteur.LARG_IMG * 24/5.0) {
            balle_dx = -balle_dx;

        } else if (balle_x + balle_dx < 0) {
            balle_dx = -balle_dx;
        }

        balle_dy += balle_ay;
        balle_y += balle_dy;
        if (balle_y >= -2*Balle.RAYON) {
            balle_dy = -balle_dy-balle_ay;
        }

        // Animation
        numAnimPerso = (numAnimPerso + 1) % theme.getNbImgPerso(Direction.DROITE);
        numAnimBad = (numAnimBad + 1) % theme.getNbImgBad(Direction.DROITE);
        numAnimOiseau = (numAnimOiseau + 1) % theme.getNbImgOiseau();

        // Et on redessine l'écran
        repaint();
    }

    /**
     * Active les animations
     */
    public void lancer() {
        // Animation !
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/30, TimeUnit.MILLISECONDS);
    }

    /**
     * Arrête les animations
     */
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    // Accesseurs
    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnContinuer() {
        return btnContinuer;
    }
}
