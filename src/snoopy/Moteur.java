package snoopy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Moteur de jeu
 * Gère les interactions entre les objets du jeu, leurs mouvements.
 * IA
 *
 * @author julien
 */
public class Moteur {
    // Constantes
    public static final int LARG_IMG = 50; // Largeur de base (d'une case de la grille)
    public static final int LONG_IMG = 50; // Longueur de base (d'une case de la grille)

    private static final int VAL_CASE_OISEAU = 4;
    private static final int MAX_DEPL = 200;
    private static final int PREVISIONS = 2;

    public static final int TAILLE_HISTORIQUE  = 10;
    public static final int ATTENTE_HISTORIQUE = 4;
    private static final double VAL_HISTORIQUE = 2;
    private static final int MALUS_STABLE = 50;

    // Attributs
    // - jeu
    private Carte carte;    // Carte affichée
    private Snoopy snoopy;  // Le personnage controlé
    private LinkedList<BadSnoopy> badSnoopies = new LinkedList<>();
    private int base_score; // Score de départ
    private int timer = 60; // Timer
    private LinkedList<Balle> balles = new LinkedList<>(); // gestion des balles

    // - ia
    private boolean auto = false; // Mode automatique
    private int attente = 0;
    private int duree_depl = 0;
    private LinkedList<Coord> historique = new LinkedList<>();

    // - animation
    private Theme theme;
    private boolean pause = false; // Arrêt de toute les animations
    private LinkedList<Animation> animations = new LinkedList<>();
    private ScheduledExecutorService scheduler;

    // - events
    private LinkedList<MoteurListener> listeners = new LinkedList<>();

    // Constructeur
    /**
     * Initialisation du Moteur de jeu
     *
     * @param carte carte du niveau
     * @param snoopy le personnage controlé
     * @param theme theme de jeu
     * @param base_score score de départ
     */
    public Moteur(Carte carte, Snoopy snoopy, Theme theme, int base_score) {
        this.carte = carte;
        this.snoopy = snoopy;
        this.theme = theme;
        this.base_score = base_score;

        // Ajout des objets animés de la carte
        animations.addAll(carte.objetsAnimes());
        badSnoopies.addAll(carte.getBadSnoopies());
    }

