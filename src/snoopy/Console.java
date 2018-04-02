package snoopy;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Gestion du mode console
 *
 * @author julien
 */
public class Console implements Moteur.MoteurListener {
    // Attributs
    private int tx, ty;
    private char[][] buffer;
    private Scanner scanner = new Scanner(System.in);

    private Moteur moteur;
    private boolean fin = false;

    private int vies = Snoopy.MAX_VIES;
    private int score = 0;

    private ScheduledExecutorService scheduler;

    // Méthodes
    /**
     * "Affiche" du texte dans la matrice interne
     *
     * @param str texte à afficher
     * @param x position du début du texte
     * @param y position du début du texte
     */
    public void print(String str, int x, int y) {
        for (char c : str.toCharArray()) {
            buffer[y][x] = c;

            ++x;
            if (x >= tx) {
                x = 0;
                ++y;
            }
        }
    }

    /**
     * Affiche le jeu dans la matrice, puis la matrice sur l'ecran
     */
    public void afficher() {
        // Carte
        int i = 1;
        for (String ligne : moteur.getCarte().afficher().split("\n")) {
            print(ligne, 0, i++);
        }

        // Balles
        for (Balle balle : moteur.getBalles()) {
            print(balle.afficher(),
                    (balle.getX() / Moteur.LARG_IMG)*3 + 1,
                    (balle.getY() / Moteur.LONG_IMG)*2 + 2
            );
        }

        // Vies
        for (i = 0; i < Snoopy.MAX_VIES; ++i) {
            print(i < moteur.getSnoopy().getVies() ? "C" : " ", tx - 2*(Snoopy.MAX_VIES - i) + 1, 0);
        }

        // Oiseaux
        for (i = 0; i < moteur.getSnoopy().getOiseaux(); ++i) {
            print(i < moteur.getSnoopy().getOiseaux() ? "Oi" : "  ", 3*i, 0);
        }

        // Infos
        print(String.format("%02d", moteur.getTimer()), tx/2-1, 0); // timer
        print(String.format("% 6d", moteur.getScore()), 1, ty - 1); // score

        if (moteur.isPause()) {
            print("PAUSE", tx - 7, ty - 1);
        } else if (moteur.isAuto()) {
            print("AUTO ", tx - 7, ty - 1);
        } else {
            print("     ", tx - 7, ty - 1);
        }

        // Ecran
        eff_ecran();
        for (int y = 0; y < ty; ++y) {
            for (int x = 0; x < tx; ++x) {
                System.out.print(buffer[y][x]);
            }

            System.out.println();
        }
    }

    /**
     * Gère les interactions clavier
     */
    public void clavier() {
        while (!fin) {
            String ligne = scanner.nextLine();

            switch (ligne.toLowerCase().toCharArray()[0]) {
                case 'z': // Haut !
                    moteur.deplacerSnoopy(0, -1);
                    break;

                case 'q': // Gauche !
                    moteur.deplacerSnoopy(-1, 0);
                    break;

                case 's': // Bas !
                    moteur.deplacerSnoopy(0, 1);
                    break;

                case 'd': // Droite !
                    moteur.deplacerSnoopy(1, 0);
                    break;

                case 'a': // Attaque !
                    moteur.attaquer();
                    break;

                case 'o': // Automatique
                    moteur.auto();
                    break;

                case 'p': // Pause
                    moteur.pause();
                    break;

                case 'e': // Quitter
                    quitterJeu();
                    break;

                case 'w': // Sauvegarde
                    break;
            }
        }
    }

    /**
     * Appelée en cas de mort du personnage
     */
    @Override
    public synchronized void mort() {
        score = 0;
        vies = 0;

        quitterJeu();
    }

    /**
     * Appelée en cas de victoire du personnage
     */
    @Override
    public synchronized void fin(int score, int vies) {
        this.score = score;
        this.vies = vies;

        quitterJeu();
    }

    @Override
    public void animer() {

    }

    /**
     * Affiche l'entête
     */
    public static void entete() {
        System.out.println("    ____                                     ");
        System.out.println("   /    \\                                    ");
        System.out.println("  /   __/ ____    ___    ___    ___  __    __");
        System.out.println(" _\\_  \\  / __ \\  / _ \\  / _ \\  / _ \\ \\ \\  / /");
        System.out.println("/     / / / / / / // / / // / / // /  \\ \\/ / ");
        System.out.println("\\____/ /_/ /_/  \\___/  \\___/ / ___/    \\  /  ");
        System.out.println("                            / /        / /   ");
        System.out.println("                           /_/        /_/    ");
    }

