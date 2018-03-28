package snoopy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
    
    private ArrayList<String> al;

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

        menu.addChgThemeListener((Theme theme) -> {
            this.theme = theme;
            perdu.setTheme(theme);
        });

        // Setup perdu
        perdu = new Perdu(theme);

        perdu.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        perdu.getBtnRecommencer().addActionListener((ActionEvent actionEvent) -> lancerJeu());

        // Setup victoire
        victoire = new Victoire();

        victoire.getBtnMenu().addActionListener((ActionEvent actionEvent) -> retourMenu());
        //victoire.getBtnContinuer().addActionListener((ActionEvent actionEvent) -> lancerJeu());
    
        
        /////////////////////////////
    	al = new ArrayList<String>();
    	//////////////////////////////
    	
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
        perdu.stop();
        
       
        //Ouverture fichier 
        File file = new File("map.txt");
    	BufferedReader buff = null;
    	FileReader filereader;
        
    	//Déclaration variables 
    	String line;
    	String recup[];
    	Carte carte = null;
    	Snoopy snoopy = null;
    	
    	try {
    		
    		filereader = new FileReader(file);
    		buff = new BufferedReader(filereader);
    		
    		//Première ligne = Taille de la map
    		line = buff.readLine();
    		recup = line.split(" ");
    		int x = Integer.parseInt(recup[0]);
    		int y = Integer.parseInt(recup[1]);
    		
    		//Création de la carte
    		carte = new Carte(x,y);
    		
    		//Dexuième ligne = Coordonnées snoopy
    		line = buff.readLine();
    		recup = line.split(" ");
    		
    		snoopy = new Snoopy(Integer.parseInt(recup[0]),Integer.parseInt(recup[1]));
       		carte.ajouter(snoopy); 		
    		
       		
    		//Le reste = Tous les autres éléments de la map
    		for(int i=0; i<x;i++) {
    			
    			//A chaque tour de boucle, une ligne est lue
    			line = buff.readLine();
    			    			
    			//On décortique la ligne pour créer le perso adécuat 
    			for(int j=0;j<y;j++) {
    				
    				switch(line.charAt(j)) {
    					
    				case 'o': //Oiseau
    						carte.ajouter(new Oiseau(j,i));
    						break;
    					case 'C': //Bloc Cassable
    						carte.ajouter(new BlocCassable(j, i));
    						break;
    					case 'P': //Piege
    						carte.ajouter(new BlocPiege(j, i));
    						break;
    					case 'D': //Déplaçable
    						carte.ajouter(new BlocPoussable(j, i));
    						break;
    				}
    			}
    			
    		}

        // Création de l'aire de jeu
        aire = new Aire(carte, snoopy, theme);
        aire.ajouterBalle(new Balle(
                (int) (2.5 * Aire.LARG_IMG), (int) (0.5 * Aire.LONG_IMG),
                -4, 4
        ));
        aire.ajouterFinListener(this);

        setContentPane(aire);
        setMinimumSize(aire.getMinimumSize());
        setSize(aire.getMinimumSize());
        aire.requestFocus();
        
     } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
     } 
    }
    
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
    }
}
