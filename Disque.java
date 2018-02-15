import java.awt.Color;


public class Disque extends InstructionDessin {
	
	private int rayon;
	private int x;
	private int y;
	
	public Disque(Color couleur, int rayon, int x,int y) {
		super(InstructionDessin.DESSIN, couleur);
		this.rayon = rayon;
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return(x);
	}
	
	public int getY() {
		return(y);
	}
	
	public int getRayon() {
		return(rayon);
	}
}
