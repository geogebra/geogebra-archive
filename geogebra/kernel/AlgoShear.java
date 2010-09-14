/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoApplyMatrix.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.Matrix.GgbVector;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoShear extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MatrixTransformable out;   
    private GeoElement geoIn, geoOut; 
    private GeoLine l;
    private GeoNumeric num;
    
  
    /**
     * Creates new apply matrix algorithm
     * @param cons
     * @param label
     * @param in
     * @param l
     * @param num
     */
    public AlgoShear(Construction cons, String label, MatrixTransformable in, GeoLine l,GeoNumeric num) {
        super(cons);
        //this.in = in;      
        this.l = l;
        this.num = num;
                  
        geoIn = in.toGeoElement();
        out = (MatrixTransformable) geoIn.copy();               
        geoOut = out.toGeoElement();                       
        setInputOutput();
              
        cons.registerEuclidianViewAlgo(this);
        
        compute();          
        geoOut.setLabel(label);
    }           
    
    public String getClassName() {
        return "AlgoShear";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[2] = geoIn; 
        input[0] = l;
        input[1] = num;
        
        setOutputLength(1);        
        setOutput(0,geoOut);        
        setDependencies(); // done by AlgoElement
    }           
    
    /**
     * Returns the resulting element
     * @return resulting element
     */
    public GeoElement getResult() { 
    	return geoOut; 
    }       
   

    protected final void compute() {
        geoOut.set(geoIn);
        
        //matrix.add
        Translateable tranOut = (Translateable) out;
        double qx, qy; 
        if (Math.abs(l.x) > Math.abs(l.y)) {
            qx = l.z / l.x;
            qy = 0.0d;
        } else {
            qx = 0.0d;
            qy = l.z / l.y;
        }
        double s=-l.x/Math.sqrt(l.x*l.x+l.y*l.y);
        double c=l.y/Math.sqrt(l.x*l.x+l.y*l.y);
        double n=num.getValue();
        // translate -Q
        tranOut.translate(new GgbVector(qx, qy,0));
        
        
        out.matrixTransform(1-c*s*n,c*c*n,-s*s*n,1+s*c*n);
         
        tranOut.translate(new GgbVector(-qx, -qy,0));
         //c -s 1 n = c (cn-s) c s = (1-csn) (ccn)  
         //s c  0 1   s (sn+c) -s c  (-ssn) (1+scn)
    }       
    

}

