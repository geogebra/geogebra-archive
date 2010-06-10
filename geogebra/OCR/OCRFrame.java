package geogebra.OCR;
// Yves Kreis -->

import geogebra.main.Application;

import java.awt.BorderLayout;

import javax.swing.JFrame;
/**
 * Frame to contain the Applet
 *
 */

public class OCRFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the OCRFrame to be called from DefaultGUIManager
	 * @param app TO find the state of the main window
	 */
	//
	
	public OCRFrame(final Application app){
		super("OCR");  
		OCRApplet applet = new OCRApplet();
		applet.init();
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);
        add(applet, BorderLayout.CENTER);
		setSize(362, 475);
		setResizable(false);
		setVisible(true);
		if (app != null)
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		else
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} 
}

// <-- Yves Kreis>