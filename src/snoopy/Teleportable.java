package snoopy;

/**
 * Rend un objet téléportable
 */
public interface Teleportable {
    /**
     * Indique s'il est possible de téléporter l'objet
     *
     * @return true si l'objet est téléportable
     */
    boolean teleportable();

    /**
     * Téléporte l'objet
     *
     * @param teleporteur téléporteur arrivée
     */
    void teleportation(Teleporteur teleporteur);
}
