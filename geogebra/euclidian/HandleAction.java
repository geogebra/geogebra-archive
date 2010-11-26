package geogebra.euclidian;

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

}
