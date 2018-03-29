package snoopy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Gestion de la fenêtre, changements entre les panels
 *
 * @author julien
 */
public class Fenetre extends JFrame implements Aire.FinListener {
    // Enumération
    private enum Etat {
        MENU, JEU, PERDU, VICTOIRE
    }

    // Attributs
    private Etat etat = null;
    private Menu menu;
    private Perdu perdu;
    private Victoire victoire;
    private Aire aire = null;
    private Theme theme = new Theme(Theme.SNOOPY);

    // Constructeur
    /**
     * Construit la fenêtre, intialise les panels
     */
    public Fenetre() {
        // Paramètres
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("SnoopMan ECE");

        // Setup menu
        menu = new Menu(theme);
        menu.getBtnJouer().addActionListener((ActionEvent actionEvent) -> lancerJeu());

        retourMenu();

        menu.addChgThemeListener((Theme theme) -> {
            this.theme = theme;
            perdu.setTheme(theme);
            victoire.setTheme(theme);
        });

        // Setup perdu
        perdu = new Perdu(theme);

        perdu.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        perdu.getBtnRecommencer().addActionListener((ActionEvent actionEvent) -> lancerJeu());

        // Setup victoire
        victoire = new Victoire(theme);

        victoire.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        //victoire.getBtnContinuer().addActionListener((ActionEvent actionEvent) -> lancerJeu());
    }

    // Méthodes
    /**
     * Prépare à l'affichage du menu
     */
    public void retourMenu() {
        // Gardien
        if (etat == Etat.MENU) return;
        etat = Etat.MENU;

        // Arrêt de l'aire
        if (aire != null) {
            aire.stop();
        }

        // Ajout du menu
        setContentPane(menu);
        setMinimumSize(menu.getMinimumSize());
        setSize(menu.getMinimumSize());

        menu.lancer();
        menu.requestFocus();
    }

    /**
     * Prépare l'affichage du jeu
     */
    public void lancerJeu() {
        // Gardien
        if (etat == Etat.JEU) return;
        etat = Etat.JEU;

        // Arrêt du menu
        menu.stop();
        perdu.stop();
        victoire.stop();

        // Création de la carte
        Carte carte = new Carte(20, 10);

        Snoopy snoopy = new Snoopy(2, 4);
        carte.ajouter(snoopy);

        carte.ajouter(new Oiseau(0, 0));
        carte.ajouter(new Oiseau(19, 0));
        carte.ajouter(new Oiseau(19, 9));
        carte.ajouter(new Oiseau(0, 9));

        carte.ajouter(new Pause(5, 6));
        carte.ajouter(new Invincible(4, 6));

        Teleporteur tp1 = new Teleporteur(4,4);
        Teleporteur tp2 = new Teleporteur(16, 6, tp1);
        tp1.setPaire(tp2);

        carte.ajouter(tp1);
        carte.ajouter(tp2);

        carte.ajouter(new BadSnoopy(6, 4));

        for (int x = 0; x < carte.getTx(); ++x) {
            carte.ajouter(new Bloc(x, 5));
        }

        // Création de l'aire de jeu
        Moteur moteur = new Moteur(carte, snoopy, theme, 0);
        moteur.ajouterBalle(new Balle(
                (int) (2.5 * Moteur.LARG_IMG), (int) (0.5 * Moteur.LONG_IMG),
                -4, 4
        ));

        aire = new Aire(moteur, theme);
        aire.ajouterFinListener(this);

        setContentPane(aire);
        setMinimumSize(aire.getMinimumSize());
        setSize(aire.getMinimumSize());
        aire.requestFocus();
    }

    /**
     * Affiche l'écran de perte
     */
    @Override
    public void perdu() {
        if (etat == Etat.PERDU) return;
        etat = Etat.PERDU;

        // Arrêt de l'aire
        if (aire != null) {
            aire.stop();
        }

        // Perdu !!!!
        setContentPane(perdu);
        setMinimumSize(perdu.getMinimumSize());
        setSize(perdu.getMinimumSize());
        perdu.requestFocus();

        perdu.lancer();
    }

    /**
     * Affiche l'écran de victoire
     */
    @Override
    public void gagne() {
        if (etat == Etat.VICTOIRE) return;
        etat = Etat.VICTOIRE;

        // Arrêt de l'aire
        if (aire != null) {
            aire.stop();
        }

        // Victoire !!!!
        setContentPane(victoire);
        setMinimumSize(victoire.getMinimumSize());
        setSize(victoire.getMinimumSize());
        victoire.requestFocus();

        victoire.lancer();
    }
}
