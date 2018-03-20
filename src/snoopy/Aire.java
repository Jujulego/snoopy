package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
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

    private int base_score;

    // - clock
    private int timer = 60;
    private String timerString;

    // - animation
    private boolean pause = false;
    private LinkedList<Balle> balles = new LinkedList<>();
    private LinkedList<Animation> animations = new LinkedList<>();
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private int etat = 0;
    private ArrayList<FinListener> listeners = new ArrayList<>();

    // Constructeur
    public Aire(Carte carte, Snoopy snoopy, Theme theme) {
        this(carte, snoopy, theme, 0);
    }

    /**
     * @param carte  carte à afficher
     * @param snoopy personnage à controller
     */
    public Aire(Carte carte, Snoopy snoopy, Theme theme, int base_score) {
        this.carte = carte;
        this.snoopy = snoopy;
        this.theme = theme;
        this.base_score = base_score;

        // Paramètres
        setDoubleBuffered(true);
        setMinimumSize(new Dimension(carte.getTx() * LARG_IMG + 1, carte.getTy() * LONG_IMG + MARGE_Y_CARTE + 62));
        addKeyListener(this);

        // Scheduler
        animations.addAll(carte.objetsAnimes());
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/FPS, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::clock, 0, 1, TimeUnit.SECONDS);
        
        // Chargement des images
        coeur_plein = Toolkit.getDefaultToolkit().getImage("images/theme"+theme.getNumTheme()+"/coeur/coeur1.png");
        coeur_vide = Toolkit.getDefaultToolkit().getImage("images/theme"+theme.getNumTheme()+"/coeur/coeur0.png");
    }

    // Méthodes
    public void ajouterBalle(Balle balle) {
        animations.add(balle);
        balles.add(balle);
    }

    public void ajouterFinListener(FinListener listener) {
        listeners.add(listener);
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

    public static boolean pointDedans(Objet obj, Balle balle) {
        return pointDedans(obj.getX(), obj.getY(), balle.getX(), balle.getY());
    }

    public static boolean pointDedans(int casex, int casey, int ptx, int pty) {
        return casex * LARG_IMG < ptx && ptx < (casex + 1) * LARG_IMG &&
                casey * LONG_IMG < pty && pty < (casey + 1) * LONG_IMG;
    }

    /**
     * Gestion des animations et mise à jour de l'écran
     * Appelée FPS fois par secondes
     */
    private void animer() {
        // Evolution des animation
        if (!pause) {
            for (Animation a : animations) {
                if (a.animation()) a.animer(carte);
            }

            etat++;
            etat %= 60;

            // Touche ?
            for (Balle balle : balles) {
                if (!balle.estAuBord(5)) {
                    if (pointDedans(snoopy, balle)) {

                        if (!balle.getTouche()) {
                            balle.setTouche(true);

                            // On tue snoopy
                            snoopy.tuer();
                            tuer();
                        }
                    } else if (balle.getTouche()) {
                        balle.setTouche(false);
                    }
                }
            }
        }

        // Rafraichissement de l'ecran
        repaint();
    }
    
    // Décompte de 60 secondes
    public void clock() {
        if (!pause) {
            // Chaque seconde il change l'état d'une variable de Air
            if (timer == 0) {
                //Si on arrive à 0, Snoopy perd une vie
                snoopy.tuer();
                tuer();

                timer = 60;
            } else if (timer <= 60) {
                timer--;
            }
        }
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void tuer() {
        // Fin du jeu ?
        if (snoopy.getVies() == 0) {
            for (FinListener listener : listeners) {
                listener.perdu();
            }
        }
    }

    private void fin() {
        // Fin du jeu ?
        if (snoopy.getOiseaux() == carte.getNbOiseaux()) {
            for (FinListener listener : listeners) {
                listener.gagne();
            }
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
        carte.afficher(g2d, theme, MARGE_X_CARTE, MARGE_Y_CARTE);

        // Balles
        for (Balle balle : balles) {
            balle.afficher(g2d, theme, MARGE_X_CARTE, MARGE_Y_CARTE);
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
            float a = (etat*theme.getNbImgOiseau()/Aire.FPS);
            int num_anim = (int) Math.floor(a) % theme.getNbImgOiseau();


            g2d.drawImage(theme.getOiseauImg(num_anim), i*25+10, 3, 20, 20, null);
        }
        
        // Affichage de l'horloge
		g2d.setColor(Color.black);
		g2d.setFont(new Font ("Plain", Font.BOLD,LARG_IMG/3));
		
		timerString = String.valueOf(timer);
		g2d.drawString(timerString, LARG_IMG*2, 20);

		// Pause
        if (pause) {
            g2d.drawString("PAUSE", 5, getHeight()-5);
        }

        // Score
        g2d.drawString(String.valueOf(getScore()), getWidth() - 50, getHeight() - 5);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public int getScore() {
        return base_score + (timer * 100);
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
                if (!pause) {
                    snoopy.deplacer(carte, 0, -1);
                    tuer();
                    fin();
                }
                break;

            case KeyEvent.VK_DOWN:  // BAS
                if (!pause) {
                    snoopy.deplacer(carte, 0, 1);
                    tuer();
                    fin();
                }
                break;

            case KeyEvent.VK_LEFT:  // GAUCHE
                if (!pause) {
                    snoopy.deplacer(carte, -1, 0);
                    tuer();
                    fin();
                }
                break;

            case KeyEvent.VK_RIGHT: // DROITE
                if (!pause) {
                    snoopy.deplacer(carte, 1, 0);
                    tuer();
                    fin();
                }
                break;

            case KeyEvent.VK_A: // Attaque !!!
                if (pause || snoopy.getVies() == 0) {
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
                        tuer();
                    }
                }

                break;

            case KeyEvent.VK_P:
                pause = !pause;
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
