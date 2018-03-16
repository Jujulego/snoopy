package snoopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Menu extends JPanel {
    // Attributs
    private int perso_x = 400;
    private int perso_y = 100;
    private int num_anim;
    private Theme theme;
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private ArrayList<ChgThemeListener> listeners = new ArrayList<>();

    private JButton btnJouer = new JButton("Jouer");

    private JLabel lblTheme = new JLabel("");
    private JButton btnThemeM = new JButton("<<");
    private JButton btnThemeP = new JButton(">>");

    // Constructeur
    public Menu(Theme theme) {
        this.theme = theme;
        lblTheme.setText(theme.getNomTheme());

        // Paramètres
        setDoubleBuffered(true);
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
                200 - taille.width/2 + insets.left, 175 + insets.top,
                taille.width, taille.height
        );

        // - gestion themes
        add(lblTheme);
        Dimension tailleT = lblTheme.getPreferredSize();
        tailleT.width = 80;
        lblTheme.setBounds(
                200 - tailleT.width/2 + insets.left, 255 + insets.top,
                tailleT.width, tailleT.height
        );
        lblTheme.setHorizontalAlignment(SwingConstants.CENTER);

        add(btnThemeM);
        taille = btnThemeM.getPreferredSize();
        btnThemeM.setBounds(
                175 - tailleT.width/2 - taille.width + insets.left, 250 + insets.top,
                taille.width, taille.height
        );
        btnThemeM.addActionListener((ActionEvent event) -> chgTheme(-1));

        add(btnThemeP);
        taille = btnThemeP.getPreferredSize();
        btnThemeP.setBounds(
                225 + tailleT.width/2 + insets.left, 250 + insets.top,
                taille.width, taille.height
        );
        btnThemeP.addActionListener((ActionEvent event) -> chgTheme(1));
    }

    // Méthodes
    @Override
    protected synchronized void paintComponent(Graphics graphics) {
        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Snoopy !
        g2d.drawImage(theme.getPersoImg(Direction.GAUCHE, num_anim), perso_x, perso_y, null);

        // Titre
        g2d.setFont(new Font ("Plain", Font.BOLD,50));
        g2d.setColor(Color.black);

        g2d.drawString("Snoopy", 100, 50);

        // On force la synchronisation de l'écran
        Toolkit.getDefaultToolkit().sync();
    }

    private synchronized void animer() {
        perso_x -= 2;
        if (perso_x <= -50) {
            perso_x = 400;
        }

        num_anim = (num_anim + 1) % theme.getNbImgPerso(Direction.GAUCHE);

        repaint();
    }

    public void lancer() {
        // Animation !
        scheduler.scheduleAtFixedRate(this::animer, 0, 50, TimeUnit.MILLISECONDS);
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
        num_anim = 0;

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
