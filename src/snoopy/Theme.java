package snoopy;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class Theme {

    private HashMap<Direction,ArrayList<Image>> perso = new HashMap<>();
    //1er= direction: 0=haut   1=bas,   2=gauche,   3=droite;



    public Theme(int num_theme)
    {
        perso.put(Direction.HAUT, new ArrayList<>());
        perso.put(Direction.BAS, new ArrayList<>());
        perso.put(Direction.GAUCHE, new ArrayList<>());
        perso.put(Direction.DROITE, new ArrayList<>());


        int z=0;
        while(  new File("images/theme"+num_theme+"/perso0/anim"+z+".png").exists()  )
        {
            perso.get(Direction.HAUT).add(Toolkit.getDefaultToolkit().getImage("images/theme"+num_theme+"/perso0/anim"+z+".png"));
            z++;
        }
        z=0;
        while(  new File("images/theme"+num_theme+"/perso1/anim"+z+".png").exists()  )
        {
            perso.get(Direction.BAS).add(Toolkit.getDefaultToolkit().getImage("images/theme"+num_theme+"/perso1/anim"+z+".png"));
            z++;
        }
        z=0;
        while(  new File("images/theme"+num_theme+"/perso2/anim"+z+".png").exists()  )
        {
            perso.get(Direction.GAUCHE).add(Toolkit.getDefaultToolkit().getImage("images/theme"+num_theme+"/perso2/anim"+z+".png"));
            z++;
        }
        z=0;
        while(  new File("images/theme"+num_theme+"/perso3/anim"+z+".png").exists()  )
        {
            perso.get(Direction.DROITE).add(Toolkit.getDefaultToolkit().getImage("images/theme"+num_theme+"/perso3/anim"+z+".png"));
            z++;
        }
    }

    public Image getPersoImg(Direction direction, int num_anim)
    {
        return perso.get(direction).get(num_anim);
    }

    public int getNbImgPerso(Direction direction) {
        return perso.get(direction).size();
    }
}
