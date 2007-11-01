package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class CASController implements KeyListener {

	private CASSession session;
	private CASView view;
	
	public CASController(CASView view, CASSession session) {
		this.session = session;
		this.view = view;
	}
	
	/*
	 * KeyListener
	 */
	
	public void keyPressed(KeyEvent e) {		
		Object src = e.getSource();
		
	
		
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
	

}
