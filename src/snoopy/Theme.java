package snoopy;

import javax.swing.*;
import java.awt.*;


public class Theme {

    private Image[] perso;


    public Theme(int i)
    {
        perso=new Image[4];
        for (int z=0;z<perso.length;z++)
        {
            perso[z]=Toolkit.getDefaultToolkit().getImage("images/theme"+i+"/perso"+z+".png");
        }
    }

    public Image get_truc(int i)
    {
        return perso[i];
    }


}
