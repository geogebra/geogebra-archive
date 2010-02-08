/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawLine.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec3D;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 * Draws a line or a ray.
 */
public final class DrawLine extends Drawable implements Previewable {

    // clipping attributes
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int BOTTOM = 3;           
    
    private GeoLine g;    
    //private double [] coeffs = new double[3];
    
    private Line2D.Double line;    
    private double y1, y2, x1, x2, k, d, gx, gy, gz;    
    private int labelPos = LEFT, p1Pos, p2Pos;
    private int x, y;    
    private boolean isVisible, labelVisible;
    
    private ArrayList points; // for preview
    private GeoPoint startPoint;
   
    // clipping attributes
    private boolean [] attr1 = new boolean[4], attr2 = new boolean[4];
    
    /** Creates new DrawLine */
    public DrawLine(EuclidianView view, GeoLine g) {      
    	this.view = view;          
        this.g = g;
        geo = g;              
        update();
    }
    
	/**
	 * Creates a new DrawLine for preview.     
	 */
	DrawLine(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;
		if (points.size() == 2) {
		GeoPoint p = (GeoPoint)points.get(1);
		p.setCoords(p.inhomX, Math.round(p.inhomY), 1);
		}
		g = new GeoLine(view.getKernel().getConstruction());
		updatePreview();
	} 
    
