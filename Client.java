import java.io.*;
import java.net.*;

public class Client {
	private String nomUtilisateur;
	private ObjectOutputStream streamOut; // flux d'écriture d'objet
	private ObjectInputStream streamIn;
	private Socket socket;
	private String ip;
	private int port;
	private ClientIHM cIHM;
	private EcouteLeServeur threadReception;

	public Client(String ip, String nomUtilisateur, int port) {
		this.ip = ip;
		this.nomUtilisateur = nomUtilisateur;
		this.port = port;
	}

	// essaye de se connecter le client au serveur
	public boolean start() {
		// essaye de se connecter
		System.out.println("Creation du socket");
		try {
			socket = new Socket(ip, port);
		}
		// sinon
		catch (Exception e) {
			System.out.print("Création du socket échouée");
			return false;
		}

		// On cree le flux entrant et le flux sortant
		System.out.println("Creation des flux d'objets");
		try {
			// Je ne sais pas pourquoi mais il faut d'abord créer le flux
			// sortant
			streamOut = new ObjectOutputStream(socket.getOutputStream());
			streamOut.flush();
			System.out.println("Creation du streamout réussie");
			streamIn = new ObjectInputStream(socket.getInputStream());
			System.out.println("StreamIn réussie");
			System.out.println("Création de l'IHM");
			creerIHM();
			// crée le Thread qui va écouter en permanence le serveur
			System.out.println("Creation de EcouteLeServeur");
			new EcouteLeServeur().start();
			
		} catch (IOException e) {
			System.out.println("La création des flux d'objets a échoué");
			return false;
		}
		
		try {
			//On envoie le nom d'utilisateur dès la connexion
			streamOut.writeObject(nomUtilisateur);
		
		} catch(IOException e) {
			System.out.println("Erreur lors de l'envoi du nom d'utilisateur");
		}

		return true;
	}

	


	// ferme les flux
	public void deconnecter() {
		envoyerMsg(new Message(Message.DECONNEXION,""));
		try {
			streamOut.close();
			streamIn.close();
			threadReception.arret();
			socket.close();
		} catch (Exception e) {
		}

	}

	public void creerIHM() {
		cIHM = new ClientIHM(this);
		cIHM.setSize(1400, 800);
		cIHM.setVisible(true);
	}
	
	// Méthode permettant d'envoyer une instruction de dessin au serveur
	public void envoyerIDes(InstructionDessin iDes) {
		try {
			streamOut.writeObject(iDes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//envoie un message au serveur
	public void envoyerMsg(Message m) {
		try {
			streamOut.writeObject(m);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	// sert si on ne veut pour faire les tests sans ihm
	public static void main(String[] args) {
		Client c = new Client("127.0.1.1", "Client1", 1800);
		if (!c.start()) {
			return;
		}

	}

	class EcouteLeServeur extends Thread {
		private boolean stop;
		
		public void run() {
			while(!stop) {
				try {
					Object o = streamIn.readObject();
					
					if(o instanceof InstructionDessin) {
						InstructionDessin iDes = (InstructionDessin) o;

						cIHM.getDessinClient().reactInstruction(iDes);						
					}
					
					else if (o instanceof Message) {
						Message m = (Message) o;
						cIHM.getChat().reactMessage(m);						
					}
						
						
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}				
		}
		
		public void arret() {
			stop=true;
		}		
	}
}
