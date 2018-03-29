package snoopy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

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

    private int score = 0;
    private int num_niveau = 1;
    private int vies = Snoopy.MAX_VIES;

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
        perdu.getBtnRecommencer().addActionListener((ActionEvent actionEvent) -> premierNiveau());

        // Setup victoire
        victoire = new Victoire(theme);

        victoire.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        victoire.getBtnContinuer().addActionListener((ActionEvent actionEvent) -> niveauSuivant());
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
     * Prépare le niveau 1
     */
    public void premierNiveau() {
        // Initialisation
        score = 0;
        num_niveau = 1;
        vies = Snoopy.MAX_VIES;

        // Activation !!!
        lancerJeu();
    }

    /**
     * Prépare le niveau suivant
     */
    public void niveauSuivant() {
        // Evolution
        num_niveau++;

        // Activation !!!
        lancerJeu();
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

        // Création de l'aire
        try {
	        Moteur moteur = Moteur.charger(String.format("map%d", num_niveau), theme, score, vies);
	        aire = new Aire(moteur, theme);
	        aire.ajouterFinListener(this);
	
	        setContentPane(aire);
	        setMinimumSize(aire.getMinimumSize());
	        setSize(aire.getMinimumSize());
	        aire.requestFocus();

	    } catch (IOException e) {
			// Pas de niveau suivant ...
			retourMenu();
	    } 
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
     * @param score score final
     * @param vies nombre de vies a la fin
     */
    @Override
    public void gagne(int score, int vies) {
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

        // Evolution
        this.score = score;
        this.vies = vies;
    }
}
