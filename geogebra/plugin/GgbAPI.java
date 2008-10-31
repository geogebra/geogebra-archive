package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import geogebra.ClassPathManipulator;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;


/** 
<h3>GgbAPI - API for PlugLets </h3>
<pre>
   The Api the plugin program can use.
</pre>
<ul><h4>Interface:</h4>
<li>GgbAPI(Allication)      //Application owns it
<li>getApplication()
<li>getKernel()
<li>getConstruction()
<li>getAlgebraProcessor()
<li>getPluginManager()
<li>evalCommand(String)
<li>and the rest of the methods from the Applet JavaScript/Java interface
<li>...
</ul>
@author      H-P Ulven
@version     31.10.08
29.05.08:
    Tranferred applet interface methods (the relevant ones) from GeoGebraAppletBase
*/

public class GgbAPI {

    ///// ----- Properties ----- /////
    private Application         app=                null;   //References ...
    private Kernel              kernel=             null;
    private Construction        construction=       null;
    private AlgebraProcessor    algebraprocessor=   null;
   // private PluginManager       pluginmanager=      null;    
    ///// ----- Interface ----- /////
   
   /** Constructor:
    *  Makes the api with a reference to the GeoGebra program.
    *  Called from GeoGebra.
    */
    public GgbAPI(Application app) {
        this.app=app;
        kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
    //    pluginmanager=app.getPluginManager();
    }//Constructor
    
    /** Returns reference to Application */
    public Application getApplication(){return this.app;}
    
    /** Returns reference to Construction */
    public Construction getConstruction(){return this.construction;}
    
    /** Returns reference to Kernel */
    public Kernel getKernel(){return this.kernel;}
    
    /** Returns reference to AlgebraProcessor */
    public AlgebraProcessor getAlgebraProcessor(){return this.algebraprocessor;}

    /** Returns reference to PluginManager */
//    public PluginManager getPluginManager() {
//    	if(pluginmanager==null){
//    		this.pluginmanager=app.getPluginManager();
//    	}//if not initialized
//    	return this.pluginmanager;
//    }//getPluginManager()

    /** Returns reference to ClassPathManipulator*/
    public ClassPathManipulator getClassPathManipulator(){
        return null;//ClassPathManipulator;
    }//getClassPathManipulator()
    
    /** Executes a GeoGebra command 
    29.05.08 commented out
    as the right one is copied from applet interface.
    I never saw that it should return boolean before now...
    
    public void evalCommand(String cmd) {
        if(algebraprocessor!=null) {
            algebraprocessor.processAlgebraCommand(cmd, true);
        }else{
            Application.debug("Cannot find the GeoGebra AlgebraProcessor!");
        }//if ggb not null
    }//evalCommand(String)
    */
    
