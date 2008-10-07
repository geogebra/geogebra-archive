package geogebra.euclidian;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {
	
	private EuclidianController ec;
	
	public MyKeyListener(EuclidianController ec) {
		this.ec = ec;
	}

	
	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent event) {
	}
	
	/** handle function keys and delete key */
	public void keyPressed(KeyEvent event) {
		if (ec.getApplication().keyPressedConsumed(event))
			event.consume();		
	}


}
