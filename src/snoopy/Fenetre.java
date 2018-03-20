package snoopy;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Fenetre extends JFrame implements Aire.FinListener {
    // Enumération
    private enum Etat {
        MENU, JEU, PERDU
    }

    // Attributs
    private Etat etat = null;
    private Menu menu;
    private Perdu perdu;
    private Aire aire = null;
    private Theme theme = new Theme(Theme.SNOOPY);

    // Constructeur
    public Fenetre() {
        // Paramètres
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("SnoopMan ECE");

        // Setup menu
        menu = new Menu(theme);
        menu.getBtnJouer().addActionListener((ActionEvent actionEvent) -> lancerJeu());

        retourMenu();

        menu.addChgThemeListener((Theme theme) -> this.theme = theme);

        // Setup perdu
        perdu = new Perdu();

        perdu.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        perdu.getBtnRecommencer().addActionListener((ActionEvent actionEvent) -> lancerJeu());
    }

    // Méthodes
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

    public void lancerJeu() {
        // Gardien
        if (etat == Etat.JEU) return;
        etat = Etat.JEU;

        // Arrêt du menu
        menu.stop();

        // Création de la carte
        Carte carte = new Carte(5, 5);

        Snoopy snoopy = new Snoopy(2, 2);
        carte.ajouter(snoopy);

        carte.ajouter(new Oiseau(0, 0));
        carte.ajouter(new Oiseau(0, 4));
        carte.ajouter(new Oiseau(4, 0));
        carte.ajouter(new Oiseau(4, 4));

        carte.ajouter(new BlocPoussable(2, 1));
        carte.ajouter(new BlocCassable(0,2));
        carte.ajouter(new BlocPiege(2, 4));

        // Création de l'aire de jeu
        aire = new Aire(carte, snoopy, theme);
        aire.ajouterBalle(new Balle(
                (int) (2.5 * Aire.LARG_IMG), (int) (0.5 * Aire.LONG_IMG),
                -2, 2
        ));
        aire.ajouterFinListener(this);

        setContentPane(aire);
        setMinimumSize(aire.getMinimumSize());
        setSize(aire.getMinimumSize());
        aire.requestFocus();
    }

    @Override
    public void perdu() {
        if (etat == Etat.PERDU) {
            return;
        }
        etat = Etat.PERDU;

        // Arrêt de l'aire
        if (aire != null) {
            aire.stop();
        }

        //
        setContentPane(perdu);
        setMinimumSize(perdu.getMinimumSize());
        setSize(perdu.getMinimumSize());
        perdu.requestFocus();
    }

    @Override
    public void gagne() {
        retourMenu();
    }
}
