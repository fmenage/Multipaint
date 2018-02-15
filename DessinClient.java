import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class DessinClient extends JPanel implements MouseMotionListener, MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2563101810677653710L;
	private int x1,y1,x2,y2;
	private Client c;
	private ClientIHM cIhm;
	private List<InstructionDessin> aDessiner;
	private int ensembleInstru=0;
	
	public DessinClient(Client c, ClientIHM cIhm) {
		super();
		this.c = c;
		this.cIhm = cIhm;
		aDessiner = new LinkedList<InstructionDessin>();
		this.setOpaque(true);
		this.setBackground(Color.white);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		setPreferredSize( new Dimension( 800, 800 ) );

	}
	
	 public void paintComponent(Graphics g) {
		  super.paintComponent(g);
		  
		  
		  for(int i=0; i<aDessiner.size(); i++) {
			  InstructionDessin iDes = aDessiner.get(i);
			  
			  //on dessine quelque chose différent selon le type d'instruction
			  
			  if(iDes instanceof Disque) {
				  Disque d = (Disque) iDes;
				  g.setColor(d.getColor());
				  g.fillOval(d.getX()-d.getRayon()/2, d.getY()-d.getRayon()/2,d.getRayon(),d.getRayon());			  
			  }
			  
			  if(iDes instanceof Ligne) {
				  //pour avoir de l'épaisseur de trait on est obligé de passer par graphics2d
				  
				  Graphics2D g2 = (Graphics2D) g;				  
				  Ligne l = (Ligne) iDes;
				  g2.setColor(l.getColor());
				  g2.setStroke(new BasicStroke(l.getEpaisseur()));
				  g2.drawLine(l.getX1(),l.getY1(), l.getX2(), l.getY2());
			  }
			  
			  if(iDes.getType()==InstructionDessin.RESET) {
				  g.setColor(Color.white);
				  g.fillRect(0, 0, 800, 800);
			  }
		  }
		  
	}
	 
	//réagit aux instructions de dessin
	public void reactInstruction(InstructionDessin iDes) {
		System.out.println("Réagit à instru");
		aDessiner.add(iDes);
		repaint();
	}
	

//réagit aux intéractions de l'utilisateur
	public void mouseDragged(MouseEvent evt) {
		Outil outil = cIhm.getOutil();
		if(outil==Outil.Pinceau) {
			InstructionDessin iDes = new Disque(cIhm.getColor(), cIhm.getTaille(), evt.getX(),evt.getY());
			iDes.setNumero(ensembleInstru);
			c.envoyerIDes(iDes);
			aDessiner.add(iDes);
			repaint();			
		}
		if(outil==Outil.Gomme) {
			InstructionDessin iDes = new Disque(Color.white, cIhm.getTaille(), evt.getX(), evt.getY());
			iDes.setNumero(ensembleInstru);
			c.envoyerIDes(iDes);
			aDessiner.add(iDes);
			repaint();
		}
		
	}
	
	public void mouseMoved(MouseEvent evt) {
		
	}
	
	public void mousePressed(MouseEvent evt) {
		x1=evt.getX();
		y1=evt.getY();
		ensembleInstru++;
		System.out.println("nouvel ensemble d'instructions");
	}
	
	public void mouseReleased(MouseEvent evt) {
		x2=evt.getX();
		y2=evt.getY();
		
		if(cIhm.getOutil() == Outil.Ligne) {
			InstructionDessin iDes = new Ligne(cIhm.getColor(), x1, y1, x2, y2, cIhm.getTaille()/5);
			iDes.setNumero(ensembleInstru);
		    c.envoyerIDes(iDes);
			aDessiner.add(iDes);
			repaint();
		}
	}
	
	public void mouseExited(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseClicked(MouseEvent evt) {}
}