    final public void update() {  
		//	take line g here, not geo this object may be used for conics too
        isVisible = g.isEuclidianVisible(); 
        if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(geo);
            gx = g.x;
            gy = g.y;
            gz = g.z;
            
            setClippedLine();
			
            // line on screen?		
    		if (!line.intersects( -EuclidianView.CLIP_DISTANCE,  -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE)) {				
    			isVisible = false;
            	// don't return here to make sure that getBounds() works for offscreen points too
    		}
            
			// draw trace
			if (g.trace) {
				isTracing = true;
				Graphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null) drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					view.updateBackground();
				}
			}			
            
            if (labelVisible) {
				labelDesc = geo.getLabelDescription();
				setLabelPosition();      
				addLabelOffset(true);
            }              
        }
    }
    
    // transform line to screen coords
    // write start and endpoint into (x1,y1), (x2,y2)
    final private void setClippedLine() {   
    // first calc two points in screen coords that are on the line
        
        // abs(slope) < 1
        // y = k x + d
        // x1 = 0, x2 = width
        if (Math.abs(gx) * view.scaleRatio < Math.abs(gy)) {
            // calc points on line in screen coords
            k = gx / gy * view.scaleRatio; 
            d = view.yZero + gz/gy * view.yscale - k * view.xZero;
            
            x1 = -EuclidianView.CLIP_DISTANCE;
            y1 = k * x1 + d;            
            x2 = view.width + EuclidianView.CLIP_DISTANCE;
            y2 = k * x2 + d; 
            p1Pos = LEFT;
            p2Pos = RIGHT;            
            clipTopBottom();
        } 
        // abs(slope) >= 1
        // x = k y + d
        // y1 = height, y2 = 0
        else {
            // calc points on line in screen coords
            k = gy / (gx * view.scaleRatio) ; 
            d = view.xZero - gz/gx * view.xscale - k * view.yZero;
            
            y1 = view.height + EuclidianView.CLIP_DISTANCE;   
            x1 = k * y1 + d;
            y2 = -EuclidianView.CLIP_DISTANCE;
            x2 = k * y2 + d;
            p1Pos = BOTTOM;
            p2Pos = TOP;                        
            clipLeftRight();
        }                 
        
        if (line == null)
        	line = new Line2D.Double();
        line.setLine(x1, y1, x2, y2);
    }
        
    // Cohen & Sutherland algorithm for line clipping on a rectangle
    // Computergraphics I (Prof. Held) pp.100
    // points (0, y1), (width, y2) -> clip on y=0 and y=height
    final private void clipTopBottom() {
        // calc clip attributes for both points (x1,y1), (x2,y2)        
        attr1[TOP]      = y1 < -EuclidianView.CLIP_DISTANCE;
        attr1[BOTTOM]   = y1 > view.height + EuclidianView.CLIP_DISTANCE;                
        attr2[TOP]      = y2 < -EuclidianView.CLIP_DISTANCE;
        attr2[BOTTOM]   = y2 > view.height + EuclidianView.CLIP_DISTANCE;
        
        // both points outside (TOP or BOTTOM)
        if ((attr1[TOP] && attr2[TOP]) ||
            (attr1[BOTTOM] && attr2[BOTTOM]))
			return;        
        // at least one point inside -> clip        
        // point1 TOP -> clip with y=0
        if (attr1[TOP]) { 
            y1 = -EuclidianView.CLIP_DISTANCE; 
            x1 = (y1 - d)/k;  
            p1Pos = TOP;
        }
        // point1 BOTTOM -> clip with y=height
        else if (attr1[BOTTOM]) { 
            y1 = view.height + EuclidianView.CLIP_DISTANCE;
            x1 = (y1 - d)/k;             
            p1Pos = BOTTOM;
        }
        
        // point2 TOP -> clip with y=0
        if (attr2[TOP]) { 
            y2 = -EuclidianView.CLIP_DISTANCE; 
            x2 = (y2 - d)/k;  
            p2Pos = TOP;
        }
        // point2 BOTTOM -> clip with y=height
        else if (attr2[BOTTOM]) { 
            y2 = view.height + EuclidianView.CLIP_DISTANCE;
            x2 = (y2 - d)/k;             
            p2Pos = BOTTOM;
        }        
    }    
    
    // Cohen & Sutherland algorithm for line clipping on a rectangle
    // Computergraphics I (Prof. Held) pp.100
    // points (x1, 0), (x2, height) -> clip on x=0 and x=width
    final private void clipLeftRight() {
        // calc clip attributes for both points (x1,y1), (x2,y2)        
        attr1[LEFT]     = x1 < -EuclidianView.CLIP_DISTANCE;
        attr1[RIGHT]    = x1 > view.width + EuclidianView.CLIP_DISTANCE;                
        attr2[LEFT]     = x2 < -EuclidianView.CLIP_DISTANCE;
        attr2[RIGHT]    = x2 > view.width + EuclidianView.CLIP_DISTANCE;
        
        // both points outside (LEFT or RIGHT)
        if ((attr1[LEFT] && attr2[LEFT]) ||
            (attr1[RIGHT] && attr2[RIGHT]))
			return;        
        // at least one point inside -> clip        
        // point1 LEFT -> clip with x=0
        if (attr1[LEFT]) { 
            x1 = -EuclidianView.CLIP_DISTANCE; 
            y1 = (x1 - d)/k;  
            p1Pos = LEFT;
        }
        // point1 RIGHT -> clip with x=width
        else if (attr1[RIGHT]) { 
            x1 = view.width + EuclidianView.CLIP_DISTANCE;
            y1 = (x1 - d)/k;             
            p1Pos = RIGHT;
        }
        
        // point2 LEFT -> clip with x=0
        if (attr2[LEFT]) { 
            x2 = -EuclidianView.CLIP_DISTANCE; 
            y2 = (x2 - d)/k;  
            p2Pos = LEFT;
        }
        // point2 RIGHT -> clip with x=width
        else if (attr2[RIGHT]) { 
            x2 = view.width + EuclidianView.CLIP_DISTANCE;
            y2 = (x2 - d)/k;             
            p2Pos = RIGHT;
        }        
    }    
    
    // set label position (xLabel, yLabel)
    final private void setLabelPosition() {                      
        // choose smallest position change                
        // 1-Norm distance between old label position 
        // and point 1, point 2                
        if ( Math.abs(xLabel - x1) + Math.abs(yLabel - y1) > 
             Math.abs(xLabel - x2) + Math.abs(yLabel - y2) ) {          
            x = (int) x2; 
            y = (int) y2;
            labelPos = p2Pos;
        } else {
            x = (int) x1; 
            y = (int) y1;
            labelPos = p1Pos;
        }        
        
        // constant to respect slope of line for additional space        
        // slope for LEFT, RIGHT: k = gx/gy
        // slope for TOP, BOTTOM: 1/k = gy/gx
        switch (labelPos) {
            case LEFT:    
                xLabel = 5;
                if (2*y < view.height) {
                    yLabel = y + 16 + (int)(16 * (gx / gy));
                } else {
                    yLabel = y - 8 + (int)(16 * (gx / gy));
                }
                break;
                
            case RIGHT:        
                xLabel = view.width - 15;
                if (2*y < view.height) {
                    yLabel = y + 16 - (int)(16 * (gx / gy));
                } else {
                    yLabel = y - 8 - (int)(16 * (gx / gy));
                }
                break;
                
            case TOP:                      
                yLabel = 15;
                if (2*x < view.width) {
                    xLabel = x + 8 + (int)(16 * (gy / gx));
                } else {
                    xLabel = x - 16 + (int)(16 * (gy / gx));
                }
                break;
                
            
        
            case BOTTOM:        
                yLabel = view.height - 5;
                if (2*x < view.width) {
                    xLabel = x + 8 - (int)(16 * (gy / gx));
                } else {
                    xLabel = x - 16 - (int)(16 * (gy / gx));
                }
                break;
        }                     
    }

    final public void draw(Graphics2D g2) {                                
        if (isVisible) {        	
            if (geo.doHighlighting()) {
                // draw line              
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(line);                              
            }
            
            // draw line              
            g2.setPaint(geo.getObjectColor());
            g2.setStroke(objStroke);            
			g2.draw(line);              

            // label
            if (labelVisible) {
				g2.setFont(view.fontLine);
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
            }                            
        }
    }
        
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());
		g2.setStroke(objStroke);  
		g2.draw(line);
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;   
		if (isVisible) {
			startPoint = (GeoPoint) points.get(0);
		}		                              			                                           
	}
	
	Point2D.Double endPoint = new Point2D.Double();

	public void updateMousePos(int mx, int my) {		
		if (isVisible) { 			
			double xRW = view.toRealWorldCoordX(mx);
			double yRW = view.toRealWorldCoordY(my);
			

			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().altDown) {
				GeoPoint p = (GeoPoint)points.get(0);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));
				
				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15; 
				
				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);
				
				endPoint.x = xRW;
				endPoint.y = yRW;
				view.getEuclidianController().setLineEndPoint(endPoint);
			}
			else
				view.getEuclidianController().setLineEndPoint(null);
			
			
			// line through first point and mouse position			
			GeoVec3D.cross(startPoint, xRW, yRW, 1.0, g);
			if (g.isZero()) {
				isVisible = false;
				return;
			}
			gx = g.x;
			gy = g.y;
			gz = g.z;
			setClippedLine();                                   			                                            
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {			            
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(line);                        		
		}
	}
	
	public void disposePreview() {	
	}
    

    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return isVisible && line.intersects(x-3, y-3, 6, 6);
    }
    
    final public boolean isInside(Rectangle rect) {  
    	return false;   
    }
    
    final public GeoElement getGeoElement() {
        return geo;
    }      
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
}
