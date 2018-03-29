package snoopy;

/**
 * Directions
 *
 * @author julien
 */
public enum Direction {
    HAUT, BAS, GAUCHE, DROITE;

    // Méthodes
    /**
     * Renvoie le déplacement en x associé
     * @return dx
     */
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

    /**
     * Renvoie le déplacement en y associé
     * @return dy
     */
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
