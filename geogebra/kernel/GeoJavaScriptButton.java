/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */


package geogebra.kernel;

import geogebra.main.Application;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * 
 * @author Michael
 * @version
 */
public class GeoJavaScriptButton extends GeoElement implements AbsoluteScreenLocateable {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean buttonFixed = false;
	
	private String script = "";
	
	public GeoJavaScriptButton(Construction c) {
		super(c);			
		setEuclidianVisible(true);
	}

	protected String getClassName() {
		return "GeoJavaScriptButton";
	}
	
    protected String getTypeString() {
		return "JavaScriptButton";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_JAVASCRIPT_BUTTON;
    }
    
	public GeoElement copy() {
		return this;
	}
	
	public boolean isGeoJavaScriptButton() {
		return true;
	}

	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return true;
	}

	public boolean showInAlgebraView() {		
		return false;
	}
	
	public boolean isFixable() {
		return true;
	}

	public void set(GeoElement geo) {
	}

	final public void setUndefined() {
	}
	
	final public void setDefined() {
	}

	final public boolean isDefined() {
		return true;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	final public String toValueString() {
		return "";
	}
	
	final public String toString() {
		StringBuffer sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		return sbToString.toString();
	}
	
	private StringBuffer sbToString;
	private StringBuffer getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuffer();
		return sbToString;
	}

	
	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<value val=\"");
		sb.append(script);
		sb.append("\"/>\n");				
				
		sb.append(getXMLvisualTags(isIndependent()));
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		
		// checkbox fixed
		if (buttonFixed) {			
			sb.append("\t<checkbox fixed=\"");
			sb.append(buttonFixed);
			sb.append("\"/>\n");	
		}
		
		return sb.toString();
	}	

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	

	public double getRealWorldLocX() {
		return 0;
	}

	public double getRealWorldLocY() {		
		return 0;
	}

	public boolean isAbsoluteScreenLocActive() {		
		return true;
	}
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	public void setAbsoluteScreenLoc(int x, int y) {		
		if (buttonFixed) return;
		
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {				
	}

	public void setRealWorldLoc(double x, double y) {				
	}

	public final boolean isButtonFixed() {
		return buttonFixed;
	}

	public final void setButtonFixed(boolean buttonFixed) {
		this.buttonFixed = buttonFixed;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		return false;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}
	
	public boolean runScript() {
		
		boolean success = true;
		
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();

            // initialise the JavaScript variable applet so that we can call
            // GgbApi functions, eg ggbApplet.evalCommand()
            Object wrappedOut = Context.javaToJS(app.getGgbApi(), scope);
            ScriptableObject.putProperty(scope, "ggbApplet", wrappedOut);

            // JavaScript to execute
            //String s = "ggbApplet.evalCommand('F=(2,3)')";
            
            // Now evaluate the string we've colected.
            Object result = cx.evaluateString(scope, script, "<cmd>", 1, null);

            // Convert the result to a string and print it.
            //Application.debug("script result: "+(Context.toString(result)));
        } catch (Exception e) {
        	success = false;
        	e.printStackTrace();
        } finally {
            // Exit from the context.
            Context.exit();
        }
        
        return success;
	}

	
}