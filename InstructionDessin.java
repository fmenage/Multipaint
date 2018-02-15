import java.awt.Color;
import java.io.*;

public class InstructionDessin implements Serializable {
	

	static int RESET=0, UNDO=1, DESSIN=2;
	private static final long serialVersionUID = -7047158651881220042L;
	//Attributs
	Color couleur;
	int provenance;
	int numero;
	int type;
	
	//Constructeur	
	public InstructionDessin(int type) {
		this.type=type;
	}
	
	public InstructionDessin(int type, Color couleur) {
		this.couleur=couleur;
		this.type=type;	
	}
	
	public int getType() {
		return type;
	}
	public Color getColor() {
		return couleur;
	}
	
	public int getProvenance() {
		return provenance;
	}

	public void setProvenance(int provenance) {
		this.provenance = provenance;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
	
}
