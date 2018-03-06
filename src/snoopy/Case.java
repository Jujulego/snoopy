package snoopy;

import com.sun.istack.internal.NotNull;

import java.util.LinkedList;

public class Case implements Affichable {
    // Attributs
    private LinkedList<Objet> objets = new LinkedList<>();

    // Méthodes
    @Override
    public String afficher() {
        if (objets.size() == 0) {
            return "  ";
        } else {
            return objets.getFirst().afficher();
        }
    }

    public boolean vide() {
        for (Objet objet : objets) {
            if (objet.estBloquant()) {
                return false;
            }
        }

        return true;
    }

    // Accesseur
    public LinkedList<Objet> listeObjets() {
        return objets;
    }

    public Objet getObjet() {
        return objets.getFirst();
    }

    public void ajouter(@NotNull Objet objet) {
        objets.add(objet);
        objets.sort((Objet obj1, Objet obj2) -> obj2.getZ() - obj1.getZ());
    }

    public void enlever(Objet objet) {
        objets.remove(objet);
    }
}