    /// --- 29.05.08 Ulven: --- ///
    
    
    ///* JAVA SCRIPT INTERFACE */
	
	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * @return null if something went wrong 
	 */
	public synchronized byte [] getGGBfile() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			app.getXMLio().writeGeoGebraFile(bos);
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return app.getXML();
	}
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
	}
	
	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public synchronized void evalXML(String xmlString) {		
		app.setXML(xmlString, false);
	}

	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(String cmdString) {
		GeoElement [] result = kernel.getAlgebraProcessor().
								processAlgebraCommand(cmdString, false);
		// return success
		return result != null;
	}

	/**
	 * Evaluates the given string as if it was entered into Yacas's 
	 * input text field. 	 
	 */
	public synchronized String evalYacas(String cmdString) {
		return 	kernel.evaluateYACASRaw(cmdString);

	}

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	
	 * ...but the actual code is in a thread to avoid JavaScript security issues 
	 */
	public synchronized void reset() {
		
		//rewrite in this context
	}
	

	
	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();		 				
	}
			
	/**
	 * Loads a construction from a  file (given URL).	
	 * ...but the actual code is in a thread to avoid JavaScript security issues  
	 */
	public synchronized void openFile(String strURL) {
		
		Application.debug("todo: rewrite in this context");
		

	}
	

	
	/*
	public synchronized void setLanguage(String isoLanguageString) {	
		app.setLanguage(new Locale(isoLanguageString));
	}
	
	public synchronized void setLanguage(String isoLanguageString, String isoCountryString) {
		app.setLanguage(new Locale(isoLanguageString, isoCountryString));
	}
	*/
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLayer(layer);		
		geo.updateRepaint();
	}
	
	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getLayer();		
	}
	
	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		if (layer<0 || layer > EuclidianView.MAX_LAYERS) return;
		String [] names = getObjNames();
		for (int i=0 ; i < names.length ; i++)
		{
			GeoElement geo = kernel.lookupLabel(names[i]);
			if (geo != null) if (geo.getLayer() == layer)
			{
				geo.setEuclidianVisible(visible);		
				geo.updateRepaint();
			}
		}	
	}
	
	

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isFixable()) {		
			geo.setFixed(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isTraceable()) {		
			((Traceable)geo).setTrace(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);		
		geo.updateRepaint();
	}
	
	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelMode(style);		
		geo.updateRepaint();
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green, int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		Color col = new Color(red, green, blue);		
		geo.setObjColor(col);
		geo.updateRepaint();
	}	
	
	/**
	 * Starts/stops an object animating
	 */
	public void startAnimating(String objName, boolean animate) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		geo.startAnimation(animate);	
	}
	
	/**
	 * Starts an object animating
	 */
	public void setAnimationSpeed(String objName, double speed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		geo.setAnimationSpeed(speed);
	}
	

	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return "#" + geogebra.util.Util.toHexString(geo.getObjectColor());		
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.remove();
		kernel.notifyRepaint();
	}	
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {		
		GeoElement geo = kernel.lookupLabel(oldName);
		if (geo == null) 
			return false;
		
		// try to rename
		boolean success = geo.rename(newName);
		kernel.notifyRepaint();
		
		return success;
	}	
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);				
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isDefined();
	}	
	
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getAlgebraDescription();
	}
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getDefinitionDescription();
	}
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getCommandDescription();
	}
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomX;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).x;
		else
			return 0;
	}
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomY;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).y;
		else
			return 0;
	}
	
	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		
		if (geo.isGeoPoint()) {
			((GeoPoint) geo).setCoords(x, y, 1);
			geo.updateRepaint();
		}
		else if (geo.isGeoVector()) {
			((GeoVector) geo).setCoords(x, y, 0);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Returns the double value of the object with the given name. Note: returns 0 if
	 * the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);		
		
		if (geo != null && geo.isNumberValue())
			return ((NumberValue) geo).getDouble();		
		else
			return 0;
	}
	
	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		
		if (geo.isGeoNumeric()) {
			((GeoNumeric) geo).setValue(x);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {		
		//Application.debug("set repainting: " + flag);
		kernel.setNotifyRepaintActive(flag);
	}	
	

	/*
	 * Methods to change the geometry window's properties	 
	 */
	
	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		app.getEuclidianView().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}
	
	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {		
		app.getEuclidianView().showAxes(xVisible, yVisible);
	}	
	
	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {		
		app.getEuclidianView().showGrid(flag);
	}
	
	/*
	 * Methods to get all object names of the construction 
	 */
	
	private String [] objNames;
	public int lastGeoElementsIteratorSize = 0;		//ulven 29.05.08: Had to change to public, used by applet
	
	/**
	 * 
	 * @return
	 */
	public String [] getObjNames() {			//ulven 29.05.08: Had to change to public, used by applet

		Construction cons = kernel.getConstruction();
		TreeSet geoSet =  cons.getGeoSetConstructionOrder();
		int size = geoSet.size();
		// don't build objNames if nothing changed
		if (size == lastGeoElementsIteratorSize)
			return objNames;		
		
		// build objNames array
		lastGeoElementsIteratorSize = size;		
		objNames = new String[size];
				
		int i=0; 
		Iterator it = geoSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			objNames[i] = geo.getLabel();
			i++;
		}
		return objNames;
		
	}
	
	/**
	 * Returns an array with all object names.
	 */
	public synchronized String [] getAllObjectNames() {			
		return getObjNames();
	}	
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {					
		return getObjNames().length;			
	}	
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {					
		String [] names = getObjNames();
					
		try {
			return names[i];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : geo.getObjectType().toLowerCase(Locale.US);
	}
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}	
	
	

	
          
}// class GgbAPI

