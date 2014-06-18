
// Example of the use java floating docking windows
// author programania.com

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Example {
  public static void main(String[] args) {
    JFrame jf;
    jf=new JFrame("Demo");
    jf.setLayout(null);
    Dimension max=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
    jf.setSize(max);
    jf.addWindowListener (new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}});
    jf.setVisible(true);

    Point l=jf.getContentPane().getLocationOnScreen();
    Dimension d=jf.getContentPane().getSize();

// Docking to ContentPane of frame
    JDWindow.g=new Rectangle(l.x,l.y, d.width,d.height);
// Docking to Screen
//  JDWindow.g=new Rectangle(0,0, max.width,max.height);

    for (int i=0; i<8; i++)
      JDWindow.showWin(null, jf, null, null, new Rectangle(i * 100, i * 100, 300, 200), "Title " + i, "Text " + i);

// Change the sizes and contents
    String t="";
    for (int y=0; y<400; y++) t+="Text"+y+(y%20==0?"<br>":"");

    ((JEditorPane)((JScrollPane) JDWindow.mf[7].c).getViewport().getComponent(0)).setText("<html><body>" + t);
    JDWindow.mf[7].setBounds(0,max.height/2,max.width/3,max.height/3);
  }

}
