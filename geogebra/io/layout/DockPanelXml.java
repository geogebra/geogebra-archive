package geogebra.io.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A storage container with all information which need to
 * be stored for a DockPanel.
 * 
 * @author Florian Sonner
 * @version 2008-08-27
 */
public class DockPanelXml {
	private int viewId;
	private boolean isVisible;
	private boolean openInFrame;
	private Rectangle windowRect;
	private String embeddedDef;
	private int embeddedSize;
	
	/**
	 * @param viewId		The view ID.
	 * @param isVisible		If this view is visible at the moment.
	 * @param openInFrame 	If this view should be opened in a separate frame.
	 * @param windowRect 	The rectangle which defines the location and size of the window for this view. 
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelXml(int viewId, boolean isVisible, boolean openInFrame, Rectangle windowRect, String embeddedDef, int embeddedSize) {
		this.viewId = viewId;
		this.setVisible(isVisible);
		this.setOpenInFrame(openInFrame);
		this.setWindowRect(windowRect);
		this.setEmbeddedDef(embeddedDef);
		this.setEmbeddedSize(embeddedSize);
	}
	
	/**
	 * @param viewId		The view ID.
	 * @param isVisible		If this view is visible at the moment.
	 * @param inFrame 		If this view is in an separate window at the moment.
	 * @param windowX		The x location of the window.
	 * @param windowY		The y location of the window.
	 * @param windowWidth	The width of the window.
	 * @param windowHeight	The height of the window.
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelXml(int viewId, boolean isVisible, boolean inFrame, int windowX, int windowY, int windowWidth, int windowHeight, String embeddedDef, int embeddedSize) {
		this(viewId, isVisible, inFrame, new Rectangle(windowX, windowY, windowWidth, windowHeight), embeddedDef, embeddedSize);
	}
	
	/**
	 * @param viewId		The view ID.
	 * @param isVisible		If this view is visible at the moment.
	 * @param inFrame 		If this view is in an separate window at the moment.
	 * @param windowLoc		The location of the window.
	 * @param windowSize 	The size of the window.
	 * @param embeddedDef	The definition string for the location of the view in the main window.
	 * @param embeddedSize	The size of the view in the main window.
	 */
	public DockPanelXml(int viewId, boolean isVisible, boolean inFrame, Point windowLoc, Dimension windowSize, String embeddedDef, int embeddedSize) {
		this(viewId, isVisible, inFrame, new Rectangle(windowLoc, windowSize), embeddedDef, embeddedSize);
	}

	/**
	 * @return The view ID.
	 */
	public int getViewId() {
		return viewId;
	}

	/**
	 * @param Set if this view is visible at the moment.
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @return If this view is visible at the moment.
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param inFrame Set if the DockPanel was shown in a frame the last time it
	 * 		was visible.
	 */
	public void setOpenInFrame(boolean inFrame) {
		this.openInFrame = inFrame;
	}

	/**
	 * @return If the DockPanel was shown in a frame the last time it 
	 * 		was visible.
	 */
	public boolean isOpenInFrame() {
		return openInFrame;
	}

	/**
	 * @param windowRect A rectangle with the size and position of the 
	 * 		frame.
	 */
	public void setWindowRect(Rectangle windowRect) {
		this.windowRect = windowRect;
	}

	/**
	 * @return the windowRect
	 */
	public Rectangle getWindowRect() {
		return windowRect;
	}

	/**
	 * @param embeddedDef the embeddedDef to set
	 */
	public void setEmbeddedDef(String embeddedDef) {
		this.embeddedDef = embeddedDef;
	}

	/**
	 * @return the embeddedDef
	 */
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @param embeddedSize the embeddedSize to set
	 */
	public void setEmbeddedSize(int embeddedSize) {
		this.embeddedSize = embeddedSize;
	}

	/**
	 * @return the embeddedSize
	 */
	public int getEmbeddedSize() {
		return embeddedSize;
	}
	
	/**
	 * @return
	 */
	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<view id=\"");
		sb.append(getViewId());
		sb.append("\" visible=\"");
		sb.append(isVisible());
		sb.append("\" inframe=\"");
		sb.append(isOpenInFrame());
		sb.append("\" location=\"");
		sb.append(getEmbeddedDef());
		sb.append("\" size=\"");
		sb.append(getEmbeddedSize());
		sb.append("\" window=\"");
		sb.append(getWindowRect().x);
		sb.append(",");
		sb.append(getWindowRect().y);
		sb.append(",");
		sb.append(getWindowRect().width);
		sb.append(",");
		sb.append(getWindowRect().height);
		sb.append("\" />\n");
		return sb.toString();
	}
	
	/**
	 * Clone this object. Required as dock panels would change the loaded perspective 
	 * automatically otherwise.
	 */
	public Object clone() {
		return new DockPanelXml(viewId, isVisible, openInFrame, windowRect, embeddedDef, embeddedSize);
	}
}
