package snoopy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Console {
    // Attributs
    private int tx, ty;
    private char[][] buffer;

    private Moteur moteur;

    private ScheduledExecutorService scheduler;

    // Constructeur
    public Console(Moteur moteur) {
        this.moteur = moteur;
        moteur.lancer(1000/60);

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
    }

    // Méthodes
    public void clear() {
        for (int i = 0; i < ty; ++i) {
            for (int j = 0; j < tx; ++j) {
                buffer[i][j] = ' ';
            }
        }
    }

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
            print(i < moteur.getSnoopy().getVies() ? "Oi" : "  ", 3*i, 0);
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

    public void lancer() {
         scheduler = new ScheduledThreadPoolExecutor(1);
         scheduler.scheduleAtFixedRate(this::afficher, 0, 1000/25, TimeUnit.MILLISECONDS);
    }

    public void arreter() {
        scheduler.shutdown();
    }

    // Méthodes statiques
    public static void eff_ecran() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        // Création de la carte
        Carte carte = new Carte(5, 5);

        Snoopy snoopy = new Snoopy(2, 2);
        carte.ajouter(snoopy);

        carte.ajouter(new Oiseau(0, 0));
        carte.ajouter(new Oiseau(0, 4));
        carte.ajouter(new Oiseau(4, 0));
        carte.ajouter(new Oiseau(4, 4));

        carte.ajouter(new BlocPoussable(2, 1));
        carte.ajouter(new BlocCassable(0,2));
        carte.ajouter(new BlocPiege(2, 4));

        // Création de l'aire de jeu
        Moteur moteur = new Moteur(carte, snoopy, new Theme(Theme.SNOOPY), 0);
        moteur.ajouterBalle(new Balle(
                (int) (2.5 * Moteur.LARG_IMG), (int) (0.5 * Moteur.LONG_IMG),
                -4, 4
        ));

        // Mode console
        Console console = new Console(moteur);
        console.lancer();
    }
}
