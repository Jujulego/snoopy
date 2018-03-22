package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * L'aire de jeu.
 * Gère les interactions et les animations
 */
public class Aire extends JPanel implements KeyListener, Moteur.MoteurListener {
    // Constantes
    public static final int FPS = 60;      // Fréquence de rafraichissement de l'écran

    public static final int MARGE_X_CARTE = 0;
    public static final int MARGE_Y_CARTE = 25;

    // Attributs
    private Moteur moteur;
    private Theme theme;

    private int etat = 0;
    private ArrayList<FinListener> listeners = new ArrayList<>();

    // Constructeur
    public Aire(Moteur moteur, Theme theme) {
        this.moteur = moteur;
        this.theme = theme;

        // Paramètres
        setDoubleBuffered(true);
        setMinimumSize(new Dimension(
                moteur.getCarte().getTx() * Moteur.LARG_IMG + 1,
                moteur.getCarte().getTy() * Moteur.LONG_IMG + MARGE_Y_CARTE + 62
        ));
        addKeyListener(this);

        // Moteur
        moteur.ajouterMoteurListener(this);
        moteur.lancer(1000/FPS);
    }

    // Méthodes
    public void ajouterFinListener(FinListener listener) {
        listeners.add(listener);
    }

    /**
     * Gestion des animations et mise à jour de l'écran
     * Appelée FPS fois par secondes
     */
    @Override
    public void animer() {
        // Evolution des animations
        etat++;
        etat %= 60;

        // Rafraichissement de l'ecran
        repaint();
    }

    public void stop() {
        moteur.stop();
    }

    @Override
    public void mort() {
        // Fin du jeu ?
        for (FinListener listener : listeners) {
            listener.perdu();
        }
    }

    @Override
    public void fin() {
        // Fin du jeu ?
        for (FinListener listener : listeners) {
            listener.gagne();
        }
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
        moteur.getCarte().afficher(g2d, theme, MARGE_X_CARTE, MARGE_Y_CARTE);

        // Balles
        for (Balle balle : moteur.getBalles()) {
            balle.afficher(g2d, theme, MARGE_X_CARTE, MARGE_Y_CARTE);
        }

        // Coeurs
        for (int i = 0; i < Snoopy.MAX_VIES; ++i) {
            g2d.drawImage(i < moteur.getSnoopy().getVies() ? theme.getCoeurPlein() : theme.getCoeurVide(),
                    moteur.getCarte().getTx() * Moteur.LARG_IMG - 25*(Snoopy.MAX_VIES-i), 0,
                    25, 25, null
            );
        }

        // Oiseaux gagnés
        for (int i = 0; i < moteur.getSnoopy().getOiseaux(); ++i) {
            float a = (etat*theme.getNbImgOiseau()/Aire.FPS);
            int num_anim = (int) Math.floor(a) % theme.getNbImgOiseau();


            g2d.drawImage(theme.getOiseauImg(num_anim), i*25+10, 3, 20, 20, null);
        }
        
        // Affichage de l'horloge
		g2d.setColor(Color.black);
		g2d.setFont(new Font ("Plain", Font.BOLD,25));

		g2d.drawString(String.valueOf(moteur.getTimer()), Moteur.LARG_IMG*2, 20);

		// Pause
        if (moteur.isPause()) {
            g2d.drawString("PAUSE", 5, getHeight()-5);
        }

        // Score
        g2d.drawString(String.valueOf(getScore()), getWidth() - 50, getHeight() - 5);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        this.moteur.setTheme(theme);
    }

    public int getScore() {
        return moteur.getScore();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // Ignoré
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        // Lancement d'une animation de déplacement
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:    // HAUT
                moteur.deplacerSnoopy(0, -1);
                break;

            case KeyEvent.VK_DOWN:  // BAS
                moteur.deplacerSnoopy(0, 1);
                break;

            case KeyEvent.VK_LEFT:  // GAUCHE
                moteur.deplacerSnoopy(-1, 0);
                break;

            case KeyEvent.VK_RIGHT: // DROITE
                moteur.deplacerSnoopy(1, 0);
                break;

            case KeyEvent.VK_A: // Attaque !!!
                moteur.attaquer();
                break;

            case KeyEvent.VK_P:
                moteur.pause();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // Ignoré
    }

    // Listenner
    public interface FinListener {
        void perdu();
        void gagne();
    }
}
