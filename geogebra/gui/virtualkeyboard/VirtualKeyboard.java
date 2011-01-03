/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */



package geogebra.gui.virtualkeyboard;

import geogebra.gui.inputbar.AutoCompleteTextField;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
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
import javax.swing.Timer;
import javax.swing.UIManager;



/**
 * @author Michael Borcherds
 * (based loosely on http://sourceforge.net/projects/virtualkey/ )
 *
 */
public class VirtualKeyboard extends JFrame implements ActionListener {


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
	private JToggleButton EnglishButton   = null;

	private String ctrlText = "Ctrl";
	private String altText = "Alt";
	private String altGrText = "AltG";
	private String escText = "Esc";

	private Application app;

	// max width character
	private final static char wideCharDefault = '@';
	private char wideChar = wideCharDefault;

	private int buttonRows = 5;
	private int buttonCols = 14;
	private double buttonSizeX, buttonSizeY;

	private double horizontalMultiplier = 1;
	private double verticalMultiplier = 1;

	private JButton[][] Buttons = new JButton[buttonRows + 1][buttonCols];

	private int windowX, windowY;

	private Font currentFont;

	private Font [] fonts = new Font[100];

	public static void mainx(String[] args) {    

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


		// TODO: use app.getFontCanDisplay()
		String fName = app.getPlainFont().getFontName();

		for (int i = 0 ; i < 100 ; i++) {
			fonts[i] = new Font(fName, Font.PLAIN, i+1);    
		}




		initialize();

		setLabels();

		windowResized();



		// make sure resizing the window dynamically updates the contents
		// doesn't seem to be needed on Java 5
		Toolkit kit = Toolkit.getDefaultToolkit();
		kit.setDynamicLayout(true);

		if (app != null)
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		else
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setVisible(true);
		addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
				//System.out.println("Window close event occur");
			}
			public void windowActivated(WindowEvent arg0) {
				//System.out.println("Window Activated");
			}
			public void windowClosing(WindowEvent arg0) {
				//System.out.println("Window Closing");
				// if closed with the X, stop it auto-opening
				Application.setVirtualKeyboardActive(false);
				app.getGuiManager().updateMenubar();
			}
			public void windowDeactivated(WindowEvent arg0) {
				//System.out.println("Window Deactivated");
			}
			public void windowDeiconified(WindowEvent arg0) {
				//System.out.println("Window Deiconified");
			}
			public void windowIconified(WindowEvent arg0) {
				//System.out.println("Window Iconified");
			}
			public void windowOpened(WindowEvent arg0) {
				//System.out.println("Window Opened");
			}
		});



		// Event Handling
		getContentPane().addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				//Application.debug("resize");	
				windowResized();
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


		// TODO: fix
		// force resizing of contentPane
		SwingUtilities.invokeLater( new Runnable(){ public void
			run() { windowResized();} });

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
		setSize(windowX, windowY);
		setPreferredSize(new Dimension(windowX, windowY));
		populateContentPane();
	}

	public void updateButtons() {

		for (int i = 1 ; i <= buttonRows ; i++)
			for (int j = 0 ; j < buttonCols ; j++)
				updateButton(i,j);	   

		updateSpaceButton();
		updateCapsLockButton();
		updateMathButton();
		updateGreekButton();
		updateEnglishButton();
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

	/*
	 * used to find the preferred size of buttons with certain characters
	 * or fonts
	 */ 
	private JButton getDummyButton() {
		if (DummyButton == null) {
			DummyButton                = new JButton(wideChar+"");
			DummyButton.setRequestFocusEnabled(false);
			DummyButton.setSize(new Dimension(10, 10));
			DummyButton.setLocation(new Point(0, 0));
			DummyButton.setMargin(new Insets(0,0,0,0));
		}
		return DummyButton;
	}//*/

	private void updateSpaceButton() {
		SpaceButton.setSize(new Dimension((int)(buttonSizeX * 5d) , (int)buttonSizeY));
		SpaceButton.setLocation(new Point((int)(buttonSizeX * 4d), (int)(buttonSizeY * 4d)));

	}

	private void updateCapsLockButton() {
		CapsLockButton.setSize(new Dimension((int)(buttonSizeX ) , (int)buttonSizeY));
		CapsLockButton.setLocation(new Point((int)(buttonSizeX / 2d), (int)(buttonSizeY * 4d)));

		CapsLockButton.setFont(getFont((int)(minButtonSize()), false));

		setColor(CapsLockButton);
	}

	private void updateCtrlButton() {
		CtrlButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		CtrlButton.setLocation(new Point((int)(buttonSizeX * 3d / 2d), (int)(buttonSizeY * 4d)));

		CtrlButton.setFont(getFont((int)(minButtonSize() / 2), false));

		setColor(CtrlButton);
	}

	private void updateAltButton() {
		AltButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		AltButton.setLocation(new Point((int)(buttonSizeX * 5d / 2d), (int)(buttonSizeY * 4d)));

		AltButton.setFont(getFont((int)(minButtonSize() / 2), false));

		setColor(AltButton);

		if (sbAlt != null) sbAlt.setLength(0);
	}

	private void updateAltGrButton() {
		AltGrButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		AltGrButton.setLocation(new Point((int)(buttonSizeX * 19d / 2d), (int)(buttonSizeY * 4d)));

		AltGrButton.setFont(getFont((int)(minButtonSize() / 2), false));

		setColor(AltGrButton);

	}

	private void updateMathButton() {
		MathButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		MathButton.setLocation(new Point((int)(buttonSizeX * 21d / 2d), (int)(buttonSizeY * 4d)));

		MathButton.setFont(getFont((int)(minButtonSize()), false));

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
		GreekButton.setLocation(new Point((int)(buttonSizeX * 25d / 2d), (int)(buttonSizeY * 4d)));

		GreekButton.setFont(getFont((int)(minButtonSize()), false));

		setColor(GreekButton);

	}

	private void updateEnglishButton() {
		EnglishButton.setSize(new Dimension((int)(buttonSizeX) , (int)buttonSizeY));
		EnglishButton.setLocation(new Point((int)(buttonSizeX * 23d / 2d), (int)(buttonSizeY * 4d)));

		EnglishButton.setFont(getFont((int)(minButtonSize()), false));

		EnglishButton.setVisible(true);

		setColor(EnglishButton);

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
					getEnglishButton().setSelected(false);

					if (KEYBOARD_MODE != KEYBOARD_MATH)
						setMode(KEYBOARD_MATH, null);
					else
						setMode(KEYBOARD_NORMAL, null);

				}
			});
		}
		return MathButton;
	}

	private boolean greek() {
		return getGreekButton().isSelected();
	}

	private JToggleButton getGreekButton() {
		if (GreekButton == null) {

			GreekButton             = new JToggleButton("\u03b1");
			updateGreekButton();
			GreekButton.setMargin(new Insets(0,0,0,0));
			GreekButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setMode(KEYBOARD_NORMAL, null);
					if (greek())
						readConf(app, new Locale("el"), false);

					getEnglishButton().setSelected(false);

					updateButtons();


				}
			});
		}
		return GreekButton;
	}

	private boolean english() {
		return getEnglishButton().isSelected();
	}

	private JToggleButton getEnglishButton() {
		if (EnglishButton == null) {

			EnglishButton             = new JToggleButton("a");
			updateEnglishButton();
			EnglishButton.setMargin(new Insets(0,0,0,0));
			EnglishButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setMode(KEYBOARD_NORMAL, null);
					if (english())
						readConf(app, new Locale("en"), false);

					getGreekButton().setSelected(false);

					updateButtons();


				}
			});
		}
		return EnglishButton;
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private void populateContentPane() {

		setLayout(null);

		for (int i = 1 ; i <= buttonRows ; i++)
			for (int j = 0 ; j < buttonCols ; j++)
				add(getButton(i,j), null);

		add(getSpaceButton(), null);
		add(getCapsLockButton(), null);
		add(getMathButton(), null);
		add(getGreekButton(), null);
		add(getEnglishButton(), null);
		add(getAltButton(), null);
		add(getAltGrButton(), null);
		add(getCtrlButton(), null);

		pack();

	}


	public static char KEYBOARD_NORMAL = ' ';
	public static char KEYBOARD_MATH = 'M';
	//public static char KEYBOARD_ALTGR = 'Q';
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
		
		AutoCompleteTextField.setVirtualKeyboardInUse(true);

		if (addchar.length() == 1) 
			switch (addchar.charAt(0)) {
			case '\u00b4' : // acute
				setMode(KEYBOARD_ACUTE, kbLocale);
				return;

			case '\u0338' : // solidus (/)
				setMode(KEYBOARD_SOLIDUS, kbLocale);
				return;

			case '\u0060' : // grave
			case '\u0300' : // combining grave
				setMode(KEYBOARD_GRAVE, kbLocale);
				return;

			case '\u02d8' : // breve
				setMode(KEYBOARD_BREVE, kbLocale);
				return;

			case '\u0303' : // tilde
				setMode(KEYBOARD_TILDE, kbLocale);
				return;

			case '\u0302' : // circumflex
				setMode(KEYBOARD_CIRCUMFLEX, kbLocale);
				return;

			case '\u0385' : // dialytika tonos
				setMode(KEYBOARD_DIALYTIKA_TONOS, kbLocale);
				return;

			case '\u00b8' : // cedilla
				setMode(KEYBOARD_CEDILLA, kbLocale);
				return;

			case '\u00a8' : // umlaut
				setMode(KEYBOARD_UMLAUT, kbLocale);
				return;

			case '\u02c7' : // caron
				setMode(KEYBOARD_CARON, kbLocale);
				return;

			case '\u02d9' : // dot above
				setMode(KEYBOARD_DOT_ABOVE, kbLocale);
				return;

			case '\u02db' : // Ogonek
				setMode(KEYBOARD_OGONEK, kbLocale);
				return;

			case '\u02da' : // ring above
				setMode(KEYBOARD_RING_ABOVE, kbLocale);
				return;

			case '\u02dd' : // double acute
				setMode(KEYBOARD_DOUBLE_ACUTE, kbLocale);
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
					StringBuilder sb = getAltStringBuilder();

					sb.append(addchar);

					AltButton.setBackground(Color.orange);

					if (sb.length() < 4) return;

					// convert string to Unicode char
					char c = (char)Integer.parseInt(sb.toString());

					// release alt
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


		// no special keys pressed, reset to normal (except eg Greek)
		setMode(KEYBOARD_NORMAL, kbLocale);

	}

	StringBuilder sbAlt;

	private StringBuilder getAltStringBuilder() {
		if (sbAlt == null)
			sbAlt = new StringBuilder();

		return sbAlt;
	}

	private void setMode(char mode, Locale loc) {

		// loc==null -> restore language (eg if greek selected before)
		readConf(app, loc, false);

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
		if (KEYBOARD_MODE == KEYBOARD_MATH) {
			getAltGrButton().setSelected(false);
		}

		updateButtons();

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
			insertText(Keys.getUpperCase());
		} else {
			insertText(Keys.getLowerCase());
		}
	}

	private StringBuilder sb = new StringBuilder();

	private keys getKey(int i, int j) {

		sb.setLength(0);
		sb.append('B');
		if (i < 10) sb.append('0'); // pad from "1" to "01"
		sb.append(i+"");
		if (j < 10) sb.append('0'); // pad from "1" to "01"
		sb.append(j+"");

		keys ret1 = myKeys.get(sb.toString());

		if (ret1 == null)
			Application.debug("KB Error: "+sb.toString());

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

	private JButton getButton(final int i, final int j) {
		if (Buttons[i][j] == null) {
			keys thisKeys = getKey(i, j); 
			Buttons[i][j]               = new JButton();
			updateButton(i,j);
			Insets Inset = new Insets(0,0,0,0);
			Buttons[i][j].setMargin(Inset);
			String text = Upper() ? thisKeys.getUpperCase() : thisKeys.getLowerCase();

			Buttons[i][j].setText(processSpecialKeys(text));

			Buttons[i][j].addMouseListener(new MouseAdapter() 
			{
				public void mousePressed(MouseEvent e)
				{
					String text = Buttons[i][j].getText();
					if (text.equals("\u2190")) {
						startAutoRepeat("<left>");
					} else if (text.equals("\u2191")) {
						startAutoRepeat("<up>");
					} else	if (text.equals("\u2192")) {
						startAutoRepeat("<right>");
					} else if (text.equals("\u2193")) {
						startAutoRepeat("<down>");
					} else if (text.equals("\u21a4")) {
						startAutoRepeat("<backspace>");
					} 
					//Application.debug(Buttons[i][j].getText());
				}      
				public void mouseReleased(MouseEvent e)
				{
					stopAutoRepeat();
				}

				public void mouseExited(MouseEvent e) {
					stopAutoRepeat();

				}      
			});


			Buttons[i][j].addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					// don't insert if timer running
					// (done in timer on auto-repeat)
					if (timer == null || !timer.isRunning())
						insertKeyText(getKey(i, j));

					boolean doUpdateButtons = false;


					// reset buttons on a keypress (one-shot mode)
					if (getCapsLockButton().isSelected()) {
						getCapsLockButton().setSelected(false);
						doUpdateButtons = true;
					}

					if (getAltGrButton().isSelected()) {
						getAltGrButton().setSelected(false);
						doUpdateButtons = true;
					}

					if (getCtrlButton().isSelected()) {
						getCtrlButton().setSelected(false);
						doUpdateButtons = true;
					}

					if (doUpdateButtons) updateButtons();

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
		if (getCurrentFont().canDisplay(c)) return c+"";
		else return alternative;
	}

	HashMap<Character, Boolean> characterIsTooWide = new HashMap<Character, Boolean>(200);

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

		String text = Buttons[i][j].getText();
		int len = text.length();



		if (len == 0) {
			len = 1;
			text = " ";

		}

		if (len == 1) {

			// make sure extra-wide characters fit (eg <=> \u21d4 )
			Boolean oversize = characterIsTooWide.get(new Character(text.charAt(0)));
			if (oversize == null) {
				/* old code
				getDummyButton().setFont(getCurrentFont());
				getDummyButton().setText(wideChar+"");
				Dimension buttonSize = DummyButton.getPreferredSize();
				getDummyButton().setText(text);
				Dimension buttonSize2 = DummyButton.getPreferredSize();
				oversize = new Boolean((buttonSize2.getWidth() > buttonSize.getWidth()));
				characterIsTooWide.put(new Character(text.charAt(0)), oversize);//*/
				
				FontRenderContext frc = new FontRenderContext(null, true, true);
				double wideCharWidth = getCurrentFont().getStringBounds(wideChar + "", frc).getWidth();
				double charWidth = getCurrentFont().getStringBounds(text, frc).getWidth();
				oversize = new Boolean(charWidth > wideCharWidth);
			}

			if (oversize.booleanValue()) {
				Buttons[i][j].setFont(getFont((int)minButtonSize() * 10 / 12, false));
			} else {				
				Buttons[i][j].setFont(getFont((int)minButtonSize(), true));
			}
		}
		else {
			// make sure "Esc" fits
			FontMetrics fm = getFontMetrics(getCurrentFont());
			int width = fm.stringWidth(Buttons[i][j].getText()); // wide arrow <=>
			int w2 = fm.stringWidth(wideChar+"");
			Buttons[i][j].setFont(getFont((int)(minButtonSize() * w2 / width), false));
		}

	}

	private Font getCurrentFont() {
		if (currentFont != null)
			return currentFont;

		return getFont((int)(minButtonSize()), true);
	}

	private HashMap<Integer, Font> fontsHash = new HashMap<Integer, Font>(30);

	private Font getFont(int size, boolean setFont) {

		Integer Size = new Integer(size);

		Font ret = fontsHash.get(Size);

		// all OK, return
		if (ret != null) return ret;

		int maxSize = 100;
		int minSize = 1;


		// interval bisection method to find desired fontsize
		while (minSize != maxSize - 1) {
			//Application.debug(minSize+" "+maxSize);
			int midSize = (minSize + maxSize) / 2;

			getDummyButton().setFont(fonts[midSize]);
			getDummyButton().setText(wideChar+"");
			Dimension buttonSize = DummyButton.getPreferredSize();

			int wideCharSize = buttonSize.width;

			if (wideCharSize < size)
				minSize = midSize;
			else
				maxSize = midSize;

		}
		
		/*
		 * fallback for Mac OS
		 * getPreferredSize() is returning a minimum of width = 75 height = 29
		 * so we just choose size / 2 for the fontSize
		 */
		if (minSize < 3) {
			minSize = size / 2;
		}

		if (setFont) currentFont = fonts[minSize];
		fontsHash.put(Size, fonts[minSize]);
		//Application.debug("KB: storing "+size+" "+minSize);
		return fonts[minSize];

	}

	private Hashtable<String, keys>   myKeys = new Hashtable<String, keys>();

	private Locale kbLocale = null;

	private void readConf(Application app, Locale loc, boolean math) {

		ResourceBundle rbKeyboard;

		Locale locale;

		if (app != null)
			locale = app.getLocale();
		else
			locale = getLocale();

		kbLocale = locale;

		if (math) {
			rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboardMath", locale);
		} else {
			if (loc == null)
				rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboard", locale);
			else {
				rbKeyboard = MyResourceBundle.createBundle("/geogebra/gui/virtualkeyboard/keyboard", loc);
				kbLocale = loc;
			}
		}

		Enumeration<String> keys = rbKeyboard.getKeys();

		while (keys.hasMoreElements()) {
			String keyU = (String) keys.nextElement();

			if (keyU.endsWith("U")) {
				keys keyItem = new keys();
				String key = keyU.substring(0,keyU.length() - 1);

				String valueU = rbKeyboard.getString(keyU);
				String valueL = rbKeyboard.getString(key+"L");


				keyItem.setLowerCase(valueL);
				keyItem.setUpperCase(valueU);

				myKeys.put(key,keyItem);
			}
		}	
	}

	/*
	 * called when eg language changed
	 */
	public void setLabels() {

		setTitle((app == null) ? "Virtual Keyboard" : app.getPlain("VirtualKeyboard"));

		readConf(app, null, false);
		
		if (kbLocale.getLanguage().equals("ml"))
			wideChar = '\u0d4c'; // widest Malayalan char
		else if (kbLocale.getLanguage().equals("ar"))
				wideChar = '\u0636'; // widest Arabic char
			else
			wideChar = wideCharDefault;
		
		if (fontsHash != null)
			fontsHash.clear();
		
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
		getEnglishButton().setSelected(false);
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

	private Timer timer;
	private String timerInsertStr = "";

	final private void startAutoRepeat(String str) {
		if (timer == null) timer = new Timer(1000, (ActionListener) this);	
		timer.stop();
		timer.setDelay(1000);
		timer.start();
		timer.setDelay(200); // long first pause then quicker repeat		
		timerInsertStr = str;
		insertAutoRepeatString();

	}

	private void insertAutoRepeatString() {
		app.getGuiManager().insertStringIntoTextfield(timerInsertStr, getAltButton().isSelected(), getCtrlButton().isSelected(), getCapsLockButton().isSelected());		
	}

	final private void stopAutoRepeat() {
		if (timer != null) timer.stop();
	}

	public void actionPerformed(ActionEvent e) {
		// timer event
		insertAutoRepeatString();
	}

}