    /**
     * Menu console
     */
    public void menu() throws InterruptedException {
        String fichier = null;
        String choix = "";

        do {
            eff_ecran();

            // Affichage
            entete();
            System.out.println();
            System.out.println();
            System.out.println("             1.      Jouer                   ");
            System.out.println("             2.     Charger                  ");
            System.out.println("             3.   Mot de passe               ");
            System.out.println("             4.     Quitter                  ");
            System.out.println();

            // Choix
            choix = scanner.nextLine();

            // Traitement
            fichier = null;

            switch (choix) {
                case "3":
                    fichier = motDePasse();

                    if (fichier == null) {
                        break;
                    }

                case "2":
                    if (fichier == null) {
                        fichier = "sauvegarde";
                    }

                case "1":
                    boolean continuer = true;
                    int num_niveau = 1;

                    while (continuer) {
                        try {
                            if (fichier == null) {
                                fichier = String.format("map%d", num_niveau);
                            }

                            lancerJeu(charger(fichier));
                            attenteJeu();

                            if (vies != 0) {
                                continuer = victoire();
                                num_niveau++;
                            } else {
                                continuer = perdu(MotDePasse.encode(fichier));
                                vies = Snoopy.MAX_VIES;
                                score = 0;
                            }

                            fichier = null;

                        } catch (IOException ignored) {
                            break;
                        }
                    }

                    break;

                case "4":
                    break;
            }
        } while(!choix.equals("4"));
    }

    /**
     * Lance le chargement du jeu et associe la console au Moteur
     */
    public Moteur charger(String fichier) throws IOException {
        // Chargement
        Moteur moteur = Moteur.charger(fichier, new Theme(Theme.CONSOLE), 0, Snoopy.MAX_VIES);
        moteur.ajouterBalle(new Balle(
                (int) (2.5 * Moteur.LARG_IMG), (int) (0.5 * Moteur.LONG_IMG),
                -4, 4
        ));

        // Association
        moteur.ajouterMoteurListener(this);

        // Fini !
        return moteur;
    }

    /**
     * Active le jeu en mode console
     */
    public void lancerJeu(Moteur moteur) {
        this.moteur = moteur;
        moteur.lancer(30);

        // Création buffer
        tx = moteur.getCarte().getTx()*3 + 1;
        ty = moteur.getCarte().getTy()*2 + 3;
        buffer = new char[ty][tx];

        // Initialisation
        for (int i = 0; i < ty; ++i) {
            for (int j = 0; j < tx; ++j) {
                buffer[i][j] = ' ';
            }
        }

        // Activation !!!
        scheduler = new ScheduledThreadPoolExecutor(2);
        scheduler.scheduleAtFixedRate(this::afficher, 0, 1000/15, TimeUnit.MILLISECONDS);
        scheduler.schedule(this::clavier, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Point d'attente : le main thread attend ici que le jeu termine
     *
     * @throws InterruptedException en cas d'interruption externe
     */
    public synchronized void attenteJeu() throws InterruptedException {
        fin = false;

        while (!fin) {
            wait();
        }

        arreterJeu();
        afficher();
    }

    /**
     * Appelée pour réveiller le main thread et mettre fin au jeu
     */
    public synchronized void quitterJeu() {
        fin = true;
        notify();
    }

    /**
     * Arrête le jeu
     */
    public void arreterJeu() {
        scheduler.shutdownNow();
        scanner = new Scanner(System.in);
    }

    /**
     * Menu victoire
     *
     * @return true si on continue à jouer
     */
    public boolean victoire() {
        String choix = "";

        do {
            eff_ecran();

            // Affichage
            entete();
            System.out.println();
            System.out.println();
            System.out.println("           1.     Continuer                  ");
            System.out.println("           2.  Retourner au menu             ");
            System.out.println();

            // Choix
            choix = scanner.nextLine();

            // Traitement
            switch (choix) {
                case "1":
                    return true;

                case "2":
                    return false;
            }
        } while(true);
    }

    /**
     * Menu perte
     *
     * @param mdp mot de passe du niveau que l'on vient de perdre
     * @return true si on continue à jouer
     */
    public boolean perdu(String mdp) {
        String choix = "";

        do {
            eff_ecran();

            // Affichage
            entete();
            System.out.println("                             mdp: " + mdp);
            System.out.println();
            System.out.println("           1.    Recommencer                 ");
            System.out.println("           2.  Retourner au menu             ");
            System.out.println();

            // Choix
            choix = scanner.nextLine();

            // Traitement
            switch (choix) {
                case "1":
                    return true;

                case "2":
                    return false;
            }
        } while(true);
    }

    /**
     * Menu mot de passe
     *
     * @return le nom du niveau à charger, ou null en cas de retour menu
     */
    public String motDePasse() {
        String choix = "";

        do {
            eff_ecran();

            // Affichage
            entete();
            System.out.println();
            System.out.println();
            System.out.println("        1.  Entrer un mot de passe           ");
            System.out.println("        2.    Retourner au menu              ");
            System.out.println();

            // Choix
            choix = scanner.nextLine();

            // Traitement
            switch (choix) {
                case "1":
                    eff_ecran();

                    // Affichage
                    entete();
                    System.out.println();
                    System.out.println();
                    System.out.print("    Mot de passe : ");

                    // Récupération
                    String fichier = MotDePasse.decode(scanner.nextLine());

                    if (Moteur.niveauExiste(fichier)) {
                        return fichier;
                    }

                    break;

                case "2":
                    return null;
            }
        } while(true);
    }

    // Méthodes statiques
    /**
     * Efface l'ecran
     */
    public static void eff_ecran() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // Version Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            } else {
                // Version Linux
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ignored) {
        }
    }
}
