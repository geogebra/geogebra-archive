package geogebra.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import geogebra.gui.virtualkeyboard.MyTextField;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class MathTextField extends MyTextField implements KeyListener {

	private static GeoGebraKeys ggbKeys = new GeoGebraKeys();
	
	public MathTextField(GuiManager guiManager) {
		super(guiManager);
		addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent e) {
		ggbKeys.keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		ggbKeys.keyReleased(e);
	}

	public void keyTyped(KeyEvent e) {
		ggbKeys.keyTyped(e);
	}
}
