/* 
GeoGebra - Dynamic Mathematics for Everyone
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra;

import javax.swing.JApplet;

/**
 * GeoGebra applet
 * 
 * @see geogebra.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-23
 */
public class GeoGebraApplet extends JApplet implements JavaScriptAPI {

	private static final long serialVersionUID = -350682076336303151L;

	private AppletManager am;
	private JavaScriptAPI js;

	public GeoGebraApplet() {
		// load geogebra_main.jar file
		// this is needed to initialize the GeoGebra applet implementation
		JarManager jarManager = JarManager.getSingleton(false);
		jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_MAIN);

		// create delegate object that implements our applet's methods
		am = new geogebra.main.DefaultApplet(this);
		js = (JavaScriptAPI) am;
	};

	// JApplet methods
	public void init() {
		am.init();
	}

	public void start() {
		am.start();
	}

	public void stop() {
		am.stop();
	}

	public void destroy() {
		am.destroy();
	}

	/*
	 * JAVASCRIPT interface
	 * 
	 * To add a new JavaScript method, do the following: 1) add the method stub
	 * to the interface geogebra.JavaScriptAPI 2) implement the method in
	 * geogebra.main.AppletImplementation 3) impplement the method here in
	 * geogebra.GeoGebraApplet by delegating it to
	 * geogebra.main.AppletImplementation
	 */

	public void deleteObject(String objName) {
		js.deleteObject(objName);
	}

	public boolean evalCommand(String cmdString) {
		return js.evalCommand(cmdString);
	}

	public void evalXML(String xmlString) {
		js.evalCommand(xmlString);
	}

	public String evalYacas(String cmdString) {
		return js.evalYacas(cmdString);
	}

	public boolean exists(String objName) {
		return js.exists(objName);
	}

	public String[] getAllObjectNames() {
		return js.getAllObjectNames();
	}

	public String getColor(String objName) {
		return js.getColor(objName);
	}

	public String getCommandString(String objName) {
		return js.getCommandString(objName);
	}

	public String getDefinitionString(String objName) {
		return js.getDefinitionString(objName);
	}

	public byte[] getGGBfile() {
		return js.getGGBfile();
	}

	public String getHostname() {
		return js.getHostname();
	}

	public String getIPAddress() {
		return js.getIPAddress();
	}

	public int getLayer(String objName) {
		return js.getLayer(objName);
	}

	public String getObjectName(int i) {
		return js.getObjectName(i);
	}

	public int getObjectNumber() {
		return js.getObjectNumber();
	}

	public String getObjectType(String objName) {
		return js.getObjectType(objName);
	}

	public double getValue(String objName) {
		return js.getValue(objName);
	}

	public String getValueString(String objName) {
		return js.getValueString(objName);
	}

	public String getXML() {
		return js.getXML();
	}

	public double getXcoord(String objName) {
		return js.getXcoord(objName);
	}

	public double getYcoord(String objName) {
		return js.getYcoord(objName);
	}

	public boolean isDefined(String objName) {
		return js.isDefined(objName);
	}

	public void openFile(String strURL) {
		js.openFile(strURL);
	}

	public void openFileNoThread(String strURL) {
		js.openFileNoThread(strURL);
	}

	public void refreshViews() {
		js.refreshViews();
	}

	public void registerAddListener(String JSFunctionName) {
		js.registerAddListener(JSFunctionName);
	}

	public void registerClearListener(String JSFunctionName) {
		js.registerClearListener(JSFunctionName);
	}

	public void registerObjectUpdateListener(String objName, String JSFunctionName) {
		js.registerObjectUpdateListener(objName, JSFunctionName);
	}

	public void registerRemoveListener(String JSFunctionName) {
		js.registerRemoveListener(JSFunctionName);
	}

	public void registerRenameListener(String JSFunctionName) {
		js.registerRenameListener(JSFunctionName);
	}

	public void registerUpdateListener(String JSFunctionName) {
		js.registerUpdateListener(JSFunctionName);
	}

	public void reset() {
		js.reset();
	}

	public void resetNoThread() {
		js.resetNoThread();
	}

	public void setAxesVisible(boolean xVisible, boolean yVisible) {
		js.setAxesVisible(xVisible, yVisible);
	}

	public void setColor(String objName, int red, int green, int blue) {
		js.setColor(objName, red, green, blue);
	}

	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		js.setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public void setCoords(String objName, double x, double y) {

		js.setCoords(objName, x, y);
	}

	public void setErrorDialogsActive(boolean flag) {

		js.setErrorDialogsActive(flag);
	}

	public void setFixed(String objName, boolean flag) {
		js.setFixed(objName, flag);
	}

	public void setGridVisible(boolean flag) {
		js.setGridVisible(flag);
	}

	public void setLabelMode(String objName, boolean visible) {
		js.setLabelMode(objName, visible);
	}

	public void setLabelStyle(String objName, int style) {
		js.setLabelStyle(objName, style);
	}

	public void setLabelVisible(String objName, boolean visible) {
		js.setLabelVisible(objName, visible);
	}

	public void setLayer(String objName, int layer) {
		js.setLayer(objName, layer);
	}

	public void setLayerVisible(int layer, boolean visible) {
		js.setLayerVisible(layer, visible);
	}

	public void setMode(int mode) {
		js.setMode(mode);
	}

	public void setRepaintingActive(boolean flag) {
		js.setRepaintingActive(flag);
	}

	public void setTrace(String objName, boolean flag) {
		js.setTrace(objName, flag);
	}

	public void setValue(String objName, double x) {
		js.setValue(objName, x);
	}

	public void setVisible(String objName, boolean visible) {
		js.setVisible(objName, visible);
	}

	public void setXML(String xml) {
		js.setXML(xml);
	}

	public void unregisterAddListener(String JSFunctionName) {
		js.unregisterAddListener(JSFunctionName);
	}

	public void unregisterClearListener(String JSFunctionName) {
		js.unregisterClearListener(JSFunctionName);
	}

	public void unregisterObjectUpdateListener(String objName) {
		js.unregisterObjectUpdateListener(objName);
	}

	public void unregisterRemoveListener(String JSFunctionName) {
		js.unregisterRemoveListener(JSFunctionName);
	}

	public void unregisterRenameListener(String JSFunctionName) {
		js.unregisterRenameListener(JSFunctionName);
	}

	public void unregisterUpdateListener(String JSFunctionName) {
		js.unregisterUpdateListener(JSFunctionName);
	}

}
