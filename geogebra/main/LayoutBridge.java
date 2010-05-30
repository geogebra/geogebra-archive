package geogebra.main;

import java.awt.Component;

/**
 * API for an easy communication between non-GUI components and the layout manager.
 * 
 * @author Florian Sonner
 */
public interface LayoutBridge
{
	/**
	 * Checks if the given component is in an external window. Used for key dispatching.
	 * 
	 * @param component
	 * @return
	 */
	public boolean inExternalWindow(Component component);
}
