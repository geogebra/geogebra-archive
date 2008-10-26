package geogebra;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Opens connections to all jar URLs. We do this in the background very early as
 * opening connections to a webserver may take a long time.
 */
public class JarConnectionManager {
	
	private URL codebase;
	
	private URLConnection [] connections;
	private BufferedInputStream [] inputStreams;

	private static JarConnectionManager singleton;
	
	/**
	 * Returns the singleton instance of JARConnectionManager
	 */
	public synchronized static JarConnectionManager getSingleton() {
		if (singleton == null) {
			singleton = new JarConnectionManager();
		}		
		return singleton;
	}
	
	
	private JarConnectionManager() {		
		// init code base for connections
		initCodeBase();		
		
		connections = new URLConnection[JarManager.JAR_FILES.length];
		inputStreams = new BufferedInputStream[JarManager.JAR_FILES.length];					
	}
	
	public void initConnectionsInBackground() {
		// open all connections in background
		Thread runner = new Thread() {
			public void run() {					
				// start with geogebra_main.jar file, we need this early
				for (int i=1; i < JarManager.JAR_FILES.length; i++) {
					try {
						// open connection and create input stream for i-th jar file
						initURLConnection(i);	
						
						// give other threads a chance to get a hold of the connections
						Thread.sleep(100);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
				
				// don't forget geogebra.jar file
				try {					
					getInputStream(JarManager.JAR_FILE_GEOGEBRA);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}				
			}
		};
		runner.start();
	}
	
	/**
	 * Initializes the codebase URL where the geogebra.jar file is located.
	 */
	private void initCodeBase() {	
		try {
			String path = JarManager.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
			// remove "geogebra.jar" from end of codebase string
			if (path.endsWith( JarManager.JAR_FILES[0])) 
				path = path.substring(0, path.length() -  JarManager.JAR_FILES[0].length());
			
			// set codebase
			codebase = new URL(path);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
					
	
	/**
	 * Opens a connection to the given jar file.
	 * 
	 * @return input stream to jar file
	 */
	public synchronized BufferedInputStream getInputStream(int jarFileIndex) throws Exception {
		if (connections[jarFileIndex] == null) {
			initURLConnection(jarFileIndex);
		}
				
		return inputStreams[jarFileIndex];						
	}
	
	public synchronized URLConnection getURLConnection(int jarFileIndex) throws Exception {
		if (connections[jarFileIndex] == null) {	
			initURLConnection(jarFileIndex);
		}
		
		return connections[jarFileIndex];						
	}
	
	private synchronized void initURLConnection(int jarFileIndex) throws Exception {
		if (connections[jarFileIndex] == null) {	
			URL src = new URL(codebase, JarManager.JAR_FILES[jarFileIndex]);
			connections[jarFileIndex] = src.openConnection();
			inputStreams[jarFileIndex] = new BufferedInputStream(
					connections[jarFileIndex].getInputStream());								
		}
	}


	public final URL getCodebase() {
		return codebase;
	}				
}
