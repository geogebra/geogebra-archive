/*
 * Créé le 18 juin 2006
 *
 * Pour changer le modèle de ce fichier généré, allez à :
 * Fenêtre&gt;Préférences&gt;Java&gt;Génération de code&gt;Code et commentaires
 */
package geogebra.export.pstricks.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author loic
 *
 */
public  abstract class TextValue extends JTextField implements KeyListener {
	JFrame jf;
	TextValue(JFrame jf, String s){
		super(s);
		this.jf=jf;
		addKeyListener(this);
	}
	public double getValue() throws NumberFormatException{
		return Double.parseDouble(getText());
	}
	public void setValue(double d){
		String s=String.valueOf(d);
		setText(s);
		// Dynamicly enlarge the JTextField if the number is too large		
		java.awt.FontMetrics fm=getFontMetrics(getFont());
		setPreferredSize(new java.awt.Dimension(fm.stringWidth(s)+10,getHeight()));
		jf.pack();
	}
	public void keyTyped(KeyEvent e){
		// Accept only numerical characters
		char c = e.getKeyChar();      
		if (!(Character.isDigit(c) ||
				(c == KeyEvent.VK_BACK_SPACE) ||
				(c == KeyEvent.VK_DELETE) ||
				(c=='.'))) {
		    e.consume();
		}
		// if character is '.', check there's no other '.' in the number		
		else if (c=='.'&&getText().indexOf('.')!=-1){
		    e.consume();
		}
		
	}
	public void keyPressed(KeyEvent e){
	}
	public abstract void keyReleased(KeyEvent e);
}
