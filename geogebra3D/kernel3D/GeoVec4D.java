/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.Traceable;

/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
public abstract class GeoVec4D extends GeoVec
implements Traceable {
       

    public GeoVec4D(Construction c) {  super(c);  }
    public GeoVec4D(Construction c, int n) {super(c,n);}  

    /** Creates new GeoVec4D with coordinates (x,y,z,w) and label */
    public GeoVec4D(Construction c, double x, double y, double z, double w) {  
     	super(c,new double[] {x,y,z,w});   
		//double[] coords = {x,y,z,w};
		//setCoords(coords);
     	
       
    }                 
    
    /** Copy constructor */
    public GeoVec4D(Construction c, GeoVec4D v) {   
    	super(c); 	
        set(v);
    }
    
    
    // TODO: polar to spherical 
    // POLAR or CARTESIAN mode    
    final public boolean isPolar() { return toStringMode == Kernel.COORD_POLAR; }
    public int getMode() { return toStringMode;  }
    public void setMode(int mode ) {
        toStringMode = mode;
    }        
    
    public void setPolar() { toStringMode = Kernel.COORD_POLAR; }
    public void setCartesian() { toStringMode = Kernel.COORD_CARTESIAN; }
    
     
	
    /**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        //TODO sb.append(super.getXMLtags());
        
        sb.append("\t<coords");
                sb.append(" x=\"" + v.get(1) + "\"");
                sb.append(" y=\"" + v.get(2) + "\"");
                sb.append(" z=\"" + v.get(3) + "\"");
                sb.append(" w=\"" + v.get(4) + "\"");
        sb.append("/>\n");
        
        return sb.toString();
    }
    
    
}
