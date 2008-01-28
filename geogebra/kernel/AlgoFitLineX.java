/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * FitLineY of a list.
 * adapted from AlgoListMax
 * @author Michael Borcherds
 * @version 14-01-2008
 */

public class AlgoFitLineX extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoLine  g;     // output   

    AlgoFitLineX(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        g = new GeoLine(cons); 
        setInputOutput(); // for AlgoElement
        
        compute();      
        g.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoFitLineX";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getFitLineX() {
        return g;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size <= 1) {
     		g.setUndefined();
    		return;
    	}
    	
    	double sigmax=0;
    	double sigmay=0;
    	//double sigmaxx=0; not needed
    	double sigmayy=0; 
    	double sigmaxy=0;
    	
        for (int i=0 ; i<size ; i++)
        {
   		 GeoElement geo = geoList.get(i); 
 		 if (geo.isGeoPoint()) {
 			double x=((GeoPoint)geo).getX();
 			double y=((GeoPoint)geo).getY();
 			double z=((GeoPoint)geo).getZ();
 			x=x/z;
 			y=y/z;
  			sigmax+=x;
  			sigmay+=y;
  			//sigmaxx+=x*x; not needed
  			sigmaxy+=x*y;
  			sigmayy+=y*y; 
 		 }
 		 else
 		 {
     		g.setUndefined();
     		return;			
 		 }
        }
        // x on y regression line
        // (x - sigmax / n) = (Syy / Sxy)*(y - sigmay / n)
        // rearranged to eliminate all divisions
		g.y=size*sigmax*sigmay-size*size*sigmaxy;
		g.x=size*size*sigmayy-size*sigmay*sigmay;
		g.z=size*sigmay*sigmaxy-size*sigmayy*sigmax; // (g.x)x + (g.y)y + g.z = 0
    }
    
}
