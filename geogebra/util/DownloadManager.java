/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {
		
	// size of byte buffer to download / copy files
	private static final int BYTE_BUFFER_SIZE = 65536;
	
	/**
	 * Copies or downloads url to destintation file.
	 */
	public static void copyURLToFile(URL src, File dest) throws Exception {		
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {			
			// open input stream to src URL
			URLConnection srcConnection = src.openConnection();
			in = new BufferedInputStream(srcConnection.getInputStream());
			if (in == null)
				throw new NullPointerException("URL not found: " + src);

			// create output file
			out = new FileOutputStream(dest);

			byte[] buf = new byte[BYTE_BUFFER_SIZE];
			int len;			
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);						
			}
			out.close();
			dest.setLastModified(srcConnection.getLastModified());
			in.close();		
		} 
		catch (Exception e) {
			try {
				in.close();
				out.close();
			} catch (Exception ex) {}	
			//dest.delete();
			
			throw e;
		}
	}
	
	private static String tempDir = null;
	
	public static String getTempDir() {
		
		if (tempDir == null) {
			tempDir = System.getProperty("java.io.tmpdir");
			
			// Mac OS doesn't add "/" at the end of directory path name
			if (!tempDir.endsWith(File.separator)) 
				tempDir += File.separator;			
		}
		
		return tempDir;
		
	}
	
	
	
   
}
