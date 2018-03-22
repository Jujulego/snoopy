package snoopy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Console {
    // Attributs
    private int tx, ty;
    private char[][] buffer;

    private ScheduledExecutorService scheduler;

    // Constructeur
    public Console(int tx, int ty) {
        this.tx = tx;
        this.ty = ty;
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
         scheduler.scheduleAtFixedRate(this::afficher, 0, 1, TimeUnit.SECONDS);
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
        Console console = new Console(50, 20);
        console.print("Test", 10, 10);
        console.lancer();
    }
}
