package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Perdu extends JPanel {
    // Attributs
    private int y = 270;
    private float dy = -8.5f;
    private float ay = 0.2f;

    private Theme theme;
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private JButton btnRecommencer = new JButton("Recommencer");
    private JButton btnMenu = new JButton("Retourner au Menu");

    // Constructeur
    public Perdu(Theme theme) {
        this.theme = theme;

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

        // Oiseaux
        BufferedImage oiseau = theme.getOiseauImg((y/5) % theme.getNbImgOiseau());
        g2d.drawImage(oiseau, 25, y, 50, 50, null);

        // Calcul du symétrique
        AffineTransform ty = AffineTransform.getScaleInstance(-1, 1);
        ty.translate(-oiseau.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(ty, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        g2d.drawImage(op.filter(oiseau, null), 325, y, 50, 50, null);
    }

    private void animer() {
        // Evolution
        dy += ay;
        y += dy;

        // Rebonds
        if (y > getHeight() - 50) {
            y = getHeight() - 50;
            dy = -8.5f;
        }

        repaint();
    }

    public void lancer() {
        // Animation !
        scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(this::animer, 0, 1000/30, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    // Accesseurs
    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnRecommencer() {
        return btnRecommencer;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
