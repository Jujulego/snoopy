package snoopy;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Balle implements Animation, Affichable {
    // Constantes
    public static final int RAYON = 10;

    // Attributs
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int angle_rot=1;

    private boolean touche = false;

    // Constructeur
    public Balle(int x, int y, int dx, int dy) {
        // Attributs
        this.dx = dx;
        this.dy = dy;

        this.x = x;
        this.y = y;
    }

    // Méthodes
    public boolean estAuBordX(int marge) {
        return (x + marge) / Moteur.LARG_IMG != (x - marge) / Moteur.LARG_IMG;
    }
    public boolean estAuBordY(int marge) {
        return (y + marge) / Moteur.LONG_IMG != (y - marge) / Moteur.LONG_IMG;
    }
    public boolean estAuBord(int marge) {
        return estAuBordX(marge) || estAuBordY(marge);
    }

    @Override
    public String afficher() {
        return "Ba";
    }

    @Override
    public void animer(Carte carte, Theme theme) {
        // Déplacement
        x += dx;
        y += dy;

        // Rebonds sur les bords
        if (x <= RAYON) {
            x = RAYON;
            dx = -dx;
        } else if (x + RAYON >= carte.getTx() * Moteur.LARG_IMG) {
            x = carte.getTx() * Moteur.LARG_IMG - RAYON;
            dx = -dx;
        }

        if (y <= RAYON) {
            y = RAYON;
            dy = -dy;
        } else if (y + RAYON >= carte.getTy() * Moteur.LONG_IMG) {
            y = carte.getTy() * Moteur.LONG_IMG - RAYON;
            dy = -dy;
        }

        // Rebond sur les blocs
        boolean rebond_x = false, rebond_y = false;
        int rx = dx < 0 ? -1 : 1;
        int ry = dy < 0 ? -1 : 1;

        // Case à côté (x)
        Case case_suiv = carte.getCase(
                ((x + RAYON * rx + dx) / Moteur.LARG_IMG),
                y / Moteur.LONG_IMG
        );
        if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
            rebond_x = true;
        }

        // Case à côté (y)
        case_suiv = carte.getCase(
                x / Moteur.LARG_IMG,
                ((y + RAYON * ry + dy) / Moteur.LONG_IMG)
        );
        if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
            rebond_y = true;
        }

        // Case en diagonale
        if (!rebond_x && ! rebond_y) {
            case_suiv = carte.getCase(
                    ((x + RAYON * rx + dx) / Moteur.LARG_IMG),
                    ((y + RAYON * ry + dy) / Moteur.LONG_IMG)
            );
            if (case_suiv != null && case_suiv.getObjet() instanceof Bloc) {
                rebond_x = true;
                rebond_y = true;
            }
        }

        if (rebond_x && (x + RAYON * rx) / Moteur.LARG_IMG != (x + RAYON * rx + dx) / Moteur.LARG_IMG) {
            dx *= -1;
        }

        if (rebond_y && (y + RAYON * ry) / Moteur.LONG_IMG != (y + RAYON * ry + dy) / Moteur.LONG_IMG) {
            dy *= -1;
        }
    }

    /**
     * Renvoie la liste des case traversées par la balle dans "delta" frames
     *
     * @param carte etat de la carte
     * @param delta nombre de frames à évaluer
     * @return les cases qui seront traversées en l'etat
     */
    public LinkedList<Case> prevision(Carte carte, int delta) {
        LinkedList<Case> cases = new LinkedList<>();

        // Déjà la case actuelle !
        cases.addFirst(carte.getCase(
                x / Moteur.LARG_IMG,
                y / Moteur.LONG_IMG
        ));

        // Copie !
        Balle balle = new Balle(x, y, dx, dy);
        for (int i = 0; i < delta; ++i) {
            // Mouvement de la balle
            balle.animer(carte, null); // le theme n'est pas utilisé !

            Case ncase = carte.getCase(balle.x / Moteur.LARG_IMG, balle.y / Moteur.LONG_IMG);
            boolean presente = false;

            for (Case case_ : cases) {
                if (case_.getX() == ncase.getX() && case_.getY() == ncase.getY()) {
                    presente = true;
                    break;
                }
            }

            if (!presente) {
                cases.addFirst(ncase);
            }
        }

        return cases;
    }

    @Override
    public boolean animation() {
        return true;
    }

    @Override
    public void afficher(Graphics2D g2d, Theme theme, int bx, int by) {

        BufferedImage bimg = theme.getBalleImg();

        if(theme.getNumTheme() != Theme.SNOOPY)
        {
            angle_rot = (angle_rot + 1) % 24;
            AffineTransform ma_rotation = AffineTransform.getRotateInstance(angle_rot*Math.PI/12, theme.getBalleImg().getWidth() / 2, theme.getBalleImg().getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(ma_rotation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            bimg = op.filter(theme.getBalleImg(), null);
        }
        g2d.drawImage(bimg,
                bx + x - RAYON , by + y - RAYON ,
                2*RAYON, 2*RAYON,
                null
        );

    }

    // - accesseurs
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getTouche() {
        return touche;
    }

    public void setTouche(boolean touche) {
        this.touche = touche;
    }
}
