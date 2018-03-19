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
    
    //Clock
    private int timer=60;
    private String timerString;
    
    // - animation
    private LinkedList<Balle> balles = new LinkedList<>();
    private LinkedList<Animation> animations = new LinkedList<>();
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public int etat=1;

    // Constructeur
    /**
     * @param carte  carte à afficher
     * @param snoopy personnage à controller
     */
    public Aire(Carte carte, Snoopy snoopy, Theme theme) {
        this.carte = carte;
        this.snoopy = snoopy;
        this.theme = theme;

        // Paramètres
        setDoubleBuffered(true);
        setMinimumSize(new Dimension(carte.getTx() * LARG_IMG + 1, carte.getTy() * LONG_IMG + MARGE_Y_CARTE + 38));
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
        for (Animation a : animations) {
            a.animer(carte);
        }

        etat++;
        etat%=60;

        // Rafraichissement de l'ecran
        repaint();
    }
    
    // Décompte de 60 secondes
    public void clock() {
       //Chaque seconde il change l'état d'une variable de Air
       if(timer==0) {
           //Si on arrive à 0, Snoopy perd une vie
           snoopy.tuer();
           timer=60;

       }
       else if(timer<=60)
           timer--;
    }

    public void stop() {
        scheduler.shutdown();
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

            // Touche ?
            if (!balle.estAuBord(5)) {
                if (pointDedans(snoopy, balle)) {

                    if (!balle.getTouche()) {
                        balle.setTouche(true);
                        snoopy.tuer();
                    }
                } else if (balle.getTouche()) {
                    balle.setTouche(false);
                }
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
            int num_anim=0;

            float a=(etat*theme.getNbImgOiseau()/Aire.FPS);
            num_anim = (int) Math.floor(a) % theme.getNbImgOiseau();


            g2d.drawImage(theme.getOiseauImg(num_anim), i*25+10, 3, 20, 20, null);
        }
        
        //Affichage de l'horloge
		g2d.setColor(Color.black);
		g2d.setFont(new Font ("Plain", Font.BOLD,LARG_IMG/3));
		
		timerString = String.valueOf(timer);
		g2d.drawString(timerString, LARG_IMG*2, 20);
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

    public void SetTheme(int a)
    {
        theme = new Theme (a);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // Ignoré
    }
}
