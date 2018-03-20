package snoopy;

import javax.swing.*;
import java.awt.*;

public class Perdu extends JPanel {
    // Attributs
    private JButton btnRecommencer = new JButton("Recommencer");
    private JButton btnMenu = new JButton("Retourner au Menu");

    // Constructeur
    public Perdu() {
        // Paramètres
        setDoubleBuffered(true);
        setMinimumSize(new Dimension(400, 320));
        setSize(400, 320);

        // Ajout des boutons
        setLayout(null);
        Insets insets = getInsets();
        Dimension taille;

        // - recommencer
        add(btnRecommencer);
        taille = btnRecommencer.getPreferredSize();
        btnRecommencer.setBounds(
                200 - taille.width/2 + insets.left, 180 + insets.top,
                taille.width, taille.height
        );

        // - retourner au menu
        add(btnMenu);
        taille = btnMenu.getPreferredSize();
        btnMenu.setBounds(
                200 - taille.width/2 + insets.left, 225 + insets.top,
                taille.width, taille.height
        );
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Perdu !!!!
        g2d.setColor(Color.black);
        g2d.setFont(new Font ("Plain", Font.BOLD,50));

        g2d.drawString("PERDU", 100, 70);
    }

    // Accesseurs
    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnRecommencer() {
        return btnRecommencer;
    }
}