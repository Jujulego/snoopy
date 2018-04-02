package snoopy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Moteur de jeu
 * Gère les interactions entre les objets du jeu, leurs mouvements.
 * Algorithmes IA
 * Chargements et sauvegardes
 *
 * @author julien marin
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

    // Méthodes statiques
    /**
     * Test l'exsitance du niveau
     *
     * @param niveau niveau à tester
     * @return true si le niveau existe
     */
    public static boolean niveauExiste(String niveau) {
        return new File("niveaux/" + niveau + ".txt").exists();
    }

    /**
     * Charge un fichier niveau
     *
     * @param fichier nom du fichier à charger
     * @param theme thème à utiliser
     * @param score score de départ
     * @param vies nombre de vies
     * @return l'objet moteur controllant le jeu
     *
     * @throws IOException envoyée en cas d'erreur d'ouverture ou de lecture du fichier
     */
    public static Moteur charger(String fichier, Theme theme, int score, int vies) throws IOException {
    	
   		
    	//Ouverture fichier 
    	File file = new File("niveaux/" + fichier + ".txt");


        FileReader filereader = new FileReader(file);
    	BufferedReader buff = new BufferedReader(filereader);
        
    	//Déclaration variables 
    	String line = null;
    	String recup[] = null;
    	Carte carte = null;
    	Snoopy snoopy = null;
		
		//Première ligne = Taille de la map
		line = buff.readLine();
		recup = line.split(" ");
		int map_x = Integer.parseInt(recup[0]);
		int map_y = Integer.parseInt(recup[1]);
		
		//Création de la carte
		carte = new Carte(map_x,map_y);
		
		//Deuxième ligne = Coordonnées snoopy
		line = buff.readLine();
		recup = line.split(" ");
		
		if(fichier.equals("sauvegarde")) //Si on est dans un fichier de sauvegarde, on récupère également le nb de vies
			snoopy = new Snoopy(Integer.parseInt(recup[0]), Integer.parseInt(recup[1]), Integer.parseInt(recup[2]));		
		else //On est pas dans un fichier de sauvegarde, on prend le nombre de vies par défaut
			snoopy = new Snoopy(Integer.parseInt(recup[0]), Integer.parseInt(recup[1]), vies);
	
		carte.ajouter(snoopy); 		
   		
		//Le reste = Tous les autres éléments de la map
		for(int i=0; i < map_y; i++) {
		    //A chaque tour de boucle, une ligne est lue
			line = buff.readLine();
			    			
			//On décortique la ligne pour créer le perso adécuat 
			for(int j=0; j < map_x; j++) {
				
				switch(line.charAt(j)) {
					
					case 'o': //Oiseau
						carte.ajouter(new Oiseau(j, i));
						break;

					case 'C': //Bloc Cassable
						carte.ajouter(new BlocCassable(j, i));
						break;

					case 'P': //Piege
						carte.ajouter(new BlocPiege(j, i));
						break;

					case 'D': //Déplaçable
						carte.ajouter(new BlocPoussable(j, i));
						break;

					case 'X': //Bad Snoopy
						carte.ajouter(new BadSnoopy(j, i));
						break;

					case 'F': //Bloc Fixe
						carte.ajouter(new Bloc(j, i));
						break;

                    case 'i': // Invincible !!!
                        carte.ajouter(new Invincible(j, i));
                        break;

                    case 'p': // Pause !!!
                        carte.ajouter(new Pause(j, i));
                        break;

					//Ajouter les téléporteur
					//Nombres dans le txt. Chaque même nombre est lié au précédant
						
				}
			}
			
		}

    	//Téléporteurs manuellement ajoutés
        Teleporteur tp1 = new Teleporteur(4,4);
        Teleporteur tp2 = new Teleporteur(16, 6, tp1);
        tp1.setPaire(tp2);
        
        carte.ajouter(tp1);
        carte.ajouter(tp2);
        
        //Si on est dans un fichier de sauvegarde, des données sont ajoutées à la fin. On prend la ligne d'après 
        if(fichier.equals("sauvegarde")) {
        	line = buff.readLine();
        	recup = line.split(" ");
    		
        	score = Integer.parseInt(recup[0]);	
    		
        }
		
        
        // Création du moteur
        Moteur moteur = new Moteur(carte, snoopy, theme, score);

        // Balle manuellement ajoutée
        moteur.ajouterBalle(new Balle(
                (int) (2.5 * Moteur.LARG_IMG), (int) (0.5 * Moteur.LONG_IMG),
                -4, 4
        ));
        
        return moteur;
    }
    
    //Timer, nombre de vie de snoopy, positions de tous les éléments, état de tous les élémtents
    //score 

    public static Moteur sauvegarder() {
		
    	
    	return null;
    	
    }
    
    // Méthodes
    /**
     * Gestion des animations et les mouvements automatiques
     * Appelée FPS fois par secondes
     */
    private void animer() {
        boolean bonusPause = bonusPauseActif();

        // Evolution des animation
        if (!pause) {
            if (bonusPause) {
                if (snoopy.aActivePause()) {
                    snoopy.animer(carte, theme);

                } else {
                    for (BadSnoopy badSnoopy : badSnoopies) {
                        if (badSnoopy.aActivePause()) {
                            badSnoopy.animer(carte, theme);
                        }
                    }
                }

            } else {
                // Mouvements
                for (Animation a : animations) {
                    if (a.animation()) a.animer(carte, theme);
                }

                // Touche ?
                for (Balle balle : balles) {
                    if (!balle.estAuBord(5)) {
                        if (balleDedans(snoopy, balle)) {
                            if (!balle.getTouche()) {
                                balle.setTouche(true);

                                // On tue snoopy
                                snoopy.tuer();
                                mort();
                            }
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
                    if (objet instanceof Deplacable && ((Deplacable) objet).deplacable()) {
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

        // Snoopy automatique
        LinkedList<Case> previsions = previsions(bonusPause);

        if (!pause && auto && snoopy.deplacable() && attente == 0) {
            if (!bonusPause || snoopy.aActivePause()) {
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
        if (!pause && !bonusPauseActif()) {
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
        historique = new LinkedList<>(); // Reset historique
    }

    /**
     * Prévision des positions interdites
     *
     * @param bonusPause indique si le bonusPause à été activé
     */
    public LinkedList<Case> previsions(boolean bonusPause) {
        LinkedList<Case> previsions = new LinkedList<>();
        for (Balle balle : balles) {
            previsions.addAll(balle.prevision(carte, bonusPause ?  0 : duree_depl * PREVISIONS));
        }

        for (BadSnoopy badSnoopy : badSnoopies) {
            int x = badSnoopy.getX();
            int y = badSnoopy.getY();

            // position actuelle
            previsions.add(carte.getCase(x, y));

            // Bonus pause
            if (bonusPause && !badSnoopy.aActivePause()) {
                continue;
            }

            // Positions futures
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
        if (pause || !snoopy.deplacable()) {
            return;
        }

        // Bonus Pause
        if (bonusPauseActif() && !snoopy.aActivePause()) {
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
            if (!badSnoopy.deplacable()) {
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
        // Mort !!
        if (!snoopy.deplacable() || pause || snoopy.getVies() == 0) {
            return;
        }

        // Bonus Pause
        if (bonusPauseActif() && !snoopy.aActivePause()) {
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
     * @param check_previsions indique s'il faut prendre en compte les prévisions
     * @param previsions positions futures des balles (si déjà calculées)
     * @return la liste des déplacements possibles
     */
    public LinkedList<Direction> directionsPossibles(int x, int y, boolean check_poussees, boolean check_casse, boolean check_previsions, LinkedList<Case> previsions) {
        // Création de l'objet
        LinkedList<Direction> liste = new LinkedList<>();

        // Prévision des balles
        if (previsions == null && check_previsions) {
            previsions = previsions(bonusPauseActif());
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
            if (check_previsions) {
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
                    true, true, true, previsions
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
     * @param fake indique que le calcul n'est pas fait dans le cadre de l'IA, mais pour un affichage
     *             => le résultat ne sera pas stocké dans l'historique
     * @return Mouvement conseillé
     */
    public Mouvement conseil(LinkedList<Case> previsions, boolean fake) {
        // Fini !
        if (snoopy.getOiseaux() == carte.getNbOiseaux()) {
            return new Mouvement(0, 0, null);
        }

        // Prévision de la position des balles
        if (previsions == null) {
            previsions = previsions(bonusPauseActif());
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
     * Teste si un personnage à activer le bonus Pause
     *
     * @return true si le bonus à été activé
     */
    public boolean bonusPauseActif() {
        // Snoopy ?
        if (snoopy.aActivePause()) {
            return true;
        }

        // BadSnoopies ?
        for (BadSnoopy badSnoopy : badSnoopies) {
            if (badSnoopy.aActivePause()) {
                return true;
            }
        }

        return false;
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
                listener.fin(getScore(), snoopy.getVies());
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

    /**
     * Accès à l'historique de l'IA
     *
     * @return les dernière positions de l'IA
     */
    public LinkedList<Coord> getHistorique() {
        return historique;
    }

    // Méthodes statiques
    /**
     * Calcule la nouvelle coordonée (x) en appliquant la direction donnée
     *
     * @param x coordonnée de départ
     * @param direction direction à applique
     * @return nouvelle coordonée
     */
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

    /**
     * Calcule la nouvelle coordonée (y) en appliquant la direction donnée
     *
     * @param y coordonnée de départ
     * @param direction direction à applique
     * @return nouvelle coordonée
     */
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
     * Indique si la balle se trouve sur la même case que l'objet
     *
     * @param obj objet à tester
     * @param balle balle à tester
     * @return true si les cases correspondent
     *
     * @see Moteur#pointDedans(int, int, int, int)
     */
    public static boolean balleDedans(Objet obj, Balle balle) {
        return pointDedans(obj.getX(), obj.getY(), balle.getX(), balle.getY());
    }

    /**
     * Indique si le point (coordonnées en pixels depuis le coin haut gauche de la carte) se
     * trouve sur la case indiquée par casex et casey
     *
     * @param casex coordonées de la case
     * @param casey coordonées de la case
     * @param ptx coordonées du point
     * @param pty coordonées du point
     * @return true si le point est dans la case
     */
    public static boolean pointDedans(int casex, int casey, int ptx, int pty) {
        return casex * LARG_IMG < ptx && ptx < (casex + 1) * LARG_IMG &&
                casey * LONG_IMG < pty && pty < (casey + 1) * LONG_IMG;
    }

    // Listener
    public interface MoteurListener {
        /**
         * Appellée quand Snoopy meurt
         */
        void mort();

        /**
         * Appellée quand Snoopy gagne
         *
         * @param score score final
         * @param vies nombre de vies à la fin de la partie
         */
        void fin(int score, int vies);

        /**
         * Appellée par Moteur.animer
         *
         * @see Moteur#animer()
         */
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

        /**
         * Calcule un nouvel objet Coord en appliquant la direction donnée a l'objet actuel
         *
         * @param dir direction à appliquer
         * @return un nouvel objet coordonnées
         */
        public Coord ajouter(Direction dir) {
            return new Coord(
                    ajouterDirX(x, dir),
                    ajouterDirY(y, dir)
            );
        }
    }

    /**
     * Stockage de coordonnées et d'une distance pour les BFS
     *
     * @see Moteur#distanceOiseau(int, int, LinkedList)
     * @see Moteur#distanceSnoopy(int, int, LinkedList)
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
     * Résultat de conseil :
     * représente un mouvement à appliquer à Snoopy
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
