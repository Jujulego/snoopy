package snoopy;

import java.awt.*;
import java.io.File;
import java.util.*;


public class Theme {
    // Constantes
    public static final int SOKOBAN = 1;
    public static final int DRUCKER = 2;
    public static final int SNOOPY = 3;

    // Attributs
    private int num_theme=0;
    private HashMap<Direction,ArrayList<Image>> perso = new HashMap<>();
    private ArrayList<Image> img_oiseau;
    private ArrayList<Image> anim_bloc;
    private ArrayList<Image> img_bloc; // bloc: 0=case vide   1=piégé   2=poussable
    private ArrayList<Image> img_case;

    private Image coeurPlein;
    private Image coeurVide;

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

        // Chargement de la cinématique de destruction
        anim_bloc = chargerAnimation("images/anim_bloc/anim%d.png");

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
    private ArrayList<Image> chargerAnimation(String fmt) {
        ArrayList<Image> liste = new ArrayList<>();
        int z = 0;

        while (true) {
            String fichier = String.format(fmt, z);

            if (new File(fichier).exists()) {
                liste.add(chargerImage(String.format(fmt, z)));
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
    private Image chargerImage(String chemin) {
        return Toolkit.getDefaultToolkit().getImage(chemin);
    }

    // Accès aux images
    public Image getPersoImg(Direction direction, int num_anim)
    {
        return perso.get(direction).get(num_anim);
    }
    public Image getOiseauImg(int num_anim)
    {
        return img_oiseau.get(num_anim);
    }
    public Image getBlocImg(int num_bloc)
    {
        return img_bloc.get(num_bloc);
    }
    public Image getAnimBlocImg(int num_anim)
    {
        return anim_bloc.get(num_anim);
    }
    public Image getCaseImg(int num_anim)
    {
        return img_case.get(num_anim);
    }

    public Image getCoeurPlein() {
        return coeurPlein;
    }

    public Image getCoeurVide() {
        return coeurVide;
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
