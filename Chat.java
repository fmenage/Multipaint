import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class Chat extends JPanel implements ActionListener {
	
	private JTextField tfAEnvoyer;
	private JTextArea conversation;
	private JButton envoyer;
	
	private Client c;
	
	public Chat(Client c) {
		this.c=c;
		tfAEnvoyer = new JTextField("Tapez le texte à envoyer", 30);
		envoyer = new JButton("Envoyer");
		conversation = new JTextArea(35,35);
		conversation.setText("Bienvenue dans le chat.\n");
		conversation.setEditable(false);
		conversation.setBackground(Color.white);
		
		JPanel haut = new JPanel();
		haut.add(tfAEnvoyer);
		haut.add(envoyer);
		
		JPanel centre = new JPanel();
		centre.add(conversation);
		JScrollPane defile = new JScrollPane(conversation);
		centre.add(defile);

		
		this.setLayout(new BorderLayout());
		
		this.add(haut,BorderLayout.NORTH);
		this.add(centre, BorderLayout.CENTER);
		
		
		//Permet de faire ENTER au lieu d'appuyer sur envoyer
		tfAEnvoyer.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        if (e.getKeyCode() == KeyEvent.VK_ENTER)  {
		          envoyer.doClick();
		        }
		      }
		    });
		
		
		envoyer.addActionListener(this);
	}
	
	
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource()==envoyer) {
			String txtMsg = tfAEnvoyer.getText();
			if(!txtMsg.equals("")) {
				tfAEnvoyer.setText("");
				Message m = new Message(Message.MESSAGE,txtMsg);
				c.envoyerMsg(m);				
			}
			
		}
		
	}
	
	public void reactMessage(Message m) {
		conversation.setText(conversation.getText() + "\n" + m);
	}
	
	
}
