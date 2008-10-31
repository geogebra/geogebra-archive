package geogebra;


/**
 * JavaScript interface of GeoGebra applets
 * 
 * @author mhohenwarter
 */
public interface JavaScriptAPI {
	
	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * @return null if something went wrong 
	 */
	public  byte [] getGGBfile();

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public  String getXML();
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public  void setXML(String xml);
	
	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public  void evalXML(String xmlString);

	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public boolean evalCommand(String cmdString);

	/**
	 * Evaluates the given string as if it was entered into Yacas's 
	 * input text field. 	 
	 */
	public String evalYacas(String cmdString);

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public void setErrorDialogsActive(boolean flag);
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	
	 * ...but the actual code is in a thread to avoid JavaScript security issues 
	 */
	public void reset();
	
	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public void refreshViews();
	
	/** 
	 * returns IP address	 
	 */
	public String getIPAddress();
			
	/**
	 *  returns hostname	 
	 */
	public String getHostname();
			
	/**
	 * Loads a construction from a  file (given URL).	
	 * ...but the actual code is in a thread to avoid JavaScript security issues  
	 */
	public void openFile(String strURL);
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public void setVisible(String objName, boolean visible);
	
	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public void setLayer(String objName, int layer);
	
	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public int getLayer(String objName);
	
	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public void setLayerVisible(int layer, boolean visible);	

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public void setFixed(String objName, boolean flag);
	
	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public void setTrace(String objName, boolean flag);
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public void setLabelVisible(String objName, boolean visible);
	
	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public void setLabelStyle(String objName, int style);
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public void setLabelMode(String objName, boolean visible);
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public void setColor(String objName, int red, int green, int blue);
	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public String getColor(String objName);
	
	/**
	 * Deletes the object with the given name.
	 */
	public void deleteObject(String objName);
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public boolean exists(String objName);
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public boolean renameObject(String oldObjName, String newObjName);
	
	/**
	 * Starts an object animating
	 */
	public void startAnimating(String objName, boolean animate);
	
	/**
	 * Starts an object animating
	 */
	public void setAnimationSpeed(String objName, double speed);
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public boolean isDefined(String objName);
	
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public String getValueString(String objName);
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public String getDefinitionString(String objName);
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public String getCommandString(String objName);
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public double getXcoord(String objName);
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public double getYcoord(String objName);
	
	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public void setCoords(String objName, double x, double y);
	
	/**
	 * Returns the double value of the object with the given name. Note: returns 0 if
	 * the object does not have a value.
	 */
	public double getValue(String objName);
	
	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 */
	public void setValue(String objName, double x);
	
	/**
	 * Turns the repainting of all views on or off.
	 */
	public void setRepaintingActive(boolean flag);
	
	
	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax);
	
	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public void setAxesVisible(boolean xVisible, boolean yVisible);
	
	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public void setGridVisible(boolean flag);	
	
	/**
	 * Returns an array with all object names.
	 */
	public String [] getAllObjectNames();
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public int getObjectNumber();
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public String getObjectName(int i);
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public String getObjectType(String objName);
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public void setMode(int mode);
		
	/**
	 * Registers a JavaScript function as an add listener for the applet's construction.
	 *  Whenever a new object is created in the GeoGebraApplet's construction, the JavaScript 
	 *  function JSFunctionName is called using the name of the newly created object as a single argument. 
	 */
	public void registerAddListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered add listener 
	 * @see registerAddListener() 
	 */
	public void unregisterAddListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a remove listener for the applet's construction.
	 * Whenever an object is deleted in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public void registerRemoveListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered remove listener 
	 * @see registerRemoveListener() 
	 */
	public void unregisterRemoveListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a clear listener for the applet's construction.
	 * Whenever the construction in the GeoGebraApplet's is cleared (i.e. all objects are removed), the JavaScript 
	 * function JSFunctionName is called using no arguments. 	
	 */
	public void registerClearListener(String JSFunctionName) ;
	
	/**
	 * Removes a previously registered clear listener 
	 * @see registerClearListener() 
	 */
	public void unregisterClearListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as a rename listener for the applet's construction.
	 * Whenever an object is renamed in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public void registerRenameListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered rename listener.
	 * @see registerRenameListener() 
	 */
	public void unregisterRenameListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript function as an update listener for the applet's construction.
	 * Whenever any object is updated in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public void registerUpdateListener(String JSFunctionName);
	
	/**
	 * Removes a previously registered update listener.
	 * @see registerRemoveListener() 
	 */
	public void unregisterUpdateListener(String JSFunctionName);
	
	/**
	 * Registers a JavaScript update listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.setChangeListener("A", "myJavaScriptFunction");
	 * Then the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A");
	 * whenever object A changes.	
	 */
	public void registerObjectUpdateListener(String objName, String JSFunctionName);
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public void unregisterObjectUpdateListener(String objName);


	
}
