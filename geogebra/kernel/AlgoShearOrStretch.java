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

import geogebra.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoShearOrStretch extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MatrixTransformable out;   
    private GeoElement geoIn, geoOut; 
    private GeoVec3D l;
    private NumberValue num;
    private boolean shear;
    
  
    /**
     * Creates new shear or stretch algorithm
     * @param cons
     * @param in
     * @param l
     * @param num
     * @param shear shear if true, stretch otherwise
     */
    public AlgoShearOrStretch(Construction cons, GeoElement in, GeoVec3D l,NumberValue num,boolean shear) {
        super(cons);
        this.shear = shear;      
        this.l = l;
        this.num = num;
         
        geoIn = in;
        if(in instanceof GeoPolygon || in instanceof GeoPolyLine){
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
        if(shear)return "AlgoShear";
        return "AlgoStretch";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[num == null?2:3];
        input[0] = geoIn; 
        input[1] = l;
        if(num!=null)
        	input[2] = num.toGeoElement();
        
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
    	if(geoIn.isGeoList()){
    		return;
    	}
    	if(geoIn.isGeoFunction()){
    		((GeoFunction)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut);
    	}
    	else geoOut.set(geoIn); 
        
        //matrix.add
        Translateable tranOut = (Translateable) out;
        double qx=0.0d, qy=0.0d,s,c;
        double n=Math.sqrt(l.x*l.x+l.y*l.y);
        if(l instanceof GeoLine){
        if (Math.abs(l.x) > Math.abs(l.y)) {
            qx = l.z / l.x;            
        } else {            
            qy = l.z / l.y;
        }
        s=-l.x/n;
        c=l.y/n;
        n=num.getDouble();
        }
        else{
        	GeoPoint sp = ((GeoVector)l).getStartPoint();
        	if(sp!=null){
        	 qx = -((GeoVector)l).getStartPoint().x;
        	 qy = -((GeoVector)l).getStartPoint().y;
        	}        	
        	 c=-l.y/n;
             s=l.x/n;             
        }        
        	
        // translate -Q
        tranOut.translate(new Coords(qx, qy,0));
        
        if(shear)
        	out.matrixTransform(1-c*s*n,c*c*n,-s*s*n,1+s*c*n);
        else
        	out.matrixTransform(c*c+s*s*n,c*s*(1-n),c*s*(1-n),s*s+c*c*n);        
        tranOut.translate(new Coords(-qx, -qy,0));        
    }       
    

}

