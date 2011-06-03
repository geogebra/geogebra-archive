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

import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.NumberValue;



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
    
  
    /**
     * Creates new apply matrix algorithm
     * @param cons
     * @param label
     * @param in
     * @param matrix
     */
    public AlgoApplyMatrix(Construction cons, String label, GeoElement in, GeoList matrix) {
    	this(cons,in,matrix);          
        geoOut.setLabel(label);
    }           
    
    /**
     * Creates new apply matrix algorithm
     * @param cons
     * @param in
     * @param matrix
     */
    public AlgoApplyMatrix(Construction cons, GeoElement in, GeoList matrix) {
        super(cons);
        //this.in = in;      
        this.matrix = matrix;
        

              
        geoIn = in.toGeoElement();
        if(in instanceof GeoPolygon|| geoIn instanceof GeoPolyLine){
	        geoOut = ((GeoPolygon)in).copyInternal(cons);
	        out = (MatrixTransformable) geoOut;
        }
        else if(geoIn.isGeoList()){
        	geoOut = new GeoList(cons);
        }
        else if(geoIn instanceof GeoFunction){
        	out = new GeoCurveCartesian(cons);
        	geoOut = (GeoElement)out;
        }
        else{
        	out = (MatrixTransformable) geoIn.copy();               
        	geoOut = out.toGeoElement();
        }                    
        setInputOutput();
              
        cons.registerEuclidianViewAlgo(this);
        
        compute();          
    }           
    
    public String getClassName() {
        return "AlgoApplyMatrix";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[1] = geoIn; 
        input[0] = matrix;
        
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
    	if(geoIn.isGeoList())
    		return;
    	if(geoIn.isGeoFunction()){
    		((GeoFunction)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut);
    	}
    	else geoOut.set(geoIn); 
        MyList list = matrix.getMyList();
		
		if (list.getMatrixCols() != list.getMatrixRows() || list.getMatrixRows() < 2 || list.getMatrixRows() > 3) {
			geoOut.setUndefined();
			return;
		}
		 
		double a,b,c,d,e,f,g,h,i;
		if(list.getMatrixRows() < 3){
		a = ((NumberValue)(MyList.getCell(list,0,0).evaluate())).getDouble();
		b = ((NumberValue)(MyList.getCell(list,1,0).evaluate())).getDouble();
		c = ((NumberValue)(MyList.getCell(list,0,1).evaluate())).getDouble();
		d = ((NumberValue)(MyList.getCell(list,1,1).evaluate())).getDouble();
		out.matrixTransform(a,b,c,d);	
		}
		else{
			a = ((NumberValue)(MyList.getCell(list,0,0).evaluate())).getDouble();
			b = ((NumberValue)(MyList.getCell(list,1,0).evaluate())).getDouble();
			c = ((NumberValue)(MyList.getCell(list,2,0).evaluate())).getDouble();
			d = ((NumberValue)(MyList.getCell(list,0,1).evaluate())).getDouble();
			e = ((NumberValue)(MyList.getCell(list,1,1).evaluate())).getDouble();
			f = ((NumberValue)(MyList.getCell(list,2,1).evaluate())).getDouble();
			g = ((NumberValue)(MyList.getCell(list,0,2).evaluate())).getDouble();
			h = ((NumberValue)(MyList.getCell(list,1,2).evaluate())).getDouble();
			i = ((NumberValue)(MyList.getCell(list,2,2).evaluate())).getDouble();
			out.matrixTransform(a,b,c,d,e,f,g,h,i);			
		}
        

    }       
    

}
