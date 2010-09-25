package geogebra.euclidian;

import geogebra.kernel.GeoLinearInequality;

import java.awt.Graphics2D;

public class DrawLinearInequality extends DrawLine {

    private GeneralPathClipped gp;

	public DrawLinearInequality(EuclidianView euclidianView,
			GeoLinearInequality geo) {
		super(euclidianView, geo);

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
    		
    		gp = new GeneralPathClipped(view);
    		gp.reset();
    		
    		// make sure x1 is min
    		if (x1 > x2) {
    			double temp = x1;
    			x1 = x2;
    			x2 = temp;
    			temp = y1;
    			y1 = y2;
    			y2 = temp;
    		}
    		
    		gp.moveTo(x2, y2);
    		gp.lineTo(x1, y1);
    		
    		boolean above = ((GeoLinearInequality)g).op == '>';
    		
    		if (above) {
	    		if (x1 < 0) 
	    			gp.lineTo(0, 0);
	    		
	    		if (y1 > view.height) {
	    			gp.lineTo(0, view.height);
	    			gp.lineTo(0, 0);
	    		} 
	    		
	    		if (y2 > 0) {
		    		gp.lineTo(view.width, 0);
		    		if (x2 < view.width) gp.lineTo(view.width, view.height);
	    		}
    		} else { // below
    			
    			if (y1 < 0) {
    				gp.lineTo(0, 0);
    				gp.lineTo(0, view.height);
    			}
    			
	    		if (x1 < 0) 
	    			gp.lineTo(0, view.height);
	    		
	    		if (y2 < view.height) {
	    			gp.lineTo(view.width, view.height);
		    		if (x2 < view.width) {
		    			gp.lineTo(view.width, 0);
		    		} 
	    		}
	    		
	    		    			
    		}
    		
    		gp.lineTo(x2, y2);
    		
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
			g2.draw(line);    
					
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
