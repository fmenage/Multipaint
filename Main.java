import javax.swing.colorchooser.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
  
public class Main extends JFrame {
   public Main() {
      super("JColorChooser Demonstration");
    
      JColorChooser colorChooser = new JColorChooser();
  
      AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
      for (int i=0; i<panels.length; i++) {
         if (panels[i].toString().indexOf("DefaultRGBChooserPanel") == -1) {
            colorChooser.removeChooserPanel(panels[i]);
         }
      }
  
      this.add(colorChooser);
   

   }
  
   public static void main(String[] args) {
      Main main = new Main();
      main.setVisible(true);
   }
}