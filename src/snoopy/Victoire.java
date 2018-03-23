package snoopy;

import javax.swing.*;
import java.awt.*;

public class Victoire extends PanneauSol {
    // Attributs
    private JButton btnContinuer = new JButton("Continuer");
    private JButton btnMenu = new JButton("Retourner au Menu");

    // Constructeur
    public Victoire(Theme theme) {
        super(theme);

        // Ajout des boutons
        setLayout(null);
        add(btnContinuer);
        add(btnMenu);
        positionBoutons();
    }

    private void positionBoutons() {
        // Boutons
        Insets insets = getInsets();
        Dimension taille;

        // - recommencer
        taille = btnContinuer.getPreferredSize();
        btnContinuer.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );

        // - retourner au menu
        taille = btnMenu.getPreferredSize();
        btnMenu.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 75 + insets.top,
                taille.width, taille.height
        );
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Gagné !!!!
        g2d.setColor(Color.black);
        g2d.setFont(new Font ("Plain", Font.BOLD,50));

        g2d.drawString("GAGNÉ", getWidth()/2-100, 70);

        // Boutons
        positionBoutons();
    }

    // Accesseurs
    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnContinuer() {
        return btnContinuer;
    }
}
