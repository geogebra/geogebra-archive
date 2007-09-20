package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.kernel.Kernel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class SpreadsheetController implements KeyListener, MouseListener,MouseMotionListener{

	private Kernel kernel;
	private Application app;
	private SpreadsheetView view;
	
	public SpreadsheetController(Kernel kernel)
	{
		this.kernel = kernel;
		app = kernel.getApplication();	
	}
	
	void setView(SpreadsheetView view) {
		this.view = view;
	}

	Application getApplication() {
		return app;
	}

	Kernel getKernel() {
		return kernel;
	}
	
	/**
	* KeyListener implementation for SpreadsheetView
	*/
	public void keyPressed(KeyEvent arg0) {
		
		
	}

	public void keyReleased(KeyEvent arg0) {
		
		
	}

	public void keyTyped(KeyEvent arg0) {
		
		
	}

	public void mouseClicked(MouseEvent arg0) {
		
		
	}

	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	public void mouseExited(MouseEvent arg0) {
		
		
	}

	public void mousePressed(MouseEvent arg0) {
		
		
	}

	public void mouseReleased(MouseEvent arg0) {
		
		
	}

	public void mouseDragged(MouseEvent arg0) {
		
		
	}

	public void mouseMoved(MouseEvent arg0) {
		
		
	}

}
