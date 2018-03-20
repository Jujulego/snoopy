package snoopy;

import java.awt.*;
import java.util.LinkedList;

/**
 * Gestion de la grille.
 * AccÃ¨s aux cases et gestion de l'affichage
 */
public class Carte implements Affichable {
    // Attributs
    private Case[][] cases;

    // Taille carte
    private int tx;
    private int ty;

    private int nbOiseaux = 0;

    // Constructeur
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
        // Grille
        g2d.setColor(Color.black);
        for (int i = 0; i <= tx; ++i) {
            g2d.drawLine(bx + i * Aire.LARG_IMG, by, bx + i * Aire.LARG_IMG, by + ty * Aire.LONG_IMG);
        }

        for (int i = 0; i <= ty; ++i) {
            g2d.drawLine(bx, by + i * Aire.LONG_IMG, bx + tx * Aire.LARG_IMG, by + i * Aire.LONG_IMG);
        }

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
    }

    /**
     * Ajoute un objet à  une case
     * @param obj objet à  ajouter
     */
    public void ajouter(Objet obj) {
        cases[obj.getY()][obj.getX()].ajouter(obj);

        if (obj instanceof Oiseau) {
            ++nbOiseaux;
        }
    }

    /**
     * Enlève un objet Ã  une case
     * @param obj objet Ã  enlever
     */
    public void enlever(Objet obj) {
        cases[obj.getY()][obj.getX()].enlever(obj);
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

    /**
     * Renvoie la case aux coordonnÃ©es données
     * @return renvoie une case ou null si la coordonnÃ©e n'existe pas
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
}
