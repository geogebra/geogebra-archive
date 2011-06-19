/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * 
 * @author  
 * @version 
 */
public class AlgoAxisStepX extends AlgoElement {

	private static final long serialVersionUID = 1L;
    protected GeoNumeric num;     // output          
        
    public AlgoAxisStepX(Construction cons, String label) {
    	super(cons);
        
        num = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
            
        num.setLabel(label);
        
        // ensure we get updates
        cons.registerEuclidianViewAlgo(this);
   }   
    
	public String getClassName() {
		return "AlgoAxisStepX";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[0];
        
        setOutputLength(1);        
        setOutput(0,num);        
        setDependencies(); // done by AlgoElement
    }    
	
    protected GeoNumeric getResult() { return num; }        
 
    public void euclidianViewUpdate() {

    	compute();
    	
    	// update num and all dependent elements
    	num.updateCascade(); 
   	
    }
    
    final public boolean wantsEuclidianViewUpdate() {
    	return true;
    }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {  
    	double axisSteps[] = kernel.getApplication().getEuclidianView().getGridDistances();
    	num.setValue(axisSteps[0]);
    }         
}
