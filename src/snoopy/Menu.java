package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Menu extends PanneauSol {
    // Attributs
    private int balle_x = -150;
    private float balle_y = -80;
    private float balle_dy = 0;
    private float balle_ay = 0.2f;
    private int perso_x = -110;
    private int oiseau_x = -50;
    private int numAnimExplo;
    private int numAnimPerso;
    private int numAnimOiseau;
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private ArrayList<ChgThemeListener> listeners = new ArrayList<>();

    private JButton btnJouer = new JButton("Jouer");

    private JLabel lblTheme = new JLabel("");
    private JButton btnThemeM = new JButton("<<");
    private JButton btnThemeP = new JButton(">>");

    // Constructeur
    public Menu(Theme theme) {
        super(theme);
        lblTheme.setText(theme.getNomTheme());

        // Boutons
        setLayout(null);
        add(btnJouer);
        add(lblTheme);
        add(btnThemeP);
        add(btnThemeM);

        positionBoutons();

        btnThemeM.addActionListener((ActionEvent event) -> chgTheme(-1));
        btnThemeP.addActionListener((ActionEvent event) -> chgTheme(1));
    }

    // Méthodes
    private void positionBoutons() {
        // Boutons
        Insets insets = getInsets();
        Dimension taille;

        // - jouer
        add(btnJouer);
        taille = btnJouer.getPreferredSize();
        btnJouer.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );

        // - gestion themes
        Dimension tailleT = lblTheme.getPreferredSize();
        tailleT.width = 80;
        lblTheme.setBounds(
                (getWidth() - tailleT.width)/2 + insets.left, getSol() + 80 + insets.top,
                tailleT.width, tailleT.height
        );
        lblTheme.setHorizontalAlignment(SwingConstants.CENTER);

        taille = btnThemeM.getPreferredSize();
        btnThemeM.setBounds(
                (getWidth() - 50 - taille.width)/2 - taille.width + insets.left, getSol() + 75 + insets.top,
                taille.width, taille.height
        );

        taille = btnThemeP.getPreferredSize();
        btnThemeP.setBounds(
                (getWidth() + 50 + taille.width)/2 + insets.left, getSol() + 75 + insets.top,
                taille.width, taille.height
        );
    }

    @Override
    protected synchronized void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Mur
        int long_ = Moteur.LONG_IMG * 4/5;
        int larg  = Moteur.LARG_IMG * 4/5;
        int x = 0, y = getSol()-long_;

        while (x < getWidth()) {
            g2d.drawImage(theme.getBlocImg((x / larg) % 2),
                    x, y,
                    larg, long_,
                    null
            );

            if ((x / larg) % 2 == 0 && numAnimExplo >= 0) {
                g2d.drawImage(theme.getBoomImg(numAnimExplo),
                        x + larg/2, y - long_,
                        larg, long_,
                        null
                );
            }

            x += larg;
        }

        // Snoopy !
        g2d.drawImage(theme.getBalleImg(),
                balle_x, (int) balle_y + getSol(),
                2*Balle.RAYON, 2*Balle.RAYON,
                null
        );

        g2d.drawImage(theme.getPersoImg(Direction.DROITE, numAnimPerso),
                perso_x, getSol()-50,
                50, 50,
                null
        );

        g2d.drawImage(theme.getOiseauImg(numAnimOiseau),
                oiseau_x, getSol()-50,
                50, 50,
                null
        );

        // Titre
        g2d.setFont(new Font ("Plain", Font.BOLD,50));
        g2d.setColor(Color.black);

        g2d.drawString("Snoopy", getWidth()/2-100, 60);

        // Boutons
        positionBoutons();

        // On force la synchronisation de l'écran
        Toolkit.getDefaultToolkit().sync();
    }

    private synchronized void animer() {
        balle_x += 2;
        if (balle_x > getWidth()) {
            balle_x = perso_x - 40;
        }

        balle_dy += balle_ay;
        balle_y += balle_dy;
        if (balle_y >= -2*Balle.RAYON) {
            balle_dy = -balle_dy-balle_ay;
        }

        perso_x += 2;
        if (perso_x > getWidth()) {
            perso_x = oiseau_x-60;
        }

        oiseau_x += 2;
        if (oiseau_x > getWidth()) {
            oiseau_x = -50;
        }

        numAnimExplo++;// = (numAnimExplo + 1) % theme.getNbImageAnimBoom();
        if (numAnimExplo == theme.getNbImageAnimBoom()) {
            numAnimExplo = -6;
        }

        numAnimPerso = (numAnimPerso + 1) % theme.getNbImgPerso(Direction.DROITE);
        numAnimOiseau = (numAnimOiseau + 1) % theme.getNbImgOiseau();

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

    private synchronized void chgTheme(int d) {
        int num = theme.getNumTheme() + d;
        if (num < Theme.SOKOBAN) {
            num = Theme.SNOOPY;
        } else if (num > Theme.SNOOPY) {
            num = Theme.SOKOBAN;
        }

        theme = new Theme(num);
        lblTheme.setText(theme.getNomTheme());
        numAnimPerso = 0;
        numAnimOiseau = 0;

        for (ChgThemeListener listener : listeners) {
            listener.chgTheme(theme);
        }
    }

    public void addChgThemeListener(ChgThemeListener listener) {
        listeners.add(listener);
    }

    // Accesseurs
    public JButton getBtnJouer() {
        return btnJouer;
    }

    // Interface
    public interface ChgThemeListener {
        void chgTheme(Theme theme);
    }
}
