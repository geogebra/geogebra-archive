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
import geogebra.main.Application;

/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined by the type of
 * the first list element. If the list is initially empty, a GeoNumeric object
 * is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListElement extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; // input
	private NumberValue num;
	private NumberValue[] num2 = null; // input
	private GeoElement numGeo, num2Geo = null;
	private GeoElement element; // output
	private boolean shortSyntax;

	/**
	 * Creates new labeled element algo
	 * 
	 * @param cons
	 * @param label
	 * @param geoList
	 * @param num
	 */
	public AlgoListElement(Construction cons, String label, GeoList geoList,
			NumberValue num) {
		super(cons);
		this.geoList = geoList;
		this.num = num;
		numGeo = num.toGeoElement();

		int initIndex = Math.max(0, (int) Math.round(num.getDouble()) - 1);

		// init return element as copy of initIndex list element
		if (geoList.size() > initIndex) {
			// create copy of initIndex GeoElement in list
			element = geoList.get(initIndex).copyInternal(cons);
		}

		// if not enough elements in list:
		// init return element as copy of first list element
		else if (geoList.size() > 0) {
			// create copy of first GeoElement in list
			element = geoList.get(0).copyInternal(cons);
		}

		// desperate case: empty list
		else {
			element = new GeoNumeric(cons);
		}

		setInputOutput();
		compute();
		element.setLabel(label);
	}

	/**
	 * Creates new unlabeed element algo
	 * 
	 * @param cons
	 * @param label
	 * @param geoList
	 * @param num2
	 * @param num
	 */
	public AlgoListElement(Construction cons, String label, GeoList geoList,
			NumberValue[] num2, boolean shortSyntax) {
		super(cons);
		this.shortSyntax = shortSyntax;
		this.geoList = geoList;		
		this.num2 = num2;
		

		element = null;
		GeoElement current = geoList;
		int k = 0;
		try {
			do {
				int initIndex = Math.max(0, (int) Math.round(num2[k]
						.getDouble()) - 1);
				// init return element as copy of initIndex list element
				if (((GeoList)current).size() > initIndex) {
					// create copy of initIndex GeoElement in list
					current = ((GeoList)current).get(initIndex);
				}

				// if not enough elements in list:
				// init return element as copy of first list element
				else if (geoList.size() > 0) {
					// create copy of first GeoElement in list
					current = ((GeoList)current).get(0);
				}
				k++;
			} while (current.isGeoList() && k < num2.length);
			element = current.copyInternal(cons);
		} catch (Exception e) {
			Application.debug("error initialising list");
		}
		;

		// desperate case: empty list, or malformed 2D array
		if (element == null)
			element = new GeoNumeric(cons);

		setInputOutput();
		compute();
		element.setLabel(label);
	}

	public String getClassName() {
		return "AlgoListElement";
	}

	protected void setInputOutput() {

		if (num2 == null) {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = numGeo;
		} else {
			input = new GeoElement[num2.length+1];
			input[0] = geoList;
			for(int i=0;i<num2.length;i++){				
				input[i+1] = num2[i].toGeoElement();
			}
		}

		setOutputLength(1);
		setOutput(0, element);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns chosen element
	 * 
	 * @return chosen element
	 */
	public GeoElement getElement() {
		return element;
	}

	protected final void compute() {
		if ((numGeo != null && !numGeo.isDefined()) || !geoList.isDefined()) {
			element.setUndefined();
			return;
		}

		if (num2 == null) {
			// index of wanted element
			int n = (int) Math.round(num.getDouble()) - 1;
			if (n >= 0 && n < geoList.size()) {
				GeoElement nth = geoList.get(n);
				// check type:
				if (nth.getGeoClassType() == element.getGeoClassType()) {
					element.set(nth);

				} else {
					element.setUndefined();
				}
			} else {
				element.setUndefined();
			}

		} else {

			for(int k=0;k<num2.length;k++)
				if(!num2[k].toGeoElement().isDefined()) {
				element.setUndefined();
				return;
			}

			
			int m = (int) Math.round(num2[num2.length-1].getDouble()) - 1;
			GeoElement current = geoList;
			for(int k=0;k<num2.length-1;k++){
				int index = (int)Math.round(num2[k].getDouble()-1);
				if(index >= 0 && current.isGeoList() && index < ((GeoList)current).size())
					current = ((GeoList)current).get(index);
				else{element.setUndefined(); return;}
			}
			

				GeoList list = ((GeoList) current);

				if (m>=0 && m < list.size())
					current = list.get(m);
				else {
					element.setUndefined();
					return;
				}

				// check type:
				if (current.getGeoClassType() == element.getGeoClassType()) {
					element.set(current);

				} else {
					element.setUndefined();
				}
			

		}
	}
	
	public String getCommandDescription(boolean real) {
		if(!shortSyntax)
			return super.getCommandDescription(real);
              
        
            sb.setLength(0);
            
            
            int length = input.length;
                  
            sb.append(geoList.getLabel()+"(");
            // input
            sb.append(real?input[1].getRealLabel():input[1].getLabel()); // Michael Borcherds 2008-05-15 added input.length>0 for Step[]
            for (int i = 2; i < length; ++i) {
                sb.append(", ");
                sb.append(real? input[i].getRealLabel():input[i].getLabel());
            }
            sb.append(")");
            return sb.toString();           
              
    }

}
