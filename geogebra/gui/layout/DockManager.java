package geogebra.gui.layout;

import geogebra.io.layout.DockPanelXml;
import geogebra.io.layout.DockSplitPaneXml;
import geogebra.main.Application;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Class responsible to manage the whole docking area of the window.
 * 
 * @author Florian Sonner
 */
public class DockManager implements AWTEventListener {
	private Application app;
	private Layout layout;
	
	/**
	 * The glass panel used for drag'n'drop.
	 */
	private DockGlassPane glassPane;	
	
	/**
	 * The root split pane.
	 */
	private DockSplitPane rootPane;
	
	/**
	 * The dock panel which has the focus at the moment.
	 */
	private DockPanel focusedDockPanel;
	
	/**
	 * A list with all registered dock panels.
	 */
	private ArrayList<DockPanel> dockPanels;
	
	/**
	 * @param app
	 * @param layout
	 */
	public DockManager(Layout layout) {
		this.layout = layout;
		this.app = layout.getApp();
		
		dockPanels = new ArrayList<DockPanel>();
		glassPane = new DockGlassPane(this);
		
		if(!app.isApplet()) {
			app.setGlassPane(glassPane);
		}
		
		// register focus changes
		//Toolkit.getDefaultToolkit().addAWTEventListener(this , AWTEvent.MOUSE_EVENT_MASK);
	}
	
	/**
	 * Register a new dock panel. Use Layout::registerPanel() as public interface.
	 * 
	 * @param dockPanel
	 */
	public void registerPanel(DockPanel dockPanel) {
		dockPanels.add(dockPanel);
		dockPanel.register(this);
	}
	
	/**
	 * Apply a certain perspective by arranging the dock panels in the requested order.
	 * 
	 * @param spInfo
	 * @param dpInfo
	 * 
	 * @see Layout::applyPerspective()
	 */
	public void applyPerspective(DockSplitPaneXml[] spInfo, DockPanelXml[] dpInfo) {		
		if(dockPanels != null) {			
			// hide existing external windows
			for(DockPanel panel : dockPanels) {
				if(panel.isOpenInFrame() && panel.isVisible()) {
					hide(panel);
				}
			}
			
			// copy dock panel info settings
			for(int i = 0; i < dpInfo.length; ++i) {
				DockPanel panel = getPanel(dpInfo[i].getViewId());
				
				if(panel == null) {
					// TODO insert error panel
				}
				
				panel.setFrameBounds(dpInfo[i].getFrameBounds());
				panel.setEmbeddedDef(dpInfo[i].getEmbeddedDef());
				panel.setEmbeddedSize(dpInfo[i].getEmbeddedSize());
				panel.setShowStyleBar(dpInfo[i].showStyleBar());
				panel.setOpenInFrame(dpInfo[i].isOpenInFrame());
				panel.setVisible(dpInfo[i].isVisible());
			}
			
			updatePanels();
		}
		
		if(spInfo.length > 0) {
			DockSplitPane[] splitPanes = new DockSplitPane[spInfo.length];
			
			// construct the split panes
			for(int i = 0; i < spInfo.length; ++i) {
				splitPanes[i] = new DockSplitPane(spInfo[i].getOrientation());
			}
			
			// cascade the split panes
			rootPane = splitPanes[0];
			
			// loop through every but the first split pane
			for(int i = 1; i < spInfo.length; ++i) {
				DockSplitPane currentParent = rootPane;
				
				// a similar system as it's used to determine the position of the dock panels (see comment in DockManager::show())
				// 0: turn left/up, 1: turn right/down
				String[] directions = spInfo[i].getLocation().split(",");
				
				// get the parent split pane, the last position is reserved for the location
				// of the current split pane and therefore ignored here
				for(int j = 0; j < directions.length - 1; ++j) {
					if(directions[j].equals("0")) {
						currentParent = (DockSplitPane)currentParent.getLeftComponent();
					} else {
						currentParent = (DockSplitPane)currentParent.getRightComponent();
					}
				}
				
				// insert the split pane
				if(directions[directions.length - 1].equals("0")) {
					currentParent.setLeftComponent(splitPanes[i]);
				} else {
					currentParent.setRightComponent(splitPanes[i]);
				}
			}

			// now insert the dock panels
			for(int i = 0; i < dpInfo.length; ++i) {
				// skip panels which will not be drawn in the main window
				if(!dpInfo[i].isVisible())
					continue;
				
				if(dpInfo[i].isOpenInFrame()) {
					show(getPanel(dpInfo[i].getViewId()));
					continue;
				}
				
				DockSplitPane currentParent = rootPane;
				String[] directions = dpInfo[i].getEmbeddedDef().split(",");
				
				/* 
				 * Get the parent split pane of this dock panel and ignore the last
				 * direction as its reserved for the position of the dock panel itself.
				 * 
				 * In contrast to the algorithm used in the show() method we'll not take care
				 * of invalid positions as the data should not be corrupted.
				 */
				for(int j = 0; j < directions.length - 1; ++j) {
					if(directions[j].equals("0") || directions[j].equals("3")) {
						currentParent = (DockSplitPane)currentParent.getLeftComponent();
					} else {
						currentParent = (DockSplitPane)currentParent.getRightComponent();
					}
				}

				if(directions[directions.length - 1].equals("0") || directions[directions.length - 1].equals("3")) {
					currentParent.setLeftComponent(getPanel(dpInfo[i].getViewId()));
				} else {
					currentParent.setRightComponent(getPanel(dpInfo[i].getViewId()));
				}
			}
			
			int windowWidth = app.getPreferredSize().width;
			int windowHeight = app.getPreferredSize().height;
			
			// set the dividers of the split panes
			for(int i = 0; i < spInfo.length; ++i) {
				if(spInfo[i].getOrientation() == DockSplitPane.VERTICAL_SPLIT)
					splitPanes[i].setDividerLocation((int)(spInfo[i].getDividerLocation() * windowHeight));
				else 
					splitPanes[i].setDividerLocation((int)(spInfo[i].getDividerLocation() * windowWidth));			
			}
		}
		
		// update all labels at once
		setLabels();
		
		// clean up as we can create a lot of mess here..
		Runtime.getRuntime().gc();
	}

