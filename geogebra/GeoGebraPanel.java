package geogebra;

import geogebra.main.Application;
import geogebra.main.DefaultApplication;
import geogebra.plugin.GgbAPI;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * GeoGebra application inside a JPanel that can be integrated
 * into other applications.
 */
public class GeoGebraPanel extends JPanel {
	
	/**
	 * Test method that demonstrates how to embed a GeoGebraPanel
	 * into another application.
	 * @param args
	 */
    public static void main(String[] args) {  
    	JFrame f = new JFrame();
    	
    	// prepare URL for ggb file
    	URL ggbURL = null;
    	try {
        	File ggbFile = new File("test.ggb");
        	ggbURL = ggbFile.toURL();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	// create GeoGebraPanel and open test file
    	GeoGebraPanel ggbPanel = new GeoGebraPanel(ggbURL);
    	// hide input bar
    	ggbPanel.setShowAlgebraInput(false);
    	// use smaller icons in toolbar
    	ggbPanel.setMaxIconSize(24); 
    	ggbPanel.buildGUI();
    
    	// add GeoGebraPanel to your application
    	f.getContentPane().add(ggbPanel);
    	f.setSize(800, 600);
    	f.setVisible(true);
    }
	
	
	private Application app;
	
	/**
     * Creates a GeoGebraPanel. Note that you need to 
     * call buildGUI() after this method.
     */
	public GeoGebraPanel() {
		this(null);
	}
	
	/**
     * Creates a GeoGebraPanel and loads the given ggbFile.
     * Note that you need to call buildGUI() after this method.
     */
	public GeoGebraPanel(URL ggbFile) {
		// use filename as argument
		String [] args = null;
		if (ggbFile != null) {
			args = new String[1];
			args[0] = ggbFile.toExternalForm();
		}
		
		// create GeoGebra application
		app = new DefaultApplication(args, this, false);
	}
	
	/**
	 * Loads a ggb or ggt file. 
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void openFile(URL url) {
		app.getGgbApi().openFile(url.toExternalForm());
	}
	
	/**
	 * Tells the panel to show/hide the tool bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowToolbar(boolean showToolBar) {
		app.setShowToolBar(showToolBar, true);	
	}
	
	/**
	 * Sets the font size of the GeoGebra user interface.
	 */
	public synchronized void setFontSize(int points) {
		app.setFontSize(points);
	}
	
	/**
	 * Sets the maximum pixel size of all icons in the GeoGebra
	 * user interface including the toolbar. 
	 * @pixel: a value between 16 and 32
	 */
	public synchronized void setMaxIconSize(int pixel) {
		app.setMaxIconSize(pixel);
	}
	
	/**
	 * Tells the panel to show/hide the menu bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowMenubar(boolean showMenuBar) {
		app.setShowMenuBar(showMenuBar);
	}
	
	/**
	 * Tells the panel to show/hide the input bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowAlgebraInput(boolean showInputBar) {
		app.setShowAlgebraInput(showInputBar);	
	}
	
	/**
	 * Sets the language of the GeoGebraPanel.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setLanguage(Locale locale) {
		app.setLanguage(locale);
	}
	
	/**
	 * Rebuilds the GeoGebra user interface in this panel.
	 */
	public synchronized void buildGUI() {
		removeAll();
		setLayout(new BorderLayout());
		
		// activate undo
		app.setUndoActive(app.showMenuBar() || app.showToolBar());
		
		// create application panel
		add(app.buildApplicationPanel(), BorderLayout.CENTER);
		
		if (isShowing())
			SwingUtilities.updateComponentTreeUI(this);
	}
	
	/**
	 * Returns the GeoGebraAPI object that lets you interact
	 * with the GeoGebra construction.
	 */
	public synchronized GgbAPI getGeoGebraAPI() {
		return app.getGgbApi();
	}

}
