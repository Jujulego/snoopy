package snoopy;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {
    // Attributs
    private Theme theme;

    private JButton btnJouer = new JButton("Jouer");

    // Constructeur
    public Menu(Theme theme) {
        this.theme = theme;

        // Paramètres
        setMinimumSize(new Dimension(400, 320));
        setSize(400, 320);

        // Boutons
        setLayout(null);
        Insets insets = getInsets();
        Dimension taille;

        // - jouer
        add(btnJouer);
        taille = btnJouer.getPreferredSize();
        btnJouer.setBounds(
                200 - taille.width/2 + insets.left, 150 + insets.top,
                taille.width, taille.height
        );
        btnJouer.setBorderPainted(false);
        btnJouer.setForeground(Color.white);
        btnJouer.setBackground(Color.blue);
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        // Snoopy !
        graphics.setFont(new Font ("Plain", Font.BOLD,50));
        graphics.setColor(Color.black);

        graphics.drawString("Snoopy", 100, 50);
    }

    // Accesseurs
    public JButton getBtnJouer() {
        return btnJouer;
    }
}
