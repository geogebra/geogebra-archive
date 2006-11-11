/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.Application;
import geogebra.util.FastHashMapKeyless;

import java.awt.Color;
import java.util.Set;

/**
 * Manages default settings for GeoElement objects in a construction.
 * @author Markus Hohenwarter
 */
public class ConstructionDefaults {
	
	// DEFAULT GeoElement types
	public static final int DEFAULT_POINT_FREE = 10;
	public static final int DEFAULT_POINT_DEPENDENT = 11;
	public static final int DEFAULT_POINT_ON_PATH = 12;
	
	public static final int DEFAULT_LINE = 20;			
	public static final int DEFAULT_VECTOR = 30;	
	public static final int DEFAULT_CONIC = 40;
		
	public static final int DEFAULT_NUMBER = 50;	
	public static final int DEFAULT_ANGLE = 52;	
	
	public static final int DEFAULT_FUNCTION = 60;		
	public static final int DEFAULT_POLYGON = 70;
	public static final int DEFAULT_LOCUS = 80;
			
	
	// DEFAULT COLORs
	// points
	private static final Color colPoint = Color.blue;
	private static final Color colDepPoint = Color.darkGray;
	private static final Color colPathPoint = new Color(125, 125, 255);

	// lines
	private static final Color colLine = Color.black;

	// conics
	private static final Color colConic = Color.black;

	// polygons
	private static final Color colPolygon = new Color(153, 51, 0);	
	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;

	// angles
	private static final Color colAngle = new Color(0, 100, 0);
	public static final float DEFAULT_ANGLE_ALPHA = 0.1f;

	// locus lines	
	private static final Color colLocus = Color.darkGray;
	
	// functions
	private static final Color colFunction = Color.darkGray;

	// numbers (slope, definite integral)
	private static final Color colNumber = Color.black;	
	
	// preview colors
	public static final Color colPreview = Color.darkGray;

	public static final Color colPreviewFill = new Color(
			colPolygon.getRed(), 
			colPolygon.getGreen(), 
			colPolygon.getBlue(), 
			(int) (DEFAULT_POLYGON_ALPHA * 255));	
	
	// construction
	private Construction cons;
	
	// defaultGeoElement list
	private FastHashMapKeyless defaultGeoElements;		
	
	/**
	 * Creates a new ConstructionDefaults object to manage the
	 * default objects of this construction.
	 * @param cons
	 */
	public ConstructionDefaults(Construction cons) {
		this.cons = cons;
		createDefaultGeoElements();
	}
	
