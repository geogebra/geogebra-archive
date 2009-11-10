package geogebra;

import java.awt.Color;

import javax.swing.JApplet;

/**
 * Applet to load all GeoGebra jar files in the background.
 */
public class GeoGebraAppletPreloader extends JApplet {

	public void init() {
		setBackground(Color.white);
		System.out.println("GeoGebraAppletPreloader " + GeoGebra.VERSION_STRING + " started");
		loadAllJarFiles();
	}
	
	/**
	 * Loads all jar files in a background task. 
	 */
	public static void loadAllJarFiles() {
		Thread jarLoader = new Thread() {
			public void run() {
				// touch on file in all jar files to force loading
				
				// load main jar
				System.out.print("loading geogebra_main.jar... ");
				System.out.flush();
				try {
					geogebra.cas.GeoGebraCAS.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load cas jar
				System.out.print("loading geogebra_cas.jar... ");
				System.out.flush();
				try {
					geogebra.cas.GeoGebraCAS.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// load gui jar
				System.out.print("loading geogebra_gui.jar... ");
				System.out.flush();
				try {
					geogebra.gui.DefaultGuiManager.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				}
				System.out.flush();
				
				// force loading properties
				System.out.print("loading geogebra_properties.jar... ");
				System.out.flush();
				try {
					Object url = GeoGebraAppletPreloader.class.getResource("/geogebra/properties/plain.properties"); 
					if (url != null)
						System.out.println("done");
					else
						System.out.println("not found");
				} catch (Exception e) {
					System.out.println("not found");
				}
				System.out.flush();
				
				// load export jar
				System.out.print("loading geogebra_export.jar... ");
				System.out.flush();
				try {
					geogebra.export.WorksheetExportDialog.class.getClass();
					System.out.println("done");
				} catch (Exception e) {
					System.out.println("failed");
				}
				System.out.flush();
			}
		};
		jarLoader.start();
	}
}
