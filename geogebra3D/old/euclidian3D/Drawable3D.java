package geogebra3D.old.euclidian3D;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;

import geogebra.euclidian.*;
import geogebra.kernel.GeoElement;

public abstract class Drawable3D 
	extends Drawable{
	
	EuclidianView3D view3D;
	
	//visibility
    boolean isVisible, labelVisible;   
	
	//drawing areas
	Area interior = new Area();
	Area outline = new Area();
	
	public double thickness = 0.0; 
	
	//hidden parts
	Area hiddenPart = new Area();
	Area hidingPart = new Area();
	//Rectangle2D bounds;
	
	//types
	static final int POINT3D = 1;
	static final int SEGMENT3D = 2;
	
	
	
	public Drawable3D(){
		
		view3D = (EuclidianView3D) view;
	}
	
	
	
	
	
	//shared methods
    public void draw(Graphics2D g2) {   
        if (isVisible) { 
        	g2.setColor(getInteriorColor());
			g2.fill(interior);
			g2.setStroke(objStroke);
			g2.setColor(getOutlineColor());
			g2.fill(outline);

            // label   
            if (labelVisible) {
				g2.setFont(view3D.fontPoint);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);			
            }                         
        }
    }
	
	
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
    /** return the color of the outline */
    public abstract Color getOutlineColor();
    
    /** return the color of the interior of the element */
    public abstract Color getInteriorColor();
    
    /** return the type of the Drawable3D */
    public abstract int getType();
    
    
    
    ////////////////////////////////
    // hidden parts
    
    /** init hiddenPart to an empty area */
    public void initAreas(){
       	hidingPart = new Area();
       	hidingPart.add(interior);
       	hidingPart.add(outline);
       	//bounds = hidingPart.getBounds2D();
    }
    
    /*
    public void initHiddenPart(){
    	hiddenPart = new Area();
    	hidingPart = new Area();
    	hidingPart.add(interior);
    	hidingPart.add(new Area(objStroke.createStrokedShape(outline)));
    	bounds = hidingPart.getBounds2D(); 
   }
   */
    
    /** hiddenPart = hiddenPart U area */
    public void addHiddenPart(Area area){
    	//hiddenPart.add(area);
    	interior.subtract(area);
    	outline.subtract(area);
    	//hidingPart.subtract(area);
    }
    
    /** return the drawing area of the Drawable3D */
    public Area getHidingPart(){
    	return hidingPart;
    }
    
    /** draw not hidden parts */
    public void drawNotHidden(Graphics2D g2, Shape totalClip){
    	/*
    	Area area = new Area(totalClip);
    	area.subtract(hiddenPart);
    	g2.setClip(area);
    	*/    	
    	//interior.intersect(hidingPart);
    	//outline.intersect(hidingPart);
    	draw(g2);
    }
    
    /** returns true if this and d have a non-empty intersection */
    /*
    public boolean hidable(Drawable3D d){
    	
		switch(this.getType()){
		case Drawable3D.POINT3D:
			switch(d.getType()){
			case Drawable3D.POINT3D:				
				return distPoint3D((DrawPoint3D) d);
				//break;
			default :
				return false;
				//break;
			}
			//break;
		default :
			return false;
			//break;				
		}
    	
    	//return d.bounds.intersects(this.bounds);
    	
    }
    */
    
    
    ///////////////////////////////////////
    // intersection functions  
    abstract public boolean intersectsPoint3D(DrawPoint3D d);
    abstract public boolean intersectsSegment3D(DrawSegment3D d);
	
	

}
