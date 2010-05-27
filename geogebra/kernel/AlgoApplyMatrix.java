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



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoApplyMatrix extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MatrixTransformable out;   
    private GeoElement geoIn, geoOut; 
    private GeoList matrix;
    
  
    
    AlgoApplyMatrix(Construction cons, String label, MatrixTransformable in, GeoList matrix) {
        super(cons);
        //this.in = in;      
        this.matrix = matrix;
        

              
        geoIn = in.toGeoElement();
        out = (MatrixTransformable) geoIn.copy();               
        geoOut = out.toGeoElement();                       
        setInputOutput();
              
        cons.registerEuclidianViewAlgo(this);
        
        compute();          
        geoOut.setLabel(label);
    }           
    
    protected String getClassName() {
        return "AlgoApplyMatrix";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[1] = geoIn; 
        input[0] = matrix;
        
        output = new GeoElement[1];        
        output[0] = geoOut;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { 
    	return geoOut; 
    }       

    final public boolean wantsEuclidianViewUpdate() {
        return geoIn.isGeoImage();
    }

    protected final void compute() {
        geoOut.set(geoIn);
        
        out.matrixTransform(matrix);
        

    }       
    

}
