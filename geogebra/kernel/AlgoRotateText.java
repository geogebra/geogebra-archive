/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;
import geogebra.main.MyError;



public class AlgoRotateText extends AlgoElement {

	private static final long serialVersionUID = 1L;
    private GeoText text; //output	
    private GeoText args; //input	
    private GeoNumeric angle; // input
     
    private StringBuffer sb = new StringBuffer();
    
    AlgoRotateText(Construction cons, String label, GeoText args, GeoNumeric angle) {
    	this(cons,  args, angle);
        text.setLabel(label);
    }

    AlgoRotateText(Construction cons, GeoText args, GeoNumeric angle) {
        super(cons);
        this.args = args;
        this.angle = angle;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
        
    }

    public String getClassName() {
        return "AlgoRotateText";
    }

    protected void setInputOutput(){
	    input = new GeoElement[2];
	    input[0] = args;
	    input[1] = angle;


        output = new GeoElement[1];
        output[0] = text;
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    protected final void compute() {
    	if (!args.isDefined() || !angle.isDefined() || angle.isInfinite()) {
    		text.setTextString("");
    		return;
    	}
    	
    	sb.setLength(0);
    	sb.append("\\rotatebox{");
    	sb.append(angle.getValue()*180/Math.PI); // convert to degrees
    	sb.append("}{");
    	sb.append(args.getTextString());
    	sb.append("}");

    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }

}
