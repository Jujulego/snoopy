package snoopy;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Représente une balle.
 * Gère le mouvement de la balle, et son affichage
 *
 * @author julien benjamin
 */
public class Balle implements Animation, Affichable {
    // Constantes
    public static final int RAYON = 10;

    // Attributs
    private int x; // Coordonnées du centre de la balle
    private int y;
    private int dx; // vecteur de vitesse
    private int dy;
    private int angle_rot=1;

    private boolean touche = false;
    private Teleporteur dernier_teleporteur = null;

    // Constructeur
    /**
     * Construit la balle
     *
     * @param x coodonnées de départ
     * @param y coodonnées de départ
     * @param dx vecteur vitesse initial
     * @param dy vecteur vitesse initial
     */
    public Balle(int x, int y, int dx, int dy) {
        // Attributs
        this.dx = dx;
        this.dy = dy;

        this.x = x;
        this.y = y;
    }

    /**
     * Copie une balle. Constructeur à usage interne
     *
     * @param copie Balle à copier
     */
    private Balle(Balle copie) {
        this(copie.x, copie.y, copie.dx, copie.dy);
        dernier_teleporteur = copie.dernier_teleporteur;
    }

    // Méthodes
    /**
     * Indique si le centre de la balle est au bord d'une case (bordures verticales)
     *
     * @param marge marge d'approximation
     * @return true si la balle est au bord
     */
    public boolean estAuBordX(int marge) {
        return (x + marge) / Moteur.LARG_IMG != (x - marge) / Moteur.LARG_IMG;
    }

    /**
     * Indique si le centre de la balle est au bord d'une case (bordures horizontales)
     *
     * @param marge marge d'approximation
     * @return true si la balle est au bord
     */
    public boolean estAuBordY(int marge) {
        return (y + marge) / Moteur.LONG_IMG != (y - marge) / Moteur.LONG_IMG;
    }

    /**
     * Indique si le centre de la balle est au bord d'une case
     *
     * @param marge marge d'approximation
     * @return true si la balle est au bord
     */
    public boolean estAuBord(int marge) {
        return estAuBordX(marge) || estAuBordY(marge);
    }

    @Override
    public String afficher() {
        return "Ba";
    }

    /**
     * Gère les déplacements de la balle, les rebonds contre les bords et les blocs,
     * ainsi que la téléportation des balles
     *
     * @param carte carte affichée
     * @param theme thème à utiliser
     */
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

        // Téléportation
        if (!estAuBord(RAYON)) {
            Case case_ = carte.getCase(x / Moteur.LARG_IMG, y / Moteur.LONG_IMG);
            Teleporteur tp = case_.getTeleporteur();

            if (tp != null && tp.getPaire() != null) {
                if (dernier_teleporteur == null) {
                    Case arr = carte.getCase(tp.getPaire().getX(), tp.getPaire().getY());

                    if (arr.accessible()) {
                        x = arr.getX() * Moteur.LARG_IMG + x % Moteur.LARG_IMG;
                        y = arr.getY() * Moteur.LONG_IMG + y % Moteur.LONG_IMG;

                        dernier_teleporteur = tp;
                    }
                }
            } else {
                dernier_teleporteur = null;
            }
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
        Balle balle = new Balle(this);

        // Prévisions
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

    /**
     * Affiche une image et la fait tourner, suaf pour le thème Snoopy
     *
     * @param g2d
     * @param theme theme à utiliser
     * @param bx coordonnées de base
     * @param by coordonnées de base
     */
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

    /**
     * Indique si la balle à déjà touché Snoopy dans cette case
     *
     * @return true si la balle à déjà touché Snoopy
     */
    public boolean getTouche() {
        return touche;
    }

    /**
     * Indique au système si la balle est en train de toucher Snoopy
     * Pour de ne pas enlever plus d'une vie par touche
     *
     * @param touche true si a balle touche Snoopy
     */
    public void setTouche(boolean touche) {
        this.touche = touche;
    }
}
