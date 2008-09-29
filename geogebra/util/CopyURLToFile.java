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

/**
 * @author Markus Hohenwarter
 */
public class CopyURLToFile  {

	/**
	 * Copies or downloads url to destintation file.
	 */
	public static void copyURLToFile(URL src, File dest) throws Exception {		
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

}