	/**
	 * Start the drag'n'drop process of a DockPanel.
	 * 
	 * @param panel
	 */
	public void drag(DockPanel panel) {
		// Do not allow docking in case this is the last view
		if(panel.getParentSplitPane() == rootPane) {
			if(rootPane.getOpposite(panel) == null) {
				return;
			}
		}
		
		glassPane.startDrag(new DnDState(panel));
	}
	
	/**
	 * Stop the drag'n'drop procedure and drop the component to the the defined
	 * location.
	 * 
	 * @param dndState
	 */
	public void drop(DnDState dndState) {
		DockPanel source = dndState.getSource();		
		DockSplitPane sourceParent = source.getParentSplitPane();
		DockPanel target = dndState.getTarget();
		Component opposite = sourceParent.getOpposite(source);
		
		// No action required
		if(target == null || target == source && !dndState.isRegionOut()) {
			return;
		}
		
		// Hide the source first
		hide(source, false);
		
		source.setVisible(true);
		
		// Add the source panel at the new position
		DockSplitPane newSplitPane = new DockSplitPane();
		int dndRegion = dndState.getRegion();
		
		// Determine the orientation of the new split pane
		if(dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT ||
			dndRegion == DnDState.RIGHT || dndRegion == DnDState.RIGHT_OUT)
		{
			newSplitPane.setOrientation(DockSplitPane.HORIZONTAL_SPLIT);
		} else {
			newSplitPane.setOrientation(DockSplitPane.VERTICAL_SPLIT);
		}
		
		if(dndState.isRegionOut() && (target.getParent() == sourceParent || target == source)) {
			dndRegion >>= 4;
			dndState.setRegion(dndRegion);
		}
		
		boolean updatedRootPane = false;
		
		if(dndState.isRegionOut()) {
			DockSplitPane targetParent = target.getParentSplitPane();
			
			if(targetParent == rootPane) {
				rootPane = newSplitPane;
			} else {
				((DockSplitPane)targetParent.getParent()).replaceComponent(targetParent, newSplitPane);
			}
			
			if(dndRegion == DnDState.LEFT_OUT || dndRegion == DnDState.TOP_OUT) {
				newSplitPane.setRightComponent(targetParent);
				newSplitPane.setLeftComponent(source);
			} else {
				newSplitPane.setRightComponent(source);
				newSplitPane.setLeftComponent(targetParent);
			}
		} else {
			if(source == target) {
				if(opposite instanceof DockPanel) {
					if(((DockPanel) opposite).getParentSplitPane().getOpposite(opposite) == null)
						rootPane = newSplitPane;
					else
						((DockPanel) opposite).getParentSplitPane().replaceComponent(opposite, newSplitPane);
				} else {
					if(opposite == rootPane)
						rootPane = newSplitPane;
					else
						((DockSplitPane)opposite.getParent()).replaceComponent(opposite, newSplitPane);
				}
				
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(opposite);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(opposite);
				}
			} else if(target.getParentSplitPane().getOpposite(target) == null && target.getParentSplitPane() == rootPane) {
				rootPane.removeAll();
				
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) { 
					rootPane.setLeftComponent(source);
					rootPane.setRightComponent(target);
				} else {
					rootPane.setLeftComponent(target);
					rootPane.setRightComponent(source);
				}
				
				updatedRootPane = true;
				rootPane.setOrientation(newSplitPane.getOrientation());
			}  else {
				target.getParentSplitPane().replaceComponent(target, newSplitPane);
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(target);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(target);
				}
			}
		}
		
		app.updateCenterPanel(true);
		
		double dividerLocation = 0;
		
		if(dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT
			|| dndRegion == DnDState.TOP || dndRegion == DnDState.TOP_OUT)
		{
			dividerLocation = 0.4;
		} else {
			dividerLocation = 0.6;
		}
		
		if(updatedRootPane) {
			rootPane.setDividerLocation(dividerLocation);
		} else {
			newSplitPane.setDividerLocation(dividerLocation);
		}

		updatePanels();

		// Manually dispatch a resize event as the size of the 
		// euclidian view isn't updated all the time.
		// TODO What does the resize do which will update the component ?!
		app.getEuclidianView().dispatchEvent(
			new ComponentEvent(rootPane, ComponentEvent.COMPONENT_RESIZED)
		);
		
		Application.debug(getDebugTree(0, rootPane));
	}
	
	/**
	 * Show a DockPanel identified by its ID.
	 * 
	 * @param viewId
	 */
	public void show(int viewId) {
		show(getPanel(viewId));
	}
	
	/**
	 * Show a DockPanel where it was displayed the last time - either in the main window
	 * or in a separate frame. 
	 * 
	 * The location of the DockPanel in the main window is given by the definition string
	 * stored in DockPanelInfo.getEmbeddedDef().
	 * A definition string can be read like a list of directions, where numbers
	 * represents the four directions we can go:
	 * 
	 * 0: Top
	 * 1: Right
	 * 2: Bottom
	 * 3: Left 
	 * 
	 * A definition string like "0,3,2" is read by the program this way:
	 * - Go to the top (=0) container of the root pane.
	 * - Go to the container at the left (=3) of the current container.
	 * - Insert the DockPanel at the bottom (=2) of the current container.
	 * 
	 * Note that the program differs between the top & left and bottom & right
	 * position while the DockSplitPane just differs between a left and right
	 * component and the orientation of the split pane.
	 * 
	 * As the layout of the panels is changed frequently and may be completely
	 * different if the DockPanel is inserted again, the algorithm ignores all
	 * directions which are not existing anymore in order to get the best possible
	 * result.
	 * Using the example from above, the second direction ("3") may be
	 * ignored if the top container of the root pane isn't divided anymore or the
	 * orientation of the container was changed. The algorithm will continue with 
	 * "2" and will insert the DockPanel at the bottom of the top container 
	 * of the root pane.
	 * 
	 * @param panel 
	 */
	public void show(DockPanel panel) {
		panel.setVisible(true);
		
		// TODO causes any problems?
		app.getGuiManager().attachView(panel.getViewId());
		
		if(panel.isOpenInFrame()) {
			panel.createFrame();
		} else {
			// Transform the definition into an array of integers
			String[] def = panel.getEmbeddedDef().split(",");
			int[] locations = new int[def.length];
			
			for(int i = 0; i < def.length; ++i) {
				if(def[i].length() == 0) {
					def[i] = "1";
				}
				
				locations[i] = Integer.parseInt(def[i]);
				
				if(locations[i] > 3 || locations[i] < 0)
					locations[i] = 3; // left as default direction
			}
			
			// We insert this panel at the left by default
			if(locations.length == 0) 
				locations = new int[] { 3 };
			
			DockSplitPane currentPane = rootPane;
			int secondLastPos = -1;
			
			// Get the location of our new DockPanel (ignore last entry)
			for(int i = 0; i < locations.length - 1; ++i) {			
				// The orientation of the current pane does not match the stored orientation, skip this
				if(currentPane.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT && (locations[i] == 0 || locations[i] == 2)) {
					continue;
				} else if(currentPane.getOrientation() == DockSplitPane.VERTICAL_SPLIT && (locations[i] == 1 || locations[i] == 3)) {
					continue;
				}
				
				Component component;
				
				if(locations[i] == 0 || locations[i] == 3)
					component = currentPane.getLeftComponent();
				else
					component = currentPane.getRightComponent();
				
				if(!(component instanceof DockSplitPane)) {
					secondLastPos = locations[i];
					break;
				} else {
					currentPane = (DockSplitPane)component;
				}
			}
	
			int size = panel.getEmbeddedSize();
			int lastPos = locations[locations.length - 1];
			
			DockSplitPane newSplitPane = new DockSplitPane();
			
			if(lastPos == 0 || lastPos == 2) {
				newSplitPane.setOrientation(DockSplitPane.VERTICAL_SPLIT);
			} else {
				newSplitPane.setOrientation(DockSplitPane.HORIZONTAL_SPLIT);
			}
			
			// the size (height / width depending upon lastPos) of the parent element,
			// this value is necessary to prevent panels which completely hide
			// their opposite element
			// TODO implement this
			int parentSize;
			
			// the component opposite to the current component
			Component opposite;
			
			if(secondLastPos == -1) {
				opposite = rootPane;
				rootPane = newSplitPane;
				
				// in root pane, the opposite may be null
				if(lastPos == 0 || lastPos == 3) {
					if(((DockSplitPane)opposite).getLeftComponent() == null) {
						opposite = ((DockSplitPane)opposite).getRightComponent();
					}
				} else {
					if(((DockSplitPane)opposite).getRightComponent() == null) {
						opposite = ((DockSplitPane)opposite).getLeftComponent();
					}
				}
			} else {
				if(secondLastPos == 0 || secondLastPos == 3) {
					opposite = currentPane.getLeftComponent();
				} else {
					opposite = currentPane.getRightComponent();
				}
				
				// in root pane, the opposite may be null
				if(opposite == null) {
					opposite = currentPane.getOpposite(opposite);
					rootPane = newSplitPane;
				} else if(opposite.getParent() == rootPane && rootPane.getOpposite(opposite) == null) {
					rootPane = newSplitPane;
				} else {
					currentPane.replaceComponent(opposite, newSplitPane);
				}
			}
			
			if(lastPos == 0 || lastPos == 3) {
				newSplitPane.setLeftComponent(panel);
				newSplitPane.setRightComponent(opposite);
			} else {
				newSplitPane.setLeftComponent(opposite);
				newSplitPane.setRightComponent(panel);
			}
			
			if(!app.isIniting())
				app.updateCenterPanel(true);
			
			if(lastPos == 0 || lastPos == 3) {
				newSplitPane.setDividerLocation(size);
			} else {
				if(newSplitPane.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT) {
					newSplitPane.setDividerLocation(newSplitPane.getWidth() - size);
				} else {
					newSplitPane.setDividerLocation(newSplitPane.getHeight() - size);
				}
			}
		}
		
		panel.updatePanel();
		
		Application.debug(getDebugTree(0, rootPane));
	}
	
	/**
	 * Hide a dock panel identified by the view ID.
	 * 
	 * @param viewId
	 */
	public void hide(int viewId) {
		hide(getPanel(viewId), true);
	}
	
	/**
	 * Hide a dock panel permanently.
	 * 
	 * @param panel
	 */
	public void hide(DockPanel panel) {
		hide(panel, true);
	}
	
	/**
	 * Hide a dock panel.
	 * 
	 * @param panel
	 * @param isPermanent If this change is permanent.
	 */
	public void hide(DockPanel panel, boolean isPermanent) {
		if(!panel.isVisible())
			throw new IllegalArgumentException("DockPane is not visible and can't be hidden.");
		
		panel.setVisible(false);
		
		if(isPermanent) {
			app.getGuiManager().detachView(panel.getViewId());
		}
		
		if(panel.isOpenInFrame()) {
			panel.removeFrame();
			panel.setOpenInFrame(true); // open in frame the next time
		} else {
			DockSplitPane parent = panel.getParentSplitPane();
			
			// Save settings
			if(parent.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT) {
				panel.setEmbeddedSize(panel.getWidth());
			} else {
				panel.setEmbeddedSize(panel.getHeight());
			}
			
			panel.setEmbeddedDef(panel.calculateEmbeddedDef());
			panel.setOpenInFrame(false);
			
			if(parent == rootPane) {
				if(parent.getOpposite(panel) instanceof DockSplitPane) {
					rootPane = (DockSplitPane)parent.getOpposite(panel);
				} else {
					parent.replaceComponent(panel, null);
				}
				app.updateCenterPanel(true);
			} else {
				DockSplitPane grandParent = (DockSplitPane)parent.getParent();
				int dividerLoc = grandParent.getDividerLocation();
				grandParent.replaceComponent(parent, parent.getOpposite(panel));
				grandParent.setDividerLocation(dividerLoc);
			}
			
			if(isPermanent) {
				app.validateComponent();
			}
		}
	}
	
	/**
	 * Listen to mouse clicks and determine if the view focus changed.
	 */
	public void eventDispatched(AWTEvent event) {
		// we also get notified about other mouse events, but we want to ignore them
		if(event.getID() != MouseEvent.MOUSE_CLICKED) {
			return;
		}
		
		// determine ancestor element of the event source which is of type
		// dock panel
		DockPanel dp = (DockPanel)SwingUtilities.getAncestorOfClass(DockPanel.class, (Component)event.getSource());
		
		if(dp != null && dp != focusedDockPanel) {
			// remove focus from previously focused dock panel
			if(focusedDockPanel != null) {
				focusedDockPanel.setFocus(false);
			}
			
			focusedDockPanel = dp;
			focusedDockPanel.setFocus(true);
		}
	}
	
	/**
	 * Update the labels of all DockPanels.
	 */
	public void setLabels() {
		for(DockPanel panel : dockPanels) {
			panel.updateLabels();
		}
	}
	
	/**
	 * Update the glass pane
	 */
	public void updateGlassPane() {
		if(!app.isApplet() && glassPane.getParent() != null) {
			app.setGlassPane(glassPane);
		}
	}
	
	/**
	 * Update the titles of the frames as they contain the file name of the current
	 * document.
	 */
	public void updateTitles() {
		for(DockPanel panel : dockPanels) {
			panel.updateTitle();
		}
	}
	
	/**
	 * Update all DockPanels.
	 * 
	 * This is required if the user changed whether the title bar should be displayed or not.
	 * 
	 * @see setLabels
	 */
	public void updatePanels() {
		for(DockPanel panel : dockPanels) {
			panel.updatePanel();
		}
	}
	
	/**
	 * Update the fonts in all dock panels.
	 */
	public void updateFonts() {
		for(DockPanel panel : dockPanels) {
			panel.updateFonts();
		}
	}
	
	/**
	 * Scale the split panes based upon the given X and Y scale. This is used to keep relative
	 * dimensions of the split panes if the user is switching between applet and frame mode. 
	 * 
	 * @param scaleX
	 * @param scaleY
	 */
	public void scale(float scaleX, float scaleY) {
		scale(scaleX, scaleY, rootPane);
	}
	
	private void scale(float scaleX, float scaleY, DockSplitPane splitPane) {
		splitPane.setDividerLocation((int)(splitPane.getDividerLocation() * (splitPane.getOrientation() == DockSplitPane.VERTICAL_SPLIT ? scaleX : scaleY)));
		
		if(splitPane.getLeftComponent() != null && splitPane.getLeftComponent() instanceof DockSplitPane) {
			scale(scaleX, scaleY, (DockSplitPane)splitPane.getLeftComponent());
		}
		
		if(splitPane.getRightComponent() != null && splitPane.getRightComponent() instanceof DockSplitPane) {
			scale(scaleX, scaleY, (DockSplitPane)splitPane.getRightComponent());
		}
	}
	
	/**
	 * Get the GeoGebraLayout instance.
	 * 
	 * @return
	 */
	public Layout getLayout() {
		return layout;
	}
	
	/**
	 * Get the glass pane which is used to draw the preview rectangle if the user dragged
	 * a DockPanel.
	 * 
	 * @return
	 */
	public DockGlassPane getGlassPane() {
		return glassPane;
	}
	
	/**
	 * Returns a specific DockPanel type.
	 * 
	 * Use the constants VIEW_EUCLIDIAN, VIEW_ALGEBRA etc. as viewId.
	 * 
	 * @param viewId
	 * @throws IllegalArgumentException
	 * @return
	 */
	public DockPanel getPanel(int viewId)
		throws IllegalArgumentException
	{
		DockPanel panel = null;
		for(DockPanel dockPanel : dockPanels) {
			if(dockPanel.getViewId() == viewId) {
				panel = dockPanel;
				break;
			}
		}
		
		if(panel != null) {
			return panel;
		} else {
	        app.setDefaultCursor();
			throw new IllegalArgumentException("viewId not found");
		}
	}
	
	/**
	 * Get all dock panels.
	 * @return
	 */
	public DockPanel[] getPanels() {
		return (DockPanel[])dockPanels.toArray(new DockPanel[0]);
	}
	
	/**
	 * Gets the root split pane which contains all other elements like DockPanels or
	 * DockSplitPanes.
	 * 
	 * @return
	 */
	public DockSplitPane getRoot() {
		return rootPane;
	}
	
	/**
	 * Return a string which can be used to debug the view tree.
	 * @param depth
	 * @param pane
	 * @return
	 */
	private String getDebugTree(int depth, DockSplitPane pane) {
		StringBuilder strBuffer = new StringBuilder();
		
		Component leftComponent = pane.getLeftComponent();
		Component rightComponent = pane.getRightComponent();
		
		strBuffer.append(strRepeat("-", depth) + "[left]");
		
		if(leftComponent == null)
			strBuffer.append("null" + "\n");
		else if(leftComponent instanceof DockSplitPane)
			strBuffer.append("\n" + getDebugTree(depth+1, (DockSplitPane)leftComponent));
		else
			strBuffer.append(leftComponent.toString() + "\n");
		
		strBuffer.append(strRepeat("-", depth) + "[right]");
		
		if(rightComponent == null)
			strBuffer.append("null" + "\n");
		else if(rightComponent instanceof DockSplitPane)
			strBuffer.append("\n" + getDebugTree(depth+1, (DockSplitPane)rightComponent));
		else
			strBuffer.append(rightComponent.toString() + "\n");
		
		return strBuffer.toString();
	}
	
	private String strRepeat(String str, int times)	{
		StringBuilder strBuffer = new StringBuilder();
		for(int i = 0; i < times; ++i)
			strBuffer.append(str);
		return strBuffer.toString();
	}
}
