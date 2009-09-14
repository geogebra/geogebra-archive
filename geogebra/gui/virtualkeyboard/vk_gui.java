package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class vk_gui extends JFrame {

//   private ComponentOrientation InputAreaCO = ComponentOrientation.RIGHT_TO_LEFT;  //  @jve:decl-index=0:
   private ComponentOrientation InputAreaCO = ComponentOrientation.LEFT_TO_RIGHT;

	Robot robot;
 
   
   private static final long serialVersionUID = 1L;
   private static Boolean Upper     = false;

   private JPanel jContentPane      = null;

   //private JTextArea jTextArea      = null;
   
   private JButton[][] Buttons = new JButton[6][13];

   private JButton SpaceButton      = null;
   private JButton CapsLockButton   = null;

   private Application app;
   
   //WindowUnicodeKeyboard kb;// = new WindowUnicodeKeyboard(robot);
   //Keyboard kb;// = new Keyboard();

   /**
    * This is the default constructor
    */
   public vk_gui(Application app) {
      super();
      initialize();
      this.app = app;
      this.setFocusableWindowState(false);
      this.setAlwaysOnTop(true);
      
      // http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/#Setting-the-Opacity-Level-of-a-Window
      //AWTUtilities.setWindowOpacity
      
      float transparency = 0.5f;
      
      try { // Java 6u10+ only
    	   Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
    	   Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
    	   mSetWindowOpacity.invoke(null, this, Float.valueOf(transparency));
    	} catch (Exception ex) {
    		
    		// fallback for OSX Leopard pre-6u10
    		this.getRootPane().putClientProperty("Window.alpha", Float.valueOf(transparency));

    	} 
    	
      
      /*
      try {

		kb = new WindowUnicodeKeyboard();
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//*/
   }

   /**
    * This method initializes this
    * 
    * @return void
    */
   private void initialize() {
      final Integer   WindowX  = Integer.valueOf(start_vk.myConf.get("WindowSizeX"));
      final Integer   WindowY  = Integer.valueOf(start_vk.myConf.get("WindowSizeY"));
      this.setSize(WindowX, WindowY);
      this.setName("MainPanel");
      this.setContentPane(getJContentPane());
      this.setTitle("Virtual Keyboard");
   }

   /**
    * This method inverts the whole keyboard from Upper to Lower and from Lower to Upper case.
    *
    * @return void
    */
   public void invertButtons() {
      
	   Upper = !Upper;
      
      for (int i = 1 ; i <= 5 ; i++)
          for (int j = 1 ; j <= 12 ; j++)
        	  changeButton(i,j);	   

   }

   /**
    * This method initializes SpaceButton   
    *    
    * @return javax.swing.JButton   
    */
   private JButton getSpaceButton() {
      if (SpaceButton == null) {
         final Integer linepos      = Integer.valueOf(start_vk.myConf.get("ButtonLine6"));
         final Integer ButtonStart  = Integer.valueOf(start_vk.myConf.get("SpaceButtonStart"));
         final Integer ButtonX      = Integer.valueOf(start_vk.myConf.get("SpaceButtonSizeX"));
         final Integer ButtonY      = Integer.valueOf(start_vk.myConf.get("SpaceButtonSizeY"));

         SpaceButton                = new JButton();
         SpaceButton.setRequestFocusEnabled(false);
         SpaceButton.setSize(new Dimension(ButtonX, ButtonY));
         SpaceButton.setLocation(new Point(ButtonStart, linepos));
         SpaceButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertText(" ");
               }
           });
      }
      return SpaceButton;
   }
   
   private JButton getCapsLockButton() {
      if (CapsLockButton == null) {
         final Integer linepos      = Integer.valueOf(start_vk.myConf.get("ButtonLine6"));
         final Integer ButtonStart  = Integer.valueOf(start_vk.myConf.get("CapsLockButtonStart"));
         final Integer ButtonX      = Integer.valueOf(start_vk.myConf.get("CapsLockButtonSizeX"));
         final Integer ButtonY      = Integer.valueOf(start_vk.myConf.get("CapsLockButtonSizeY"));

         Image img = getToolkit().getImage("geogebra/gui/virtualkeyboard/caps_lock.gif");
         MediaTracker mt = new MediaTracker(this);
         mt.addImage(img, 0);
         try {
            //Warten, bis das Image vollständig geladen ist,
            mt.waitForAll();
         } catch (InterruptedException e) {
            //nothing
         }
         ImageIcon ImageIcon = new ImageIcon(img);
         CapsLockButton             = new JButton(ImageIcon);
         CapsLockButton.setSize(new Dimension(ButtonX, ButtonY));
         CapsLockButton.setLocation(new Point(ButtonStart, linepos));
         CapsLockButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent e) {
                  invertButtons();
               }
           });
      }
      return CapsLockButton;
   }


   /**
    * This method initializes jContentPane
    * 
    * @return javax.swing.JPanel
    */
   private JPanel getJContentPane() {
      if (jContentPane == null) {
         jContentPane = new JPanel();

         jContentPane.setLayout(null);
         //jContentPane.add(getJTextArea(), null);
         
         for (int i = 1 ; i <= 5 ; i++)
         for (int j = 1 ; j <= 12 ; j++)
        	 jContentPane.add(getButton(i,j), null);
         
         jContentPane.add(getSpaceButton(), null);
         jContentPane.add(getCapsLockButton(), null);

      }
      return jContentPane;
   }

   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertText(String addchar) {
	   
	   if (addchar.equals("<enter>"))
		   addchar="\n";

      app.getGuiManager().insertStringIntoTextfield(addchar);
      
   }

   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertKeyText(keys Keys) {
      if(Upper) {
    	  //kb.type(Keys.getUpperCase());
    	  insertText(Keys.getUpperCase());
      } else {
    	  //kb.type(Keys.getLowerCase());
    	  insertText(Keys.getLowerCase());
      }
   }//*/

   private StringBuffer sb = new StringBuffer();
   
   /* to replace start_vk.myKeys.get("B0101char");
    * 
    */
   private keys getKey(int i, int j) {
   
	   sb.setLength(0);
	   sb.append('B');
	   if (i < 10) sb.append('0'); // pad from "1" to "01"
	   sb.append(i+"");
	   if (j < 10) sb.append('0'); // pad from "1" to "01"
	   sb.append(j+"");
	   sb.append("char");
	   
	   return start_vk.myKeys.get(sb.toString());
   }
   
   private String pad(int i) {
	   if (i < 10) return "0"+i;
	   else return ""+i;
   }
   
   private JButton getButton(int i, int j) {
	      if (Buttons[i][j] == null) {
	         final keys      thisKeys = getKey(i, j); // start_vk.myKeys.get("B0101char");
	         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button"+pad(j)+"Start"));
	         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine"+i));
	         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
	         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
	         Buttons[i][j]               = new JButton();
	         Buttons[i][j].setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
	         Insets Inset = new Insets(0,0,0,0);
	         Buttons[i][j].setMargin(Inset);
	         String text = Upper ? thisKeys.getUpperCase() : thisKeys.getLowerCase();
	         
			 Buttons[i][j].setText(processSpecialKeys(text));
			         
	         Buttons[i][j].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	                  insertKeyText(thisKeys);
	            }
	         });
	      }
	      return Buttons[i][j];
	   }
   
   private String processSpecialKeys(String text) {
	   
	   // check first for speed
	   if (!text.startsWith("<")) return text;
	   
	   if (text.equals("<enter>")) return "\u21b2";
	   if (text.equals("<backspace>")) return "\u21a4";
	   if (text.equals("<escape>")) return "Esc";
	   if (text.equals("<left>")) return "\u2190";
	   if (text.equals("<up>")) return "\u2191";
	   if (text.equals("<right>")) return "\u2192";
	   if (text.equals("<down>")) return "\u2193";
	   
	   return text;
   }
  
   private void changeButton(int i, int j) {
	   keys k = getKey(i, j);
	      if(Upper) {
	         Buttons[i][j].setText(processSpecialKeys(k.getUpperCase()));
	      } else {
	         Buttons[i][j].setText(processSpecialKeys(k.getLowerCase()));
	      }
	   }

}
