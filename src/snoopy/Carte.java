package snoopy;

import com.sun.istack.internal.Nullable;

public class Carte implements Affichable {
    // Attributs
    private Case[][] cases;

    // Taille carte
    private int tx;
    private int ty;

    // Constructeur
    public Carte(int tx, int ty) {
        // Matrice
        cases = new Case[ty][tx];
        this.tx = tx;
        this.ty = ty;

        for (int i = 0; i < ty; ++i) {
            for (int j = 0; j < tx; ++j) {
                cases[i][j] = new Case();
            }
        }
    }

    // Méthodes
    @Override
    public String afficher() {
        StringBuilder builder = new StringBuilder("");

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

    public void ajouter(Objet obj) {
        // Déjà un objet
        cases[obj.getY()][obj.getX()].ajouter(obj);
    }

    public void enlever(Objet obj) {
        cases[obj.getY()][obj.getX()].enlever(obj);
    }

    @Nullable
    public Case getCase(int x, int y) {
        try {
            return cases[y][x];
        } catch (ArrayIndexOutOfBoundsException err) {
            return null;
        }
    }
}
