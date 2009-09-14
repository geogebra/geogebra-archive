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
   private static Boolean Upper     = true;

   private JPanel jContentPane      = null;

   //private JTextArea jTextArea      = null;
   
   private JButton[][] Buttons = new JButton[6][13];

   /*
   private JButton Button0101       = null;
   private JButton Button0102       = null;
   private JButton Button0103       = null;
   private JButton Button0104       = null;
   private JButton Button0105       = null;
   private JButton Button0106       = null;
   private JButton Button0107       = null;
   private JButton Button0108       = null;
   private JButton Button0109       = null;
   private JButton Button0110       = null;
   private JButton Button0111       = null;
   private JButton Button0112       = null;
   private JButton Button0201       = null;
   private JButton Button0202       = null;
   private JButton Button0203       = null;
   private JButton Button0204       = null;
   private JButton Button0205       = null;
   private JButton Button0206       = null;
   private JButton Button0207       = null;
   private JButton Button0208       = null;
   private JButton Button0209       = null;
   private JButton Button0210       = null;
   private JButton Button0211       = null;
   private JButton Button0212       = null;
   private JButton Button0301       = null;
   private JButton Button0302       = null;
   private JButton Button0303       = null;
   private JButton Button0304       = null;
   private JButton Button0305       = null;
   private JButton Button0306       = null;
   private JButton Button0307       = null;
   private JButton Button0308       = null;
   private JButton Button0309       = null;
   private JButton Button0310       = null;
   private JButton Button0311       = null;
   private JButton Button0312       = null;
   private JButton Button0401       = null;
   private JButton Button0402       = null;
   private JButton Button0403       = null;
   private JButton Button0404       = null;
   private JButton Button0405       = null;
   private JButton Button0406       = null;
   private JButton Button0407       = null;
   private JButton Button0408       = null;
   private JButton Button0409       = null;
   private JButton Button0410       = null;
   private JButton Button0411       = null;
   private JButton Button0412       = null;
   private JButton Button0501       = null;
   private JButton Button0502       = null;
   private JButton Button0503       = null;
   private JButton Button0504       = null;
   private JButton Button0505       = null;
   private JButton Button0506       = null;
   private JButton Button0507       = null;
   private JButton Button0508       = null;
   private JButton Button0509       = null;
   private JButton Button0510       = null;
   private JButton Button0511       = null;
   private JButton Button0512       = null;
   */
   
   private JButton SpaceButton      = null;
   private JButton CapsLockButton   = null;
   private JButton TOButton         = null;

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
    * This method inverts the hole keyboard from Upper to Lower and from Lower to Upper case.
    *
    * @return void
    */
   public void invertButtons() {
      if(Upper) {
         Upper = false;
      } else {
         Upper = true;
      }

      for (int i = 1 ; i <= 5 ; i++)
      for (int j = 1 ; j <= 12 ; j++)
    	  changeButton(i,j);

      /*
      changeButton0101();
      changeButton0102();
      changeButton0103();
      changeButton0104();
      changeButton0105();
      changeButton0106();
      changeButton0107();
      changeButton0108();
      changeButton0109();
      changeButton0110();
      changeButton0111();
      changeButton0112();
      changeButton0201();
      changeButton0202();
      changeButton0203();
      changeButton0204();
      changeButton0205();
      changeButton0206();
      changeButton0207();
      changeButton0208();
      changeButton0209();
      changeButton0210();
      changeButton0211();
      changeButton0212();
      changeButton0301();
      changeButton0302();
      changeButton0303();
      changeButton0304();
      changeButton0305();
      changeButton0306();
      changeButton0307();
      changeButton0308();
      changeButton0309();
      changeButton0310();
      changeButton0311();
      changeButton0312();
      changeButton0401();
      changeButton0402();
      changeButton0403();
      changeButton0404();
      changeButton0405();
      changeButton0406();
      changeButton0407();
      changeButton0408();
      changeButton0409();
      changeButton0410();
      changeButton0411();
      changeButton0412();
      changeButton0501();
      changeButton0502();
      changeButton0503();
      changeButton0504();
      changeButton0505();
      changeButton0506();
      changeButton0507();
      changeButton0508();
      changeButton0509();
      changeButton0510();
      changeButton0511();
      changeButton0512();*/
   }



   /**
    * This method initializes jTextArea   
    *    
    * @return javax.swing.JTextArea   
    *
   private JTextArea getJTextArea() {
      if (jTextArea == null) {
         jTextArea = new JTextArea();
         jTextArea.setBounds(new Rectangle(34, 17, 561, 39));
         jTextArea.setToolTipText("Inputarea");
         String fromConf = start_vk.myConf.get("DefaultTextOrientation");
         if(fromConf.equals("RIGHT_TO_LEFT")) {
            InputAreaCO = ComponentOrientation.RIGHT_TO_LEFT;
         } else {
            InputAreaCO = ComponentOrientation.LEFT_TO_RIGHT;
         }
         jTextArea.setComponentOrientation(InputAreaCO);
         jTextArea.setEditable(true);
      }
      return jTextArea;
   }//*/

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

    private JButton getTOButton() {
      if (TOButton == null) {
         final Integer linepos      = Integer.valueOf(start_vk.myConf.get("ButtonLineTO"));
         final Integer ButtonStart  = Integer.valueOf(start_vk.myConf.get("TOButtonStart"));
         final Integer ButtonX      = Integer.valueOf(start_vk.myConf.get("TOButtonSizeX"));
         final Integer ButtonY      = Integer.valueOf(start_vk.myConf.get("TOButtonSizeY"));
         
         Image img = getToolkit().getImage("img/left_right.gif");
         MediaTracker mt = new MediaTracker(this);
         mt.addImage(img, 0);
         try {
            //Warten, bis das Image vollständig geladen ist,
            mt.waitForAll();
         } catch (InterruptedException e) {
            //nothing
         }
         ImageIcon ImageIcon = new ImageIcon(img);
         TOButton                = new JButton(ImageIcon);
         TOButton.setSize(new Dimension(ButtonX, ButtonY));
         TOButton.setLocation(new Point(ButtonStart, linepos));
         TOButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent e) {
                  //if(jTextArea.getComponentOrientation().isLeftToRight()) {
                  //   jTextArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                  //} else {
                  //   jTextArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                  //}
               }
           });
      }
      return TOButton;
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
         
         /*
         jContentPane.add(getButton0101(), null);
         jContentPane.add(getButton0102(), null);
         jContentPane.add(getButton0103(), null);
         jContentPane.add(getButton0104(), null);
         jContentPane.add(getButton0105(), null);
         jContentPane.add(getButton0106(), null);
         jContentPane.add(getButton0107(), null);
         jContentPane.add(getButton0108(), null);
         jContentPane.add(getButton0109(), null);
         jContentPane.add(getButton0110(), null);
         jContentPane.add(getButton0111(), null);
         jContentPane.add(getButton0112(), null);
         jContentPane.add(getButton0201(), null);
         jContentPane.add(getButton0202(), null);
         jContentPane.add(getButton0203(), null);
         jContentPane.add(getButton0204(), null);
         jContentPane.add(getButton0205(), null);
         jContentPane.add(getButton0206(), null);
         jContentPane.add(getButton0207(), null);
         jContentPane.add(getButton0208(), null);
         jContentPane.add(getButton0209(), null);
         jContentPane.add(getButton0210(), null);
         jContentPane.add(getButton0211(), null);
         jContentPane.add(getButton0212(), null);
         jContentPane.add(getButton0301(), null);
         jContentPane.add(getButton0302(), null);
         jContentPane.add(getButton0303(), null);
         jContentPane.add(getButton0304(), null);
         jContentPane.add(getButton0305(), null);
         jContentPane.add(getButton0306(), null);
         jContentPane.add(getButton0307(), null);
         jContentPane.add(getButton0308(), null);
         jContentPane.add(getButton0309(), null);
         jContentPane.add(getButton0310(), null);
         jContentPane.add(getButton0311(), null);
         jContentPane.add(getButton0312(), null);
         jContentPane.add(getButton0401(), null);
         jContentPane.add(getButton0402(), null);
         jContentPane.add(getButton0403(), null);
         jContentPane.add(getButton0404(), null);
         jContentPane.add(getButton0405(), null);
         jContentPane.add(getButton0406(), null);
         jContentPane.add(getButton0407(), null);
         jContentPane.add(getButton0408(), null);
         jContentPane.add(getButton0409(), null);
         jContentPane.add(getButton0410(), null);
         jContentPane.add(getButton0411(), null);
         jContentPane.add(getButton0412(), null);
         jContentPane.add(getButton0501(), null);
         jContentPane.add(getButton0502(), null);
         jContentPane.add(getButton0503(), null);
         jContentPane.add(getButton0504(), null);
         jContentPane.add(getButton0505(), null);
         jContentPane.add(getButton0506(), null);
         jContentPane.add(getButton0507(), null);
         jContentPane.add(getButton0508(), null);
         jContentPane.add(getButton0509(), null);
         jContentPane.add(getButton0510(), null);
         jContentPane.add(getButton0511(), null);
         jContentPane.add(getButton0512(), null);

*/
         jContentPane.add(getSpaceButton(), null);
         jContentPane.add(getCapsLockButton(), null);

         //Image img = getToolkit().getImage("img/left_right.gif");
         //MediaTracker mt = new MediaTracker(this);
         //mt.addImage(img, 0);
         //try {
         //   //Warten, bis das Image vollständig geladen ist,
         //   mt.waitForAll();
         //} catch (InterruptedException e) {
         //   //nothing
         //}
         //ImageIcon ImageIcon = new ImageIcon(img);
         jContentPane.add(getTOButton(), null);
      }
      return jContentPane;
   }

   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertText(String addchar) {
      //KeyEvent ke = new KeyEvent(new javax.swing.JCheckBox(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_A, 'a', KeyEvent.KEY_LOCATION_STANDARD);
      //app.getGlobalKeyDispatcher().dispatchKeyEvent(ke);
	   
	   if (addchar.equals("<enter>"))
		   addchar="\n";

      app.getGuiManager().insertStringIntoTextfield(addchar);
      
      //kb.type(addchar);
      //kb.type("hello\u00b0");
      //Application.debug("");

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
	         Buttons[i][j].setText(thisKeys.getUpperCase());
	         Buttons[i][j].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent e) {
	                  insertKeyText(thisKeys);
	            }
	         });
	      }
	      return Buttons[i][j];
	   }
  
   private void changeButton(int i, int j) {
	   keys k = getKey(i, j);
	      final String upChar   = k.getUpperCase();
	      final String downChar = k.getLowerCase();
	      if(Upper) {
	         Buttons[i][j].setText(upChar);
	      } else {
	         Buttons[i][j].setText(downChar);
	      }
	   }

}
