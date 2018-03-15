package snoopy;

import java.awt.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Gestion d'une case. Permet la présence de plusieurs objets au même endroit
 */
public class Case implements Affichable {
    // Attributs
    private LinkedList<Objet> objets = new LinkedList<>();

    // Méthodes
    @Override
    public String afficher() {
        // Affiche uniqement l'objet avec l'indice z le plus grand
        if (objets.size() == 0) {
            return "  ";
        } else {
            return objets.getFirst().afficher();
        }
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        // Affiche uniqement l'objet avec l'indice z le plus grand
        if (objets.size() != 0) {
            objets.getFirst().afficher(g2d, theme, bx, by);
        }
    }

    /**
     * Indique si la case est accessible.
     * Une case est accessible si il n'y a aucun objet bloquant dessus.
     *
     * @return true si accessible, false sinon
     */
    public boolean accessible() {
        for (Objet objet : objets) {
            if (objet.estBloquant()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ajoute un objet à la case
     * @param objet l'objet à ajouter
     */
    public void ajouter(Objet objet) {
        objets.add(objet);

        // Tri décroissant sur l'indice Z
        objets.sort((Objet obj1, Objet obj2) -> obj2.getZ() - obj1.getZ());
    }

    /**
     * Enlève un objet à la case
     * @param objet l'objet à enlever
     */
    public void enlever(Objet objet) {
        objets.remove(objet);
    }

    // Accesseurs
    public LinkedList<Objet> listeObjets() {
        return objets;
    }


    public Objet getObjet() {
        try {
        	Objet obj = objets.getFirst();
            return obj;
        } catch (NoSuchElementException err) {
            return null;
        }
    }
}
