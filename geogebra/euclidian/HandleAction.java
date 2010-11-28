package geogebra.euclidian;

import geogebra.Matrix.GgbVector;

import java.awt.event.KeyEvent;

/**
 * @author mathieu
 *
 * For previewable that handles keys (return, esc, ...)
 */
public interface HandleAction {
	
	/**
	 * handle the key and return true if has done something
	 * @param event
	 * @return true if has done something
	 */
	public boolean handleKey(KeyEvent event);
	
	
	/**
	 * handle OK action
	 * @return true if has done something
	 */
	public boolean handleOK();
	
	/**
	 * handle Cancel action
	 * @return true if has done something
	 */
	public boolean handleCancel();
	
	/**
	 * handle new start position
	 * @param pos
	 */	
	public void handleStartPosition(GgbVector pos);
	
	
	/**
	 * handle new position
	 * @param pos
	 */
	public void handlePosition(GgbVector pos);
	

	/**
	 * 
	 * @return main direction (for handle)
	 */
	public GgbVector getMainDirection();
	
}
