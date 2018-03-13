package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * L'aire de jeu.
 * Gère les interactions et les animations
 */
public class Aire extends JPanel implements KeyListener {
    // Constantes
    public static final int FPS = 60;      // Fréquence de rafraichissement de l'écran
    public static final int LARG_IMG = 50; // Largeur de base (d'une case de la grille)
    public static final int LONG_IMG = 50; // Longueur de base (d'une case de la grille)

    public static final int MARGE_X_CARTE = 0;
    public static final int MARGE_Y_CARTE = 25;

    // Attributs
    // - jeu
    private Carte carte;   // Carte affichée
    private Snoopy snoopy; // Le personnage controllé

    private Image coeur_plein;
    private Image coeur_vide;
    private Theme theme;

    // - animation
    private LinkedList<Balle> balles = new LinkedList<>();
    private LinkedList<Animation> animations = new LinkedList<>();
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    // Constructeur

    /**
     * @param carte  carte à afficher
     * @param snoopy personnage à controller
     */
    public Aire(Carte carte, Snoopy snoopy) {
        this.carte = carte;
        this.snoopy = snoopy;

        // Paramètres
        setMinimumSize(new Dimension(carte.getTx() * LARG_IMG + 1, carte.getTy() * LONG_IMG + MARGE_Y_CARTE + 38));
        addKeyListener(this);

        // Scheduler
        animations.addAll(carte.objetsAnimes());
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/FPS, TimeUnit.MILLISECONDS);

        // Chargement des images
        coeur_plein = Toolkit.getDefaultToolkit().getImage("images/coeur_plein.png");
        coeur_vide = Toolkit.getDefaultToolkit().getImage("images/coeur_vide.png");

        theme = new Theme(1);

    }

    // Méthodes
    public void ajouterBalle(Balle balle) {
        animations.add(balle);
        balles.add(balle);
    }

    public static int ajouterDirX(int x, Direction direction) {
        switch (direction) {
            case DROITE:
                return x+1;

            case GAUCHE:
                return x-1;

            default:
                return x;
        }
    }
    public static int ajouterDirY(int y, Direction direction) {
        switch (direction) {
            case BAS:
                return y+1;

            case HAUT:
                return y-1;

            default:
                return y;
        }
    }

    /**
     * Gestion des animations et mise à jour de l'écran
     * Appelée FPS fois par secondes
     */
    private void animer() {
        // Evolution des animation
        for (Animation a : animations) {
            a.animer(carte);
        }

        // Rafraichissement de l'ecran
        repaint();
    }

    /**
     * Affichage de la grille
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        // Options
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Objets
        carte.afficher(g2d, theme, MARGE_X_CARTE, MARGE_Y_CARTE);

        // Balles
        for (Balle balle : balles) {
            balle.afficher(g2d,theme,  MARGE_X_CARTE, MARGE_Y_CARTE);

            // Touche ?
            if (snoopy.getX() * LARG_IMG < balle.getX() && balle.getX() < (snoopy.getX() + 1) * LARG_IMG &&
                    snoopy.getY() * LONG_IMG < balle.getY() && balle.getY() < (snoopy.getY() + 1) * LONG_IMG) {

                if (!balle.getTouche()) {
                    balle.setTouche(true);
                    snoopy.tuer();
                }
            } else if (balle.getTouche()) {
                balle.setTouche(false);
            }
        }

        // Coeurs
        for (int i = 0; i < Snoopy.MAX_VIES; ++i) {
            g2d.drawImage(i < snoopy.getVies() ? coeur_plein : coeur_vide,
                    carte.getTx() * LARG_IMG - 25*(Snoopy.MAX_VIES-i), 0,
                    25, 25, null
            );
        }

        // Oiseaux gagnés
        for (int i = 0; i < snoopy.getOiseaux(); ++i) {
            g2d.setColor(Color.blue);
            g2d.fillOval(i*20+5, 5, 15, 15);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // Ignoré
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        // Ignoré si animation en cours
        if (snoopy.animation()) {
            return;
        }

        // Lancement d'une animation de déplacement
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:    // HAUT
                snoopy.deplacer(carte, 0, -1);
                break;

            case KeyEvent.VK_DOWN:  // BAS
                snoopy.deplacer(carte, 0, 1);
                break;

            case KeyEvent.VK_LEFT:  // GAUCHE
                snoopy.deplacer(carte, -1, 0);
                break;

            case KeyEvent.VK_RIGHT: // DROITE
                snoopy.deplacer(carte, 1, 0);
                break;

            case KeyEvent.VK_A: // Attaque !!!
                if (snoopy.getVies() == 0) {
                    break;
                }

                Case case_ = carte.getCase(
                        ajouterDirX(snoopy.getX(), snoopy.getDirection()),
                        ajouterDirY(snoopy.getY(), snoopy.getDirection())
                );

                if (case_ != null) {
                    Objet objet = case_.getObjet();

                    if (objet instanceof BlocCassable) {
                        ((BlocCassable) objet).casser(carte);
                    } else if (objet instanceof BlocPiege) {
                        ((BlocPiege) objet).toucher(carte, snoopy);
                    }
                }

                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // Ignoré
    }
}
