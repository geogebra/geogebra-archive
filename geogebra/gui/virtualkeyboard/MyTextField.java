package geogebra.gui.virtualkeyboard;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.main.Application;
import geogebra.main.GuiManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class MyTextField extends JTextField implements FocusListener, VirtualKeyboardListener, CaretListener {

	GuiManager guiManager;

	public MyTextField(GuiManager guiManager) {
		super();
		this.guiManager = guiManager;
		addFocusListener(this);
		addCaretListener(this);


	}

	public MyTextField(GuiManager guiManager, int i) {
		super(i);
		this.guiManager = guiManager;
		addFocusListener(this);
	}
	
	boolean caretUpdated = true;
	boolean caretShowing = true;

	public void caretUpdate(CaretEvent e) {
		caretUpdated = true;
		repaint();
	}

	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield((VirtualKeyboardListener)this, false);
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));

	}

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

		setCaretPosition(pos + text.length());


		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField)this;
			tf.updateCurrentWord();
			tf.updateAutoCompletion();
		}


	}
	
	private float pos = 0;
	private int scrollOffset = 0;
	private int w = 0, h = 0;
	private FontRenderContext frc;
	private Font font;
	private Graphics2D g2;
	private Insets insets;
	
	public void paintComponent(Graphics gr) {

		// moving caret doesn't work without this... why?
		super.paintComponent(gr);
		
		// flash caret if there's been no caret movement since last repaint
		if (caretUpdated) caretShowing = false;
		else caretShowing = !caretShowing;
		
		caretUpdated = false;

		g2 = (Graphics2D)gr;
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		insets = getInsets();
		
		String text = getText();

		w = getWidth();
		h = getHeight();

		//g2.setClip(0, 0, w, h);

		g2.setColor(Color.white);
		g2.fillRect(0, 0, w, h);

		frc = ((Graphics2D) g2).getFontRenderContext();

		scrollOffset = getScrollOffset();

		font = g2.getFont();
		int caret = getCaretPosition();

		int bracket1pos = -1;
		int bracket2pos = -1;
		
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


		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();


		pos = 0;
		float caretPos = -1;

		if (caret == 0) caretPos = 0;
		textMode = false;
		for (int i = 0 ; i < text.length() ; i++) {
			if(text.charAt(i) == '\"') textMode = !textMode;
			if (i == bracket1pos || i == bracket2pos) {
				if (bracket2pos > -1) g2.setColor(Color.RED); // matched
				else g2.setColor(Color.GREEN); // unmatched
			}
			else g2.setColor(Color.BLACK);
			if(textMode || text.charAt(i) == '\"')g2.setColor(Color.GRAY);
			drawText(text.charAt(i)+"", i >= selStart && i < selEnd);

			if (i + 1 == caret) caretPos = pos;
		}

		if (caretShowing && caretPos > -1 && hasFocus()) {
			g2.setColor(Color.black);
			g2.fillRect((int)caretPos - scrollOffset + insets.left, insets.bottom + 2 , 1, h - insets.bottom - insets.top - 4);
			g2.setPaintMode();

		}
	}


	private void drawText(String str, boolean selected) {
		if ("".equals(str)) return;
		TextLayout layout = new TextLayout(str, font, frc);
		g2.setFont(font);
		float advance = layout.getAdvance();

		if (selected) {
			g2.setColor(getSelectionColor());
			g2.fillRect((int)pos - scrollOffset + insets.left, insets.bottom + 2 , (int)advance, h - insets.bottom - insets.top - 4);
			g2.setColor(getSelectedTextColor());
		}
		g2.setClip(0, 0, w, h);
		if (pos - scrollOffset + advance + insets.left > 0 && pos - scrollOffset < w)
			g2.drawString(str, pos - scrollOffset + insets.left, h - insets.bottom - insets.top - 4);
		pos += layout.getAdvance();

	}
}
