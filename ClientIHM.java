
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class ClientIHM extends JFrame implements ActionListener, ChangeListener, WindowListener {
	
	//Attributs
	private Client c;
	
	//OUTILS
	private JButton pinceau;
	private JButton gomme;
	private JButton ligne;
	private JButton undo;
	
	
	
	private Outil outilActuel; // où Outil est un type énuméré pour une meilleure lisibilité
	
	private JSlider tailleSelect;
	private JLabel tailleLab;
	
	private int taille;
	private Color couleurActuelle;
	private int r,v,b=0;
	
	//composants du sélecteur de couleur
	private JSlider rSelect;
	private JSlider vSelect;
	private JSlider bSelect;
	private JLabel showColor;
	
	JColorChooser selectCouleur;
	
	//zone de dessin
	private DessinClient zoneDessin;
	
	//Pour le chat
	private JList lUsers;
	private JTextArea texteChat;
	private Chat chat;
	
	//Organisation
	private JPanel barreOutils;
	private JPanel selecteurCouleur;
	private GridLayout grilleBo;
	private BoxLayout layCouleur;
	private BorderLayout layoutGlobal;

	
	//Constructeurs
	public ClientIHM(Client c) {
		super("Logiciel client");
		this.c=c;
		taille=30;
		//Conteneurs
		barreOutils = new JPanel();
		selecteurCouleur = new JPanel();
		selectCouleur = new JColorChooser();
		
		chat = new Chat(c);
				
		//Création des bouttons
		pinceau = new JButton("Pinceau");
		gomme = new JButton("Gomme");
		ligne = new JButton("Ligne");
		undo = new JButton("Undo");
		
		//--------------------Slider pour la sélection de la taille-------------------
		
		tailleLab = new JLabel("Taille", JLabel.CENTER);
		tailleSelect = new JSlider(0,150);
		//on définit l'écart entre les grands, puis les petits marqueurs
		tailleSelect.setMajorTickSpacing(50);
		tailleSelect.setMinorTickSpacing(10);
		//On affiche les marqueurs
		tailleSelect.setPaintTicks(true);
		tailleSelect.setPaintLabels(true);
		tailleSelect.setValue(taille);
		
		
		rSelect = new JSlider(0,255);
		vSelect = new JSlider(0,255);
		bSelect = new JSlider(0,255);
		showColor = new JLabel("      ");
		
		
		showColor.setOpaque(true);
		
		zoneDessin = new DessinClient(c,this);
		
		JPanel contientzd = new JPanel();
		contientzd.add(zoneDessin);
		contientzd.setBackground(new Color(220,220,230,100));
		
		//Ajout des boutons
		barreOutils.add(pinceau);
		barreOutils.add(gomme);
		barreOutils.add(ligne);
		barreOutils.add(undo);
		
		selecteurCouleur.add(rSelect);
		selecteurCouleur.add(vSelect);
		selecteurCouleur.add(bSelect);
		selecteurCouleur.add(showColor);
		//selecteurCouleur.add(selectCouleur);
		
		//Mise en forme barre outils
		grilleBo = new GridLayout(2,1);
		barreOutils.setLayout(grilleBo);
		
		//Mise en forme du sélecteur de couleur
		selecteurCouleur.setLayout(new BoxLayout(selecteurCouleur, BoxLayout.Y_AXIS));
		
		//Mise en forme globale
		layoutGlobal=new BorderLayout();
		this.setLayout(layoutGlobal);
		
		JPanel contbGauche = new JPanel();
		contbGauche.add(barreOutils);
		contbGauche.add(selecteurCouleur);
		contbGauche.add(tailleLab);
		contbGauche.add(tailleSelect);
		contbGauche.setLayout(new BoxLayout(contbGauche, BoxLayout.Y_AXIS));
		
		//Ajout des éléments à l'appli
		this.add(contbGauche, BorderLayout.WEST);
		//this.add(tailleSelect, BorderLayout.EAST);
	    this.add(contientzd, BorderLayout.CENTER);
		this.add(chat, BorderLayout.EAST);
		
		//Listeners
		pinceau.addActionListener(this);
		gomme.addActionListener(this);
		ligne.addActionListener(this);
		undo.addActionListener(this);
		
		tailleSelect.addChangeListener(this);
		rSelect.addChangeListener(this);
		vSelect.addChangeListener(this);
		bSelect.addChangeListener(this);
		

        this.addWindowListener(this);

		
	}
	
	//Getters
	public DessinClient getDessinClient() {
		return zoneDessin;
	}
	
	public int getTaille() {
		return(taille);		
	}
	
	public Color getColor() {
		return(couleurActuelle);
	}
	
	public Outil getOutil() {
		return(outilActuel);
	}
	
	public Chat getChat() {
		return(chat);
	}
	
	
	
	
	//Gestion d'événements
	
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource()==pinceau) {
			outilActuel=Outil.Pinceau;
		}
		
		if(evt.getSource()==gomme) {
			outilActuel=Outil.Gomme;			
		}
		
		if(evt.getSource()==ligne) {
			outilActuel=Outil.Ligne;
		}
		
		if(evt.getSource() == undo) {
			c.envoyerIDes(new InstructionDessin(InstructionDessin.UNDO));
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==tailleSelect) {
			taille = ((JSlider) e.getSource()).getValue();
		}
		if(e.getSource()==rSelect) {
			r = ((JSlider) e.getSource()).getValue();
			couleurActuelle=new Color(r,v,b);
			showColor.setBackground(couleurActuelle);
		}
		if(e.getSource()==vSelect) {
			v = ((JSlider) e.getSource()).getValue();
			couleurActuelle=new Color(r,v,b);
			showColor.setBackground(couleurActuelle);
		}
		if(e.getSource()==bSelect) {
			b = ((JSlider) e.getSource()).getValue();
			couleurActuelle=new Color(r,v,b);
			showColor.setBackground(couleurActuelle);
		}
	}
	
	public void windowClosing(WindowEvent evt) {
		c.deconnecter();
		System.exit(0);
	}
	
	public void windowOpened(WindowEvent evt) {}
	public void windowClosed(WindowEvent evt) {}
	public void windowIconified(WindowEvent evt) {}
    public void windowDeiconified(WindowEvent evt) {}
	public void windowActivated(WindowEvent evt) {}
	public void windowDeactivated(WindowEvent evt) {}

}
