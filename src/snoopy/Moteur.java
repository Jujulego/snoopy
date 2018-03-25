package snoopy;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Moteur {
    // Constantes
    public static final int LARG_IMG = 50; // Largeur de base (d'une case de la grille)
    public static final int LONG_IMG = 50; // Longueur de base (d'une case de la grille)

    public static final int VAL_OISEAU      = 25;
    public static final double VAL_PX_BALLE = 0.1;
    public static final int VAL_CASE_OISEAU = 5;
    public static final int MAX_DEPL = 200;

    // Attributs
    // - jeu
    private Carte carte;    // Carte affichée
    private Snoopy snoopy;  // Le personnage controlé
    private boolean auto = false; // Mode automatique
    private int base_score; // Score de départ
    private int timer = 60; // Timer
    private LinkedList<Balle> balles = new LinkedList<>(); // gestion des balles
    private int attente = 0;

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

        // Automatique
        if (!pause && auto && !snoopy.animation() && attente == 0) {
            Mouvement mvt = conseil();

            if (mvt.dir != null) {
                // Déplacement
                deplacerSnoopy(mvt.dir.dx(), mvt.dir.dy());

                // Attaque !
                if (mvt.casse) {
                    attaquer();
                    deplacerSnoopy(mvt.dir.dx(), mvt.dir.dy());
                }
            } else {
                attente = 12;
            }
        } else if (attente > 0) {
            attente--;
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
     * Gestion du mode auto
     */
    public void auto() {
        auto = !auto;
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
     * Renvoie les déplacements possibles pour Snoopy en un point donné.
     *
     * @param x coordonée du point
     * @param y coordonée du point
     * @param check_poussees indique s'il faut prendre en compte les possibilités de poussées
     * @param check_casse indique s'il faut prendre en compte les possibilités de casse
     * @param check_balles indique s'il faut prendre en compte les balles
     * @return la liste des déplacements possibles
     */
    public LinkedList<Direction> directionsPossibles(int x, int y, boolean check_poussees, boolean check_casse, boolean check_balles) {
        // Création de l'objet
        LinkedList<Direction> liste = new LinkedList<>();

        // Remplissage
        for (Direction dir : Direction.values()) {
            int nx = ajouterDirX(x, dir);
            int ny = ajouterDirY(y, dir);
            Case case_ = carte.getCase(nx, ny);

            // La case n'existe pas
            if (case_ == null) {
                continue;
            }

            // Objet
            Objet obj = case_.getObjet();
            if (check_poussees && obj instanceof Poussable) {
                // Y a un poussable : peut on pousser ?
                int px = ajouterDirX(nx, dir);
                int py = ajouterDirY(ny, dir);

                case_ = carte.getCase(px, py);
                if ((case_ == null) || !case_.accessible()) {
                    continue;
                }
            } else if (!(check_casse && obj instanceof BlocCassable) && !case_.accessible()) {
                continue;
            }

            // Balles !!!!
            if (check_balles) {
                // Check balles !
                boolean echec = false;
                for (Balle balle : balles) {
                    // Check à l'instant t
                    if ((nx == balle.getX() / LARG_IMG) && (ny == balle.getY() / LONG_IMG)) {
                        echec = true;
                        break;
                    }

                    //TODO: check à l'instant t+1 et t+2
                }

                if (echec) {
                    continue;
                }
            }

            // Ok !
            liste.addFirst(dir);
        }

        return liste;
    }

    /**
     * Applique directionsPossibles sur la position actuelle de Snoopy
     *
     * @param check_poussees indique s'il faut prendre en compte les possibilités de poussées
     * @param check_casse indique s'il faut prendre en compte les possibilités de casse
     * @param check_balles indique s'il faut prendre en compte les balles
     * @return la liste des déplacements possibles
     */
    public LinkedList<Direction> directionsPossibles(boolean check_poussees, boolean check_casse, boolean check_balles) {
        return directionsPossibles(snoopy.getX(), snoopy.getY(), check_poussees, check_casse, check_balles);
    }

    /**
     * Cherche le chemin vers l'oiseau le plus proche (BFS)
     *
     * @param dep_x point de départ
     * @param dep_y point de départ
     * @return distance à parcourir jusqu'à l'oiseau le plus proche
     */
    private int distanceOiseau(int dep_x, int dep_y) {
        // Cas de base
        if (snoopy.getOiseaux() == carte.getNbOiseaux()) {
            return 0;
        }

        for (Objet obj : carte.getCase(dep_x, dep_y).listeObjets()) {
            if (obj instanceof Oiseau) {
                return 0;
            }
        }

        // Initialisation
        LinkedList<CoordDist> file = new LinkedList<>();
        Set<Coord> marques = new HashSet<>();
        LinkedList<Direction> directions;

        // BFS !
        CoordDist coord = new CoordDist(dep_x, dep_y, 0);
        marques.add(coord);
        file.addFirst(coord);

        while (!file.isEmpty()) {
            // Défilage
            coord = file.getLast();
            file.removeLast();

            // Directions
            directions = directionsPossibles(coord.x, coord.y,
                    false, false, false
            );
            for (Direction dir : directions) {
                CoordDist ncoord = coord.ajouter(dir);

                // Oiseau ?
                for (Objet obj : carte.getCase(ncoord.x, ncoord.y).listeObjets()) {
                    // La casse compte pour 1
                    if (obj instanceof BlocCassable) {
                        ncoord.distance++;

                    } if (obj instanceof Oiseau) {
                        return ncoord.distance;
                    }
                }

                // Ajout à la file sauf si déjà traité
                if (!marques.contains(ncoord)) {
                    file.addFirst(ncoord);
                }
            }
        }

        return 0;
    }

    /**
     * Heuristique, évalue le plateau. plus la valeur est grande mieux c'est !
     *
     * @param x point d'application de l'heuristique
     * @param y point d'application de l'heuristique
     * @return la valeur de l'heuristique
     */
    public double heuristique(int x, int y) {
        double heu = 0;

        // 1er critère : le nombre d'oiseaux récupéré
        heu += snoopy.getOiseaux() * VAL_OISEAU;

        // 2eme critère : distance aux balles
        double snoopy_x = (x + 0.5) * LARG_IMG;
        double snoopy_y = (y + 0.5) * LONG_IMG;

        for (Balle balle : balles) {
            heu += Math.sqrt(
                    Math.pow(snoopy_x - balle.getX(), 2) + Math.pow(snoopy_y - balle.getY(), 2)
            ) * VAL_PX_BALLE;
        }

        // 3eme critère : distance jusqu'à l'oiseau le plus proche
        heu += (MAX_DEPL - distanceOiseau(x, y)) * VAL_CASE_OISEAU;

        return heu;
    }

    /**
     * Conseil sur le mouvement à réaliser
     *
     * @return Mouvement conseillé
     */
    public Mouvement conseil() {
        LinkedList<Direction> directions = directionsPossibles(true, true, true);
        Mouvement conseil = new Mouvement(snoopy.getX(), snoopy.getY(), null);
        double heu = heuristique(conseil.x, conseil.y);

        for (Direction dir : directions) {
            int nx = Moteur.ajouterDirX(getSnoopy().getX(), dir);
            int ny = Moteur.ajouterDirY(getSnoopy().getY(), dir);
            double nheu = heuristique(nx, ny);

            if (nheu > heu) {
                heu = nheu;
                conseil.x = nx;
                conseil.y = ny;
                conseil.dir = dir;
                conseil.casse = false;

                // Casse ?
                for (Objet obj : carte.getCase(nx, ny).listeObjets()) {
                    // La casse compte pour 1
                    if (obj instanceof BlocCassable) {
                        conseil.casse = true;
                        break;
                    }
                }
            }
        }

        return conseil;
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

    /**
     * Retourne le temps restant (secondes)
     * @return timer !
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Retourne l'objet Snoopy
     * @return Snoopy !
     */
    public Snoopy getSnoopy() {
        return snoopy;
    }

    /**
     * Retourne la carte
     * @return carte !
     */
    public Carte getCarte() {
        return carte;
    }

    /**
     * Retourne les balles
     * @return balles !
     */
    public LinkedList<Balle> getBalles() {
        return balles;
    }

    /**
     * Indique si le moteur est en pause (animations et balles arrêtées)
     * @return True si en pause
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * Indique si le mode automatique est activé
     * @return True si en actif
     */
    public boolean isAuto() {
        return auto;
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
     *
     * @param freq Fréquence d'execution de la méthodes animer (impacte la rapidité des animations)
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

    // Classes
    private class Coord {
        // Attributs
        protected int x, y;

        // Constructeur
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Méthodes
        public Coord ajouter(Direction dir) {
            return new Coord(
                    ajouterDirX(x, dir),
                    ajouterDirY(y, dir)
            );
        }
    }
    private class CoordDist extends Coord {
        // Attributs
        private int distance;

        // Constructeur
        public CoordDist(int x, int y, int distance) {
            super(x, y);
            this.distance = distance;
        }

        // Méthodes
        @Override
        public CoordDist ajouter(Direction dir) {
            Coord coord = super.ajouter(dir);
            return new CoordDist(coord.x, coord.y, distance+1);
        }
    }
    public class Mouvement extends Coord {
        // Attributs
        private Direction dir;
        private boolean casse = false;

        // Constructeur
        public Mouvement(int x, int y, Direction dir) {
            super(x, y);
            this.dir = dir;
        }

        // Méthodes
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Direction getDir() {
            return dir;
        }

        public boolean isCasse() {
            return casse;
        }
    }
}
