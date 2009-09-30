/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */



package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;



/**
 * @author Michael Borcherds
 * (based loosely on http://sourceforge.net/projects/virtualkey/ )
 *
 */
public class VirtualKeyboard extends JFrame {


	private Robot robot;


	private static final long serialVersionUID = 1L;
	//   private static Boolean Upper     = false;

	//private JPanel jContentPane      = null;

	//private JTextArea jTextArea      = null;


	private JButton SpaceButton      = null;
	private JButton DummyButton      = null;
	private JToggleButton CapsLockButton   = null;
	private JToggleButton AltButton   = null;
	private JToggleButton AltGrButton   = null;
	private JToggleButton CtrlButton   = null;
	private JToggleButton MathButton   = null;
	private JToggleButton GreekButton   = null;

	private String ctrlText = "Ctrl";
	private String altText = "Alt";
	private String altGrText = "AltG";
	private String escText = "Esc";

	private Application app;

	// max width character
	private char wideChar = '@';//'\u21d4'; // wide arrow <=>

	private int buttonRows = 5;
	private int buttonCols = 14;
	private double buttonSizeX, buttonSizeY;

	// make sure fonts fit in buttons
	//private double horizontalMultiplier = 0.84;
	//private double verticalMultiplier = 0.8;
	private double horizontalMultiplier = 1;
	private double verticalMultiplier = 1;

	private JButton[][] Buttons = new JButton[buttonRows + 1][buttonCols];

	private int windowX, windowY;

	private Font font;

	private Font [] fonts = new Font[100];
	//int fontWidths[] = new int[100];

	//WindowUnicodeKeyboard kb;// = new WindowUnicodeKeyboard(robot);
	//Keyboard kb;// = new Keyboard();

	public static void main(String[] args) {    

		try {							
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}	

		VirtualKeyboard virtualKeyboard = new VirtualKeyboard(400, 235);

		virtualKeyboard.setVisible(true);



	}
	
	public VirtualKeyboard(int sizeX, int sizeY) {
		this(null, sizeX, sizeY, 0.7f);
	}
	
	
	
	/**
	 * This is the default constructor
	 */
	public VirtualKeyboard(final Application app, int sizeX, int sizeY, float transparency) {

		super();

		readConf(app, null, false);

		windowX = sizeX;
		windowY = sizeY;
		this.app = app;
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);


		String fName;

		if (app != null)
			fName = app.getAppFontNameSansSerif();
		else
			fName = "Arial Unicode MS";

		//if (fName == "SansSerif") {
			//horizontalMultiplier = 1.0;
			//verticalMultiplier = 1.0;

		//}

		
		for (int i = 0 ; i < 100 ; i++) {
			fonts[i] = new Font(fName, Font.PLAIN, i+1);    
			//FontMetrics fm = getFontMetrics(fonts[i]);
			//fontWidths[i] = 5 + Math.max(fm.stringWidth("\u21d4"),fm.stringWidth("W")); // wide arrow <=>, W
			//fontWidths[i] = fm.stringWidth("W"); 
			//Application.debug(fm.stringWidth("W")+" "+fm.stringWidth("\u21d4"));
		}




		initialize();

		setLabels();

		windowResized();

		
		// make sure resizing the window dynamically updates the contents
		// doesn't seem to be needed on Java 5
		Toolkit kit = Toolkit.getDefaultToolkit();
		kit.setDynamicLayout(true);

		
		// Event Handling
		getContentPane().addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				//Application.debug("resize");	
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

		

		try { // Java 6u10+ only
			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			mSetWindowOpacity.invoke(null, this, Float.valueOf(transparency));
		} catch (Exception ex) {

			// fallback for OSX Leopard pre-6u10
			this.getRootPane().putClientProperty("Window.alpha", Float.valueOf(transparency));

		} 




		//windowResized();

		// TODO: fix
		// force resizing of contentPane
		SwingUtilities.invokeLater( new Runnable(){ public void
			run() { windowResized();} });


