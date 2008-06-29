/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra3D.old.euclidian3D;


import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPoint3D;
//import geogebra.euclidian.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;




/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawPoint3D extends Drawable3D {
	 	
	//private  int SELECTION_OFFSET;
	       
    private GeoPoint3D P;    
    
	//private int diameter, selDiameter, pointSize;
    // for dot and selection
	//private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Ellipse2D.Double circleSel = new Ellipse2D.Double();
	//private Line2D.Double line1, line2;// for cross	
    private GgbVector coords = new GgbVector(4);
    
    //private static BasicStroke borderStroke = EuclidianView3D.getDefaultStroke();
    private static BasicStroke [] crossStrokes = new BasicStroke[10];
    
    /** Creates new DrawPoint */
    public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D P) {      
    	this.view3D = view3D;          
        this.P = P;
        setGeoElement(P);
        
        crossStrokes[1] = new BasicStroke(1f);

        update();
    }
    
    
    final public void update() {       
        
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
    	
 		coords.set(P.getCoords());//.getCoordsLast1(); //TODO remove getCoordsLast1()     
        
        // TODO point outside screen?
        /*
        if (coords[0] > view3D.getXmax() || coords[0] < view3D.getXmin()
        	|| coords[1] > view3D.getYmax() || coords[1] < view3D.getYmin())  
        {
        	isVisible = false;
        	return;	
        }*/
        
        // convert to screen
		view3D.toScreenCoords3D(coords);						
        
		// init areas
		initAreas();
		
    }
    
    
    
	public void initAreas(){
		
		//TODO link values to properties
		float t = 3f;
		objStroke = new BasicStroke(t);		
		double pointSize = (double) 10.0;// P.pointSize;
		thickness = ((double) t)/2.0 + pointSize;
		
		Ellipse2D.Double ellipse = new Ellipse2D.Double(
				coords.get(1) - pointSize,coords.get(2) - pointSize, 
				2.0 * pointSize, 2.0 * pointSize ); 
		
		outline = new Area(objStroke.createStrokedShape(ellipse));		
		interior = new Area(ellipse);
		
		super.initAreas();
		
	}

	
	

    
    
    
    public Color getOutlineColor(){
    	return Color.BLACK;
    }
    
    public Color getInteriorColor(){
    	return geo.objColor;
    }
    
    public int getType(){
    	return POINT3D;
    }
	
    
    
    /** returns the n first coords of the point in physical coordinates */
    public GgbVector getCoords(int n){
    	return coords.subVector(1, n);
    }
 
    
    
    ///////////////////////////////////////
    // Z-functions for intersection 
	public double getZ(){
		
		return coords.get(3);
	}

	
	
    ///////////////////////////////////////
    // intersection functions 
    public boolean intersectsPoint3D(DrawPoint3D d){
    	
    	double dist = coords.subVector(1,2).distance(d.coords.subVector(1,2));
    	//System.out.println("dist = "+dist);
    	boolean ret = (dist <= thickness + d.thickness);
    	//if (ret) System.out.println("caché");
    	return ret;
    }
 
    public boolean intersectsSegment3D(DrawSegment3D d){
    	
    	double dist = getCoords(2).distLine(d.getOrigin(2),d.getVector(2));
    	//System.out.println("dist = "+dist);
    	boolean ret = (dist <= thickness + d.thickness);
    	//if (ret) System.out.println("caché");
    	return ret;
    }
    
    
    
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return circleSel.contains(x, y);        
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(circleSel.getBounds());  
    }
    

}


