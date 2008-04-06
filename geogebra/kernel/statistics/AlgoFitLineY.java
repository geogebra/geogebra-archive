/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoLine;
import geogebra.kernel.Construction;

/**
 * FitLineY of a list.
 * adapted from AlgoListMax
 * @author Michael Borcherds
 * @version 14-01-2008
 */

public class AlgoFitLineY extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoLine  g;     // output   

    public AlgoFitLineY(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        g = new GeoLine(cons); 
        setInputOutput(); // for AlgoElement
        
        compute();      
        g.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoFitLineY";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getFitLineY() {
        return g;
    }

    protected final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size <= 1) {
     		g.setUndefined();
    		return;
    	}
    	
    	double sigmax=0;
    	double sigmay=0;
    	double sigmaxx=0;
    	//double sigmayy=0; not needed
    	double sigmaxy=0;
    	
        for (int i=0 ; i<size ; i++)
        {
   		 GeoElement geo = geoList.get(i); 
 		 if (geo.isGeoPoint()) {
 			double xy[] = new double[2];
 			((GeoPoint)geo).getInhomCoords(xy);
 			double x=xy[0];
 			double y=xy[1];
  			sigmax+=x;
  			sigmay+=y;
  			sigmaxx+=x*x;
  			sigmaxy+=x*y;
  			// sigmayy+=y*y; not needed
 		 }
 		 else
 		 {
     		g.setUndefined();
     		return;			
 		 }
        }
        // y on x regression line
        // (y - sigmay / n) = (Sxx / Sxy)*(x - sigmax / n)
        // rearranged to eliminate all divisions
		g.x=size*sigmax*sigmay-size*size*sigmaxy;
		g.y=size*size*sigmaxx-size*sigmax*sigmax;
		g.z=size*sigmax*sigmaxy-size*sigmaxx*sigmay; // (g.x)x + (g.y)y + g.z = 0
    }
    
}
