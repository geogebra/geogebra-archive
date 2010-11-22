/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ValidExpression;

/**
 * Makes it possible to switch between the Extended form and the Input form (Exactly the way the user entered it)
 * subclasses must call super.toString() and provide the "normal" or extended form with .valueString().
 *
 */
public abstract class GeoUserInputElement extends GeoElement {
	
	private ValidExpression userInput;
	private boolean inputForm;

	public GeoUserInputElement(Construction c) {
		super(c);
	}
	
	public GeoUserInputElement(Construction c,ValidExpression userInput) {
		super(c);
		this.userInput=userInput;
	}
	
	public void setInputForm(){
		inputForm=true;
	}
	
	public void setExtendedForm(){
		inputForm=false;
	}
	
	public boolean isInputForm() {
		return inputForm;
	}
	
	public void setUserInput(ValidExpression input){
		userInput=input;
	}

	public String toString(){
		if (inputForm&&userInput!=null){
			return label+": "+userInput.toValueString();
		}else{
			return label+": "+toValueString();
		}
	}
	
	public void set(GeoElement geo){
		if (!(geo instanceof GeoUserInputElement))
			return;
		userInput=((GeoUserInputElement)geo).userInput;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<userinput show=\"");
		sb.append(inputForm);
		if (isIndependent()){ //if dependent we save the expression somewhere else anyway
			sb.append("\" value=\"");
			sb.append(userInput);
		}
		sb.append("\" />\n");
	}
	
	

}
