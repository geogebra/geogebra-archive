package geogebra.gui.virtualkeyboard;

import geogebra.gui.GuiManager;
import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.SymbolTable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/**
 * Extends JTextField to add these features:
 * 
 * 1) Custom text drawing with dynamic coloring of bracket symbols 
 * 2) Support for in-line icons 
 * 3) Popup symbol table to insert special characters where the popup 
 *    is triggered by either a mouse click or ctrl-up
 * 
 */
public class MyTextField extends JTextField implements FocusListener, VirtualKeyboardListener, CaretListener {

	private GuiManager guiManager;

	// fields for the symbol table popup 
	private JPopupMenu popup;
	private MyTextField thisField = this;
	private SymbolTable symbolTable;
	private int caretPosition; // restores caret position when popup is done 


	// fields to handle custom drawing
	private boolean rollOver = false;
	private float pos = 0;

	private ImageIcon icon = GeoGebraIcon.createSymbolTableIcon(this.getFont(), false);
	private ImageIcon rollOverIcon = GeoGebraIcon.createSymbolTableIcon(this.getFont(), true);
	private int iconOffset = 0;
	private boolean showSymbolTableIcon = false;
	private DefaultCaret myCaret;


	private int bracket1pos;
	private int bracket2pos;
	private Color bracketColor;


	public MyTextField(GuiManager guiManager) {
		super();
		this.guiManager = guiManager;
		initField();
	}

	public MyTextField(GuiManager guiManager, int i) {
		super(i);
		this.guiManager = guiManager;
		initField();
	}


	private void initField(){

		addFocusListener(this);
		addMouseMotionListener(new MyMouseMotionListener());
		addMouseListener(new MyMouseListener());
		setOpaque(true);
		addCaretListener(this);
		
		
	}


