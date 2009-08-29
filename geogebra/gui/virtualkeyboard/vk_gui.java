package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
   
   private JButton SpaceButton      = null;
   private JButton CapsLockButton   = null;
   private JButton TOButton         = null;

   private Application app;
   
   WindowUnicodeKeyboard kb;// = new WindowUnicodeKeyboard(robot);
   //Keyboard kb;// = new Keyboard();

   /**
    * This is the default constructor
    */
   public vk_gui(Application app) {
      super();
      initialize();
      this.app = app;
      this.setFocusableWindowState(false);
      
      try {

		kb = new WindowUnicodeKeyboard();
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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
      changeButton0512();
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
      KeyEvent ke = new KeyEvent(new javax.swing.JCheckBox(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_A, 'a', KeyEvent.KEY_LOCATION_STANDARD);
      app.getGlobalKeyDispatcher().dispatchKeyEvent(ke);

      kb.type(addchar);
      //kb.type("hello\u00b0");
      Application.debug("");

   }

   /**
    * This method adds a char to the text-field
    *
    * @return void
    */
   private void insertKeyText(keys Keys) {
      if(Upper) {
    	  kb.type(Keys.getUpperCase());
      } else {
    	  kb.type(Keys.getLowerCase());
      }
      Application.debug("");
   }//*/


   /**
   * This method initializes Button0101
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0101() {
      if (Button0101 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0101char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button01Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0101               = new JButton();
         Button0101.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0101.setMargin(Inset);
         Button0101.setText(thisKeys.getUpperCase());
         Button0101.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0101;
   }
   private void changeButton0101() {
      final String upChar   = start_vk.myKeys.get("B0101char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0101char").getLowerCase();
      if(Upper) {
         Button0101.setText(upChar);
      } else {
         Button0101.setText(downChar);
      }
   }
   /**
   * This method initializes Button0102
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0102() {
      if (Button0102 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0102char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button02Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0102               = new JButton();
         Button0102.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0102.setMargin(Inset);
         Button0102.setText(thisKeys.getUpperCase());
         Button0102.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0102;
   }
   private void changeButton0102() {
      final String upChar   = start_vk.myKeys.get("B0102char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0102char").getLowerCase();
      if(Upper) {
         Button0102.setText(upChar);
      } else {
         Button0102.setText(downChar);
      }
   }
   /**
   * This method initializes Button0103
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0103() {
      if (Button0103 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0103char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button03Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0103               = new JButton();
         Button0103.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0103.setMargin(Inset);
         Button0103.setText(thisKeys.getUpperCase());
         Button0103.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0103;
   }
   private void changeButton0103() {
      final String upChar   = start_vk.myKeys.get("B0103char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0103char").getLowerCase();
      if(Upper) {
         Button0103.setText(upChar);
      } else {
         Button0103.setText(downChar);
      }
   }
   /**
   * This method initializes Button0104
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0104() {
      if (Button0104 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0104char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button04Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0104               = new JButton();
         Button0104.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0104.setMargin(Inset);
         Button0104.setText(thisKeys.getUpperCase());
         Button0104.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0104;
   }
   private void changeButton0104() {
      final String upChar   = start_vk.myKeys.get("B0104char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0104char").getLowerCase();
      if(Upper) {
         Button0104.setText(upChar);
      } else {
         Button0104.setText(downChar);
      }
   }
   /**
   * This method initializes Button0105
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0105() {
      if (Button0105 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0105char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button05Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0105               = new JButton();
         Button0105.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0105.setMargin(Inset);
         Button0105.setText(thisKeys.getUpperCase());
         Button0105.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0105;
   }
   private void changeButton0105() {
      final String upChar   = start_vk.myKeys.get("B0105char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0105char").getLowerCase();
      if(Upper) {
         Button0105.setText(upChar);
      } else {
         Button0105.setText(downChar);
      }
   }
   /**
   * This method initializes Button0106
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0106() {
      if (Button0106 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0106char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button06Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0106               = new JButton();
         Button0106.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0106.setMargin(Inset);
         Button0106.setText(thisKeys.getUpperCase());
         Button0106.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0106;
   }
   private void changeButton0106() {
      final String upChar   = start_vk.myKeys.get("B0106char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0106char").getLowerCase();
      if(Upper) {
         Button0106.setText(upChar);
      } else {
         Button0106.setText(downChar);
      }
   }
   /**
   * This method initializes Button0107
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0107() {
      if (Button0107 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0107char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button07Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0107               = new JButton();
         Button0107.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0107.setMargin(Inset);
         Button0107.setText(thisKeys.getUpperCase());
         Button0107.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0107;
   }
   private void changeButton0107() {
      final String upChar   = start_vk.myKeys.get("B0107char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0107char").getLowerCase();
      if(Upper) {
         Button0107.setText(upChar);
      } else {
         Button0107.setText(downChar);
      }
   }
   /**
   * This method initializes Button0108
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0108() {
      if (Button0108 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0108char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button08Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0108               = new JButton();
         Button0108.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0108.setMargin(Inset);
         Button0108.setText(thisKeys.getUpperCase());
         Button0108.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0108;
   }
   private void changeButton0108() {
      final String upChar   = start_vk.myKeys.get("B0108char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0108char").getLowerCase();
      if(Upper) {
         Button0108.setText(upChar);
      } else {
         Button0108.setText(downChar);
      }
   }
   /**
   * This method initializes Button0109
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0109() {
      if (Button0109 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0109char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button09Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0109               = new JButton();
         Button0109.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0109.setMargin(Inset);
         Button0109.setText(thisKeys.getUpperCase());
         Button0109.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0109;
   }
   private void changeButton0109() {
      final String upChar   = start_vk.myKeys.get("B0109char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0109char").getLowerCase();
      if(Upper) {
         Button0109.setText(upChar);
      } else {
         Button0109.setText(downChar);
      }
   }
   /**
   * This method initializes Button0110
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0110() {
      if (Button0110 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0110char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button10Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0110               = new JButton();
         Button0110.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0110.setMargin(Inset);
         Button0110.setText(thisKeys.getUpperCase());
         Button0110.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0110;
   }
   private void changeButton0110() {
      final String upChar   = start_vk.myKeys.get("B0110char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0110char").getLowerCase();
      if(Upper) {
         Button0110.setText(upChar);
      } else {
         Button0110.setText(downChar);
      }
   }
   /**
   * This method initializes Button0111
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0111() {
      if (Button0111 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0111char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button11Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0111               = new JButton();
         Button0111.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0111.setMargin(Inset);
         Button0111.setText(thisKeys.getUpperCase());
         Button0111.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0111;
   }
   private void changeButton0111() {
      final String upChar   = start_vk.myKeys.get("B0111char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0111char").getLowerCase();
      if(Upper) {
         Button0111.setText(upChar);
      } else {
         Button0111.setText(downChar);
      }
   }
   /**
   * This method initializes Button0112
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0112() {
      if (Button0112 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0112char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button12Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine1"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0112               = new JButton();
         Button0112.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0112.setMargin(Inset);
         Button0112.setText(thisKeys.getUpperCase());
         Button0112.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0112;
   }
   private void changeButton0112() {
      final String upChar   = start_vk.myKeys.get("B0112char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0112char").getLowerCase();
      if(Upper) {
         Button0112.setText(upChar);
      } else {
         Button0112.setText(downChar);
      }
   }
   /**
   * This method initializes Button0201
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0201() {
      if (Button0201 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0201char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button01Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0201               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0201.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0201.setMargin(Inset);
         Button0201.setText(thisKeys.getUpperCase());
         Button0201.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0201;
   }
   private void changeButton0201() {
      final String upChar   = start_vk.myKeys.get("B0201char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0201char").getLowerCase();
      if(Upper) {
         Button0201.setText(upChar);
      } else {
         Button0201.setText(downChar);
      }
   }
   /**
   * This method initializes Button0202
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0202() {
      if (Button0202 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0202char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button02Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0202               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0202.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0202.setMargin(Inset);
         Button0202.setText(thisKeys.getUpperCase());
         Button0202.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0202;
   }
   private void changeButton0202() {
      final String upChar   = start_vk.myKeys.get("B0202char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0202char").getLowerCase();
      if(Upper) {
         Button0202.setText(upChar);
      } else {
         Button0202.setText(downChar);
      }
   }
   /**
   * This method initializes Button0203
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0203() {
      if (Button0203 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0203char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button03Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0203               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0203.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0203.setMargin(Inset);
         Button0203.setText(thisKeys.getUpperCase());
         Button0203.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0203;
   }
   private void changeButton0203() {
      final String upChar   = start_vk.myKeys.get("B0203char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0203char").getLowerCase();
      if(Upper) {
         Button0203.setText(upChar);
      } else {
         Button0203.setText(downChar);
      }
   }
   /**
   * This method initializes Button0204
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0204() {
      if (Button0204 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0204char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button04Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0204               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0204.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0204.setMargin(Inset);
         Button0204.setText(thisKeys.getUpperCase());
         Button0204.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0204;
   }
   private void changeButton0204() {
      final String upChar   = start_vk.myKeys.get("B0204char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0204char").getLowerCase();
      if(Upper) {
         Button0204.setText(upChar);
      } else {
         Button0204.setText(downChar);
      }
   }
   /**
   * This method initializes Button0205
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0205() {
      if (Button0205 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0205char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button05Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0205               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0205.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0205.setMargin(Inset);
         Button0205.setText(thisKeys.getUpperCase());
         Button0205.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0205;
   }
   private void changeButton0205() {
      final String upChar   = start_vk.myKeys.get("B0205char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0205char").getLowerCase();
      if(Upper) {
         Button0205.setText(upChar);
      } else {
         Button0205.setText(downChar);
      }
   }
   /**
   * This method initializes Button0206
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0206() {
      if (Button0206 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0206char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button06Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0206               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0206.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0206.setMargin(Inset);
         Button0206.setText(thisKeys.getUpperCase());
         Button0206.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0206;
   }
   private void changeButton0206() {
      final String upChar   = start_vk.myKeys.get("B0206char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0206char").getLowerCase();
      if(Upper) {
         Button0206.setText(upChar);
      } else {
         Button0206.setText(downChar);
      }
   }
   /**
   * This method initializes Button0207
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0207() {
      if (Button0207 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0207char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button07Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0207               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0207.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0207.setMargin(Inset);
         Button0207.setText(thisKeys.getUpperCase());
         Button0207.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0207;
   }
   private void changeButton0207() {
      final String upChar   = start_vk.myKeys.get("B0207char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0207char").getLowerCase();
      if(Upper) {
         Button0207.setText(upChar);
      } else {
         Button0207.setText(downChar);
      }
   }
   /**
   * This method initializes Button0208
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0208() {
      if (Button0208 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0208char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button08Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0208               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0208.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0208.setMargin(Inset);
         Button0208.setText(thisKeys.getUpperCase());
         Button0208.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0208;
   }
   private void changeButton0208() {
      final String upChar   = start_vk.myKeys.get("B0208char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0208char").getLowerCase();
      if(Upper) {
         Button0208.setText(upChar);
      } else {
         Button0208.setText(downChar);
      }
   }
   /**
   * This method initializes Button0209
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0209() {
      if (Button0209 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0209char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button09Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0209               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0209.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0209.setMargin(Inset);
         Button0209.setText(thisKeys.getUpperCase());
         Button0209.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0209;
   }
   private void changeButton0209() {
      final String upChar   = start_vk.myKeys.get("B0209char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0209char").getLowerCase();
      if(Upper) {
         Button0209.setText(upChar);
      } else {
         Button0209.setText(downChar);
      }
   }
   /**
   * This method initializes Button0210
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0210() {
      if (Button0210 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0210char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button10Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0210               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0210.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0210.setMargin(Inset);
         Button0210.setText(thisKeys.getUpperCase());
         Button0210.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0210;
   }
   private void changeButton0210() {
      final String upChar   = start_vk.myKeys.get("B0210char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0210char").getLowerCase();
      if(Upper) {
         Button0210.setText(upChar);
      } else {
         Button0210.setText(downChar);
      }
   }
   /**
   * This method initializes Button0211
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0211() {
      if (Button0211 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0211char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button11Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0211               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0211.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0211.setMargin(Inset);
         Button0211.setText(thisKeys.getUpperCase());
         Button0211.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0211;
   }
   private void changeButton0211() {
      final String upChar   = start_vk.myKeys.get("B0211char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0211char").getLowerCase();
      if(Upper) {
         Button0211.setText(upChar);
      } else {
         Button0211.setText(downChar);
      }
   }
   /**
   * This method initializes Button0212
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0212() {
      if (Button0212 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0212char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button12Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine2"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0212               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0212.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0212.setMargin(Inset);
         Button0212.setText(thisKeys.getUpperCase());
         Button0212.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0212;
   }
   private void changeButton0212() {
      final String upChar   = start_vk.myKeys.get("B0212char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0212char").getLowerCase();
      if(Upper) {
         Button0212.setText(upChar);
      } else {
         Button0212.setText(downChar);
      }
   }
   /**
   * This method initializes Button0301
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0301() {
      if (Button0301 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0301char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button01Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0301               = new JButton();
         Button0301.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0301.setMargin(Inset);
         Button0301.setText(thisKeys.getUpperCase());
         Button0301.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0301;
   }
   private void changeButton0301() {
      final String upChar   = start_vk.myKeys.get("B0301char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0301char").getLowerCase();
      if(Upper) {
         Button0301.setText(upChar);
      } else {
         Button0301.setText(downChar);
      }
   }
   /**
   * This method initializes Button0302
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0302() {
      if (Button0302 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0302char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button02Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0302               = new JButton();
         Button0302.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0302.setMargin(Inset);
         Button0302.setText(thisKeys.getUpperCase());
         Button0302.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0302;
   }
   private void changeButton0302() {
      final String upChar   = start_vk.myKeys.get("B0302char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0302char").getLowerCase();
      if(Upper) {
         Button0302.setText(upChar);
      } else {
         Button0302.setText(downChar);
      }
   }
   /**
   * This method initializes Button0303
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0303() {
      if (Button0303 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0303char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button03Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0303               = new JButton();
         Button0303.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0303.setMargin(Inset);
         Button0303.setText(thisKeys.getUpperCase());
         Button0303.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0303;
   }
   private void changeButton0303() {
      final String upChar   = start_vk.myKeys.get("B0303char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0303char").getLowerCase();
      if(Upper) {
         Button0303.setText(upChar);
      } else {
         Button0303.setText(downChar);
      }
   }
   /**
   * This method initializes Button0304
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0304() {
      if (Button0304 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0304char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button04Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0304               = new JButton();
         Button0304.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0304.setMargin(Inset);
         Button0304.setText(thisKeys.getUpperCase());
         Button0304.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0304;
   }
   private void changeButton0304() {
      final String upChar   = start_vk.myKeys.get("B0304char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0304char").getLowerCase();
      if(Upper) {
         Button0304.setText(upChar);
      } else {
         Button0304.setText(downChar);
      }
   }
   /**
   * This method initializes Button0305
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0305() {
      if (Button0305 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0305char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button05Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0305               = new JButton();
         Button0305.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0305.setMargin(Inset);
         Button0305.setText(thisKeys.getUpperCase());
         Button0305.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0305;
   }
   private void changeButton0305() {
      final String upChar   = start_vk.myKeys.get("B0305char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0305char").getLowerCase();
      if(Upper) {
         Button0305.setText(upChar);
      } else {
         Button0305.setText(downChar);
      }
   }
   /**
   * This method initializes Button0306
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0306() {
      if (Button0306 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0306char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button06Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0306               = new JButton();
         Button0306.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0306.setMargin(Inset);
         Button0306.setText(thisKeys.getUpperCase());
         Button0306.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0306;
   }
   private void changeButton0306() {
      final String upChar   = start_vk.myKeys.get("B0306char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0306char").getLowerCase();
      if(Upper) {
         Button0306.setText(upChar);
      } else {
         Button0306.setText(downChar);
      }
   }
   /**
   * This method initializes Button0307
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0307() {
      if (Button0307 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0307char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button07Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0307               = new JButton();
         Button0307.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0307.setMargin(Inset);
         Button0307.setText(thisKeys.getUpperCase());
         Button0307.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0307;
   }
   private void changeButton0307() {
      final String upChar   = start_vk.myKeys.get("B0307char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0307char").getLowerCase();
      if(Upper) {
         Button0307.setText(upChar);
      } else {
         Button0307.setText(downChar);
      }
   }
   /**
   * This method initializes Button0308
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0308() {
      if (Button0308 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0308char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button08Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0308               = new JButton();
         Button0308.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0308.setMargin(Inset);
         Button0308.setText(thisKeys.getUpperCase());
         Button0308.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0308;
   }
   private void changeButton0308() {
      final String upChar   = start_vk.myKeys.get("B0308char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0308char").getLowerCase();
      if(Upper) {
         Button0308.setText(upChar);
      } else {
         Button0308.setText(downChar);
      }
   }
   /**
   * This method initializes Button0309
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0309() {
      if (Button0309 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0309char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button09Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0309               = new JButton();
         Button0309.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0309.setMargin(Inset);
         Button0309.setText(thisKeys.getUpperCase());
         Button0309.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0309;
   }
   private void changeButton0309() {
      final String upChar   = start_vk.myKeys.get("B0309char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0309char").getLowerCase();
      if(Upper) {
         Button0309.setText(upChar);
      } else {
         Button0309.setText(downChar);
      }
   }
   /**
   * This method initializes Button0310
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0310() {
      if (Button0310 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0310char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button10Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0310               = new JButton();
         Button0310.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0310.setMargin(Inset);
         Button0310.setText(thisKeys.getUpperCase());
         Button0310.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0310;
   }
   private void changeButton0310() {
      final String upChar   = start_vk.myKeys.get("B0310char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0310char").getLowerCase();
      if(Upper) {
         Button0310.setText(upChar);
      } else {
         Button0310.setText(downChar);
      }
   }
   /**
   * This method initializes Button0311
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0311() {
      if (Button0311 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0311char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button11Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0311               = new JButton();
         Button0311.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0311.setMargin(Inset);
         Button0311.setText(thisKeys.getUpperCase());
         Button0311.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0311;
   }
   private void changeButton0311() {
      final String upChar   = start_vk.myKeys.get("B0311char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0311char").getLowerCase();
      if(Upper) {
         Button0311.setText(upChar);
      } else {
         Button0311.setText(downChar);
      }
   }
   /**
   * This method initializes Button0312
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0312() {
      if (Button0312 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0312char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button12Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine3"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0312               = new JButton();
         Button0312.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0312.setMargin(Inset);
         Button0312.setText(thisKeys.getUpperCase());
         Button0312.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0312;
   }
   private void changeButton0312() {
      final String upChar   = start_vk.myKeys.get("B0312char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0312char").getLowerCase();
      if(Upper) {
         Button0312.setText(upChar);
      } else {
         Button0312.setText(downChar);
      }
   }
   /**
   * This method initializes Button0401
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0401() {
      if (Button0401 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0401char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button01Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0401               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0401.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0401.setMargin(Inset);
         Button0401.setText(thisKeys.getUpperCase());
         Button0401.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0401;
   }
   private void changeButton0401() {
      final String upChar   = start_vk.myKeys.get("B0401char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0401char").getLowerCase();
      if(Upper) {
         Button0401.setText(upChar);
      } else {
         Button0401.setText(downChar);
      }
   }
   /**
   * This method initializes Button0402
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0402() {
      if (Button0402 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0402char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button02Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0402               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0402.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0402.setMargin(Inset);
         Button0402.setText(thisKeys.getUpperCase());
         Button0402.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0402;
   }
   private void changeButton0402() {
      final String upChar   = start_vk.myKeys.get("B0402char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0402char").getLowerCase();
      if(Upper) {
         Button0402.setText(upChar);
      } else {
         Button0402.setText(downChar);
      }
   }
   /**
   * This method initializes Button0403
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0403() {
      if (Button0403 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0403char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button03Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0403               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0403.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0403.setMargin(Inset);
         Button0403.setText(thisKeys.getUpperCase());
         Button0403.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0403;
   }
   private void changeButton0403() {
      final String upChar   = start_vk.myKeys.get("B0403char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0403char").getLowerCase();
      if(Upper) {
         Button0403.setText(upChar);
      } else {
         Button0403.setText(downChar);
      }
   }
   /**
   * This method initializes Button0404
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0404() {
      if (Button0404 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0404char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button04Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0404               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0404.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0404.setMargin(Inset);
         Button0404.setText(thisKeys.getUpperCase());
         Button0404.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0404;
   }
   private void changeButton0404() {
      final String upChar   = start_vk.myKeys.get("B0404char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0404char").getLowerCase();
      if(Upper) {
         Button0404.setText(upChar);
      } else {
         Button0404.setText(downChar);
      }
   }
   /**
   * This method initializes Button0405
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0405() {
      if (Button0405 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0405char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button05Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0405               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0405.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0405.setMargin(Inset);
         Button0405.setText(thisKeys.getUpperCase());
         Button0405.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0405;
   }
   private void changeButton0405() {
      final String upChar   = start_vk.myKeys.get("B0405char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0405char").getLowerCase();
      if(Upper) {
         Button0405.setText(upChar);
      } else {
         Button0405.setText(downChar);
      }
   }
   /**
   * This method initializes Button0406
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0406() {
      if (Button0406 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0406char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button06Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0406               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0406.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0406.setMargin(Inset);
         Button0406.setText(thisKeys.getUpperCase());
         Button0406.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0406;
   }
   private void changeButton0406() {
      final String upChar   = start_vk.myKeys.get("B0406char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0406char").getLowerCase();
      if(Upper) {
         Button0406.setText(upChar);
      } else {
         Button0406.setText(downChar);
      }
   }
   /**
   * This method initializes Button0407
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0407() {
      if (Button0407 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0407char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button07Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0407               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0407.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0407.setMargin(Inset);
         Button0407.setText(thisKeys.getUpperCase());
         Button0407.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0407;
   }
   private void changeButton0407() {
      final String upChar   = start_vk.myKeys.get("B0407char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0407char").getLowerCase();
      if(Upper) {
         Button0407.setText(upChar);
      } else {
         Button0407.setText(downChar);
      }
   }
   /**
   * This method initializes Button0408
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0408() {
      if (Button0408 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0408char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button08Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0408               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0408.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0408.setMargin(Inset);
         Button0408.setText(thisKeys.getUpperCase());
         Button0408.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0408;
   }
   private void changeButton0408() {
      final String upChar   = start_vk.myKeys.get("B0408char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0408char").getLowerCase();
      if(Upper) {
         Button0408.setText(upChar);
      } else {
         Button0408.setText(downChar);
      }
   }
   /**
   * This method initializes Button0409
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0409() {
      if (Button0409 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0409char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button09Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0409               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0409.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0409.setMargin(Inset);
         Button0409.setText(thisKeys.getUpperCase());
         Button0409.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0409;
   }
   private void changeButton0409() {
      final String upChar   = start_vk.myKeys.get("B0409char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0409char").getLowerCase();
      if(Upper) {
         Button0409.setText(upChar);
      } else {
         Button0409.setText(downChar);
      }
   }
   /**
   * This method initializes Button0410
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0410() {
      if (Button0410 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0410char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button10Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0410               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0410.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0410.setMargin(Inset);
         Button0410.setText(thisKeys.getUpperCase());
         Button0410.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0410;
   }
   private void changeButton0410() {
      final String upChar   = start_vk.myKeys.get("B0410char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0410char").getLowerCase();
      if(Upper) {
         Button0410.setText(upChar);
      } else {
         Button0410.setText(downChar);
      }
   }
   /**
   * This method initializes Button0411
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0411() {
      if (Button0411 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0411char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button11Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0411               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0411.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0411.setMargin(Inset);
         Button0411.setText(thisKeys.getUpperCase());
         Button0411.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0411;
   }
   private void changeButton0411() {
      final String upChar   = start_vk.myKeys.get("B0411char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0411char").getLowerCase();
      if(Upper) {
         Button0411.setText(upChar);
      } else {
         Button0411.setText(downChar);
      }
   }
   /**
   * This method initializes Button0412
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0412() {
      if (Button0412 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0412char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button12Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine4"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0412               = new JButton();
         final Integer   myOffset    = Integer.valueOf(start_vk.myConf.get("ButtonLineOffset"));
         Button0412.setBounds(new Rectangle(start + myOffset, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0412.setMargin(Inset);
         Button0412.setText(thisKeys.getUpperCase());
         Button0412.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0412;
   }
   private void changeButton0412() {
      final String upChar   = start_vk.myKeys.get("B0412char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0412char").getLowerCase();
      if(Upper) {
         Button0412.setText(upChar);
      } else {
         Button0412.setText(downChar);
      }
   }
   /**
   * This method initializes Button0501
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0501() {
      if (Button0501 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0501char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button01Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0501               = new JButton();
         Button0501.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0501.setMargin(Inset);
         Button0501.setText(thisKeys.getUpperCase());
         Button0501.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0501;
   }
   private void changeButton0501() {
      final String upChar   = start_vk.myKeys.get("B0501char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0501char").getLowerCase();
      if(Upper) {
         Button0501.setText(upChar);
      } else {
         Button0501.setText(downChar);
      }
   }
   /**
   * This method initializes Button0502
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0502() {
      if (Button0502 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0502char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button02Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0502               = new JButton();
         Button0502.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0502.setMargin(Inset);
         Button0502.setText(thisKeys.getUpperCase());
         Button0502.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0502;
   }
   private void changeButton0502() {
      final String upChar   = start_vk.myKeys.get("B0502char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0502char").getLowerCase();
      if(Upper) {
         Button0502.setText(upChar);
      } else {
         Button0502.setText(downChar);
      }
   }
   /**
   * This method initializes Button0503
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0503() {
      if (Button0503 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0503char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button03Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0503               = new JButton();
         Button0503.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0503.setMargin(Inset);
         Button0503.setText(thisKeys.getUpperCase());
         Button0503.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0503;
   }
   private void changeButton0503() {
      final String upChar   = start_vk.myKeys.get("B0503char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0503char").getLowerCase();
      if(Upper) {
         Button0503.setText(upChar);
      } else {
         Button0503.setText(downChar);
      }
   }
   /**
   * This method initializes Button0504
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0504() {
      if (Button0504 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0504char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button04Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0504               = new JButton();
         Button0504.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0504.setMargin(Inset);
         Button0504.setText(thisKeys.getUpperCase());
         Button0504.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0504;
   }
   private void changeButton0504() {
      final String upChar   = start_vk.myKeys.get("B0504char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0504char").getLowerCase();
      if(Upper) {
         Button0504.setText(upChar);
      } else {
         Button0504.setText(downChar);
      }
   }
   /**
   * This method initializes Button0505
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0505() {
      if (Button0505 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0505char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button05Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0505               = new JButton();
         Button0505.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0505.setMargin(Inset);
         Button0505.setText(thisKeys.getUpperCase());
         Button0505.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0505;
   }
   private void changeButton0505() {
      final String upChar   = start_vk.myKeys.get("B0505char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0505char").getLowerCase();
      if(Upper) {
         Button0505.setText(upChar);
      } else {
         Button0505.setText(downChar);
      }
   }
   /**
   * This method initializes Button0506
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0506() {
      if (Button0506 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0506char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button06Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0506               = new JButton();
         Button0506.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0506.setMargin(Inset);
         Button0506.setText(thisKeys.getUpperCase());
         Button0506.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0506;
   }
   private void changeButton0506() {
      final String upChar   = start_vk.myKeys.get("B0506char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0506char").getLowerCase();
      if(Upper) {
         Button0506.setText(upChar);
      } else {
         Button0506.setText(downChar);
      }
   }
   /**
   * This method initializes Button0507
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0507() {
      if (Button0507 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0507char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button07Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0507               = new JButton();
         Button0507.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0507.setMargin(Inset);
         Button0507.setText(thisKeys.getUpperCase());
         Button0507.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0507;
   }
   private void changeButton0507() {
      final String upChar   = start_vk.myKeys.get("B0507char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0507char").getLowerCase();
      if(Upper) {
         Button0507.setText(upChar);
      } else {
         Button0507.setText(downChar);
      }
   }
   /**
   * This method initializes Button0508
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0508() {
      if (Button0508 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0508char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button08Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0508               = new JButton();
         Button0508.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0508.setMargin(Inset);
         Button0508.setText(thisKeys.getUpperCase());
         Button0508.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0508;
   }
   private void changeButton0508() {
      final String upChar   = start_vk.myKeys.get("B0508char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0508char").getLowerCase();
      if(Upper) {
         Button0508.setText(upChar);
      } else {
         Button0508.setText(downChar);
      }
   }
   /**
   * This method initializes Button0509
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0509() {
      if (Button0509 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0509char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button09Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0509               = new JButton();
         Button0509.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0509.setMargin(Inset);
         Button0509.setText(thisKeys.getUpperCase());
         Button0509.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0509;
   }
   private void changeButton0509() {
      final String upChar   = start_vk.myKeys.get("B0509char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0509char").getLowerCase();
      if(Upper) {
         Button0509.setText(upChar);
      } else {
         Button0509.setText(downChar);
      }
   }
   /**
   * This method initializes Button0510
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0510() {
      if (Button0510 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0510char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button10Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0510               = new JButton();
         Button0510.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0510.setMargin(Inset);
         Button0510.setText(thisKeys.getUpperCase());
         Button0510.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0510;
   }
   private void changeButton0510() {
      final String upChar   = start_vk.myKeys.get("B0510char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0510char").getLowerCase();
      if(Upper) {
         Button0510.setText(upChar);
      } else {
         Button0510.setText(downChar);
      }
   }
   /**
   * This method initializes Button0511
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0511() {
      if (Button0511 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0511char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button11Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0511               = new JButton();
         Button0511.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0511.setMargin(Inset);
         Button0511.setText(thisKeys.getUpperCase());
         Button0511.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0511;
   }
   private void changeButton0511() {
      final String upChar   = start_vk.myKeys.get("B0511char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0511char").getLowerCase();
      if(Upper) {
         Button0511.setText(upChar);
      } else {
         Button0511.setText(downChar);
      }
   }
   /**
   * This method initializes Button0512
   *
   * @return javax.swing.JButton
   */
   private JButton getButton0512() {
      if (Button0512 == null) {
         final keys      thisKeys = start_vk.myKeys.get("B0512char");
         final Integer   start    = Integer.valueOf(start_vk.myConf.get("Button12Start"));
         final Integer   linepos  = Integer.valueOf(start_vk.myConf.get("ButtonLine5"));
         final Integer   ButtonX  = Integer.valueOf(start_vk.myConf.get("ButtonSizeX"));
         final Integer   ButtonY  = Integer.valueOf(start_vk.myConf.get("ButtonSizeY"));
         Button0512               = new JButton();
         Button0512.setBounds(new Rectangle(start, linepos, ButtonX, ButtonY));
         Insets Inset = new Insets(0,0,0,0);
         Button0512.setMargin(Inset);
         Button0512.setText(thisKeys.getUpperCase());
         Button0512.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  insertKeyText(thisKeys);
            }
         });
      }
      return Button0512;
   }
   private void changeButton0512() {
      final String upChar   = start_vk.myKeys.get("B0512char").getUpperCase();
      final String downChar = start_vk.myKeys.get("B0512char").getLowerCase();
      if(Upper) {
         Button0512.setText(upChar);
      } else {
         Button0512.setText(downChar);
      }
   }
}
