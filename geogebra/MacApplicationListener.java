package geogebra;

import java.io.File;

public class MacApplicationListener implements com.apple.eawt.ApplicationListener  {
	
	public static void initMacApplicationListener() {
		com.apple.eawt.Application app = new com.apple.eawt.Application();
		app.addApplicationListener(new MacApplicationListener());
	}
	
	/**
	 * Gets active instance of GeoGebra window. This method waits
	 * until an active instance was created by GeoGebra.main()
	 * @return
	 */
	private synchronized GeoGebra getGGBInstance() {
		GeoGebra wnd = null;
		while (wnd == null) {
			try {
				Thread.sleep(100);			
				wnd = GeoGebra.getActiveInstance();	
			} catch (Exception e) {
				Application.debug("MacApplicationListener.getGGBInstance(): " + e.getMessage());
				wnd = null;
			}
		}
		return wnd;
	}
	
	public synchronized void handleQuit(com.apple.eawt.ApplicationEvent ev) {
		// quit all frames
		Application app = getGGBInstance().getApplication();					
		app.exitAll();	
	}			
						
	public synchronized void handleAbout(com.apple.eawt.ApplicationEvent event) {
		 event.setHandled(true);
         Application app = getGGBInstance().getApplication();	
         app.showAboutDialog();
     }

	public synchronized void handleOpenFile(com.apple.eawt.ApplicationEvent ev) {	
		Application.debug("handleOpenFile event, filename: " + ev.getFilename());
		
		// open file			
		String fileName = ev.getFilename();		
											
		if (fileName != null) {				
			File openFile = new File(fileName);
			if (openFile.exists()) {
				// get application instance
				GeoGebra ggb = getGGBInstance();
				Application app = ggb.getApplication();
				
				// open file 
				File [] files = { openFile };
				boolean openInThisWindow = app.getCurrentFile() == null;
				app.doOpenFiles(files, openInThisWindow);
				
				// make sure window is visible
				if (openInThisWindow)
					ggb.setVisible(true);							
			}
		}
	}
	
	public synchronized void handlePrintFile(com.apple.eawt.ApplicationEvent event) {
		Application.debug("handlePrintFile event, filename: " + event.getFilename());
		
		handleOpenFile(event);
		getGGBInstance().getApplication().showPrintPreview();
	}

	public synchronized void handleOpenApplication(com.apple.eawt.ApplicationEvent ev) {
		Application.debug("handleOpenApplication event, filename: " + ev.getFilename());
		
		// open file			
		String fileName = ev.getFilename();		
		if (fileName != null) {
			handleOpenFile(ev);
		} else {
			GeoGebra wnd = getGGBInstance();
			if (!wnd.isShowing())
				wnd.setVisible(true);
		}
	}

	public synchronized void handlePreferences(com.apple.eawt.ApplicationEvent arg0) {
		Application.debug("handlePreferences event, filename: " + arg0.getFilename());
	}

	public synchronized void handleReOpenApplication(com.apple.eawt.ApplicationEvent arg0) {
		Application.debug("handleReOpenApplication event, filename: " + arg0.getFilename());
	}
		
	
}