    // Méthodes
    /**
     * Gestion des animations et les mouvements automatiques
     * Appelée FPS fois par secondes
     */
    private void animer() {
        // Evolution des animation
        if (!pause) {
            // Mouvements
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

        // Téléportation
        for (int x = 0; x < carte.getTx(); ++x) {
            for (int y = 0; y < carte.getTy(); ++y) {
                Case case_ = carte.getCase(x, y);
                Teleporteur tp = case_.getTeleporteur();

                // Pas de téléporteur
                if (tp == null || tp.getPaire() == null) {
                    continue;
                }

                // Téléportation !!!
                Teleporteur tp_arr = tp.getPaire();
                Case case_arr = carte.getCase(tp_arr.getX(), tp_arr.getY());

                // Il peu y avoir un bloc sur le téléporteur arrivée
                if (!case_arr.accessible()) {
                    continue;
                }

                // On fait un copie de la liste, car on va modifier l'original
                for (Objet objet : new LinkedList<>(case_.listeObjets())) {
                    // On ne téléporte pas les objets en pleine animation
                    if (objet instanceof Animation && ((Animation) objet).animation()) {
                        continue;
                    }

                    // Téléportation !
                    if (objet instanceof Teleportable && ((Teleportable) objet).teleportable()) {
                        case_.enlever(objet);
                        case_arr.ajouter(objet);

                        ((Teleportable) objet).teleportation(tp_arr);
                    }
                }
            }
        }

        // Listeners
        for (MoteurListener listener : listeners) {
            listener.animer();
        }

        // Automatique
        LinkedList<Case> previsions = previsions();

        if (!pause && auto && !snoopy.animation() && attente == 0) {
            Mouvement mvt = conseil(previsions, false);

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

        // Mouvement des bad snoopies
        deplacerBadSnoopies(previsions);
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
     * Prévision des positions interdites
     */
    public LinkedList<Case> previsions() {
        LinkedList<Case> previsions = new LinkedList<>();
        for (Balle balle : balles) {
            previsions.addAll(balle.prevision(carte, duree_depl * PREVISIONS));
        }

        for (BadSnoopy badSnoopy : badSnoopies) {
            int x = badSnoopy.getX();
            int y = badSnoopy.getY();

            previsions.add(carte.getCase(x, y));

            for (Direction dir : directionsPossibles(x, y, false, false, false, previsions)) {
                int nx = ajouterDirX(x, dir);
                int ny = ajouterDirY(y, dir);

                Case case_ = carte.getCase(nx, ny);
                previsions.add(case_);

                // Cas de la téléportation
                if (case_.getTeleporteur() != null && case_.getTeleporteur().getPaire() != null) {
                    Teleporteur tp = case_.getTeleporteur().getPaire();
                    previsions.add(carte.getCase(tp.getX(), tp.getY()));
                }
            }
        }

        return previsions;
    }

    /**
     * Gestion du déplacement de snoopy
     *
     * @param dx mvt en x
     * @param dy mvt en y
     */
    public void deplacerSnoopy(int dx, int dy) {
        // Ignoré si animation en cours
        if (snoopy.animation() || pause || !snoopy.deplacable()) {
            return;
        }

        // Mouvement !
        snoopy.deplacer(carte, theme, dx, dy);

        // Check fin
        mort();
        fin();
    }

    /**
     * Gestion du déplacement des bad snoopies
     */
    public void deplacerBadSnoopies(LinkedList<Case> previsions) {
        // Pause !!!
        if (pause || badSnoopies.size() == 0) {
            return;
        }

        // Mouvement !
        boolean mouv = false;
        for (BadSnoopy badSnoopy : badSnoopies) {
            // Ignoré si animation en cours
            if (badSnoopy.animation() || !badSnoopy.deplacable()) {
                continue;
            }

            // En direction de Snoopy
            int dist = Moteur.MAX_DEPL;
            int dx = 0, dy = 0;

            if (badSnoopy.getX() != snoopy.getX() || badSnoopy.getY() != snoopy.getY()) {
                for (Direction dir : directionsPossibles(badSnoopy.getX(), badSnoopy.getY(), false, false, false, previsions)) {
                    int x = ajouterDirX(badSnoopy.getX(), dir);
                    int y = ajouterDirY(badSnoopy.getY(), dir);

                    int d = distanceSnoopy(x, y, previsions);
                    if (d < dist) {
                        dist = d;
                        dx = dir.dx();
                        dy = dir.dy();

                        if (d == 0) {
                            break;
                        }
                    }
                }
            }

            // Déplacement !
            if (dx != 0 || dy != 0) {
                badSnoopy.deplacer(carte, theme, dx, dy);
            }

            mouv = true;
        }

        // Check fin (si il y à eu du mouvement ...)
        if (mouv) {
            mort();
        }
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
                ((BlocCassable) objet).casser(carte, theme.getNumTheme() == Theme.CONSOLE);
            } else if (objet instanceof BlocPiege) {
                ((BlocPiege) objet).toucher(snoopy, carte, theme.getNumTheme() == Theme.CONSOLE);
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
     * @param previsions positions futures des balles (si déjà calculées)
     * @return la liste des déplacements possibles
     */
    public LinkedList<Direction> directionsPossibles(int x, int y, boolean check_poussees, boolean check_casse, boolean check_balles, LinkedList<Case> previsions) {
        // Création de l'objet
        LinkedList<Direction> liste = new LinkedList<>();

        // Prévision des balles
        if (previsions == null) {
            previsions = previsions();
        }

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
            if (!case_.accessible()) {
                Poussable poussable = case_.getPoussable();
                Bloc obj = case_.getBloc();

                if (check_poussees && poussable != null) { // Y a un poussable : peut on pousser ?
                    if (!poussable.poussable()) {
                        continue;
                    }

                    int px = ajouterDirX(nx, dir);
                    int py = ajouterDirY(ny, dir);

                    case_ = carte.getCase(px, py);
                    if ((case_ == null) || !case_.accessible()) {
                        continue;
                    }
                } else if (check_casse && (obj instanceof BlocCassable)) {
                } else {
                    continue;
                }
            }

            // Balles !!!!
            if (check_balles) {
                // Check balles !
                boolean echec = false;
                for (Case c : previsions) {
                    if (c.getX() == nx && c.getY() == ny) {
                        echec = true;
                        break;
                    }
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
        return directionsPossibles(snoopy.getX(), snoopy.getY(), check_poussees, check_casse, check_balles, null);
    }

    /**
     * Cherche le chemin vers l'oiseau le plus proche (BFS)
     *
     * @param dep_x point de départ
     * @param dep_y point de départ
     * @param previsions positions futures des balles
     * @return distance à parcourir jusqu'à l'oiseau le plus proche
     */
    private int distanceOiseau(int dep_x, int dep_y, LinkedList<Case> previsions) {
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

            Case case_ = carte.getCase(coord.x, coord.y);

            // Téléportation
            if (case_.getTeleporteur() != null && case_.getTeleporteur().getPaire() != null) {
                Teleporteur tp = case_.getTeleporteur().getPaire();
                case_ = carte.getCase(tp.getX(), tp.getY());

                if (case_.accessible()) {
                    // Téléportation !!
                    coord.x = case_.getX();
                    coord.y = case_.getY();

                    // La case arrivée est déjà traitée !!!
                    if (marques.contains(coord)) {
                        continue;
                    }
                    marques.add(coord);
                }
            }

            // Directions
            directions = directionsPossibles(coord.x, coord.y,
                    true, true, false, previsions
            );
            for (Direction dir : directions) {
                CoordDist ncoord = coord.ajouter(dir);
                case_ = carte.getCase(ncoord.x, ncoord.y);

                // Oiseau ?
                for (Objet obj : case_.listeObjets()) {
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
                    marques.add(ncoord);
                }
            }
        }

        return Moteur.MAX_DEPL;
    }

    /**
     * Cherche le chemin le plus court vers Snoopy (BFS)
     *
     * @param dep_x point de départ
     * @param dep_y point de départ
     * @param previsions positions futures des balles
     * @return distance à parcourir
     */
    private int distanceSnoopy(int dep_x, int dep_y, LinkedList<Case> previsions) {
        // Cas de base
        if (snoopy.getX() == dep_x && snoopy.getY() == dep_y) {
            return 0;
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

            Case case_ = carte.getCase(coord.x, coord.y);

            // Téléportation
            if (case_.getTeleporteur() != null && case_.getTeleporteur().getPaire() != null) {
                Teleporteur tp = case_.getTeleporteur().getPaire();
                case_ = carte.getCase(tp.getX(), tp.getY());

                if (case_.accessible()) {
                    // Téléportation !!
                    coord.x = case_.getX();
                    coord.y = case_.getY();

                    // La case arrivée est déjà traitée !!!
                    if (marques.contains(coord)) {
                        continue;
                    }
                    marques.add(coord);
                }
            }

            // Directions
            directions = directionsPossibles(coord.x, coord.y,
                    false, false, false, previsions
            );
            for (Direction dir : directions) {
                CoordDist ncoord = coord.ajouter(dir);
                case_ = carte.getCase(ncoord.x, ncoord.y);

                // Snoopy ?
                for (Objet obj : case_.listeObjets()) {
                    // La casse compte pour 1
                    if (obj instanceof Snoopy) {
                        return ncoord.distance;
                    }
                }

                // Ajout à la file sauf si déjà traité
                if (!marques.contains(ncoord)) {
                    file.addFirst(ncoord);
                    marques.add(ncoord);
                }
            }
        }

        return Moteur.MAX_DEPL;
    }

    /**
     * Heuristique, évalue le plateau. plus la valeur est grande mieux c'est !
     *
     * @param x point d'application de l'heuristique
     * @param y point d'application de l'heuristique
     * @param previsions positions futures des balles
     * @return la valeur de l'heuristique
     */
    public int heuristique(int x, int y, LinkedList<Case> previsions) {
        int heu = 0;

        // 1er critère : distance aux balles
        for (Case c : previsions) {
            if (c.getX() == x && c.getY() == y) {
                return 0; // Faut bouger de la !!!
            }
        }

        // 2eme critère : distance jusqu'à l'oiseau le plus proche
        heu += (MAX_DEPL - distanceOiseau(x, y, previsions)) * VAL_CASE_OISEAU;

        return heu;
    }

    /**
     * Conseil sur le mouvement à réaliser
     *
     * @param previsions positions futures des balles (si déjà calculées)
     * @param fake
     * @return Mouvement conseillé
     */
    public Mouvement conseil(LinkedList<Case> previsions, boolean fake) {
        // Prévision de la position des balles
        if (previsions == null) {
            previsions = previsions();
        }

        // Case actuelle
        LinkedList<Direction> directions = directionsPossibles(true, true, true);
        Mouvement conseil = new Mouvement(snoopy.getX(), snoopy.getY(), null);
        double heu = heuristique(conseil.x, conseil.y, previsions);
        if (heu == 0) {
            heu = -1;
        } else {
            heu -= MALUS_STABLE;
        }

        // Mouvement
        for (Direction dir : directions) {
            int nx = Moteur.ajouterDirX(getSnoopy().getX(), dir);
            int ny = Moteur.ajouterDirY(getSnoopy().getY(), dir);
            double nheu = heuristique(nx, ny, previsions);

            // Prise en compte de l'historique
            for (int i = ATTENTE_HISTORIQUE; i < historique.size(); ++i) {
                Coord c = historique.get(i);

                if (c.x == nx && c.y == ny) {
                    nheu -= (TAILLE_HISTORIQUE - i) * VAL_HISTORIQUE;
                }
            }

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

        // Historique
        if (!fake) {
            historique.addFirst(conseil);
            if (historique.size() > TAILLE_HISTORIQUE) {
                historique.removeLast();
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
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/freq, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::clock, 0, 1, TimeUnit.SECONDS);

        duree_depl = freq/Snoopy.DUREE_DEPL;
    }

    /**
     * Arrête le scheduler
     */
    public void stop() {
        scheduler.shutdown();
    }

    public LinkedList<Coord> getHistorique() {
        return historique;
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
    /**
     * Stockage de coordonneées
     */
    public class Coord {
        // Attributs
        protected int x, y;

        // Constructeur
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Méthodes
        @Override
        public int hashCode() {
            return x<<32 + y;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Coord) {
                return ((Coord) o).x == x && ((Coord) o).y == y;
            }

            return false;
        }

        public Coord ajouter(Direction dir) {
            return new Coord(
                    ajouterDirX(x, dir),
                    ajouterDirY(y, dir)
            );
        }
    }

    /**
     * Stockage de coordonneées et la distance pour les BFS
     */
    public class CoordDist extends Coord {
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

    /**
     * Résultat de conseil
     *
     * @see Moteur#conseil(LinkedList, boolean)
     */
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
