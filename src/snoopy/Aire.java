package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Aire extends JPanel implements KeyListener {
    // Constantes
    public static final int LARG_IMG = 50;
    public static final int LONG_IMG = 50;

    // Attributs
    private Carte carte;
    private Snoopy snoopy;

    // Constructeur
    public Aire(Carte carte, Snoopy snoopy) {
        this.carte = carte;
        this.snoopy = snoopy;

        // Paramètres
        setMinimumSize(new Dimension(carte.getTx() * LARG_IMG, carte.getTy() * LONG_IMG + 35));
        addKeyListener(this);
    }

    // Méthodes
    @Override
    protected void paintComponent(Graphics graphics) {
        // Options
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Grille
        g2d.setColor(Color.black);
        for (int i = 0; i <= carte.getTx(); ++i) {
            g2d.drawLine(i * LARG_IMG, 0, i * LARG_IMG, carte.getTy() * LONG_IMG);
        }

        for (int i = 0; i <= carte.getTy(); ++i) {
            g2d.drawLine(0, i * LONG_IMG, carte.getTx() * LARG_IMG, i * LONG_IMG);
        }

        // Objets
        carte.afficher(g2d);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP:
                snoopy.deplacer(carte, 0, -1);
                repaint();
                break;

            case KeyEvent.VK_DOWN:
                snoopy.deplacer(carte, 0, 1);
                repaint();
                break;

            case KeyEvent.VK_LEFT:
                snoopy.deplacer(carte, -1, 0);
                repaint();
                break;

            case KeyEvent.VK_RIGHT:
                snoopy.deplacer(carte, 1, 0);
                repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
}
