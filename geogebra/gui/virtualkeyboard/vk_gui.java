package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;



public class vk_gui extends JFrame {


	Robot robot;
 
   
   private static final long serialVersionUID = 1L;
//   private static Boolean Upper     = false;

   private JPanel jContentPane      = null;

   //private JTextArea jTextArea      = null;
   
   private JButton[][] Buttons = new JButton[6][13];

   private JButton SpaceButton      = null;
   private JToggleButton CapsLockButton   = null;
   private JToggleButton AltButton   = null;
   private JToggleButton CtrlButton   = null;
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
	   
	   readConf(app, null, false);
	   
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
    *
   public void invertButtons() {
      
	   Upper = !Upper;
      
	   updateButtons();

   }//*/
   
   public void updateButtons() {
	      for (int i = 1 ; i <= buttonRows ; i++)
	          for (int j = 1 ; j <= buttonCols ; j++)
	        	  updateButton(i,j);	   
	      
	      updateSpaceButton();
	      updateCapsLockButton();
	      updateMathButton();
	      updateGreekButton();
	      updateAltButton();
	      updateCtrlButton();
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
       SpaceButton.setSize(new Dimension(buttonSize * 5 , buttonSize));
       SpaceButton.setLocation(new Point(buttonSize * 4, buttonSize * 4));
	   
   }
   
   private void updateCapsLockButton() {
	   CapsLockButton.setSize(new Dimension(buttonSize, buttonSize));
	   CapsLockButton.setLocation(new Point(buttonSize / 2, buttonSize * 4));
	   
	   CapsLockButton.setFont(getSmallFont(buttonSize * 5 / 18));
	   
	   //CapsLockButton.set
   }
   
   private void updateCtrlButton() {
	   CtrlButton.setSize(new Dimension(buttonSize, buttonSize));
	   CtrlButton.setLocation(new Point(buttonSize * 3 / 2, buttonSize * 4));
	   
	   CtrlButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private void updateAltButton() {
	   AltButton.setSize(new Dimension(buttonSize, buttonSize));
	   AltButton.setLocation(new Point(buttonSize * 5 / 2, buttonSize * 4));
	   
	   AltButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private void updateMathButton() {
	   MathButton.setSize(new Dimension(buttonSize, buttonSize));
	   MathButton.setLocation(new Point(buttonSize * 19 / 2, buttonSize * 4));
	   
	   MathButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private void updateGreekButton() {
	   GreekButton.setSize(new Dimension(buttonSize, buttonSize));
	   GreekButton.setLocation(new Point(buttonSize * 21 / 2, buttonSize * 4));
	   
	   GreekButton.setFont(getSmallFont(buttonSize * 5 / 18));
   }
   
   private JToggleButton getCapsLockButton() {
	      if (CapsLockButton == null) {

	         CapsLockButton             = new JToggleButton("\u21e7");
	         updateCapsLockButton();
	         CapsLockButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  updateButtons();
	               }
	           });
	      }
	      return CapsLockButton;
	   }

   private JToggleButton getAltButton() {
	      if (AltButton == null) {

	    	  AltButton             = new JToggleButton("Alt");
	         updateAltButton();
	         /*
	         AltButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  altPressed = !altPressed;
	                  updateAltButton();
	               }
	           });*/
	      }
	      return AltButton;
	   }

   private JToggleButton getCtrlButton() {
	      if (CtrlButton == null) {

	    	  CtrlButton             = new JToggleButton("Ctrl");
	         updateCtrlButton();
	         /*
	         CtrlButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  ctrlPressed = !ctrlPressed;
	                  updateCtrlButton();
	               }
	           });*/
	      }
	      return CtrlButton;
	   }

