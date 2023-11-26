import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class InterfaceGraphique  extends JFrame {
    // creation de trois bouton et une zone de texte
    // en plus de la variable serialVersionUID statitic et constante réclamée par le compilateur pour préciser la version de la classe
    JButton boutonChercher;
    JButton boutonJouer;
    JButton boutonFermer;
    JTextArea zoneTexte;
    JTextField champTexte;
    private static final long serialVersionUID = 1L;

    // Declaration des attributs de types JmenuItem
    JMenuItem boutonChercherMenu;
    JMenuItem boutonJouerMenu;
    JMenuItem boutonFermerMenu;

    Client client;
    static final String host = "localhost";
    static final int port = 3331;

    public InterfaceGraphique(){
        try {
            client = new Client(host, port);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du client : " + e.getMessage());
            System.exit(1);
        }

        //le jpanel pour y mettre les boutons, ce dernier est lui-même situé dans la zone sud du JFrame
        JPanel panelBouton = new JPanel();

        //Creation de la zone de texteavec une  taille suffisante  en spécifiant un nombre de lignes 15 et de colonnes 15.
        zoneTexte = new JTextArea(15,15);

        //Ajout des ascenseurs au zoneTexte pour le rendre réellement utilisable horizentalement et verticalment.
        JScrollPane scrollPane = new JScrollPane(zoneTexte);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(BorderLayout.CENTER,scrollPane);

        //Ajout d'un chamop de recherche a panelbouton en plus des bouton
        champTexte = new JTextField("");
        champTexte.setColumns(40);
        champTexte.setCaretColor(Color.RED);
        champTexte.setBackground(Color.LIGHT_GRAY);
        panelBouton.add(champTexte);
        panelBouton.add(boutonChercher= new JButton("Chercher"));
        panelBouton.add(boutonJouer = new JButton("Lancer"));
        panelBouton.add(boutonFermer = new JButton("Fermer"));
        //Asssocier des couleurs a chaque boutons
        boutonChercher.setBackground(Color.WHITE);
        boutonJouer.setBackground(Color.GREEN);
        boutonFermer.setBackground(Color.RED);
        // Taille personnalisée (largeur x hauteur) unifier la taille des boutons
        Dimension boutonSize = new Dimension(100, 30);
        boutonChercher.setPreferredSize(boutonSize);
        boutonJouer.setPreferredSize(boutonSize);
        boutonFermer.setPreferredSize(boutonSize);

        // Associer des listeners aux boutons
        boutonChercher.addActionListener(new Chercher());
        boutonJouer.addActionListener(new Jouer());
        boutonFermer.addActionListener(new Fermer());

        //JMenu(String s) Constructs a new JMenu with the supplied string as its text D'apres la documentation docs.oracle
        JMenu menu = new JMenu("Liste des actions");
        //Faire un focus sur le menu
        menu.requestFocusInWindow();
        //Rajouter une barre de menus (JMenuBar)" barmenu" comprenant un menu déroulant (JMenu) "menu"
        //La barre de menus sera ajoutée à la fenêtre via sa méthode setJMenuBar()."
        JMenuBar menubar = new JMenuBar();
        menubar.add(menu);
        setJMenuBar(menubar);

        // barre d'outils (JToolBar). Il faudra, comme de juste, placer la barre d'outils dans la zone nord de la fenêtre principale.
        JToolBar toolbar = new JToolBar();
        toolbar.add(panelBouton);
        add(BorderLayout.NORTH, toolbar);

        //Creation des JmenuItems
        boutonChercherMenu = new JMenuItem("Chercher");
        boutonJouerMenu = new JMenuItem("Lancer");
        boutonFermerMenu = new JMenuItem("Fermer");
        boutonChercherMenu.addActionListener(new Chercher());
        boutonJouerMenu.addActionListener(new Jouer());
        boutonFermerMenu.addActionListener(new Fermer());
        menu.add(boutonChercherMenu);
        menu.add(boutonJouerMenu);
        menu.add(boutonFermerMenu);

        // Titre du JFrame
        setTitle("Gallerie");
        //N'oubliez pas d'appeler les méthodes suivantes de JFrame:
        // setDefaultCloseOperation(int) pour que la fermeture de l'interface entraîne la terminaison de l'application
        //pack() pour calculer la disposition spatiale des composants graphiques
        //setVisible(boolean) pour faire apparaître l'interface
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);




    }
    // Utilisation de la version 3 du cours avec des sous-classes
    class Chercher implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String requete = client.send("CHERCHER "+ champTexte.getText());
           // requete = parseResult(requete);
            zoneTexte.append(requete);
        }
    }

    class Jouer implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String reponse = client.send("LANCER "+ champTexte.getText());
            //reponse = parseResult(reponse);
            zoneTexte.append(reponse);
        }

    }
    class Fermer implements ActionListener {
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    }

    public static void main(String arg[]){
        new InterfaceGraphique();
    }
    class Client{

        private Socket sock;
        private BufferedReader input;
        private BufferedWriter output;

        Client(String host, int port) throws UnknownHostException, IOException {
            try {
                this.sock = new java.net.Socket(host, port);
            }
            catch (java.net.UnknownHostException e) {
                System.err.println("Client: Couldn't find host "+host+":"+port);
                throw e;
            }
            catch (java.io.IOException e) {
                System.err.println("Client: Couldn't reach host "+host+":"+port);
                throw e;
            }

            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            }
            catch (java.io.IOException e) {
                System.err.println("Client: Couldn't open input or output streams");
                throw e;
            }
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        public String send(String request) {
            // Envoyer la requete au serveur
            try {
                request += "\n";  // ajouter le separateur de lignes
                output.write(request, 0, request.length());
                output.flush();
            }
            catch (java.io.IOException e) {
                System.err.println("Client: Couldn't send message: " + e);
                return null;
            }

            // Recuperer le resultat envoye par le serveur
            try {
                return input.readLine();
            }
            catch (java.io.IOException e) {
                System.err.println("Client: Couldn't receive message: " + e);
                return null;
            }
        }
    }
}


