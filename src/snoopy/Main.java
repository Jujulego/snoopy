package snoopy;

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

        boolean continuer = true;

        while (continuer) {
            gotoxy(10, 0);
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

    public static void gotoxy(int x, int y) {
        System.out.print(String.format("%c[%d;%dm", 0x1B, y, x));
    }
}
