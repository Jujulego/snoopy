package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * L'aire de jeu.
 * Gère la zone de jeu graphique
 *
 * @author julien
 */
public class Aire extends JPanel implements KeyListener, Moteur.MoteurListener {
    // Constantes
    public static final int FPS = 30; // Fréquence de rafraichissement de l'écran

    public static final int MARGE_Y_CARTE = 25; // Marge au dessus de la carte

    // Attributs
    private Moteur moteur;
    private Theme theme;
    private boolean debug = false;

    private int etat = 0;
    private ArrayList<FinListener> listeners = new ArrayList<>();

    private JLabel lbl_score = new JLabel("");

    // Constructeur
    /**
     * Prépare l'aire de jeu.
     *
     * @param moteur moteur de jeu à utiliser
     * @param theme thème à utiliser
     *
     * @author julien
     */
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
        moteur.lancer(FPS);

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
    /**
     * Ajoute des listeners de fin de jeu
     * @param listener
     */
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
        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * Arrête le moteur
     * @see Moteur#stop()
     */
    public void stop() {
        moteur.stop();
    }

    /**
     * Snoopy est mort !!!
     * Appelle les listeners de fin de jeu
     *
     * @see FinListener#mort()
     * @see Aire#fin()
     */
    @Override
    public void mort() {
        // Fin du jeu ?
        for (FinListener listener : listeners) {
            listener.perdu();
        }
    }

    /**
     * Snoopy à gagné !!!
     * Appelle les listeners
     *
     * @see FinListener#fin()
     * @see Aire#mort()
     */
    @Override
    public void fin() {
        // Fin du jeu ?
        for (FinListener listener : listeners) {
            listener.gagne();
        }
    }

    /**
     * Affichage de la carte, de la barre de vie, les oiseaux récupérés
     * le mode de jeu (PAUSE ou AUTO) et le score
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

        // Historique
        if (debug) {
            g2d.setColor(Color.blue);
            for (int i = Moteur.ATTENTE_HISTORIQUE; i < moteur.getHistorique().size(); ++i) {
                Moteur.Coord c = moteur.getHistorique().get(i);
                g2d.fillOval(
                        (int) ((c.x + 0.5) * Moteur.LARG_IMG) - 10 + carte_x,
                        (int) ((c.y + 0.5) * Moteur.LONG_IMG) - 10 + carte_y,
                        20, 20
                );
            }
        }

        // Balles
        if (debug) {
            g2d.setColor(Color.red);
            for (Case c : moteur.previsions(moteur.bonusPauseActif())) {
                g2d.fillOval(
                        (int) ((c.getX() + 0.5) * Moteur.LARG_IMG) - 10 + carte_x,
                        (int) ((c.getY() + 0.5) * Moteur.LONG_IMG) - 10 + carte_y,
                        20, 20
                );
            }
        }

        for (Balle balle : moteur.getBalles()) {
            balle.afficher(g2d, theme, carte_x, carte_y);
        }

        // Conseils
        if (debug) {
            Moteur.Mouvement conseil = moteur.conseil(null, true);

            g2d.setColor(Color.green);
            g2d.fillOval(
                    (int) ((conseil.x + 0.5) * Moteur.LARG_IMG) - 10 + carte_x,
                    (int) ((conseil.y + 0.5) * Moteur.LONG_IMG) - 10 + carte_y,
                    20, 20
            );
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
        } else if (moteur.isAuto()) {
            g2d.drawString("AUTO",  carte_x+larg_carte-75, carte_y+long_carte+20);
        }

        // Score
        g2d.drawString(String.valueOf(moteur.getScore()), carte_x + 5, carte_y+long_carte+20);
    }

    /**
     * Modifie le thème à utiliser
     *
     * @param theme nouveau thème
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
        this.moteur.setTheme(theme);
    }

    /**
     * Retourne le score
     *
     * @return score actuel
     *
     * @see Moteur#getScore()
     */
    public int getScore() {
        return moteur.getScore();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        // Ignoré
    }

    /**
     * Traitement des évenements claviers
     *
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        // Modes
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_O: // Automatique ;)
                moteur.auto();
                break;

            case KeyEvent.VK_P: // Pause ...
                moteur.pause();
                break;

            case KeyEvent.VK_D: // Debug
                debug = !debug;
                break;
        }

        // Controle du jeu (ignoré en mode auto)
        if (!moteur.isAuto()) {
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
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // Ignoré
    }

    // Listener
    /**
     * Interface de fin de jeu
     */
    public interface FinListener {
        /**
         * Appelée quand snoopy meurt
         *
         * @see Aire#mort()
         */
        void perdu();

        /**
         * Appelée quand snoopy à récupéré tous les oiseaux
         *
         * @see Aire#fin()
         */
        void gagne();
    }
}
