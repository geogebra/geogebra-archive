/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.util;

import geogebra.Application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Markus Hohenwarter
 */
public class CopyURLToFile extends Thread {

	private URL src;
	private File dest;
	private Application app;

	/**     
		 * 	Copies the file associated with the URL argument to the local
		*     hard drive with the given filename showing a progress dialog in app
		*/
	public CopyURLToFile(Application app, URL src, File dest) {
		this.src = src;
		this.dest = dest;
		this.app = app;
	}

	public void run() {
		copyURLToFile(app, src, dest);
	}
	
	/*
	void doUpdate(Runnable r) {
		try {
			SwingUtilities.invokeAndWait(r);
		}
		catch (InvocationTargetException e1) {
			System.err.println(e1);
		}
		catch (InterruptedException e2) {
			System.err.println(e2);
		}
	}*/
	
	/**
	 * Returns success state.
	 */
	public static boolean copyURLToFile(Application app, URL src, File dest) {		
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {			
			URLConnection connection = src.openConnection();			
			in = new BufferedInputStream(connection.getInputStream());			
			out = new FileOutputStream(dest);			
			
			byte[] buf = new byte[4096];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);			
			}
			out.close();
			dest.setLastModified(connection.getLastModified());
			in.close();		
			return true;
		} 
		catch (Exception e) {
			try {
				in.close();
				out.close();
			} catch (Exception ex) {}
			dest.delete();
			
			e.printStackTrace();
			app.showError(app.getError("SaveFileFailed") + ": " + dest);
			return false;
		}
	}

}