   private JButton getMathButton() {
	      if (MathButton == null) {

	    	  MathButton             = new JButton("\u222b");
	         updateMathButton();
	         MathButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
		                  if (KEYBOARD_MODE != KEYBOARD_MATH)
		                	  setMode(KEYBOARD_MATH);
		                  else
		                	  setMode(KEYBOARD_NORMAL);
		                	  

	               }
	           });
	      }
	      return MathButton;
	   }
   
   boolean greek = false;

   private JButton getGreekButton() {
	      if (GreekButton == null) {

	    	  GreekButton             = new JButton("\u03c3");
	         updateGreekButton();
	         GreekButton.addActionListener(new java.awt.event.ActionListener() {
	               public void actionPerformed(java.awt.event.ActionEvent e) {
	                  greek = !greek;
	                  setMode(KEYBOARD_NORMAL);
	                  if (greek)
	                	  readConf(app, new Locale("el"), false);
	                  else
		            	  readConf(app, null, false);
	                	  
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
         jContentPane.add(getAltButton(), null);
         jContentPane.add(getCtrlButton(), null);

      }
      return jContentPane;
   }
   
   
   public static char KEYBOARD_NORMAL = ' ';
   public static char KEYBOARD_MATH = 'M';
   public static char KEYBOARD_GREEK = 'H';
   public static char KEYBOARD_ACUTE = 'A';
   public static char KEYBOARD_GRAVE = 'G';
   public static char KEYBOARD_UMLAUT = 'U';
   public static char KEYBOARD_CEDILLA = 'c';
   public static char KEYBOARD_CARON = 'v';
   public static char KEYBOARD_CIRCUMFLEX = 'C';
   public static char KEYBOARD_BREVE = 'B';
   public static char KEYBOARD_TILDE = 'T';
   public static char KEYBOARD_OGONEK = 'O';
   public static char KEYBOARD_DOT_ABOVE = 'D';
   public static char KEYBOARD_RING_ABOVE = 'R';
   public static char KEYBOARD_DIALYTIKA_TONOS = 'd';
   public static char KEYBOARD_DOUBLE_ACUTE = 'a';

   
   public char KEYBOARD_MODE = KEYBOARD_NORMAL;

   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertText(String addchar) {
	   
	   if (addchar.length() == 1) 
		   switch (addchar.charAt(0)) {
		   case '\u00b4' : // acute
			   setMode(KEYBOARD_ACUTE);
			   return;

		   case '\u0060' : // grave
			   setMode(KEYBOARD_GRAVE);
			   return;
			   
		   case '\u02d8' : // breve
			   setMode(KEYBOARD_BREVE);
			   return;

		   case '\u0303' : // tilde
			   setMode(KEYBOARD_TILDE);
			   return;

		   case '\u005e' : // circumflex
			   setMode(KEYBOARD_CIRCUMFLEX);
			   return;

		   case '\u0385' : // dialytika tonos
			   setMode(KEYBOARD_DIALYTIKA_TONOS);
			   return;

		   case '\u00b8' : // tilde
			   setMode(KEYBOARD_CEDILLA);
			   return;

		   case '\u00a8' : // umlaut
			   setMode(KEYBOARD_UMLAUT);
			   return;

		   case '\u02c7' : // caron
			   setMode(KEYBOARD_CARON);
			   return;

		   case '\u02d9' : // dot above
			   setMode(KEYBOARD_DOT_ABOVE);
			   return;

		   case '\u02db' : // Ogonek
			   setMode(KEYBOARD_OGONEK);
			   return;

		   case '\u02da' : // ring above
			   setMode(KEYBOARD_RING_ABOVE);
			   return;

		   case '\u02dd' : // double acute
			   setMode(KEYBOARD_DOUBLE_ACUTE);
			   return;

		   	}
	   
	   if (addchar.equals("<enter>"))
		   addchar="\n";

      app.getGuiManager().insertStringIntoTextfield(addchar, getAltButton().isSelected(), getCtrlButton().isSelected(), getCapsLockButton().isSelected());
      
   }

   private void setMode(char mode) {
	   if (KEYBOARD_MODE == mode) {
		   KEYBOARD_MODE = KEYBOARD_NORMAL;
	   		updateButtons();
	   } else {
		   // reset first
		   KEYBOARD_MODE = KEYBOARD_NORMAL;
		   updateButtons();
		   
		   // new mode
		   KEYBOARD_MODE = mode;
		   updateButtons();
	   }
	   
	   //Application.debug("mode="+KEYBOARD_MODE);

   }
   
   private boolean Upper() {
	   return getCapsLockButton().isSelected();
   }
   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertKeyText(keys Keys) {
      if(Upper()) {
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
	   
	   keys ret1 = myKeys.get(sb.toString());
	   
	   //Application.debug("keyboard mode="+KEYBOARD_MODE);
	   
	   
	   if (KEYBOARD_MODE == ' ')
		   return ret1; // no accent needed
		
	   sb.append(KEYBOARD_MODE); // append 'A' for acute etc
	   
	   keys ret2 = myKeys.get(sb.toString());
	   
	   return ret2 != null ? ret2 : ret1;
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
	         String text = Upper() ? thisKeys.getUpperCase() : thisKeys.getLowerCase();
	         
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
	      if(Upper()) {
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

   private Hashtable<String, keys>   myKeys = new Hashtable<String, keys>();
   
   private void readConf(Application app, Locale loc, boolean math) {
	   
		//ResourceBundle rbKeyboard = MyResourceBundle.loadSingleBundleFile("/geogebra/gui/virtualkeyboard/keyboard_en_UK");

	   ResourceBundle rbKeyboard;
	   
	   if (math) {
		   rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboardMath", app.getLocale());
	   } else {
		   if (loc == null)
				rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboard", app.getLocale());
		   else {
			   rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboard", new Locale("el"));
		   }
	   }
	   
	   //myKeys.clear();
		
		Enumeration keys = rbKeyboard.getKeys();
		while (keys.hasMoreElements()) {
			String keyU = (String) keys.nextElement();
			
			if (keyU.endsWith("U")) {
				keys keyItem = new keys();
				String key = keyU.substring(0,keyU.length() - 1);

				String valueU = rbKeyboard.getString(keyU);
				String valueL = rbKeyboard.getString(key+"L");
				
				
				keyItem.setLowerCase(valueL);
				keyItem.setUpperCase(valueU);
				
				//Application.debug(key+"char "+valueL+" "+valueU);
				
				myKeys.put(key,keyItem);
			}
		}	
   }

}
