package snoopy;

public class Oiseau extends Objet {
    // Constructeur
    public Oiseau(int x, int y) {
        super(x, y);
    }

    // Méthodes
    @Override
    public String afficher() {
        return "Oi";
    }

    @Override
    public boolean estBloquant() {
        return false;
    }
}
