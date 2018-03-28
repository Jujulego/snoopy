package snoopy;

import java.awt.*;
import java.util.LinkedList;

/**
 * Gestion de la grille.
 * Accès aux cases et gestion de l'affichage global
 *
 * @author julien
 */
public class Carte implements Affichable {
    // Attributs
    private Case[][] cases;

    // Taille carte
    private int tx;
    private int ty;

    private int nbOiseaux = 0;
    private LinkedList<BadSnoopy> badSnoopies = new LinkedList<>();

    // Constructeur
    /**
     * Construit une carte vide
     *
     * @param tx largeur de la carte
     * @param ty longueur de la carte
     */
    public Carte(int tx, int ty) {
        // Création de la matrice
        this.cases = new Case[ty][tx];
        this.tx = tx;
        this.ty = ty;

        // Création des cases
        for (int i = 0; i < ty; ++i) {
            for (int j = 0; j < tx; ++j) {
                cases[i][j] = new Case(j, i);
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
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {
        // Affichage des cases
        for (Case[] ligne : cases) {
            for (Case c : ligne) {
                c.afficher(g2d, theme, bx, by);
            }
        }

        for (Case[] ligne : cases) {
            for (Case c : ligne) {
                c.afficher_obj(g2d, theme, bx, by);
            }
        }

        // Grille
        g2d.setColor(Color.black);
        g2d.drawLine(bx, by, bx, by + ty * Moteur.LONG_IMG);
        g2d.drawLine(bx + tx * Moteur.LARG_IMG, by, bx + tx * Moteur.LARG_IMG, by + ty * Moteur.LONG_IMG);
        g2d.drawLine(bx, by, bx + tx * Moteur.LARG_IMG, by);
        g2d.drawLine(bx, by + ty * Moteur.LONG_IMG, bx + tx * Moteur.LARG_IMG, by + ty * Moteur.LONG_IMG);
    }

    /**
     * Ajoute un objet à sa case.
     * Compte les oiseaux et ajoute les BadSnoopies dans une liste
     *
     * @param obj objet à  ajouter
     */
    public void ajouter(Objet obj) {
        // Ajoute l'objet
        cases[obj.getY()][obj.getX()].ajouter(obj);

        if (obj instanceof Oiseau) {
            ++nbOiseaux; // compte des
        } else if (obj instanceof BadSnoopy) {
            badSnoopies.add((BadSnoopy) obj);
        }
    }

    /**
     * Enlève un objet à une case
     *
     * @param obj objet à enlever
     */
    public void enlever(Objet obj) {
        cases[obj.getY()][obj.getX()].enlever(obj);
    }

    /**
     * Construit la liste des objets animés
     *
     * @return liste des animations
     */
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

    /**
     * Renvoie la case aux coordonnées données
     *
     * @return renvoie une case ou null si la coordonnée n'existe pas
     */
    public Case getCase(int x, int y) {
        try {
            return cases[y][x];
        } catch (ArrayIndexOutOfBoundsException err) {
            return null;
        }
    }
 
    // Accesseurs
    public int getTx() {
        return tx;
    }
    public int getTy() {
        return ty;
    }
    public int getNbOiseaux() {
        return nbOiseaux;
    }
    public LinkedList<BadSnoopy> getBadSnoopies() {
        return badSnoopies;
    }
}
