package snoopy;

import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.LinkedList;

/**
 * Gestion de la grille.
 * Accès aux cases et gestion de l'affichage
 */
public class Carte implements Affichable {
    // Attributs
    private Case[][] cases;

    // Taille carte
    private int tx;
    private int ty;

    // Constructeur
    public Carte(int tx, int ty) {
        // Création de la matrice
        this.cases = new Case[ty][tx];
        this.tx = tx;
        this.ty = ty;

        // Création des cases
        for (int i = 0; i < ty; ++i) {
            for (int j = 0; j < tx; ++j) {
                cases[i][j] = new Case();
            }
        }
    }

    // Méthodes
    @Override
    public String afficher() {
        // Stringbuilder est plus efficace que string quand on en ajoute beaucoup
        StringBuilder builder = new StringBuilder("");

        // Affichage de la grille, et du contenu des cases
        for (int i = 0; i < 2 * ty +1; ++i) {
            for (int j = 0; j < 2 * tx +1; ++j) {
                if (i % 2 == 0 && j % 2 == 0) {
                    builder.append("+");
                } else if (i % 2 == 0) {
                    builder.append("--");
                } else if (j % 2 == 0) {
                    builder.append("|");
                } else {
                    builder.append(cases[i/2][j/2].afficher());
                }
            }

            builder.append("\n");
        }

        return builder.toString();
    }

    @Override
    public void afficher(Graphics2D g2d) {
        // Affichage des cases
        for (Case[] ligne : cases) {
            for (Case c : ligne) {
                c.afficher(g2d);
            }
        }
    }

    /**
     * Ajoute un objet à une case
     * @param obj objet à ajouter
     */
    public void ajouter(Objet obj) {
        cases[obj.getY()][obj.getX()].ajouter(obj);
    }

    /**
     * Enlève un objet à une case
     * @param obj objet à enlever
     */
    public void enlever(Objet obj) {
        cases[obj.getY()][obj.getX()].enlever(obj);
    }

    /**
     * Renvoie la case aux coordonnées données
     * @return renvoie une case ou null si la coordonnée n'existe pas
     */
    @Nullable
    public Case getCase(int x, int y) {
        try {
            return cases[y][x];
        } catch (ArrayIndexOutOfBoundsException err) {
            return null;
        }
    }

    public LinkedList<Animation> objetsAnimes() {
        LinkedList<Animation> animations = new LinkedList<>();
        for (Case[] ligne : cases) {
            for (Case case_ : ligne) {
                for (Objet objet : case_.listeObjets()) {
                    if (objet instanceof Animation) {
                        animations.add((Animation) objet);
                    }
                }
            }
        }

        return animations;
    }

    // Accesseurs
    public int getTx() {
        return tx;
    }
    public int getTy() {
        return ty;
    }
}
