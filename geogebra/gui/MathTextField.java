package geogebra.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.main.Application;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class MathTextField extends MyTextField implements KeyListener {

	private static GeoGebraKeys ggbKeys;
	
	public MathTextField(Application app) {
		super(app.getGuiManager());
		ggbKeys = new GeoGebraKeys(app);
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
