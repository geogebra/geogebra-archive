package geogebra.euclidian;

import geogebra.kernel.GeoLinearInequality;
import geogebra.kernel.Kernel;
import geogebra.util.Unicode;

import java.awt.Graphics2D;

/**
 * Graphical representation of linear inequality
 * @author Michael Borcherds
 *
 */
public class DrawLinearInequality extends DrawLine {

    private GeneralPathClipped gp;
    private boolean offScreen; //to avoid OpenJDK glitch we don't draw offscreen line

    /**
     * Creates new drawable linear inequality
     * @param euclidianView
     * @param geo
     */
	public DrawLinearInequality(EuclidianView euclidianView,
			GeoLinearInequality geo) {
		super(euclidianView, geo);

	}
	
	final public void update() {  
		//	take line g here, not geo this object may be used for conics too
        isVisible = g.isEuclidianVisible(); 
        offScreen = false;
        if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(geo);
            gx = g.x;
            gy = g.y;
            gz = g.z;
            
            setClippedLine();
            
            // make copies so that label positioning (DrawLine.setLabelPosition()) works
            double xx1 = x1;
            double xx2 = x2;
            double yy1 = y1;
            double yy2 = y2;
			
    		// make sure x1 is min
    		if (xx1 > xx2) {
    			double temp = xx1;
    			xx1 = xx2;
    			xx2 = temp;
    			temp = yy1;
    			yy1 = yy2;
    			yy2 = temp;
    		}
    		
    		// make sure y1 & y2 in right order for vertical line
    		if (yy1 < yy2 && Kernel.isEqual(xx1, xx2)) {
    			double temp = xx1;
    			xx1 = xx2;
    			xx2 = temp;
    			temp = yy1;
    			yy1 = yy2;
    			yy2 = temp;    			
    		}
 
    		char op = ((GeoLinearInequality)g).op;
       		
    		boolean above = op == '>' || op == Unicode.GREATER_EQUAL;
    		
    		//Application.debug("above = "+above);
    		if (g.y <= 0) above = !above;
    		//Application.debug("above = "+above);

    		// line on screen?		
    		if (!line.intersects( -EuclidianView.CLIP_DISTANCE,  -EuclidianView.CLIP_DISTANCE, view.width + EuclidianView.CLIP_DISTANCE, view.height + EuclidianView.CLIP_DISTANCE)) {				
    			
    			double meanX = (view.getXmax() + view.getXmin()) / 2 ;
    			double meanY = (view.getYmax() + view.getYmin()) / 2 ;
    			
    			// check if midpoint of screen is above or below line
    			double onScreen = g.x * meanX + g.y * meanY + g.z;
    			
    			//Application.debug(onScreen+" "+above+" "+xx1+" "+xx2);
    			
    			// vertical line
    			if (Kernel.isZero(g.y)) onScreen = -onScreen;
    			
    			if ((onScreen > 0) == above) {
    	    		if (gp == null) gp = new GeneralPathClipped(view);
    	    		else gp.reset();
    	    		//Application.debug("drawing rectangle");
    	    		offScreen = true; 
    	    		gp.moveTo(0, 0);
    	    		gp.lineTo(0, view.height);
    	    		gp.lineTo(view.width, view.height);
    	    		gp.lineTo(view.width, 0);
    	    		gp.lineTo(0, 0);
    	    		gp.closePath();
    				
    			}
    			
    			else {
    				isVisible = false;
    			}
    		}
    		else
    		{
    		
	    		if (gp == null) gp = new GeneralPathClipped(view);
	    		else gp.reset();
	    		
	   		
	    		gp.moveTo(xx2, yy2);
	    		gp.lineTo(xx1, yy1);
	    		
	
	    		if (above) {
		    		if (xx1 < 0) 
		    			gp.lineTo(0, 0);
		    		
		    		if (yy1 > view.height) {
		    			gp.lineTo(0, view.height);
		    			gp.lineTo(0, 0);
		    		} 
		    		
		    		if (yy2 > 0) {
			    		gp.lineTo(view.width, 0);
			    		if (xx2 < view.width) gp.lineTo(view.width, view.height);
		    		}
	    		} else { // below
	    			
	    			if (yy1 < 0) {
	    				gp.lineTo(0, 0);
	    				gp.lineTo(0, view.height);
	    			}
	    			
		    		if (xx1 < 0) 
		    			gp.lineTo(0, view.height);
		    		
		    		if (yy2 < view.height) {
		    			gp.lineTo(view.width, view.height);
			    		if (xx2 < view.width) {
			    			gp.lineTo(view.width, 0);
			    		} 
		    		}
		    		
		    		    			
	    		}
	    		
	    		gp.lineTo(xx2, yy2);
	    		
	    		//gp.append(line, true);
	    		gp.closePath();
	            
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
            
    		}
            if (labelVisible) {
				labelDesc = geo.getLabelDescription();
				setLabelPosition();      
				addLabelOffset(true);
            }              
        }
    }
	
    public void draw(Graphics2D g2) {                                
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
            //in OpenJDK drawing offscreen line causes trouble, so we have to check
			if(!offScreen)g2.draw(line);
					
			g2.setColor(geo.getObjectColor());
			fill(g2, gp, false);

            // label
            if (labelVisible) {
				g2.setFont(view.fontLine);
				g2.setColor(geo.getLabelColor());
				drawLabel(g2);
            }                            
        }
    }
        



}
