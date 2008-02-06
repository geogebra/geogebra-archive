package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class CASKeyController implements KeyListener{

	private CASSession session;
	private CASView view;
	private JTable	consoleTable;
	
	public CASKeyController(CASView view, CASSession session, JTable table) {
		this.session = session;
		this.view = view;
		this.consoleTable = table;
	}
	
	/*
	 * KeyListener
	 */
	
	public void keyPressed(KeyEvent e) {		
		Object src = e.getSource();
		System.out.println("Key Pressed " + src.getClass().getName());
		
		if (src instanceof JTextArea) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				JTextArea ta = (JTextArea) src;	
				String inputText = ta.getText();								
				String evaluation = view.getCAS().evaluateJASYMCA(inputText);												
				
				// show message box
				StringBuffer sb = new StringBuffer();
				sb.append("in: ");
				sb.append(inputText);
				sb.append("\nout: ");				
				sb.append(evaluation);
				JOptionPane.showMessageDialog(view, sb.toString());				
			}									
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e) {
		Object src = e.getSource();
		System.out.println("Mouse Clicked--------- " + src.getClass().getName());
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();
		System.out.println("Mouse Pressed--------- " + src.getClass().getName());
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		Object src = e.getSource();
		System.out.println("Mouse Released--------- " + src.getClass().getName());		
	}
	

}
