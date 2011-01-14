/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math.stat.Frequency;




public class AlgoFrequencyTable extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private GeoBoolean showRelative; //input
	private GeoList table; //output	

	private GeoList[] geoLists;

	private StringBuffer sb = new StringBuffer();

	AlgoFrequencyTable(Construction cons, String label, GeoList geoList, GeoBoolean showRelative) {
		this(cons, geoList, showRelative);
		table.setLabel(label);
	}

	AlgoFrequencyTable(Construction cons, GeoList geoList, GeoBoolean showRelative) {
		super(cons);
		this.geoList = geoList;
		this.showRelative = showRelative;

		table = new GeoList(cons);

		setInputOutput();
		compute();

	}

	public String getClassName() {
		return "AlgoFrequencyTable";
	}

	protected void setInputOutput(){

		input = new GeoElement[showRelative == null ? 1 : 2];
		input[0] = geoList;

		if (showRelative != null)
			input[1] = showRelative;


		output = new GeoElement[1];
		output[0] = table;
		setDependencies(); // done by AlgoElement
	}

	GeoList getResult() {
		return table;
	}



	protected final void compute() {
		int size = geoList.size();

		if (!geoList.isDefined() ||  size == 0) {
			table.setUndefined();
			// return empty list
			//table.setDefined(true);
			//table.clear();
			return;
			//throw new MyError(app, app.getError("InvalidInput"));   		
		}


		Frequency f = new Frequency();
		if(geoList.getElementType() == GeoElement.GEO_CLASS_TEXT 
				|| geoList.getElementType() == GeoElement.GEO_CLASS_NUMERIC ){	
			for (int i=0 ; i<geoList.size() ; i++){
				if(geoList.getElementType() == GeoElement.GEO_CLASS_TEXT)
					f.addValue(((GeoText)geoList.get(i)).toValueString());
				if(geoList.getElementType() == GeoElement.GEO_CLASS_NUMERIC)
					f.addValue(((GeoNumeric)geoList.get(i)).getDouble());
			}

			Iterator itr = f.valuesIterator();
			while(itr.hasNext()) {

				GeoList row = new GeoList(cons);

				if(geoList.getElementType() == GeoElement.GEO_CLASS_TEXT){
					String s = (String) itr.next();
					GeoText text = new GeoText(cons);
					text.setTextString(s);
					row.add(text);
					row.add(new GeoNumeric(cons,f.getCount((Comparable)s)));
					if(input.length == 2 && showRelative.getBoolean())
						row.add(new GeoNumeric(cons,f.getPct((Comparable)s)));
				}

				if(geoList.getElementType() == GeoElement.GEO_CLASS_NUMERIC){
					Double n = (Double) itr.next();
					row.add(new GeoNumeric(cons,n));
					row.add(new GeoNumeric(cons,f.getCount((Comparable)n)));
					if(input.length == 2 && showRelative.getBoolean())
						row.add(new GeoNumeric(cons,f.getPct((Comparable)n)));
				}

				table.add(row);

			} 

		}
		//	System.out.println(f.toString());


		for (int i=0 ; i<geoList.size() ; i++){
			//table.add(geoList.get(i).copyInternal(cons));
		}
	}

}
