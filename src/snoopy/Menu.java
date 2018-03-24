package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Menu extends PanneauSol {
    // Attributs
    private int oiseau_x = -50;
    private int perso_x = -130;
    private int balle_x = -190;

    private float balle_y = -80;
    private float balle_dy = 0;
    private float balle_ay = 0.2f;

    private int numAnimPerso;
    private int numAnimOiseau;
    private static Random random = new Random();
    private ArrayList<Integer> numAnimExplo = new ArrayList<>();
    private ScheduledExecutorService scheduler;

    private ArrayList<ChgThemeListener> listeners = new ArrayList<>();

    private JButton btnJouer = new JButton("Jouer");
    private JButton btnCharger = new JButton("Charger");
    private JButton btnMDP = new JButton("Mot de passe");

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
        add(btnCharger);
        add(btnMDP);
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
        taille = btnJouer.getPreferredSize();
        btnJouer.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 15 + insets.top,
                taille.width, taille.height
        );

        // - charger
        taille = btnCharger.getPreferredSize();
        btnCharger.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 55 + insets.top,
                taille.width, taille.height
        );

        // - mot de passe
        taille = btnMDP.getPreferredSize();
        btnMDP.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 95 + insets.top,
                taille.width, taille.height
        );

        // - gestion themes
        Dimension tailleT = lblTheme.getPreferredSize();
        tailleT.width = 80;
        lblTheme.setBounds(
                (getWidth() - tailleT.width)/2 + insets.left, getSol() + 140 + insets.top,
                tailleT.width, tailleT.height
        );
        lblTheme.setHorizontalAlignment(SwingConstants.CENTER);

        taille = btnThemeM.getPreferredSize();
        btnThemeM.setBounds(
                (getWidth() - 50 - taille.width)/2 - taille.width + insets.left, getSol() + 135 + insets.top,
                taille.width, taille.height
        );

        taille = btnThemeP.getPreferredSize();
        btnThemeP.setBounds(
                (getWidth() + 50 + taille.width)/2 + insets.left, getSol() + 135 + insets.top,
                taille.width, taille.height
        );
    }

    @Override
    protected synchronized void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Mur
        int long_ = (int) (Moteur.LONG_IMG * 4/5.0);
        int larg  = (int) (Moteur.LARG_IMG * 4/5.0);
        int x = 0, y = getSol()-long_;

        while (x < getWidth()) {
            g2d.drawImage(theme.getBlocImg((x / larg) % 2),
                    x, y,
                    larg, long_,
                    null
            );

            if (x < getWidth() - larg && (x / larg) % 2 == 0) {
                int i = (x / (2*larg));

                if (numAnimExplo.size() <= i) {
                    numAnimExplo.add(random.nextInt(theme.getNbImageAnimBoom()+6)-6);

                }

                if (numAnimExplo.get(i) >= 0) {
                    g2d.drawImage(theme.getBoomImg(numAnimExplo.get(i)),
                            x + larg / 2, y - long_,
                            larg, long_,
                            null
                    );
                }
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
            balle_x = perso_x-60;
        }

        balle_dy += balle_ay;
        balle_y += balle_dy;
        if (balle_y >= -2*Balle.RAYON) {
            balle_dy = -balle_dy-balle_ay;
        }

        perso_x += 2;
        if (perso_x > getWidth()) {
            perso_x = oiseau_x-80;
        }

        oiseau_x += 2;
        if (oiseau_x > getWidth()) {
            oiseau_x = -50;
        }

        for (int i = 0; i < numAnimExplo.size(); ++i) {
            Integer num = numAnimExplo.get(i)+1;

            if (num == theme.getNbImageAnimBoom()) {
                num = -random.nextInt(6);
            }

            numAnimExplo.set(i, num);
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
