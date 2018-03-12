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

    // Attributs
    // - jeu
    private Carte carte;   // Carte affichée
    private Snoopy snoopy; // Le personnage controllé

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
        setMinimumSize(new Dimension(carte.getTx() * LARG_IMG + 1, carte.getTy() * LONG_IMG + 38));
        addKeyListener(this);

        // Scheduler
        animations.addAll(carte.objetsAnimes());
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/FPS, TimeUnit.MILLISECONDS);
    }

    // Méthodes
    public void ajouterBalle(Balle balle) {
        animations.add(balle);
        balles.add(balle);
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

        // Grille
        g2d.setColor(Color.black);
        for (int i = 0; i <= carte.getTx(); ++i) {
            g2d.drawLine(i * LARG_IMG, 0, i * LARG_IMG, carte.getTy() * LONG_IMG);
        }

        for (int i = 0; i <= carte.getTy(); ++i) {
            g2d.drawLine(0, i * LONG_IMG, carte.getTx() * LARG_IMG, i * LONG_IMG);
        }

        // Objets
        carte.afficher(g2d);

        // Balles
        for (Balle balle : balles) {
            balle.afficher(g2d);
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
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // Ignoré
    }
}
