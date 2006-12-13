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

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Util;

import java.awt.image.BufferedImage;
import java.util.Vector;

public class GeoImage extends GeoElement 
implements Locateable, AbsoluteScreenLocateable,
		   PointRotateable, Mirrorable, Translateable, Dilateable {
	 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName; // image file
	private GeoPoint [] corners; // corners of the image
	private BufferedImage image;	
	private int pixelWidth, pixelHeight;
	private boolean inBackground;
	private boolean hasAbsoluteLocation;
	
	// for absolute screen location
	private int screenX, screenY;
	boolean hasAbsoluteScreenLocation = false;	
	
	// corner points for transformations
	private GeoPoint [] tempPoints;
	
	private static Vector instances = new Vector();
	
	public GeoImage(Construction c) {
		super(c);
		
		instances.add(this);
		
		// three corners of the image: first, second and fourth
		corners = new GeoPoint[3]; 				
		
		setAlphaValue(1f);
		setAlgebraVisible(false); // don't show in algebra view			
	}  

	
	public GeoImage(Construction c, String label, String fileName) {
		this(c);
		setFileName(fileName);
		setLabel(label);
	}  
	
	/**
	 * Copy constructor
	 */
	public GeoImage(GeoImage img) {
		this(img.cons);
		set(img);				
	}

	public GeoElement copy() {
		return new GeoImage(this);
	}
	
	private void initTempPoints() {
		if (tempPoints == null) {
			//		 temp corner points for transformations and absolute location
			tempPoints = new GeoPoint[3];
	    	for (int i = 0; i < tempPoints.length; i++) {
	    		tempPoints[i] = new GeoPoint(cons);    		
	    	}
	    	corners[0] = tempPoints[0];
		}
	}

	public void set(GeoElement geo) {
		GeoImage img = (GeoImage) geo;
		setFileName(img.fileName);
		
		// macro output: don't set corners
		if (cons != geo.cons && isAlgoMacroOutput) 
			return;
		
		// location settings
		hasAbsoluteScreenLocation = img.hasAbsoluteScreenLocation;
			
		if (hasAbsoluteScreenLocation) {
			screenX = img.screenX;
			screenY = img.screenY;			
		}
		else {
			hasAbsoluteLocation = true;
			for (int i=0; i < corners.length; i++) {
				if (img.corners[i] == null) {
					corners[i] = null;
				} else {
					initTempPoints();
					
					tempPoints[i].setCoords(img.corners[i]);
					corners[i] = tempPoints[i];
				}
			}
		}
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoImage()) {
			inBackground = ((GeoImage)geo).inBackground;
		}
	}
	
	/**
	 * Reloads images from internal image cache	 
	 */
	public static void updateInstances() {
		for (int i=instances.size()-1; i >= 0 ; i--) {
			GeoImage geo = (GeoImage) instances.get(i);
			geo.setFileName(geo.fileName);
			geo.updateCascade();
		}		
	}
	
	public boolean showToolTipText() {
		return !inBackground;
	}
	
	final public boolean isInBackground() {
		return inBackground;
	}
	
	public void setInBackground(boolean flag) {
		inBackground = flag;		
	}
	
	/**
	 * Tries to load the image using the given fileName.
	 * @param fileName
	 */
	public void setFileName(String fileName) {	
		this.fileName = fileName;
		image = app.getExternalImage(fileName);	
		if (image != null) {
			pixelWidth = image.getWidth();
			pixelHeight = image.getHeight();
		} else {
			pixelWidth = 0;
			pixelHeight = 0;
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	final public BufferedImage getImage() {
		return image;
	}		
	
	public void setStartPoint(GeoPoint p) throws CircularDefinitionException {    
		setCorner(p, 0);
	}
	
	public void removeStartPoint(GeoPoint p) {    
		for (int i=0; i < corners.length; i++) {
			if (corners[i] == p)
				setCorner(null, i);
		}
	}
	
	public void setStartPoint(GeoPoint p, int number) throws CircularDefinitionException {
		setCorner(p, number);
	}
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPoint p, int number) {
		corners[number] = p;
	}
	
	/**
	 * Sets a corner of this image. 
	 * @param p
	 * @param number: 0, 1 or 2 (first, second and fourth corner)
	 */
	public void setCorner(GeoPoint p, int number)  {
		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput) return; 
		
		if (corners[0] == null && number > 0) return;
		
		// check for circular definition
		if (isParentOf(p))
			//throw new CircularDefinitionException();
			return;		

		// set new location	
		if (p == null) {
			//	remove old dependencies
			if (corners[number] != null) 
				corners[number].unregisterLocateable(this);	
						
			// copy old first corner as absolute position
			if (number == 0 && corners[0] != null) {
				GeoPoint temp = new GeoPoint(cons);
				temp.setCoords(corners[0]);
				corners[0] = temp;
			} else
				corners[number] = null;
		} else {
			// check if this point is already available
			for (int i=0; i < corners.length; i++) {
				if (p == corners[i])
					return;
			}
			
			// remove old dependencies
			if (corners[number] != null) 
				corners[number].unregisterLocateable(this);		
			
			corners[number] = p;
			//	add new dependencies
			corners[number].registerLocateable(this);								
		}					
		
		// absolute screen position should be deactivated
		setAbsoluteScreenLocActive(false);	
		updateHasAbsoluteLocation();				
	}
	
	/**
	 * Sets hasAbsoluteLocation flag to true iff all corners
	 * are absolute start points (i.e. independent and unlabeled). 
	 */
	private void updateHasAbsoluteLocation() {
		hasAbsoluteLocation = true;
			
		for (int i=0; i < corners.length; i++) {
			if (!(corners[i] == null || corners[i].isAbsoluteStartPoint())) {
				hasAbsoluteLocation = false;
				return;
			}						
		}
	}		
	
	void doRemove() {
		instances.remove(this);		
		
		// remove background image
		if (inBackground) {
			inBackground = false;
			notifyUpdate();
		}
		
		super.doRemove();		
		for (int i=0; i < corners.length; i++) {
			// tell corner	
			if (corners[i] != null) corners[i].unregisterLocateable(this);
		}		
	}
	
	public GeoPoint getStartPoint() {
		return corners[0];
	}
	
	public GeoPoint [] getStartPoints() {
		return corners;
	}
	
	final public GeoPoint getCorner(int number) {
		return corners[number];
	}
	
	final public boolean hasAbsoluteLocation() {
		return hasAbsoluteLocation;
	}	
	
	public void setWaitForStartPoint() {
		// this can be ignored for an image 
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}
	
	public void setMode(int mode) {
	}

	public int getMode() {
		return 0;
	}

	final public boolean isDefined() {
		for (int i=0; i < corners.length; i++) {
			if (corners[i] != null  && !corners[i].isDefined())
					return false;
		}
		return true;
	}

	/**
	 * doesn't do anything
 	*/
	public void setUndefined() {
	}

	public String toValueString() {
		return toString();
	}
	
	public String toString() {				
		return label;
	}	

	boolean showInAlgebraView() {
		return false;
	}

	boolean showInEuclidianView() {		
		return image != null && isDefined();
	}

	String getClassName() {
		return "GeoImage";
	}
	
	String getTypeString() {
		return "Image";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_IMAGE;
    }
	
	/**
	 * Returns whether this image can be 
	 * moved in Euclidian View.
	 */
	final public boolean isMoveable() {		
		return (hasAbsoluteScreenLocation || hasAbsoluteLocation) && isChangeable();
	}
	
	/**
	 * Returns whether this image can be 
	 * rotated in Euclidian View.
	 */
	final public boolean isRotateMoveable() {
		return !hasAbsoluteScreenLocation && hasAbsoluteLocation && isChangeable();
	}
	
	
	public boolean isFixable() {
		return (hasAbsoluteLocation || hasAbsoluteScreenLocation) && isIndependent();
	}
	
	public boolean isFillable() {
		return true;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}
	
	public boolean isGeoImage() {
		return true;
	}

	public boolean isPolynomialInstance() {
		return false;
	}
	
	public boolean isTextValue() {
		return false;
	}

	/**
	* returns all class-specific xml tags for getXML
	*/
   	String getXMLtags() {   	
	   	StringBuffer sb = new StringBuffer();
	   		   		   	
	   	sb.append(getXMLvisualTags());
	   	sb.append(getBreakpointXML());
		
	   	// name of image file
		sb.append("\t<file name=\"");
		sb.append(fileName);
		sb.append("\"/>\n");
		
	 	// name of image file
		sb.append("\t<inBackground val=\"");
		sb.append(inBackground);
		sb.append("\"/>\n");	

		// locateion of image
		if (hasAbsoluteScreenLocation) {
			sb.append(getXMLabsScreenLoc());
		} 
		else {
			// store location of corners		
			for (int i=0; i < corners.length; i++) {
				if (corners[i] != null) {
					sb.append(getCornerPointXML(i));
				}
			}
		}

	   return sb.toString();   
   	}
   	
   	private String getXMLabsScreenLoc() {
   		StringBuffer sb = new StringBuffer();
   		
   		sb.append("\t<absoluteScreenLocation x=\"");
   		sb.append(screenX);
   		sb.append("\" y=\"");
   		sb.append(screenY);
   		sb.append("\"/>");
   		return sb.toString();
   	}
   	
    private String getCornerPointXML(int number) {
    	StringBuffer sb = new StringBuffer();    	
		sb.append("\t<startPoint number=\"");
		sb.append(number);
		sb.append("\"");
		
    	if (corners[number].isAbsoluteStartPoint()) {		
			sb.append(" x=\"" + corners[number].x + "\"");
			sb.append(" y=\"" + corners[number].y + "\"");
			sb.append(" z=\"" + corners[number].z + "\"");			
    	} else {
			sb.append(" exp=\"");
			boolean oldValue = kernel.isTranslateCommandName();
			kernel.setTranslateCommandName(false);
			sb.append(Util.encodeXML(corners[number].getLabel()));
			kernel.setTranslateCommandName(oldValue);
			sb.append("\"");			    	
    	}
		sb.append("/>\n");
		return sb.toString();
    }

	public void setAbsoluteScreenLoc(int x, int y) {
		screenX = x;
		screenY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return screenX;
	}

	public int getAbsoluteScreenLocY() {		
		return screenY;
	}
	
	public void setRealWorldLoc(double x, double y) {
		GeoPoint loc = getStartPoint();
		if (loc == null) {
			loc = new GeoPoint(cons);	
			setCorner(loc, 0);
		}				
		loc.setCoords(x, y, 1.0);		
	}
	
	public double getRealWorldLocX() {
		if (corners[0] == null)
			return 0;
		else
			return corners[0].inhomX;
	}
	
	public double getRealWorldLocY() {
		if (corners[0] == null)
			return 0;
		else
			return corners[0].inhomY;
	}
	
	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;	
		if (flag) {
			// remove startpoints
			for (int i=0; i < 3; i++) {
				if (corners[i] != null) {
					corners[i].unregisterLocateable(this);						
				}
			}	
			corners[1] = null;
			corners[2] = null;
		}
	}

	public boolean isAbsoluteScreenLocActive() {	
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocSetable() {
		return isIndependent();
	}
	
	
	/* **************************************
	 * Transformations 
	 * **************************************/
	
	/**
	 * Calculates the n-th corner point of this image in real world
	 * coordinates. Note: if this image
	 * has an absolute screen location, result is set to undefined.
	 * 
	 * @param result: here the result is stored.
	 * @param number of the corner point 1, 2, 3 or 4) 
	 */
	public void calculateCornerPoint(GeoPoint result, int n) {		
		if (hasAbsoluteScreenLocation) {
			result.setUndefined();
			return;
		}
		
		switch (n) {
			case 1: // get A
				result.setCoords(corners[0]);
				break;
			
			case 2: // get B
				getInternalCornerPointCoords(tempCoords, 1);
				result.setCoords(tempCoords[0], tempCoords[1], 1.0);
				break;
				
			case 3: // get C
				double [] b = new double[2];
				double [] d = new double[2];
				getInternalCornerPointCoords(b, 1);
				getInternalCornerPointCoords(d, 2);
				result.setCoords(d[0] + b[0] - corners[0].inhomX,
								 d[1] + b[1] - corners[0].inhomY,
								 1.0);
				break;
				
			case 4: // get D
				getInternalCornerPointCoords(tempCoords, 2);
				result.setCoords(tempCoords[0], tempCoords[1], 1.0);
				break;
				
			default:
				result.setUndefined();
		}	
	}
		
	// coords is the 2d result array for (x, y); n is 0, 1, or 2
	private double [] tempCoords = new double[2];
	private void getInternalCornerPointCoords(double [] coords, int n) {		
		GeoPoint A = corners[0];
		GeoPoint B = corners[1];
		GeoPoint D = corners[2];
		
		double xscale = kernel.getXscale();
		double yscale = kernel.getYscale();
		double width = pixelWidth;
		double height = pixelHeight;
		
		// different scales: change height
		if (xscale != yscale) {
			height = height * yscale / xscale;
		}
		
		switch (n) {
			case 0: // get A
				coords[0] = A.inhomX;
				coords[1] = A.inhomY;
				break;
			
			case 1: // get B
				if (B != null) {
					coords[0] = B.inhomX;
					coords[1] = B.inhomY;				
				} else { // B is not defined
					if (D == null) { 
						// B and D are not defined
						coords[0] = A.inhomX + width / xscale;
						coords[1] = A.inhomY;							
					} else {
						// D is defined, B isn't
						double nx = D.inhomY - A.inhomY;
						double ny = A.inhomX - D.inhomX;
						double factor = width / height;
						coords[0] = A.inhomX + factor * nx;
						coords[1] = A.inhomY + factor * ny;										
					}					
				}
				break;							
				
			case 2: // D
				if (D != null) {
					coords[0] = D.inhomX;
					coords[1] = D.inhomY;
				} else { // D is not defined
					if (B == null) { 
						// B and D are not defined
						coords[0] = A.inhomX;
						coords[1] = A.inhomY + height / yscale;										 
					} else {
						// B is defined, D isn't
						double nx = A.inhomY - B.inhomY;
						double ny = B.inhomX - A.inhomX;
						double factor = height / width;
						coords[0] = A.inhomX + factor * nx;
						coords[1] = A.inhomY + factor * ny;										 
					}					
				}
				break;
				
			default:
				coords[0] = Double.NaN;
				coords[1] = Double.NaN;
		}					
	}
	
	private boolean initTransformPoints() {
    	if (hasAbsoluteScreenLocation || !hasAbsoluteLocation) 
    		return false;    	    
    	
    	initTempPoints();
    	calculateCornerPoint(tempPoints[0], 1);
    	calculateCornerPoint(tempPoints[1], 2);
    	calculateCornerPoint(tempPoints[2], 4);    	
    	return true;
    }	
	

    /**
     * rotate this image by angle phi around (0,0)
     */
    final public void rotate(NumberValue phiValue) {
    	if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].rotate(phiValue);    		
    		corners[i] = tempPoints[i];
    	}
    }
    
    /**
     * rotate this image by angle phi around Q
     */    
    final public void rotate(NumberValue phiValue, GeoPoint Q) {
    	if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].rotate(phiValue, Q);    	
    		corners[i] = tempPoints[i];    			
    	}      
    }
     
	public void mirror(GeoPoint Q) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].mirror(Q);    	
    		corners[i] = tempPoints[i];    			
    	}     
	}

	public void mirror(GeoLine g) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].mirror(g);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}

	public void translate(GeoVector v) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].translate(v);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}	
	
	final public boolean isTranslateable() {
		return true;
	}

	public void dilate(NumberValue r, GeoPoint S) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].dilate(r, S);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}
}
