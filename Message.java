import java.io.*;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6812385187961753957L;

	static int DECONNEXION = 0, MESSAGE = 1;
	
	String contenu;
	int type;
	
	public Message(int type, String s) {
		this.contenu = s;
		this.type=type;
	}
	
	public String toString() {
		return(contenu);
	}
	
	public int getType() {
		return type;
	}

}
