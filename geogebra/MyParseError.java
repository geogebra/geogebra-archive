/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * MyError.java
 *
 * Created on 04. Oktober 2001, 09:29
 */

package geogebra;

/**
 *
 * @author  Markus
 * @version 
 */
public class MyParseError extends MyError {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates new MyError */
    public MyParseError(Application app, String errorName) {        
        super(app, errorName);
    }
    
    public MyParseError(Application app, String [] str) {
        // set localized message        
        super(app, str);
    }        
        
    public String getLocalizedMessage() {       
        return  app.getError("InvalidInput") + " :\n" +
                super.getLocalizedMessage();
    }

}
