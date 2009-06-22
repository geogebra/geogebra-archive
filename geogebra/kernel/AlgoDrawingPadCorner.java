/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

// adapted from AlgoTextCorner by Michael Borcherds 2008-05-10

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.NumberValue;

public class AlgoDrawingPadCorner extends AlgoElement 
implements EuclidianViewAlgo {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private GeoPoint corner;     // output    
    private NumberValue number;
    
    AlgoDrawingPadCorner(Construction cons, String label, NumberValue number) {        
        super(cons);
        this.number = number;
        
              
        corner = new GeoPoint(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        corner.setEuclidianVisible(false);   	// hidden by default
        corner.setLabel(label);                  
        
        cons.registerEuclidianViewAlgo(this);
    
    
    }   
    
    protected String getClassName() {
        return "AlgoDrawingPadCorner";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = number.toGeoElement();
        
        output = new GeoElement[1];
        output[0] = corner;        
        setDependencies(); // done by AlgoElement
    }       
         
    GeoPoint getCorner() { return corner; }        
    
    protected final void compute() {   
    	
		//x1 = x1 / invXscale + xZero;
		//x2 = x2 / invXscale + xZero;

		EuclidianView ev=cons.getApplication().getEuclidianView();
		
		double width=ev.toRealWorldCoordX((double)(ev.getWidth())+1);
		double height=ev.toRealWorldCoordY((double)(ev.getHeight())+1);
		double zeroX=ev.toRealWorldCoordX(-1);
		double zeroY=ev.toRealWorldCoordY(0-1);
		
		switch ((int) number.getDouble())
		{
		case 1:
	    	corner.setCoords(zeroX,height,1.0);
			break;
		case 2:
	    	corner.setCoords(width,height,1.0);
			break;
		case 3:
	    	corner.setCoords(width,zeroY,1.0);
			break;
		case 4:
	    	corner.setCoords(zeroX,zeroY,1.0);
			break;
		default:
			corner.setUndefined();
			break;
				
			
		}
		

    	}    	
    
    final public boolean wantsEuclidianViewUpdate() {
    	return true;
    }
    
    public final void euclidianViewUpdate() {
    	compute();

    	// update output:
    	corner.updateCascade();    
    }
    
    final public String toString() {
        return getCommandDescription();
    }
	
}
