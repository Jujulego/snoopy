package snoopy;

public abstract class Bloc extends Objet {
    // Constantes
    public static final int MARGE = 8;

    // Constructeur
    public Bloc(int x, int y) {
        super(x, y, 2);
    }
}
