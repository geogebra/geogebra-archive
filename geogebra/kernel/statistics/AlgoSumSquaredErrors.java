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
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Construction;


/**
 * Command: SumSquaredErrors[<list>,<function>]
 * Calculates Sum[(y(<list>)-f(x(<list>))^2] for a function f(x) fitted to the list.
 * @author 	Hans-Petter Ulven
 * @version 2010-02-21
 */

public class AlgoSumSquaredErrors extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoFunction function; //input
    private GeoNumeric sse; //output	
    private int size;

    public AlgoSumSquaredErrors(Construction cons, String label, GeoList inputList,GeoFunction function) {
    	this(cons, inputList,function);
        sse.setLabel(label);
    }

    public AlgoSumSquaredErrors(Construction cons, GeoList inputList,GeoFunction function) {
        super(cons);
        this.inputList = inputList;
        this.function=function;
               
        sse = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    protected String getClassName() {
        return "AlgoSumSquaredErrors";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = inputList;
        input[1] = function;

        output = new GeoElement[1];
        output[0] = sse;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getsse() {
        return sse;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  !function.isDefined()) {
    		sse.setUndefined();
    		return;
    	} 
        //Calculate sse:
    	double	errorsum	=	0.0d;
    	GeoElement geo		=	null;
    	GeoPoint  point	=	null;
    	double	x,y,v;
    	for(int i=0;i<size;i++){
    		geo=inputList.get(i);
    		if(geo.isGeoPoint()){
    			point=(GeoPoint)geo;
    			x=point.getX();
    			y=point.getY();
    			v=function.evaluate(x);
    			errorsum+=(v-y)*(v-y);
    		} else{
    			sse.setUndefined();
        		return;   			
    		}//if calculation is possible
    	}//for all points
       
       sse.setValue(errorsum);
      
    }//compute()
}//class AlgoSumSquaredErrors

