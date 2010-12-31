/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class AlgoOrdinal extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoNumeric n;  // input
    protected GeoText text;     // output           
        
    protected AlgoOrdinal(Construction cons, String label, GeoNumeric list) {       
	  super(cons); 
      this.n = list;

      text = new GeoText(cons); 
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      text.setLabel(label);
    }   
  
    public String getClassName() {
        return "AlgoOrdinal";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = n;
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoText getResult() { return text; }        

      
    protected void compute() {
    	
    	if (!n.isDefined()) {
    		text.setTextString("");
    		text.setUndefined();
    		return;
    	}
    	
    	double num = n.getDouble();
    	
    	if (num < 0 || Double.isNaN(num) || Double.isInfinite(num)){
    		text.setTextString("");
    		text.setUndefined();
    		return;   		
    	}
    	
    	text.setTextString(app.getOrdinalNumber((int)num));
    	
    }
}
