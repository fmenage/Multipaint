import java.awt.Color;


public class Rectangle extends InstructionDessin {
	
	private int x,y,lx,ly;
	
	public Rectangle(Color couleur, int x, int y, int lx, int ly) {
		super(InstructionDessin.DESSIN, couleur);		
		this.x=x;
		this.y=y;
		this.lx=lx;
		this.ly=ly;
	}

}
