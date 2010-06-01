package geogebra.main;

import java.awt.Component;

import javax.swing.JComponent;

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
	
	/**
	 * @return The root component of the layout manager.
	 */
	public JComponent getRootComponent();
	
	/**
	 * Append the layout XML information to the given string builder.
	 * 
	 * @param sb The string builder to which the XML is appended
	 * @param isPreference
	 */
	public void getXml(StringBuilder sb, boolean isPreference);
}
