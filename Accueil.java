import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Accueil extends JFrame implements ActionListener {
	
	private JLabel titre;
	private JTextField tName;
	private JLabel labName;
	private JTextField tServeur;
	private JLabel labServeur;
	private JLabel labPort;
	private JTextField tPort;
	private JButton connexion;
	private JLabel informations;
	
	private GridLayout grille;
	
	public Accueil() {
		super("Formulaire de connexion");
		
		//Initialisation des objets
		titre = new JLabel();
		tName = new JTextField("Flo");
		labName = new JLabel("Nom d'utilisateur : ");
		tServeur = new JTextField("127.0.1.1");
		labServeur = new JLabel("Adresse Ip du serveur : ");
		tPort = new JTextField("1800");
		labPort = new JLabel("Port utilisé : ");
		informations = new JLabel("Informations");
		connexion = new JButton("Se connecter");
		
		
		//On ajoute les composants
		grille = new GridLayout(4,2);
		
		this.setLayout(grille);
		this.add(labName);
		this.add(tName);
		this.add(labServeur);
		this.add(tServeur);
		this.add(labPort);
		this.add(tPort);
		this.add(informations);
		this.add(connexion);
		
		//Création d'écouteurs
		connexion.addActionListener(this);

	}
	public void actionPerformed(ActionEvent e) {
		//si l'utilisateur appuye sur le bouton de connexion, on essaie de se connecter au serveur décrit par l'utilisateur
		if(e.getSource()==connexion) {
			tentativeDeCo();
			
		}
		
	}
	
	//le client essaye de se connecter au serveur. si la connexion est réussie, on renvoie true
	private boolean tentativeDeCo() {
		System.out.println("Connexion en cours...");
		boolean b = false;
		Client c=null;
		if(!isInteger(tPort.getText())) {
			informations.setText("Le port indiqué n'est pas un entier");
			return false;
		}
		
		else {
			int port = Integer.parseInt(tPort.getText());
			String ip = tServeur.getText();
			String nomU = tName.getText();
			
			//on crée un profil de client qu'on essaye de démarrer
			c = new Client(ip,nomU,port);
			b = c.start();	

			if(b==true) {
				this.setVisible(false);
				return true;
			}
			
			else{
				informations.setText("Connexion échouée. Vérifiez vos informations de connexion");
				return false;
			}
		}
		
	}
	
	public void close() {
		
	}
	
	//méthode qui permet de vérifier qu'une chaîne de caractère est un entier
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}

	
}


