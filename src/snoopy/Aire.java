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

    private JLabel lbl_score = new JLabel("");

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

        // Score
        setLayout(null);
        Insets insets = getInsets();
        Dimension taille;

        add(lbl_score);
        taille = lbl_score.getPreferredSize();
        lbl_score.setBounds(
                insets.right - taille.width - 5, insets.bottom - taille.height - 5,
                taille.width, taille.height
        );
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        // Centre
        int centre_x = getWidth()/2;
        int centre_y = getHeight()/2;

        Carte carte = moteur.getCarte();
        int larg_carte = carte.getTx()*Moteur.LARG_IMG;
        int long_carte = carte.getTy()*Moteur.LONG_IMG;

        int carte_x = centre_x-larg_carte/2;
        int carte_y = centre_y-long_carte/2;

        // Objets
        carte.afficher(g2d, theme, carte_x, carte_y);

        // Balles
        for (Balle balle : moteur.getBalles()) {
            balle.afficher(g2d, theme, carte_x, carte_y);
        }

        // Coeurs
        for (int i = 0; i < Snoopy.MAX_VIES; ++i) {
            g2d.drawImage(i < moteur.getSnoopy().getVies() ? theme.getCoeurPlein() : theme.getCoeurVide(),
                    carte_x + larg_carte - 25*(Snoopy.MAX_VIES-i), carte_y - MARGE_Y_CARTE,
                    25, 25, null
            );
        }

        // Oiseaux gagnés
        for (int i = 0; i < moteur.getSnoopy().getOiseaux(); ++i) {
            float a = (etat*theme.getNbImgOiseau()/Aire.FPS);
            int num_anim = (int) Math.floor(a) % theme.getNbImgOiseau();

            g2d.drawImage(theme.getOiseauImg(num_anim),
                    carte_x+i*25+10, carte_y - MARGE_Y_CARTE + 3,
                    20, 20,
                    null
            );
        }
        
        // Affichage de l'horloge
		g2d.setColor(Color.black);
		g2d.setFont(new Font ("Plain", Font.BOLD,20));

		g2d.drawString(
		        String.valueOf(moteur.getTimer()),
                carte_x+larg_carte/2-15, carte_y - 3
        );

		// Pause
        if (moteur.isPause()) {
            g2d.drawString("PAUSE", carte_x+larg_carte-75, carte_y+long_carte+20);
        }

        // Score
        g2d.drawString(String.valueOf(moteur.getScore()), carte_x + 5, carte_y+long_carte+20);
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
