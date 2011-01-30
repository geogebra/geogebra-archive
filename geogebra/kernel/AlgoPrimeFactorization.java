/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Prime factors of a number. Adapted from AlgoPrimeFactors
 * @author Zbynek Konecny
 */

public class AlgoPrimeFactorization extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue num; //input
    private GeoList outputList; //output	    
    
    private static double LARGEST_INTEGER=9007199254740992d;

    
    /**
     * Creates new factorization algo 
     * @param cons
     * @param label
     * @param num Number to factorize
     */
    public AlgoPrimeFactorization(Construction cons, String label, NumberValue num) {
        super(cons);        
        this.num = num;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoPrimeFactorization";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = num.toGeoElement();

        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the list of points (prime,exponent)
     * @return the list of points (prime,exponent)
     */
    public GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	

    	double n = Math.round(num.getDouble());
    	
    	if (n == 1) {
    		outputList.clear();
    		outputList.setDefined(true);
    		return;
    	}
    	
    	if (n < 2 || n > LARGEST_INTEGER) {
    		outputList.setUndefined();
    		return;
    	}
       
    	      
       outputList.setDefined(true);
       outputList.clear();
       
       int count = 0;

		for (int i = 2; i <= n / i; i++) {
			int exp = 0;
			while (n % i == 0) {
				exp++;
				n /= i;
			}
			if(exp > 0){
				setListElement(count++, i,exp);
			}
		}
		if (n > 1) {
			setListElement(count++, n,1);
		}
       
    }
    
    // copied from AlgoInterationList.java
    // TODO should it be centralised?
    private void setListElement(int index, double value, double exp) {
    	GeoPoint listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoPoint) outputList.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoPoint(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	listElement.setCoords(value, exp,1);
    }    

}
