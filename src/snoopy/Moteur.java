package snoopy;

import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Moteur {
    // Constantes
    public static final int LARG_IMG = 50; // Largeur de base (d'une case de la grille)
    public static final int LONG_IMG = 50; // Longueur de base (d'une case de la grille)

    // Attributs
    // - jeu
    private Carte carte;    // Carte affichée
    private Snoopy snoopy;  // Le personnage controlé
    private int base_score; // Score de départ
    private int timer = 60; // Timer
    private LinkedList<Balle> balles = new LinkedList<>(); // gestion des balles

    // - animation
    private Theme theme;
    private boolean pause = false; // Arrêt de toute les animations
    private LinkedList<Animation> animations = new LinkedList<>();
    private ScheduledExecutorService scheduler;

    // - events
    private LinkedList<MoteurListener> listeners = new LinkedList<>();

    // Constructeur
    public Moteur(Carte carte, Snoopy snoopy, Theme theme, int base_score) {
        this.carte = carte;
        this.snoopy = snoopy;
        this.theme = theme;
        this.base_score = base_score;

        // Ajout des objets animés de la carte
        animations.addAll(carte.objetsAnimes());
    }

    // Méthodes
    /**
     * Gestion des animations et mise à jour de l'écran
     * Appelée FPS fois par secondes
     */
    private void animer() {
        // Evolution des animation
        if (!pause) {
            for (Animation a : animations) {
                if (a.animation()) a.animer(carte, theme);
            }

            // Touche ?
            for (Balle balle : balles) {
                if (!balle.estAuBord(5)) {
                    if (pointDedans(snoopy, balle)) {

                        if (!balle.getTouche()) {
                            balle.setTouche(true);

                            // On tue snoopy
                            snoopy.tuer();
                            mort();
                        }
                    } else if (balle.getTouche()) {
                        balle.setTouche(false);
                    }
                }
            }
        }

        // Listeners
        for (MoteurListener listener : listeners) {
            listener.animer();
        }
    }

    /**
     * Gestion du timer
     */
    private void clock() {
        if (!pause) {
            // Chaque seconde il change l'état d'une variable de Air
            if (timer == 0) {
                //Si on arrive à 0, Snoopy perd une vie
                snoopy.tuer();
                mort();

                timer = 60;
            } else if (timer <= 60) {
                timer--;
            }
        }
    }

    /**
     * Gestion de la pause
     */
    public void pause() {
        pause = !pause;
    }

    /**
     * Gestion du déplacement de snoopy
     *
     * @param dx mvt en x
     * @param dy mvt en y
     */
    public void deplacerSnoopy(int dx, int dy) {
        // Ignoré si animation en cours
        if (snoopy.animation() || pause) {
            return;
        }

        // Mouvement !
        snoopy.deplacer(carte, dx, dy);

        // Check fin
        mort();
        fin();
    }

    /**
     * Snoopy attaque le bloc face à lui : soit il casse, soit il était piégé
     */
    public void attaquer() {
        if (snoopy.animation() || pause || snoopy.getVies() == 0) {
            return;
        }

        // Case attaquée
        Case case_ = carte.getCase(
                ajouterDirX(snoopy.getX(), snoopy.getDirection()),
                ajouterDirY(snoopy.getY(), snoopy.getDirection())
        );

        if (case_ != null) {
            Objet objet = case_.getObjet();

            if (objet instanceof BlocCassable) {
                ((BlocCassable) objet).casser();
            } else if (objet instanceof BlocPiege) {
                ((BlocPiege) objet).toucher(snoopy);
                mort(); // Fin ?
            }
        }
    }

    /**
     * Condition de fin : mort
     */
    private void mort() {
        // Fin du jeu ?
        if (snoopy.getVies() == 0) {
            // Listeners
            for (MoteurListener listener : listeners) {
                listener.mort();
            }
        }
    }

    /**
     * Condition de fin : victoire
     */
    private void fin() {
        // Fin du jeu ?
        if (snoopy.getOiseaux() == carte.getNbOiseaux()) {
            // Listeners
            for (MoteurListener listener : listeners) {
                listener.fin();
            }
        }
    }

    /**
     * Ajoute une balle au jeu
     *
     * @param balle balle à ajouter
     */
    public void ajouterBalle(Balle balle) {
        animations.add(balle);
        balles.add(balle);
    }

    /**
     * Modifie le theme
     * @param theme nouveau theme !
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Retourne le score actuel
     * @return score !
     */
    public int getScore() {
        return base_score + (timer * 100);
    }

    public int getTimer() {
        return timer;
    }

    public Snoopy getSnoopy() {
        return snoopy;
    }

    public Carte getCarte() {
        return carte;
    }

    public LinkedList<Balle> getBalles() {
        return balles;
    }

    public boolean isPause() {
        return pause;
    }

    /**
     * Gestion des listeners
     *
     * @param listener listeners à ajouter
     */
    public void ajouterMoteurListener(MoteurListener listener) {
        listeners.add(listener);
    }

    /**
     * Active le scheduler
     */
    public void lancer(int freq) {
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(this::animer, 0, freq, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::clock, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Arrête le scheduler
     */
    public void stop() {
        scheduler.shutdown();
    }

    // Méthodes statiques
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

    // Listener
    public interface MoteurListener {
        void mort();
        void fin();

        void animer();
    }
}
