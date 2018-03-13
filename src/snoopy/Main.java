package snoopy;

import javax.swing.*;
import java.util.Scanner;

public class Main {
    // Attributs
    public static final Scanner scanner = new Scanner(System.in);

    // Main
    public static void main(String[] args) {
        // Init
        Carte carte = new Carte(5, 5);

        Snoopy snoopy = new Snoopy(2, 2);
        carte.ajouter(snoopy);

        carte.ajouter(new Oiseau(0, 0));
        carte.ajouter(new Oiseau(0, 4));
        carte.ajouter(new Oiseau(4, 0));
        carte.ajouter(new Oiseau(4, 4));

        carte.ajouter(new BlocPoussable(2, 1));

        // Question !
        String rep = "";

        while (!rep.equals("o") && !rep.equals("n")) {
            System.out.println("Graphique ? o/n");
            rep = scanner.nextLine();
        }

        if (rep.equals("o")) {
            // Graphisme
            Aire aire = new Aire(carte, snoopy);
            aire.ajouterBalle(new Balle(
                    (int) (2.5 * Aire.LARG_IMG),
                    (int) (0.5 * Aire.LARG_IMG),
                    -2, 2
            ));

            JFrame fenetre = new JFrame();

            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setVisible(true);
            fenetre.setTitle("SnoopMan ECE");

            fenetre.setContentPane(aire);
            fenetre.setMinimumSize(aire.getMinimumSize());
            aire.requestFocus();

        } else {
            // Console
            boolean continuer = true;

            while (continuer) {
                System.out.println(carte.afficher());
                String cmd = scanner.next();

                switch (cmd) {
                    case "z":
                        snoopy.deplacer(carte, 0, -1);
                        break;

                    case "q":
                        snoopy.deplacer(carte, -1, 0);
                        break;

                    case "s":
                        snoopy.deplacer(carte, 0, 1);
                        break;

                    case "d":
                        snoopy.deplacer(carte, 1, 0);
                        break;

                    case "p":
                        continuer = false;
                        break;
                }
            }
        }
    }
}
