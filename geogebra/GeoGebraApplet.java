/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra;

/*
 * GeoGebraApplet.java
 *
 * Created on 23. J�nner 2003, 22:37
 */

import geogebra.euclidian.EuclidianView;
import geogebra.gui.GeoGebra;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author  Markus Hohenwarter
 */
public class GeoGebraApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;
	private JButton btOpen;
	private DoubleClickListener dcListener;
	private EuclidianView ev;
	boolean showOpenButton, showToolBar, showAlgebraInput, undoActive;
	boolean showMenuBar = false; // not yet supported
	boolean showResetIcon = false;
	private boolean firstAppOpen = true;
	Color bgColor;
	private String fileStr, customToolBar;	
	private boolean showFrame = true;
	private GeoGebra wnd;

	/** Creates a new instance of GeoGebraApplet */
	public GeoGebraApplet() {}

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		// get parameters
		// filename of construction
		fileStr = getParameter("filename");
		if (fileStr != null && 
			!( fileStr.startsWith("http") || fileStr.startsWith("file") )) {
			fileStr = getCodeBase() + fileStr;			
		}		

		// type = "button" or parameter is not available 
		String typeStr = getParameter("type");
		showOpenButton = typeStr != null && typeStr.equals("button");

		// showToolBar = "true" or parameter is not available
		showToolBar = "true".equals(getParameter("showToolBar"));
		
		// customToolBar = "0 1 2 | 3 4 5 || 7 8 12" to set the visible toolbar modes
		customToolBar = getParameter("customToolBar");
		
		// showMenuBar = "true" or parameter is not available
		showMenuBar = "true".equals(getParameter("showMenuBar"));
		
		// showResetIcon = "true" or parameter is not available
		showResetIcon = "true".equals(getParameter("showResetIcon"));
		
		// showAlgebraInput = "true" or parameter is not available
		showAlgebraInput = "true".equals(getParameter("showAlgebraInput"));

		// showFrame = "true" or "false"  states whether it is possible
		// to open the application frame by double clicking on the drawing pad
		// !false is used for downward compatibility	
		showFrame = !"false".equals(getParameter("framePossible"));
		
		undoActive = showToolBar || showMenuBar;
		
		// set language manually by iso language string
		String language = getParameter("language");
		String country = getParameter("country");		
		if (language != null) {
			if (country != null)
				setLocale(new Locale(language, country));
			else
				setLocale(new Locale(language));
		}	

		// bgcolor = "#CCFFFF" specifies the background color to be used for
		// the button panel
		try {
			bgColor = Color.decode(getParameter("bgcolor"));
		} catch (Exception e) {
			bgColor = Color.white;
		}

		//	build application and open file		
		if (fileStr == null) {
			app = new Application(null, this, undoActive);
		} else {						
			String[] args = { fileStr };
			app = new Application(args, this, undoActive);
		}
		
		kernel = app.getKernel();
		
		initGUI();
	}

	public void start() {
		//	for some strange reason this is needed to get the right font size		
		//showApplet();
		repaint();
	}

	public void stop() {
		repaint();
	}

	private void initGUI() {
		// show only button to open application window	
		if (showOpenButton) {
			btOpen =
				new JButton(
					app.getPlain("Open")
						+ " "
						+ app.getPlain("ApplicationName"));
			btOpen.addActionListener(new ButtonClickListener());
			getContentPane().setBackground(bgColor);
			getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(btOpen);

		}
		// show interactive drawing pad
		else {
			JPanel p = new JPanel(new BorderLayout());
			app.setUndoActive(undoActive);			
			app.setShowMenuBar(showMenuBar);
			app.setShowAlgebraInput(showAlgebraInput);
			app.setShowToolBar(showToolBar);		
			if (customToolBar != null && customToolBar.length() > 0)
				app.setCustomToolBar(customToolBar);
			app.setShowResetIcon(showResetIcon);
			ev = app.getEuclidianView();
			p.add(app.buildApplicationPanel(), BorderLayout.CENTER);
			ev.updateBackground();
			p.setBorder(BorderFactory.createLineBorder(Color.gray));
			getContentPane().add(p);

			if (showFrame) {
				//	open frame on double click
				dcListener = new DoubleClickListener();				
				ev.addMouseListener(dcListener);
			}
			
			//app.setMoveMode();
		}
	}

	private class DoubleClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				showFrame();
			}
		}
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showFrame();
		}
	}

	private void showFrame() {
		if (showOpenButton) {
			btOpen.setEnabled(false);
		} else {
			//	clear applet		 
			Container cp = getContentPane();
			cp.removeAll();
			if (ev != null)
				ev.removeMouseListener(dcListener);

			JPanel p = new JPanel(new BorderLayout());
			p.setBackground(Color.white);
			JLabel label = new JLabel(app.getPlain("WindowOpened") + "...");
			label.setFont(app.getPlainFont());
			p.add(label, BorderLayout.CENTER);
			cp.add(p);
			SwingUtilities.updateComponentTreeUI(this);				
		}
		
//		 build application panel 
		if (firstAppOpen) {
			wnd = new GeoGebra();
			wnd.setApplication(app);
			wnd.initFrame();
		}
		app.setFrame(wnd);		
		app.setShowMenuBar(true);
		app.setShowAlgebraInput(true);		
		app.setUndoActive(true);
		app.setShowToolBar(true);		
		if (customToolBar != null && customToolBar.length() > 0)
			app.setCustomToolBar(customToolBar);
			
		app.updateContentPane();
		app.resetFonts();

		// open frame
		if (firstAppOpen) {
			wnd.setVisible(true);
			firstAppOpen = false;
		} else {
			wnd.setVisible(true);
		}
	}

	public void showApplet() {
		wnd.setVisible(false); // hide frame
		
		if (showOpenButton) {
			btOpen.setEnabled(true);
		} else {		
			reinitGUI();
		}
	}
	
	private void reinitGUI() {
		Container cp = getContentPane();
		cp.removeAll();
		
		app.setApplet(this);
		initGUI();
		
		app.resetFonts();
		app.refreshViews();
		SwingUtilities.updateComponentTreeUI(this);
		System.gc();
	}
					

	/* JAVA SCRIPT INTERFACE */

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return app.getXML();
	}
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
	}
	
	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public synchronized void evalXML(String xmlString) {		
		app.setXML(xmlString, false);
	}

	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized void evalCommand(String cmdString) {
		app.getAlgebraController().processAlgebraCommand(cmdString, false);
	}

	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	 
	 */
	public synchronized void reset() {
		try {		
			URL ggbURL = new URL(fileStr);
			app.loadXML(ggbURL);
			reinitGUI();	
		} catch (Exception e) {
			e.printStackTrace();
		} 					
	}
	
	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();		 				
	}
			
	/**
	 * Loads a construction from a  file (given URL).	 
	 */
	public synchronized void openFile(String strURL) {
		try {
			if (!( strURL.startsWith("http") || strURL.startsWith("file") )) {
					strURL = getCodeBase() + strURL;
			}		
			URL ggbURL = new URL(strURL);			
			app.loadXML(ggbURL);
			reinitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/*
	public synchronized void setLanguage(String isoLanguageString) {	
		app.setLanguage(new Locale(isoLanguageString));
	}
	
	public synchronized void setLanguage(String isoLanguageString, String isoCountryString) {
		app.setLanguage(new Locale(isoLanguageString, isoCountryString));
	}
	*/
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green, int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		Color col = new Color(red, green, blue);		
		geo.setObjColor(col);
		geo.updateRepaint();
	}	
	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return "#" + geogebra.util.Util.toHexString(geo.getColor());		
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.remove();
		kernel.notifyRepaint();
	}	
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);				
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isDefined();
	}	
	
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getAlgebraDescription();
	}
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getDefinitionDescription();
	}
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getCommandDescription();
	}
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomX;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).x;
		else
			return 0;
	}
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomY;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).y;
		else
			return 0;
	}
	
	/**
	 * Returns the double value of the object with the given name. Note: returns 0 if
	 * the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo instanceof NumberValue)
			return ((NumberValue) geo).getDouble();		
		else
			return 0;
	}
	
	/**
	 * Turns the repainting of this applet on (true) or off (false).
	 */
	public synchronized void setRepaintingActive(boolean flag) {
		// NOTE: kept for downward compatibility
		//System.out.println("set repainting: " + flag);
		//kernel.setNotifyViewsActive(flag);
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized String [] getAllObjectNames() {			
		Construction cons = kernel.getConstruction();
		Iterator it = cons.getAllGeoElementsIterator();
		String [] objNames = new String[cons.getGeoElementsIteratorSize()];
				
		int i=0; 
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			objNames[i] = geo.getLabel();
			i++;
		}
		return objNames;
	}	
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : geo.getObjectType().toLowerCase();
	}
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}	
	
}
