package snoopy;

import java.util.Scanner;

/**
 * Classe de départ !!!
 *
 * @author julien
 */
public class Main {
    // Main
    /**
     * Propose le choix entre les modes console et graphique
     * puis active le mode choisi
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Choix du mode !
        boolean continuer = true;
        boolean graphique = true;

        while (continuer) {
            // Affichage
            Console.eff_ecran();
            Console.entete();
            System.out.println();
            System.out.println();
            System.out.println("    Choix du mode :");
            System.out.println("      1.   Console");
            System.out.println("      2.  Graphique");
            System.out.println();

            // Réponse
            String ligne = scanner.nextLine();
            switch (ligne) {
                case "1":
                    graphique = false;

                case "2":
                    continuer = false;
                    break;
            }
        }

        if (graphique) {
            // Menu !!!
            Fenetre fenetre = new Fenetre();

        } else {
            try {
                // Mode console
                Console console = new Console();
                console.menu();

            } catch (InterruptedException err) {
                err.printStackTrace();
            }

            System.exit(0);
        }
    }
}
	