		/*
      try {

		kb = new WindowUnicodeKeyboard();
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//*/
	}

	final private void windowResized() {

		int sizeX = getContentPane().getWidth();
		int sizeY = getContentPane().getHeight();

		if (sizeX == 0) sizeX = getWidth();
		if (sizeY == 0) sizeY = getHeight();

		buttonSizeX = 0.15 +(double)sizeX / (double)(buttonCols );
		buttonSizeY = 0.25 + (double)sizeY / (double)(buttonRows + 1);
		//if (buttonSize < 20) buttonSize = 20;

		updateButtons();

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(windowX, windowY);
		this.setPreferredSize(new Dimension(windowX, windowY));
		this.setName("MainPanel");
		populateContentPane();
		this.setTitle((app == null) ? "Virtual Keyboard" : app.getPlain("VirtualKeyboard"));
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
	
	//int maxX, maxY;

	public void updateButtons() {
		
		//maxX = 0;
		//maxY = 0;
		for (int i = 1 ; i <= buttonRows ; i++)
			for (int j = 0 ; j < buttonCols ; j++)
				updateButton(i,j);	   

		//Application.debug(maxX*14+" "+maxY*6+" "+getWidth()+" "+getHeight());
		
	     // setSize(maxX * 14, maxY * 6);
	     // validate();

		
		updateSpaceButton();
		updateCapsLockButton();
		updateMathButton();
		updateGreekButton();
		updateAltButton();
		updateAltGrButton();
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
			SpaceButton.setMargin(new Insets(0,0,0,0));
			SpaceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					insertText(" ");
				}
			});
		}
		return SpaceButton;
	}

	private JButton getDummyButton() {
		if (DummyButton == null) {

			DummyButton                = new JButton(wideChar+"");
			DummyButton.setRequestFocusEnabled(false);
			DummyButton.setSize(new Dimension(10, 10));
			DummyButton.setLocation(new Point(0, 0));
			DummyButton.setMargin(new Insets(0,0,0,0));
		}
		return DummyButton;
	}

	private void updateSpaceButton() {
		SpaceButton.setSize(new Dimension((int)(buttonSizeX * 5d) , (int)buttonSizeY));
		SpaceButton.setLocation(new Point((int)(buttonSizeX * 4d), (int)(buttonSizeY * 4d)));

	}

	private void updateCapsLockButton() {
		CapsLockButton.setSize(new Dimension((int)(buttonSizeX ) , (int)buttonSizeY));
		CapsLockButton.setLocation(new Point((int)(buttonSizeX / 2d), (int)(buttonSizeY * 4d)));

		CapsLockButton.setFont(getFont((int)(minButtonSize())));

		setColor(CapsLockButton);
		//app.getGuiManager().getKeyboard().shiftPressed(CapsLockButton.isSelected());
	}

	private void updateCtrlButton() {
		CtrlButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		CtrlButton.setLocation(new Point((int)(buttonSizeX * 3d / 2d), (int)(buttonSizeY * 4d)));

		CtrlButton.setFont(getFont((int)(minButtonSize() / 2)));

		setColor(CtrlButton);
		//app.getGuiManager().getKeyboard().ctrlPressed(CtrlButton.isSelected());
	}

	private void updateAltButton() {
		AltButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		AltButton.setLocation(new Point((int)(buttonSizeX * 5d / 2d), (int)(buttonSizeY * 4d)));

		AltButton.setFont(getFont((int)(minButtonSize() / 2)));

		setColor(AltButton);

		if (sbAlt != null) sbAlt.setLength(0);

		//app.getGuiManager().getKeyboard().altPressed(AltButton.isSelected());
	}

	private void updateAltGrButton() {
		AltGrButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		AltGrButton.setLocation(new Point((int)(buttonSizeX * 19d / 2d), (int)(buttonSizeY * 4d)));

		AltGrButton.setFont(getFont((int)(minButtonSize() / 2)));

		setColor(AltGrButton);

	}

	private void updateMathButton() {
		MathButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		MathButton.setLocation(new Point((int)(buttonSizeX * 21d / 2d), (int)(buttonSizeY * 4d)));

		MathButton.setFont(getFont((int)(minButtonSize())));

		setColor(MathButton);
	}

	private void setColor(JToggleButton tb) {
		if (tb.isSelected())
			tb.setBackground(Color.cyan);
		else
			tb.setBackground(null);	   
	}

	private void updateGreekButton() {
		GreekButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		GreekButton.setLocation(new Point((int)(buttonSizeX * 23d / 2d), (int)(buttonSizeY * 4d)));

		GreekButton.setFont(getFont((int)(minButtonSize())));

		setColor(GreekButton);

	}

	private double minButtonSize() {
		double ret = Math.min(buttonSizeX * horizontalMultiplier,
				buttonSizeY * verticalMultiplier);

		return (ret == 0) ? 1 : ret;
	}

	private JToggleButton getCapsLockButton() {
		if (CapsLockButton == null) {

			CapsLockButton             = new JToggleButton("\u21e7");
			updateCapsLockButton();
			CapsLockButton.setMargin(new Insets(0,0,0,0));
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

			AltButton             = new JToggleButton(altText);
			updateAltButton();
			AltButton.setMargin(new Insets(0,0,0,0));

			AltButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//altPressed = !altPressed;
					updateAltButton();
				}
			});
		}
		return AltButton;
	}

	private JToggleButton getAltGrButton() {
		if (AltGrButton == null) {

			AltGrButton             = new JToggleButton(altGrText);
			updateAltGrButton();
			AltGrButton.setMargin(new Insets(0,0,0,0));

			AltGrButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//altPressed = !altPressed;
					//if (KEYBOARD_MODE != KEYBOARD_ALTGR)
					//	setMode(KEYBOARD_ALTGR);
					//else
					//	setMode(KEYBOARD_NORMAL);
					updateButtons();
					updateAltGrButton();
				}
			});
		}
		return AltGrButton;
	}

	private JToggleButton getCtrlButton() {
		if (CtrlButton == null) {

			CtrlButton             = new JToggleButton(ctrlText);
			updateCtrlButton();
			CtrlButton.setMargin(new Insets(0,0,0,0));

			CtrlButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//ctrlPressed = !ctrlPressed;
					updateCtrlButton();
				}
			});
		}
		return CtrlButton;
	}

	private JToggleButton getMathButton() {
		if (MathButton == null) {

			MathButton             = new JToggleButton("\u222b");
			updateMathButton();
			MathButton.setMargin(new Insets(0,0,0,0));
			MathButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					getGreekButton().setSelected(false);

					if (KEYBOARD_MODE != KEYBOARD_MATH)
						setMode(KEYBOARD_MATH);
					else
						setMode(KEYBOARD_NORMAL);

				}
			});
		}
		return MathButton;
	}

	//boolean greek = false;

	private boolean greek() {
		return getGreekButton().isSelected();
	}

	private JToggleButton getGreekButton() {
		if (GreekButton == null) {

			GreekButton             = new JToggleButton("\u03c3");
			updateGreekButton();
			GreekButton.setMargin(new Insets(0,0,0,0));
			GreekButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//greek = !greek;
					setMode(KEYBOARD_NORMAL);
					if (greek())
						readConf(app, new Locale("el"), false);
					//else
					//	readConf(app, null, false);

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
	private void populateContentPane() {
		//if (jContentPane == null) {
		//jContentPane = new JPanel();

		setLayout(null);
		//jContentPane.add(getJTextArea(), null);

		for (int i = 1 ; i <= buttonRows ; i++)
			for (int j = 0 ; j < buttonCols ; j++)
				add(getButton(i,j), null);

		add(getSpaceButton(), null);
		add(getCapsLockButton(), null);
		add(getMathButton(), null);
		add(getGreekButton(), null);
		add(getAltButton(), null);
		add(getAltGrButton(), null);
		add(getCtrlButton(), null);

		//jContentPane.setSize(getWidth(), getHeight());
		//pack();

		// }
		//return getContentPane();
	}


	public static char KEYBOARD_NORMAL = ' ';
	public static char KEYBOARD_MATH = 'M';
	//public static char KEYBOARD_ALTGR = 'Q';
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
	public static char KEYBOARD_SOLIDUS = '/';


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

			case '\u0338' : // solidus (/)
				setMode(KEYBOARD_SOLIDUS);
				return;

			case '\u0060' : // grave
			case '\u0300' : // combining grave
				setMode(KEYBOARD_GRAVE);
				return;

			case '\u02d8' : // breve
				setMode(KEYBOARD_BREVE);
				return;

			case '\u0303' : // tilde
				setMode(KEYBOARD_TILDE);
				return;

			case '\u0302' : // circumflex
				setMode(KEYBOARD_CIRCUMFLEX);
				return;

			case '\u0385' : // dialytika tonos
				setMode(KEYBOARD_DIALYTIKA_TONOS);
				return;

			case '\u00b8' : // cedilla
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

			case '0' :
			case '1' :
			case '2' :
			case '3' :
			case '4' :
			case '5' :
			case '6' :
			case '7' :
			case '8' :
			case '9' :
				if (AltButton.isSelected()) {
					StringBuilder sb = getAltStringBuffer();

					sb.append(addchar);

					AltButton.setBackground(Color.orange);

					if (sb.length() < 4) return;

					// convert string to Unicode char
					char c = (char)Integer.parseInt(sb.toString());

					// release alt
					//app.getGuiManager().getKeyboard().altPressed(false);
					AltButton.setSelected(false);
					updateAltButton();

					// type Unicode char
					app.getGuiManager().insertStringIntoTextfield(c + "", false, false, false);

					sb.setLength(0);
					return;

				} // else pass on as normal


			}

		if (addchar.equals("<enter>"))
			addchar="\n";

		if (app != null)
			app.getGuiManager().insertStringIntoTextfield(addchar, getAltButton().isSelected(), getCtrlButton().isSelected(), getCapsLockButton().isSelected());
		else
			getKeyboard().doType(getAltButton().isSelected(), getCtrlButton().isSelected(), getCapsLockButton().isSelected(), addchar);


	}

	StringBuilder sbAlt;

	private StringBuilder getAltStringBuffer() {
		if (sbAlt == null)
			sbAlt = new StringBuilder();

		return sbAlt;
	}

	private void setMode(char mode) {

		// restore language (eg if greek selected before)
		readConf(app, null, false);

		if (KEYBOARD_MODE == mode) {
			KEYBOARD_MODE = KEYBOARD_NORMAL;
		} else {
			// reset first
			KEYBOARD_MODE = KEYBOARD_NORMAL;
			updateButtons();

			// new mode
			KEYBOARD_MODE = mode;
		}

		if (KEYBOARD_MODE != KEYBOARD_MATH) {
			getMathButton().setSelected(false);
		} 
		if (KEYBOARD_MODE == KEYBOARD_MATH
				|| KEYBOARD_MODE == KEYBOARD_GREEK		

		) {
			getAltGrButton().setSelected(false);
		}

		updateButtons();



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

	private StringBuilder sb = new StringBuilder();

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

		if (ret1 == null)
			Application.debug(sb.toString());

		sb.append(KEYBOARD_MODE); // append 'A' for acute , ' ' for default etc

		keys ret2 = myKeys.get(sb.toString());

		// check for AltGr (Q) code if no accent etc available
		if (ret2 == null && getAltGrButton().isSelected()) {
			sb.setLength(sb.length() - 1); // remove MODE
			sb.append("Q"); 
			ret2 = myKeys.get(sb.toString());
		}

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

		if (text.equals("<enter>")) return unicodeString('\u21b2', "");
		if (text.equals("<backspace>")) return "\u21a4";
		if (text.equals("<escape>")) return (app == null) ? escText : app.getPlain("Esc");
		if (text.equals("<left>")) return "\u2190";
		if (text.equals("<up>")) return "\u2191";
		if (text.equals("<right>")) return "\u2192";
		if (text.equals("<down>")) return "\u2193";

		return text;
	}

	private String unicodeString(char c, String alternative) {
		if (font.canDisplay(c)) return c+"";
		else return alternative;
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

		int height = (int)buttonSizeY;

		// enter key: double height
		if (i == 3 && j == 13) Buttons[i][j].setVisible(KEYBOARD_MODE == KEYBOARD_MATH);
		if (i == 2 && j == 13 && KEYBOARD_MODE != KEYBOARD_MATH) height *= 2;

		Buttons[i][j].setBounds(new Rectangle((int)(0.5 + buttonSizeX * (double)j), (int)(0.5 + buttonSizeY * (double)(ii - 1)), (int)buttonSizeX, height));

		Dimension size = Buttons[i][j].getPreferredSize();
		//maxX = Math.max(size.width, maxX);
		//maxY = Math.max(size.height, maxY);
		// make sure "Esc" fits



		int len = Buttons[i][j].getText().length();
		if (len == 0) len = 1;

		if (len == 1)
			Buttons[i][j].setFont(getFont((int)minButtonSize() ));
		else {
			// make sure "Esc" fits
			FontMetrics fm = getFontMetrics(font);
			int width = 5 + fm.stringWidth(Buttons[i][j].getText()); // wide arrow <=>
			int w2 = fm.stringWidth(wideChar+"");
			Buttons[i][j].setFont(getFont((int)(minButtonSize() * w2 / width)));
		}

	}

	private HashMap<Integer, Font> fontsHash = new HashMap(30);

	private Font getFont(int size) {
		
		if (size < 10) {
			font = fonts[0];
			return font;
		}

		Integer Size = new Integer(size);

		Font ret = (Font)fontsHash.get(Size);

		// all OK, return
		if (ret != null) return ret;
		
		int maxSize = 100;
		int minSize = 1;
		
		
		// interval bisection method to find desired fontsize
		while (minSize != maxSize - 1) {
			//Application.debug(minSize+" "+maxSize);
			int midSize = (minSize + maxSize) / 2;
			getDummyButton().setFont(fonts[midSize]);
			Dimension buttonSize = DummyButton.getPreferredSize();
			
			if (buttonSize.width < size)
				minSize = midSize;
			else
				maxSize = midSize;
			
		}
		
		font = fonts[minSize];
		fontsHash.put(Size, font);
		Application.debug("storing "+size+" "+minSize);
		return font;
		
		/*

		//Application.debug("starting loop"+size);
		for (int i = fonts.length - 1 ; i >= 0 ; i--) {

			getDummyButton().setFont(fonts[i]);
			Dimension buttonSize = DummyButton.getPreferredSize();
			if (buttonSize.width < size) {
				
				font = fonts[i];
				fontsHash.put(Size, font);
				Application.debug("storing "+size+" "+i);
				return font;
			}
		}*/
		
		/*
		for (int i = 0 ; i < fonts.length - 1 ; i++) {
			if (fontWidths[i] < size && fontWidths[i+1] >= size)
			{
				font = fonts[i+1];
				fontsHash.put(Size, font);
				return font;
			}
		}*/

		//font = fonts[0];
		//return font;

	}

	private Hashtable<String, keys>   myKeys = new Hashtable<String, keys>();

	private void readConf(Application app, Locale loc, boolean math) {

		//ResourceBundle rbKeyboard = MyResourceBundle.loadSingleBundleFile("/geogebra/gui/virtualkeyboard/keyboard_en_UK");

		ResourceBundle rbKeyboard;

		Locale locale;

		if (app != null)
			locale = app.getLocale();
		else
			locale = getLocale();
		
		//System.out.println("Using locale "+locale.toString());

		if (math) {
			rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboardMath", locale);
		} else {
			if (loc == null)
				rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboard", locale);
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

	/*
	 * called when eg language changed
	 */
	public void setLabels() {
		readConf(app, null, false);
		if (app != null) {
			getCtrlButton().setText(app.getPlain("Ctrl"));
			getAltButton().setText(app.getPlain("Alt"));
			getAltGrButton().setText(app.getPlain("AltGr"));
			updateCtrlButton();
			updateAltButton();
			updateAltGrButton();
		}	      
		getAltButton().setSelected(false);
		getAltGrButton().setSelected(false);
		getMathButton().setSelected(false);
		getGreekButton().setSelected(false);
		getCtrlButton().setSelected(false);
		getCapsLockButton().setSelected(false);
		KEYBOARD_MODE = KEYBOARD_NORMAL;
		updateButtons();
	}


	private WindowsUnicodeKeyboard kb = null;

	public WindowsUnicodeKeyboard getKeyboard() {

		try{
			kb = new WindowsUnicodeKeyboard();
		} catch (Exception e) {}
		return kb;
	}





}
