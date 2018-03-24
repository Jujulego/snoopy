package snoopy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Theme {
    // Constantes
    public static final int SOKOBAN = 1;
    public static final int DRUCKER = 2;
    public static final int SNOOPY = 3;

    // Attributs
    private int num_theme=0;
    private HashMap<Direction,ArrayList<BufferedImage>> perso = new HashMap<>();
    private ArrayList<BufferedImage> img_oiseau;
    private ArrayList<BufferedImage> anim_bloc;
    private ArrayList<BufferedImage> anim_boom;
    private ArrayList<BufferedImage> img_bloc; // bloc: 0=case vide   1=piégé   2=poussable
    private ArrayList<BufferedImage> img_case;

    private BufferedImage balle;
    private BufferedImage coeurPlein;
    private BufferedImage coeurVide;

    // Constructeur
    public Theme(int num_theme)
    {
        this.num_theme = num_theme;

        // Chargement images personnage
        perso.put(Direction.HAUT,   chargerAnimation(cheminTheme("perso0/anim%d")));
        perso.put(Direction.BAS,    chargerAnimation(cheminTheme("perso1/anim%d")));
        perso.put(Direction.GAUCHE, chargerAnimation(cheminTheme("perso2/anim%d")));
        perso.put(Direction.DROITE, chargerAnimation(cheminTheme("perso3/anim%d")));

        // Chargement des oiseaux
        img_oiseau = chargerAnimation(cheminTheme("oizo/anim%d"));

        // Chargement des cases
        img_case = chargerAnimation(cheminTheme("case/case%d"));

        // Chargement des blocs
        img_bloc = chargerAnimation("images/bloc/bloc%d.png");

        // Chargement de la cinématique de désaparition
        anim_bloc = chargerAnimation("images/anim_bloc/anim%d.png");

        //Chargement de la cinématique du boom boom
        anim_boom = chargerAnimation("images/anim_destruction/anim%d.png");

        //Chargement balle
        balle = chargerImage(cheminTheme("balle/balle"));

        // Chargment coeurs
        coeurPlein = chargerImage(cheminTheme("coeur/coeur1"));
        coeurVide = chargerImage(cheminTheme("coeur/coeur0"));
    }

    // Méthodes

    /**
     * Génère le chemin vers une image/animation spécifique au thème actuel
     *
     * @param img partie du chemin spécifique à l'image
     * @return chemin vers l'image/l'animation
     *
     * @see this.chargerAnimation
     * @see this.chargerImage
     */
    private String cheminTheme(String img) {
        return String.format("images/theme%d/%s.png", num_theme, img);
    }

    /**
     * Charge une animation dans un tableau
     *
     * @param fmt chemin vers l'animation. Doit contenir un "%d" qui sera remplacé par un numéro.
     *            ex : "images/theme3/perso0/anim%d.png"
     * @return le tableau contenant les images des animations
     */
    private ArrayList<BufferedImage> chargerAnimation(String fmt) {
        ArrayList<BufferedImage> liste = new ArrayList<>();
        int z = 0;

        while (true) {
            String fichier = String.format(fmt, z);

            if (new File(fichier).exists()) {
                liste.add(chargerImage(fichier));
                ++z;
            } else {
                break;
            }
        }

        return liste;
    }

    /**
     * Charge l'image demandée
     *
     * @param chemin chemin vers l'image
     * @return l'image
     */
    private BufferedImage chargerImage(String chemin) {
        try {
            Image img = ImageIO.read(new File(chemin));
            BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            bimg.getGraphics().drawImage(img, 0, 0, null);

            return bimg;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Accès aux images
    public BufferedImage getPersoImg(Direction direction, int num_anim)
    {
        return perso.get(direction).get(num_anim);
    }
    public BufferedImage getOiseauImg(int num_anim)
    {
        return img_oiseau.get(num_anim);
    }
    public BufferedImage getBlocImg(int num_bloc)
    {
        return img_bloc.get(num_bloc);
    }
    public BufferedImage getAnimBlocImg(int num_anim)
    {
        return anim_bloc.get(num_anim);
    }
    public BufferedImage getCaseImg(int num_anim)
    {
        return img_case.get(num_anim);
    }
    public BufferedImage getBoomImg(int num_anim)
    {
        return anim_boom.get(num_anim);
    }

    public BufferedImage getCoeurPlein() {
        return coeurPlein;
    }
    public BufferedImage getCoeurVide() {
        return coeurVide;
    }
    public BufferedImage getBalleImg() {
        return balle;
    }


    // le nombre d'images par animations
    public int getNbImgPerso(Direction direction) {
        return perso.get(direction).size();
    }
    public int getNbImgOiseau()
    {
        return img_oiseau.size();
    }
    public int getNbImageAnimBloc()
    {
        return anim_bloc.size();
    }
    public int getNbImageAnimBoom()
    {
        return anim_boom.size();
    }

    /**
     * Calcule le symétrique d'une image
     */
    public BufferedImage symetriqueX(BufferedImage img) {
        AffineTransform ty = AffineTransform.getScaleInstance(-1, 1);
        ty.translate(-img.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(ty, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return op.filter(img, null);
    }

    /**
     * Renvoie le numéro du thème
     *
     * @return le numéro du thème
     */
    public int getNumTheme()
    {
        return num_theme;
    }

    /**
     * Renvoie le nom du thème
     *
     * @return le nom du thème
     */
    public String getNomTheme() {
        switch (num_theme) {
            case SOKOBAN:
                return "Sokoban";

            case DRUCKER:
                return "Drucker";

            case SNOOPY:
                return "Snoopy";
        }

        return "";
    }
}
