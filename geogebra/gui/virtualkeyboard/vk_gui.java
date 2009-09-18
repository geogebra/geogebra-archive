package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class vk_gui extends JFrame {


	Robot robot;
 
   
   private static final long serialVersionUID = 1L;
   private static Boolean Upper     = false;

   private JPanel jContentPane      = null;

   //private JTextArea jTextArea      = null;
   
   private JButton[][] Buttons = new JButton[6][13];

   private JButton SpaceButton      = null;
   private JButton CapsLockButton   = null;
   private JButton MathButton   = null;
   private JButton GreekButton   = null;

   private Application app;
   
   private int buttonRows = 5;
   private int buttonCols = 12;
   private int buttonSize;
   
   private int windowX, windowY;
   
   private Font font, smFont;
   
   //WindowUnicodeKeyboard kb;// = new WindowUnicodeKeyboard(robot);
   //Keyboard kb;// = new Keyboard();

   /**
    * This is the default constructor
    */
   public vk_gui(final Application app, int sizeX, int sizeY) {
      
	   super();
	   windowX = sizeX;
	   windowY = sizeY;
      this.app = app;
      this.setFocusableWindowState(false);
      this.setAlwaysOnTop(true);
      initialize();
     
   // Event Handling
      this.addComponentListener(new ComponentAdapter()
      {
      public void componentResized(ComponentEvent e)
      {
      JFrame tmp = (JFrame)e.getSource();
      
     
      if (tmp instanceof vk_gui)
    	  windowResized();
      /*
      if (tmp.getWidth()<300)
      {
      tmp.setSize(300, getHeight());
      validate();
      }
      else
      if(tmp.getHeight()<600)
      {
      tmp.setSize(getWidth(), 600);
      validate();
      }
      else if (tmp.getWidth()<300 && tmp.getHeight()<600)
      {
      tmp.setSize(300,650);
      validate();
      }*/
      }
      });
      
      
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
    	
      windowResized();
      /*
      try {

		kb = new WindowUnicodeKeyboard();
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//*/
   }
   
   final private void windowResized() {
	   
	      int sizeX = getWidth();
	      int sizeY = getHeight();
	      
	      buttonSize = Math.min(sizeX / (buttonCols ), sizeY / (buttonRows + 2));
	      if (buttonSize < 20) buttonSize = 20;
	   
	   updateButtons();
	   
   }

   /**
    * This method initializes this
    * 
    * @return void
    */
   private void initialize() {
      this.setSize(windowX, windowY);
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
      
	   updateButtons();

   }
   
   public void updateButtons() {
	      for (int i = 1 ; i <= buttonRows ; i++)
	          for (int j = 1 ; j <= buttonCols ; j++)
	        	  updateButton(i,j);	   
	      
	      updateSpaceButton();
	      updateCapsLockButton();
	      updateMathButton();
	      updateGreekButton();
   }

   /**
    * This method initializes SpaceButton   
    *    
    * @return javax.swing.JButton   
    */
   private JButton getSpaceButton() {
      if (SpaceButton == null) {

         SpaceButton                = new JButton();
         SpaceButton.setRequestFocusEnabled(false);
         //SpaceButton.setSize(new Dimension(ButtonX, ButtonY));
         //SpaceButton.setLocation(new Point(ButtonStart, linepos));
         updateSpaceButton();
         SpaceButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertText(" ");
               }
           });
      }
      return SpaceButton;
   }
   
   private void updateSpaceButton() {
       SpaceButton.setSize(new Dimension(buttonSize * 6, buttonSize / 2));
       SpaceButton.setLocation(new Point(buttonSize * 4, buttonSize * 17 / 4));
	   
   }
   
   private void updateCapsLockButton() {
	   CapsLockButton.setSize(new Dimension(buttonSize, buttonSize / 2));
	   CapsLockButton.setLocation(new Point(buttonSize / 2, buttonSize * 17 / 4));
	   
	   CapsLockButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private void updateMathButton() {
	   MathButton.setSize(new Dimension(buttonSize, buttonSize / 2));
	   MathButton.setLocation(new Point(buttonSize * 3 / 2, buttonSize * 17 / 4));
	   
	   MathButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private void updateGreekButton() {
	   GreekButton.setSize(new Dimension(buttonSize, buttonSize / 2));
	   GreekButton.setLocation(new Point(buttonSize * 5 / 2, buttonSize * 17 / 4));
	   
	   GreekButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private JButton getCapsLockButton() {
	      if (CapsLockButton == null) {

	         CapsLockButton             = new JButton("\u21e7");
	         updateCapsLockButton();
	         CapsLockButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  invertButtons();
	               }
	           });
	      }
	      return CapsLockButton;
	   }

   private JButton getMathButton() {
	      if (MathButton == null) {

	    	  MathButton             = new JButton("\u222b");
	         updateMathButton();
	         MathButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
		                  math = !math;
		                  greek = false;
		                  if (math)
		                	  start_vk.readConf(app, null, true);
		                  else
			            	  start_vk.readConf(app, null, false);
		                	  
		            	   updateButtons();
	               }
	           });
	      }
	      return MathButton;
	   }
   
   boolean greek = false;
   boolean math = false;

   private JButton getGreekButton() {
	      if (GreekButton == null) {

	    	  GreekButton             = new JButton("\u03c3");
	         updateGreekButton();
	         GreekButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  greek = !greek;
	                  math = false;
	                  if (greek)
	                	  start_vk.readConf(app, new Locale("el"), false);
	                  else
		            	  start_vk.readConf(app, null, false);
	                	  
	            	   updateButtons();
	               }
	           });
	      }
	      return GreekButton;
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
         jContentPane.add(getMathButton(), null);
         jContentPane.add(getGreekButton(), null);

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
   
   private JButton getButton(final int i, final int j) {
	      if (Buttons[i][j] == null) {
	         keys thisKeys = getKey(i, j); // start_vk.myKeys.get("B0101char");
	         Buttons[i][j]               = new JButton();
	         updateButton(i,j);
	         Insets Inset = new Insets(0,0,0,0);
	         Buttons[i][j].setMargin(Inset);
	         String text = Upper ? thisKeys.getUpperCase() : thisKeys.getLowerCase();
	         
			 Buttons[i][j].setText(processSpecialKeys(text));
			 
	         Buttons[i][j].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	                  insertKeyText(getKey(i, j));
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
  
   private void updateButton(int i, int j) {
	   keys k = getKey(i, j);
	      if(Upper) {
	         Buttons[i][j].setText(processSpecialKeys(k.getUpperCase()));
	      } else {
	         Buttons[i][j].setText(processSpecialKeys(k.getLowerCase()));
	      }
	      
	      
	      // skip a row (for spacebar etc)
	      int ii = (i == 5) ? 6 : i;
	      
	         Buttons[i][j].setBounds(new Rectangle(buttonSize * (j - 1), buttonSize * (ii - 1), buttonSize, buttonSize));
	         
	         // make sure "Esc" fits
	         int len = (Buttons[i][j].getText().length() + 1) / 2;
	         if (len == 0) len = 1;
	         
	         Buttons[i][j].setFont(getFont(buttonSize * 10 / 12 / len));
      
	   }
   
   private Font getFont(int size) {
	   
	   if (font == null || font.getSize() != size)
		   font = new Font(app.getAppFontNameSansSerif(), Font.PLAIN, size);
	   
	   return font;
   }
   
   private Font getSmallFont(int size) {
	   
	   if (smFont == null || smFont.getSize() != size)
		   smFont = new Font(app.getAppFontNameSansSerif(), Font.PLAIN, size);
	   
	   return smFont;
   }

}