	/**
	 * Returns a set of all default GeoElements used by this construction.
	 */
	public Set getDefaultGeos() {
		return defaultGeoElements.entrySet();
	}
	
	
	private void createDefaultGeoElements() {
		defaultGeoElements = new FastHashMapKeyless();		
		
		Application app = cons.getApplication();		
		String strFree = " (" + app.getPlain("free") + ")";
		String strDependent = " (" + app.getPlain("dependent") + ")";
						
		// free point
		GeoPoint freePoint = new GeoPoint(cons);	
		freePoint.setLocalVariableLabel(app.getPlain("Point") + strFree);
		freePoint.setObjColor(colPoint);
		defaultGeoElements.put(DEFAULT_POINT_FREE, freePoint);
		
		// dependent point
		GeoPoint depPoint = new GeoPoint(cons);	
		depPoint.setLocalVariableLabel(app.getPlain("Point") + strDependent);
		depPoint.setObjColor(colDepPoint);
		defaultGeoElements.put(DEFAULT_POINT_DEPENDENT, depPoint);
		
		// point on path
		GeoPoint pathPoint = new GeoPoint(cons);	
		pathPoint.setLocalVariableLabel(app.getPlain("PointOn"));
		pathPoint.setObjColor(colPathPoint);
		defaultGeoElements.put(DEFAULT_POINT_ON_PATH, pathPoint);
		
		
		// line
		GeoLine line = new GeoLine(cons);	
		line.setLocalVariableLabel(app.getPlain("Line"));
		line.setObjColor(colLine);
		defaultGeoElements.put(DEFAULT_LINE, line);
		
		// polygon
		GeoPolygon polygon = new GeoPolygon(cons, null);	
		polygon.setLocalVariableLabel(app.getPlain("Polygon"));
		polygon.setObjColor(colPolygon);
		polygon.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		defaultGeoElements.put(DEFAULT_POLYGON, polygon);
										
		// conic
		GeoConic conic = new GeoConic(cons);	
		conic.setLocalVariableLabel(app.getPlain("Conic"));
		conic.setObjColor(colConic);
		defaultGeoElements.put(DEFAULT_CONIC, conic);		
		
		// number
		GeoNumeric number = new GeoNumeric(cons);	
		number.setLocalVariableLabel(app.getPlain("Numeric"));
		number.setObjColor(colNumber);
		defaultGeoElements.put(DEFAULT_NUMBER, number);
				
		// angle
		GeoAngle angle = new GeoAngle(cons);	
		angle.setLocalVariableLabel(app.getPlain("Angle"));
		angle.setObjColor(colAngle);		
		angle.setAlphaValue(DEFAULT_ANGLE_ALPHA);
		angle.setDrawable(true);
		//angle.setParentAlgorithm(new AlgoElement(cons));
		defaultGeoElements.put(DEFAULT_ANGLE, angle);
		
		// function
		GeoFunction function = new GeoFunction(cons);	
		function.setLocalVariableLabel(app.getPlain("Function"));
		function.setObjColor(colFunction);
		defaultGeoElements.put(DEFAULT_FUNCTION, function);
		
		// locus
		GeoLocus locus = new GeoLocus(cons, null);	
		locus.setLocalVariableLabel(app.getPlain("Locus"));
		locus.setObjColor(colLocus);		
		defaultGeoElements.put(DEFAULT_LOCUS, locus);		
	}
	
	/**
	 * Returns a default GeoElement of this construction.
	 * @param type: use DEFAULT_* constants (e.g. DEFAULT_POINT_FREE) 
	 */
	public GeoElement getDefaultGeo(int type) {
		return (GeoElement) defaultGeoElements.get(type);		
	}
	
	
	
	/**
	 * Sets default color for given geo. 
	 * Note: this is mostly kept for downward compatibility.
	 */
	public void setDefaultVisualStyles(GeoElement geo) {
		// all object types that are not specifically supported
		// should get the default values of a line
		int type = DEFAULT_LINE;
		
		// free
		if (geo.isIndependent()) {		
			if (geo.isGeoPoint())
				type = DEFAULT_POINT_FREE;		
			else if (geo.isGeoLine())
				type = DEFAULT_LINE;
			else if (geo.isGeoVector())
				type = DEFAULT_VECTOR;
			else if (geo.isGeoConic())
				type = DEFAULT_CONIC;
			else if (geo.isGeoFunction())
				type = DEFAULT_FUNCTION;		
			else if (geo.isGeoNumeric())
				type = DEFAULT_NUMBER;				
		} 
		
		// dependent
		else {			
			if (geo.isGeoPoint()) {
				GeoPoint p = (GeoPoint) geo;
				if (p.hasPath())
					type = DEFAULT_POINT_ON_PATH;	
				else
					type = DEFAULT_POINT_DEPENDENT;
			} 
			else if (geo.isGeoLine())
				type = DEFAULT_LINE;
			else if (geo.isGeoVector())
				type = DEFAULT_VECTOR;
			else if (geo.isGeoConic())
				type = DEFAULT_CONIC;
			else if (geo.isGeoFunction())
				type = DEFAULT_FUNCTION;	
			else if (geo.isGeoPolygon())
				type = DEFAULT_POLYGON;			
			else if (geo.isGeoAngle())
				type = DEFAULT_ANGLE;	
			else if (geo.isGeoNumeric())
				type = DEFAULT_NUMBER;	
			else if (geo.isGeoLocus())
				type = DEFAULT_LOCUS;	
		}
		
		// default
		GeoElement defaultGeo = getDefaultGeo(type);
		if (defaultGeo != null)
			geo.setAllVisualProperties(defaultGeo);
	}

}