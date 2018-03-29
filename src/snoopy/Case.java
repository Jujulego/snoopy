package snoopy;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Gestion d'une case. Permet la présence de plusieurs objets au même endroit
 *
 * @author julien
 */
public class Case implements Affichable, Animation {
    // Attributs
    private int x;
    private int y;
    private Teleporteur teleporteur = null;

    private LinkedList<Objet> objets = new LinkedList<>();
    private double etat = 1.0;

    // Constructeur
    /**
     * Construit la case
     *
     * @param x position dans la carte
     * @param y position dans la carte
     */
    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    // Méthodes
    @Override
    public void animer(Carte carte, Theme theme) {
        etat++;
        etat%=60;
    }

    @Override
    public boolean animation() {
        return true;
    }

    @Override
    public synchronized void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        // Affiche uniqement l'objet avec l'indice z le plus grand
        g2d.drawImage(theme.getCaseImg(0),
                bx+x*Moteur.LARG_IMG, by+y*Moteur.LONG_IMG,
                Moteur.LARG_IMG, Moteur.LONG_IMG,
                null
        );

    }

    /**
     * Affiche les objets posés sur la case
     *
     * @param g2d
     * @param theme thème à utiliser
     * @param bx coordonnées du coin haut gauche de la carte
     * @param by coordonnées du coin haut gauche de la carte
     */
    public synchronized void afficher_obj(Graphics2D g2d, Theme theme, int bx, int by) {
        Iterator<Objet> it = objets.descendingIterator();

        while (it.hasNext()) {
            Objet obj = it.next();
            obj.afficher(g2d, theme, bx, by);
        }
    }

    /**
     * Indique si la case est accessible.
     * Une case est accessible si il n'y a aucun objet bloquant dessus.
     *
     * @return true si accessible, false sinon
     */
    public synchronized boolean accessible() {
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
    public synchronized void ajouter(Objet objet) {
        objets.add(objet);

        // Tri décroissant sur l'indice Z
        objets.sort((Objet obj1, Objet obj2) -> obj2.getZ() - obj1.getZ());

        // Cas du teleporteur
        if (objet instanceof Teleporteur) {
            teleporteur = (Teleporteur) objet;
        }
    }

    /**
     * Enlève un objet à la case
     * @param objet l'objet à enlever
     */
    public synchronized void enlever(Objet objet) {
        if (!objets.remove(objet)) {
            return;
        }

        // Cas du téléporteur
        if (objet instanceof Teleporteur) {
            teleporteur = null;
        }
    }

    // Accesseurs
    public synchronized LinkedList<Objet> listeObjets() {
        return objets;
    }
    public synchronized Objet getObjet() {
        if (objets.size() == 0) {
            return null;
        }

        return objets.getFirst();
    }
    public synchronized Bloc getBloc() {
        for (Objet obj : objets) {
            if (obj instanceof Bloc) {
                return (Bloc) obj;
            }
        }

        return null;
    }
    public synchronized Bonus getBonus() {
        for (Objet obj : objets) {
            if (obj instanceof Bonus) {
                return (Bonus) obj;
            }
        }

        return null;
    }
    public synchronized Poussable getPoussable() {
        for (Objet obj : objets) {
            if (obj instanceof Poussable) {
                return (Poussable) obj;
            }
        }

        return null;
    }
    public Teleporteur getTeleporteur() {
        return teleporteur;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
