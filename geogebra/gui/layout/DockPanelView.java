package geogebra.gui.layout;

/**
 * Interface for dock panels which have been added to the layout manager. 
 * 
 * Implementing this interface is optional, in case an implementation is found the
 * interface methods are called in certain situations to allow the component to react
 * to drag'n'drop or resizing.  
 * 
 * @author Florian Sonner
 */
public interface DockPanelView {
	/**
	 * Method called by the layout manager at the beginning of drag'n'drop.
	 */
	public void beginDrag();
	
	/**
	 * Method called by the layout manager at the end of drag'n'drop.
	 */
	public void endDrag();
	
	/**
	 * Method called when resizing starts.
	 */
	public void beginResize();
	
	/**
	 * Method calling when resizing ends.
	 */
	public void endResize();
}
