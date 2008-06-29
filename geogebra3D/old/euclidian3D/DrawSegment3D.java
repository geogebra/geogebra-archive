package geogebra3D.old.euclidian3D;


import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.Linalg.GgbMatrix;
import geogebra3D.kernel3D.Linalg.GgbVector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class DrawSegment3D extends Drawable3D {

	GeoSegment3D S;
	GgbMatrix m; //representative matrix in physical coordinates
	GgbVector coords0 = new GgbVector(4);
	GgbVector coords1 = new GgbVector(4);
	
	public DrawSegment3D(EuclidianView3D view, GeoSegment3D s){
		this.S=s;
		this.view3D=view;
		setGeoElement(s);
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		
        // compute lower left corner of bounding boxS
		coords0.set(S.getPoint(0));//.getCoordsLast1();//.getInhomCoords();
		coords1.set(S.getPoint(1));//.getCoordsLast1();//.getInhomCoords();
		
		m = S.getMatrix().copy();
       
        // convert to screen
		view3D.toScreenCoords3D(coords0);
		view3D.toScreenCoords3D(coords1);
		
		view3D.toScreenCoords3D(m);
		//m.SystemPrint();
		
		
		//init areas
		initAreas();
        
	}
	
	
	
	
	public void initAreas(){
		
		//TODO link values to properties
		objStroke = new BasicStroke(8f);
		
		outline = new Area(objStroke.createStrokedShape(
				new Line2D.Double(
						new Point2D.Double(coords0.get(1),coords0.get(2)) , 
						new Point2D.Double(coords1.get(1),coords1.get(2)) 				
				)));
		
		/*outline = new Line2D.Double(
						new Point2D.Double(coords0.get(1),coords0.get(2)) , 
						new Point2D.Double(coords1.get(1),coords1.get(2)) 
				);
		*/
		interior = new Area();
		
		super.initAreas();
	}
	
	
	
	/*
	public void draw(Graphics2D g2) {
		
		
		//g2.drawLine((int)coords0.get(1), (int)coords0.get(2), (int)coords1.get(1), (int)coords1.get(2));
		g2.setColor(geo.objColor);
		g2.draw(outline);
	}
	*/
	
	
	
	

    public Color getOutlineColor(){
    	return geo.objColor;
    }
    
    public Color getInteriorColor(){
    	return Color.BLACK;
    }
    
    public int getType(){
    	return SEGMENT3D;
    }
    
    
    /** returns the n first coords of the origin in physical coordinates */
    public GgbVector getOrigin(int n){
    	
    	return m.getColumn(2).subVector(1, n);
    	
    }
    
    /** returns the n first coords of the direction vector in physical coordinates */
    public GgbVector getVector(int n){
    	
    	return m.getColumn(1).subVector(1, n);
    	
    }
    

    ///////////////////////////////////////
    // Z-functions for intersection 
	public double getZ(DrawPoint3D d){
		
		GgbVector p = d.getCoords(3).projectLine(getOrigin(3),getVector(3));		
		return p.get(3);
	}

	
    ///////////////////////////////////////
    // intersection functions 
    public boolean intersectsPoint3D(DrawPoint3D d){
    	
    	return d.intersectsSegment3D(this);
    }
    
    public boolean intersectsSegment3D(DrawSegment3D d){
    	
    	return false;
    }

    
    
 


	public boolean hit(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
