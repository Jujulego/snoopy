package snoopy;

public enum Direction {
    HAUT, BAS, GAUCHE, DROITE;

    // Méthodes
    public int dx() {
        switch (this) {
            case GAUCHE:
                return -1;

            case DROITE:
                return 1;

            default:
                return 0;
        }
    }

    public int dy() {
        switch (this) {
            case HAUT:
                return -1;

            case BAS:
                return 1;

            default:
                return 0;
        }
    }
}