	/**
	 * Repaints the field on caret changes. This is needed for the custom colored text drawing. 
	 */
	public void caretUpdate(CaretEvent arg0) {
		repaint();
	}

	
	public void focusGained(FocusEvent e) {
		// adjust the icon offset if we are going to have an icon 
		iconOffset =  (showSymbolTableIcon && hasFocus()) ? 16 : 0;
		thisField.repaint();
		guiManager.setCurrentTextfield((VirtualKeyboardListener)this, false);
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
		// adjust the icon offset if we are going to have an icon 
		iconOffset =  (showSymbolTableIcon && hasFocus()) ? 16 : 0;
		thisField.repaint();
	}

	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}




	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();      

		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}


		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));            
		setText(sb.toString());

		//setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();

		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField)this;
			tf.updateCurrentWord();
			tf.updateAutoCompletion();
		}

		// Under a Mac OS the string is always selected after the insert. A runnable prevents
		// this by resetting the caret to cancel the selection. 
		SwingUtilities.invokeLater(new Runnable() {  
			public void run() {  
				setCaretPosition(newPos); 
			}   
		});  

	}



	/** 
	 * Creates an instance of JPopupMenu and adds a symbol table to it.
	 */
	private void createPopup(){
		popup = new JPopupMenu();
		popup.setFocusable(false);
		symbolTable = new SymbolTable(guiManager.app, this);
		popup.add(symbolTable);
		popup.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow));
	}


	/** 
	 * Gets the pixel location of the caret. Used to locate the popup. 
	 * */
	private Point getCaretPixelPosition(){
		int width = thisField.getSize().width;  
		int position = thisField.getCaretPosition();  
		Rectangle r;
		try {
			r = thisField.modelToView(position);
		} catch (BadLocationException e) {
			r = null;
		}  
		return new Point(r.x, r.y - popup.getPreferredSize().height-10);
	}


	/** 
	 * Hides the popup and inserts selected symbol. (Called by symbol table
	 * on Enter key press). 
	 * */
	public void handlePopupSelection(){	
		popup.setVisible(false);
		setCaretPosition(caretPosition);
		insertString((String) symbolTable.getSelectedValue());
	}

	/**
	 * Overrides processKeyEvents so that the symbol table popup can be
	 * triggered by ctrl-up.
	 * */
	public void processKeyEvent(KeyEvent e) {

		int keyCode = e.getKeyCode(); 

		if ((e.isControlDown()||Application.isControlDown(e)) && keyCode == KeyEvent.VK_UP){
			caretPosition = thisField.getCaretPosition();
			if(popup == null)
				createPopup();
			symbolTable.updateFonts();
			popup.show(thisField, getCaretPixelPosition().x, getCaretPixelPosition().y);
			return;
		}

		if(popup != null && popup.isShowing() && e.getID()==KeyEvent.KEY_PRESSED){

			switch(keyCode){

			case KeyEvent.VK_ENTER:
				handlePopupSelection();
				return;

			case KeyEvent.VK_ESCAPE:
				popup.setVisible(false);
				return;

			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:

				int row = symbolTable.getSelectedRow();
				int column = symbolTable.getSelectedColumn();
				if(keyCode == KeyEvent.VK_RIGHT && column != symbolTable.getColumnCount()-1) ++column;	
				if(keyCode == KeyEvent.VK_LEFT && column >= 0) --column;	
				if(keyCode == KeyEvent.VK_DOWN && row != symbolTable.getRowCount()-1) ++row;
				if(keyCode == KeyEvent.VK_UP && row >= 0) --row; 

				symbolTable.changeSelection(row, column, false, false);
				return;	

			default:
				popup.setVisible(false);
				return;
			}
		}
		super.processKeyEvent(e);
	}



	/**
	 * Overrides processMouseEvent to handle a mousePressed event in the icon
	 * region. Handling icon mousePressed is done here so that the mousePressed
	 * event does not also reset the caret position to the end of the string.
	 */
	public void processMouseEvent(MouseEvent e) {

		if(rollOver && e.getID() == MouseEvent.MOUSE_PRESSED){
			if(popup == null)
				createPopup();
			symbolTable.updateFonts();
			caretPosition = thisField.getCaretPosition();
			Dimension d  = popup.getPreferredSize();
			popup.show(thisField, thisField.getX() + thisField.getWidth() - d.width, - d.height);

			return;
		}

		super.processMouseEvent(e);

	}

	/**
	 * Sets the rollover flag when the mouse is over the icon region
	 */
	private class MyMouseMotionListener extends MouseMotionAdapter{

		public void mouseMoved(MouseEvent e) {

			Insets insets = thisField.getInsets();
			int iconStart = thisField.getWidth() - insets.right;
			Rectangle r = new Rectangle(iconStart,0,iconOffset,thisField.getHeight());
			boolean isOverIcon  = r.contains(e.getPoint());
			if(rollOver != isOverIcon){
				rollOver = isOverIcon;
				thisField.repaint();
			}
		}
	}

	/**
	 * Sets the rollover flag when the mouse leaves the icon region
	 */
	private class MyMouseListener extends MouseAdapter{

		public void mouseExited(MouseEvent e) {
			rollOver = false;
			thisField.repaint();
		}
	}


	/**
	 * Overrides getInsets so that an icon can be inserted on the far left.
	 * (Note: setMargin() should really be used for this but it will not work
	 * when custom borders are applied.)
	 */
	public Insets getInsets(){
		Insets insets = super.getInsets();
		insets.right = insets.right + iconOffset;
		insets.left = insets.left ; //left margin
		insets.top = insets.top + 2;
		insets.bottom = insets.bottom + 2;
		return insets;
	}




	/**
	 * Draws a text string with colored caret brackets and an optional icon on
	 * the far right of the field.
	 */
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);

		// prepare for custom drawing
		Graphics2D g2 = (Graphics2D)gr;
		g2.setBackground(getBackground());
		Insets insets = getInsets();
		int height = getHeight();

		
		// color the caret brackets
		this.updateCaretBrackets();
		if(this.bracket1pos >=0)
			drawColoredChar(g2,bracket1pos, bracketColor);
		if(this.bracket2pos >=0)
			drawColoredChar(g2,bracket2pos, bracketColor);
	
		
		// draw the icon
		if(showSymbolTableIcon && thisField.hasFocus())
			if(rollOver)
				rollOverIcon.paintIcon(this, g2, getWidth() - insets.right + (iconOffset - icon.getIconWidth() ) /2, 
						(height - icon.getIconHeight())/2);
			else
				icon.paintIcon(this, g2, getWidth() - insets.right + (iconOffset - icon.getIconWidth() ) /2, 
						(height - icon.getIconHeight())/2);
	}


	
	
	/**
	 * Overwrites a character at a given position in the text string and
	 * draws it in the specified color
	 */
	private void drawColoredChar(Graphics2D g2, int position, Color c){
		
		// set font variables
		FontRenderContext frc = ((Graphics2D) g2).getFontRenderContext();
		Font font = g2.getFont();
		Rectangle r = null;
		try {
			r = thisField.modelToView(position);
			if(r !=null){
				String s = this.getDocument().getText(position,1);
				if(s!=null){
					TextLayout layout = new TextLayout(s, font, frc);
					g2.setFont(font);
					float advance = layout.getAdvance();
					
					//erase the character by drawing it in white
					g2.setColor(Color.white);
					g2.setFont(font.deriveFont(Font.BOLD));
					g2.drawString(s, r.x, r.y + g2.getFontMetrics().getMaxAscent());
					
					// now draw it in the given color
					g2.setFont(font);
					g2.setColor(c);				
					g2.drawString(s, r.x, r.y + g2.getFontMetrics().getMaxAscent());
				}
			}

		} catch (BadLocationException e) {
			//e.printStackTrace();
		}

	}
	
	
	
	

	/**
	 * Updates the caret brackets and their color.
	 */
	private void updateCaretBrackets(){

		int caret = getCaretPosition();
		String text = getText();
		 
		// if the caret is to the immediate left of a bracket character then this 
		// bracket and its match (if it exists) will be colored

		bracket1pos = -1;
		bracket2pos = -1;

		int searchDirection = 0;
		int searchEnd = 0;

		char bracketToMatch = ' ';
		char oppositeBracketToMatch = ' ';

		if (getSelectionStart() == getSelectionEnd())
			if (caret > 0 && caret <= text.length()) {

				char c = text.charAt(caret-1);
				bracket1pos = caret - 1;
				switch (c) {
				case '(' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '(';
					bracketToMatch = ')';
					break;
				case '{' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '{';
					bracketToMatch = '}';
					break;
				case '[' :
					searchDirection = +1;
					searchEnd = text.length();
					oppositeBracketToMatch = '[';
					bracketToMatch = ']';
					break;
				case ')' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = ')';
					bracketToMatch = '(';
					break;
				case '}' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = '}';
					bracketToMatch = '{';
					break;
				case ']' :
					searchDirection = -1;
					searchEnd = -1;
					oppositeBracketToMatch = ']';
					bracketToMatch = '[';
					break;
				default:
					searchDirection = 0;
					bracket1pos = -1;
					bracket2pos = -1;
					break;

				}
			}


		//Lines containing  textMode by Zbynek Konecny, 2010-05-09
		boolean textMode = false;

		if (searchDirection != 0) {
			int count = 0;
			for (int i = caret - 1 ; i != searchEnd ; i += searchDirection) {
				if(text.charAt(i) == '\"') textMode = !textMode;
				if (!textMode && text.charAt(i) == bracketToMatch) count ++;
				else if (!textMode && text.charAt(i) == oppositeBracketToMatch) count --;

				if (count == 0) {
					bracket2pos = i;
					break;
				}
			}
		}

 		
		// iterate through the text chars to get bracket color
		
		textMode = false;
		for (int i = 0 ; i < text.length() ; i++) {

			if(text.charAt(i) == '\"') textMode = !textMode;

			if (i == bracket1pos || i == bracket2pos) {
				if (bracket2pos > -1) 
					bracketColor = Color.RED; // matched
				else 
					bracketColor = Color.GREEN; // unmatched
			}

			if(textMode || text.charAt(i) == '\"')
				bracketColor = Color.GRAY;   // text mode
		}

	}

	
}
