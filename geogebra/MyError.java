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
public class MyError extends java.lang.Error {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Application app;
    private String [] strs;
    
    /** Creates new MyError */
    public MyError(Application app, String errorName) {
        // set localized message
        super(errorName);
        this.app = app;
    }
    
    public MyError(Application app, String [] strs) {
        this.app = app;
        // set localized message        
        this.strs = strs;
    }
        
    public String getLocalizedMessage() {              
        if (strs == null) 
            return app.getError(getMessage());
        else {
            StringBuffer sb = new StringBuffer();
            sb.append(app.getError(strs[0]) + ":\n");
            for (int i = 1; i < strs.length; i++) {
                sb.append(app.getError(strs[i]) + " ");
            }
            return sb.toString();
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(getClass().toString() + ": ");
        if (strs == null) 
            sb.append(app.getError(getMessage()));
        else {            
            for (int i = 0; i < strs.length; i++) {
                sb.append(app.getError(strs[i]) + " : ");
            }            
        }
        return sb.toString();
    }

}
