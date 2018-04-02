package snoopy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.LinkedList;

/**
 * Gestion des mots de passe
 *
 * @author julien
 */
public class MotDePasse extends PanneauSol implements DocumentListener {
    // Attributs
    private String niveau = null;
    private LinkedList<MotDePasseListener> listeners = new LinkedList<>();

    private JTextField champ = new JTextField(10);
    private JButton btnCharger = new JButton("Charger");
    private JButton btnRetour = new JButton("Retour");

    // Constructeur
    /**
     * Prépare le JPanel
     *
     * @param theme thème à utiliser
     */
    public MotDePasse(Theme theme) {
        super(theme);

        // Ajout du champ
        setLayout(null);
        add(champ);
        add(btnCharger);
        add(btnRetour);
        positionner();

        // Setup
        btnCharger.setEnabled(false);
        btnCharger.addActionListener((ActionEvent actionEvent) -> {
            try {
                charger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        champ.getDocument().addDocumentListener(this);
        champ.setHorizontalAlignment(JTextField.CENTER);
    }

    // Méthodes statiques
    /**
     * Décode le mot de passe
     *
     * @param mdp mot de passe entré
     * @return nom du fichier
     */
    public static String decode(String mdp) {
        try {
            // Ajustement longueur
            while (mdp.length() % 4 != 0) {
                mdp += "=";
            }

            return new String(Base64.getDecoder().decode(mdp));
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    /**
     * Encode le nom de fichier pour créer le mot de passe
     *
     * @param fichier nom du fichier
     * @return mot de passe
     */
    public static String encode(String fichier) {
        String mdp = Charset.forName("ascii").decode(Base64.getEncoder().encode(Charset.forName("ascii").encode(fichier))).toString();
        return mdp.replace("=", "");
    }

    // Méthodes
    /**
     * Positionne les widgets dans la fenêtre
     */
    private void positionner() {
        // Widgets
        Insets insets = getInsets();
        Dimension taille;

        // - champ texte
        taille = champ.getPreferredSize();
        champ.setBounds(
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
        taille = btnRetour.getPreferredSize();
        btnRetour.setBounds(
                (getWidth() - taille.width)/2 + insets.left, getSol() + 95 + insets.top,
                taille.width, taille.height
        );
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Initialisation
        Graphics2D g2d = (Graphics2D) graphics;

        // Titre
        g2d.setFont(new Font ("Plain", Font.BOLD,50));
        g2d.setColor(Color.black);

        g2d.drawString("Mot de Passe", getWidth()/2-170, 60);

        // Positionnement !!!
        positionner();

        // On force la synchronisation de l'écran
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Teste si le mot de est valide
     *
     * @param doc document contenant le mot de passe tapé par l'utilisateur
     */
    private void test(Document doc) {
        try {
            // Récupération du mot de passe
            String mdp = doc.getText(0, doc.getLength());

            // Décodage
            String fichier = decode(mdp);

            // Test
            if (Moteur.niveauExiste(fichier)) {
                btnCharger.setEnabled(true);
                niveau = fichier;

            } else {
                btnCharger.setEnabled(false);
                niveau = null;
            }

        } catch (BadLocationException e) { // En théorie ... n'arrive pas !!!
            e.printStackTrace();
        }
    }

    /**
     * Charge le niveau
     *
     * @throws IOException en cas d'erreur de lecture
     */
    private void charger() throws IOException {
        Moteur moteur = Moteur.charger(niveau, theme, 0, Snoopy.MAX_VIES);

        for (MotDePasseListener listener : listeners) {
            listener.motDePasseOK(moteur);
        }
    }

    // Evenements du JTextField
    @Override
    public void insertUpdate(DocumentEvent e) {
        test(e.getDocument());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        test(e.getDocument());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Ignoré
    }

    /**
     * Ajout un MotDePasseListener
     *
     * @param listener listener à ajouter
     *
     * @see MotDePasseListener
     */
    public void ajouterMotDePasseListener(MotDePasseListener listener) {
        listeners.add(listener);
    }

    // - accesseurs
    public JButton getBtnRetour() {
        return btnRetour;
    }

    // Listener

    /**
     * MotDePasseListener
     */
    public interface MotDePasseListener {
        /**
         * Appelée quand l'utilisateur appuye sur Charge avec un mot de passe valide
         *
         * @param moteur moteur de jeu correspondant au niveau chargé
         */
        void motDePasseOK(Moteur moteur);
    }
}
