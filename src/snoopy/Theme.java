package snoopy;

import javax.swing.*;
import java.awt.*;


public class Theme {

    private Image snoopy_droite;


    public void charger()
    {
        snoopy_droite=Toolkit.getDefaultToolkit().getImage("images/snoopy_droite.jpg");
    }

    private void afficher(Graphics2D m) {

        m.drawImage(snoopy_droite,30,30,null);


    }

}
