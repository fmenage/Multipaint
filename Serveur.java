import java.util.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.*;



public class Serveur {
	//attributs
	List<ThreadParClient> lClients;
	List<String> lNomClients;
	int port;
	boolean continuer;
	List<InstructionDessin> lInstructions;
	
	//constructeur
	public Serveur(int port) {
		this.port=port;
		lClients= new ArrayList<ThreadParClient>();
		lNomClients = new ArrayList<String>();
		lInstructions = new ArrayList<InstructionDessin>();
	}
	
	//méthodes
	public void start() {
		continuer = true;
		
		try {
			//on essaye de lancer le serveur
			ServerSocket sSocket = new ServerSocket(port);
			System.out.println("Serveur lancé sur la machine " + InetAddress.getLocalHost().getHostAddress());
			
			//boucle qui attend les nouvelles connexions
			while(continuer) {
				System.out.println("Serveur en attente sur le port " + port + ".");
				
				Socket socket = sSocket.accept();  	// le serveur accepte la connexion
				
				//si le serveur doit s'arrêter
				if(!continuer) 
					break;
				
				ThreadParClient t = new ThreadParClient(socket, this);  // on crée un nouvel thread dédié au nouveau client
				lClients.add(t);
				for(int i=0; i<lInstructions.size();i++) {
					InstructionDessin iDes = lInstructions.get(i);
					t.envoyerIDes(iDes);
				}
				
				t.start();				
				System.out.println("Nouveau client accepté");
			}
		} catch(IOException e) {
			System.err.print(e);
			
		}
		
	}
	
	public void deconnecterClient(int idClient) {
	
		boolean trouve = false;
		int i=0;
		while(i<lClients.size()&& trouve == false) {
			if(lClients.get(i).getIdUnique()!=idClient) {
				trouve=true;
				lClients.get(i).deconnexion();
				
				lClients.remove(i);
			}
			i++;
		}
			
	}
	
	//Envoie un message à tous les clients avec le nom de l'expéditeur
	public synchronized void diffuserMessage(Message m, String nomUtilisateur) {
		Message aEnvoyer = new Message(Message.MESSAGE, nomUtilisateur + " : " + m);
		for(ThreadParClient cl : lClients) {
			cl.envoyerMessage(aEnvoyer);
		}				
	}
	
	//Envoie des instructions de dessin à tout le monde - utilisé pour le undo par exemple
	public synchronized void diffuserInstru(InstructionDessin iDes) {
		for(ThreadParClient cl : lClients) {
			cl.envoyerInstru(iDes);
		}	
	}
	
	//Réagit à l'instruction de dessin d'un client : le serveur diffuse l'instruction à tout les clients sauf celui qui 
	// l'a envoyée
	
	public void reactInstruction(InstructionDessin iDes) {
		lInstructions.add(iDes);
		for(int i=0; i< lClients.size() ; i++) {
			if(lClients.get(i).getIdUnique()!=iDes.getProvenance()) {
				lClients.get(i).envoyerIDes(iDes);
			}
		}
	}
	
	public void undo(int idUnique) {
		//On cherche s'il y a une instruction de dessin déjà effectuée par un client
		System.out.println("Le client d'id " + idUnique + "a demandé un retour arrière");
		int i = lInstructions.size()-1;
		boolean trouve = false;
		int numEnsemble=0;
		while(i>=0 && !trouve) {
			if(lInstructions.get(i).getProvenance()==idUnique) {
				trouve = true;
				numEnsemble = lInstructions.get(i).getNumero();
			}
			i--;
		}
		
		if(trouve) {
			i = lInstructions.size()-1;
			boolean fini = false;
			while(i>=0 && !fini) {
				int prov = lInstructions.get(i).getProvenance();
				int num = lInstructions.get(i).getNumero();
				if(prov==idUnique &&  num == numEnsemble) {
					lInstructions.remove(i);					
				}
				
				else if (prov == idUnique && num !=numEnsemble) {
					fini = true;
				}			
				i--;				
			}
			//On réinitialise toutes les toiles et renvoie toutes les instructions effectuées sauf celles supprimées
			diffuserInstru(new InstructionDessin(InstructionDessin.RESET));
			for(int j = 0; j<lInstructions.size(); j++) {
				diffuserInstru(lInstructions.get(j));
				System.out.println("je rediffuse instructions");
			}							
		}
	}
	
	public static void main(String[] args) {
		//Par défaut on choisira le port numéro 1800
		int port = 1800;
		
		// On crée un serveur et on le lance
		Serveur serveur = new Serveur(port);
		serveur.start();
	}
}

class ThreadParClient extends Thread {
	static int indexThread=-1;
	//Attributs
	
	Serveur serveur;
	int idUnique;
	Socket client;
	ObjectInputStream streamIn;
	ObjectOutputStream streamOut;
	int idClient;
	boolean stop = false;
	Object objetrecu;
	String nomUtilisateur;
	
	
	//Constructeur
	public ThreadParClient(Socket socket, Serveur serveur) {
		
		super();
		this.client=socket;
		this.serveur = serveur;
		
		//On incrémente le nombre de 
		indexThread++;
		idUnique=indexThread;
		
		System.out.println("Création des flux d'objets");
		try
		{
			streamIn = new ObjectInputStream(socket.getInputStream());
			streamOut = new ObjectOutputStream(socket.getOutputStream());
			streamOut.flush();
		} catch (IOException e) {
			System.out.println("Erreur lors de la création des flux d'objets côté serveur : " + e);
			return;
		}
		
		try {
			Object ob = streamIn.readObject();
			if(ob instanceof String) {
				nomUtilisateur = (String) ob;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
	}
	
	public void run() {
		while(!stop) {
			try {
				
				//Le thread reçoit des objets de msg/instructions
				System.out.println("Boucle executee");
				objetrecu = streamIn.readObject();
				
				
				//Selon leur type, il agit différemment				
				if(objetrecu instanceof InstructionDessin) {
					System.out.println("Le serveur a reçu une instruction de dessin du client " + idUnique);
					InstructionDessin iDes = (InstructionDessin) objetrecu;
					
					if(iDes.getType()==InstructionDessin.UNDO) {
						serveur.undo(idUnique);
					}
					
					else {
						iDes.setProvenance(idUnique);
						serveur.reactInstruction(iDes);
					}
					
				}
				
				else if(objetrecu instanceof Message) {
					Message msg = (Message) objetrecu;
					if(msg.getType() == Message.DECONNEXION) {
						stop = true;
						serveur.diffuserMessage(new Message(Message.MESSAGE, nomUtilisateur + " s'est déconnecté."), "Serveur");
						System.out.println("Message de deconnexion recu");
					}
					
					else {
						serveur.diffuserMessage(msg, nomUtilisateur);
					}
				}
				
				
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
		}
		deconnexion();
	}
	
	public void deconnexion() {
		try {
			streamIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			streamOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void envoyerIDes(InstructionDessin iDes) {
		try {
			streamOut.writeObject(iDes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
	
//envoyerInstru et envoyerMessage sont pareil, mais c'était pour avoir une meilleure lisibilité du code
	
	public void envoyerInstru(InstructionDessin iDes) {
		try {
			streamOut.writeObject(iDes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void envoyerMessage(Message m) {
		try {
			streamOut.writeObject(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public int getIdUnique() {
		return(idUnique);
	}
	
	public String getUsername() {
		return(nomUtilisateur);
	}
}