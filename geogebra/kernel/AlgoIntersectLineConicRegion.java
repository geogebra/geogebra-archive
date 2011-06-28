/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLineConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianConstants;

import java.awt.Color;

/**
 *
 * @version 
 */
public class AlgoIntersectLineConicRegion extends AlgoIntersectLineConic {    

	private static final long serialVersionUID = 1L;
	
	private GeoLine[] lines ; //output
	private int numberOfPoints;
	private int numberOfLineParts;
	private int numberOfOutputLines;
	String labelPrefixForLines;

    public String getClassName() {
        return "AlgoIntersectLineConicRegion";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECTION_CURVE;
    }
    
    
    AlgoIntersectLineConicRegion(Construction cons, String[] labels, GeoLine g, GeoConic c) {
        super(cons, g, c);
        
        setLabels(labels);
    }
    
    protected void initElements() {
    	super.initElements();
    	numberOfPoints = P.length;
        for (int i = 0; i<numberOfPoints; i++) {
        	setOutputDependencies(P[i]);

        }
    	
        
        Color BLUE_VIOLET= new Color(153,0,255);
        int THICK_LINE_WITHIN_LINE = 4;
        
        lines = new GeoLine[4];
        lines[0] = new GeoLine(cons);
        lines[1] = new GeoRay(cons);
        lines[2] = new GeoSegment(cons);
        lines[3] = new GeoRay(cons);
        for (int i = 0; i<4; i++) {
        	setOutputDependencies(lines[i]);
           	lines[i].setLineThickness(THICK_LINE_WITHIN_LINE); 
            lines[i].setObjColor(BLUE_VIOLET);
        }
    }

	protected GeoLine[] getIntersectionLines() {
		GeoLine[] ret = new GeoLine[numberOfOutputLines];
		for (int i = 0; i<numberOfOutputLines; i++){
			ret[i] = (GeoLine)output[numberOfPoints+i];
		}
		return ret;
	}
   
	public int getNumOfLineParts() {
		return numberOfLineParts;
	}
	public int getOutputSize() {
		return numberOfOutputLines;
	}
	protected void compute() {
        super.compute();
        
        //build lines
        
        switch (intersectionType) {
        case INTERSECTION_PRODUCING_LINE: //contained in degenerate conic
		case INTERSECTION_ASYMPTOTIC_LINE: //intersect at no point
		case INTERSECTION_PASSING_LINE: //intersect at no point
			numberOfLineParts = 1;
    		lines[0].set(g);

    		lines[1].setUndefined();
    		lines[2].setUndefined();
			lines[3].setUndefined();

    		break;
		case INTERSECTION_MEETING_LINE:
			numberOfLineParts = 2;
			lines[0].setUndefined();
			
    		((GeoRay)lines[3]).set(Q[0], g);
    		lines[2].setUndefined();
    		((GeoRay)lines[1]).set(Q[0], g); lines[1].changeSign();
    		
    		break;
    		
		case INTERSECTION_TANGENT_LINE: //tangent at one point
			numberOfLineParts = 3;
			lines[0].setUndefined();
			((GeoRay)lines[3]).set(Q[0], g);
			((GeoSegment)lines[2]).set(Q[0], Q[0], g);
			((GeoRay)lines[1]).set(Q[0], g); lines[1].changeSign();
			
    		break;
    		
		case INTERSECTION_SECANT_LINE: //intersect at two points
			numberOfLineParts = 3;
			lines[0].setUndefined();

			((GeoRay)lines[3]).set(Q[1], g);
			((GeoSegment)lines[2]).set(Q[0], Q[1], g);
			((GeoRay)lines[1]).set(Q[0], g); lines[1].changeSign();

    		break;
        }

        //set visibility according to number of points and type of intersection

        boolean currentPartIsInRegion = false;
        Coords ex = null;
        double t0, t1 = 0;
        switch (c.type){
        case GeoConic.CONIC_PARABOLA:
        	ex = c.getEigenvec(0);
        	if (numberOfLineParts == 2) {
        	
        	currentPartIsInRegion = Kernel.isGreater(0, 
        				g.getCoords().dotproduct(ex));
        	}
        	break;
        case GeoConic.CONIC_HYPERBOLA:
        	ex = c.getEigenvec(0);
        	
        	if (numberOfLineParts == 2) {
        		c.pointChanged(Q[0]);
        		t0 = Q[0].getPathParameter().getT();
        		currentPartIsInRegion = Kernel.isGreater(1, t0) ^
        				Kernel.isGreater(g.getCoords().dotproduct(ex), 0);
        	} else if (numberOfLineParts == 3) {
        		c.pointChanged(Q[0]);
        		c.pointChanged(Q[1]);
        		t0 = Q[0].getPathParameter().getT();
        		t1 = Q[1].getPathParameter().getT();
        		currentPartIsInRegion = Kernel.isGreater(1, t0) ^
        				Kernel.isGreater(1, t1); 
        	}
        	break;
        case GeoConic.CONIC_INTERSECTING_LINES:
        	if (numberOfLineParts == 1) {
        		currentPartIsInRegion = true;
        	} else if (numberOfLineParts == 2) {
        		c.pointChanged(Q[0]);
        		t0 = Q[0].getPathParameter().getT();
        		currentPartIsInRegion = 
        			(inOpenInterval(t0,1,2) || inOpenInterval(t0,-1,0)) 
        			^ Kernel.isGreater(g.getCoords().dotproduct(ex), 0);
        	} else if (numberOfLineParts == 3) {
        		c.pointChanged(Q[0]);
        		c.pointChanged(Q[1]);
        		t0 = Q[0].getPathParameter().getT();
        		t1 = Q[1].getPathParameter().getT();
        		currentPartIsInRegion = 
        			(inOpenInterval(t0,-1,0) && inOpenInterval(t1,1,2)) ||
        			(inOpenInterval(t1,-1,0) && inOpenInterval(t0,1,2)) ||
        			(inOpenInterval(t0,0,1) && inOpenInterval(t1,2,3)) ||
        			(inOpenInterval(t1,0,1) && inOpenInterval(t0,2,3));
        	}
        	break;
        case GeoConic.CONIC_PARALLEL_LINES:
        	if (numberOfLineParts == 1) {
        		if (Kernel.isGreater( -g.z/((g.x)*(g.x)+(g.y)*(g.y)), 0))
        			currentPartIsInRegion = true;
        	}
        	break;
        default:
        	//currentPartIsInRegion = false;
        	break;
        }
        currentPartIsInRegion ^= c.isInverseFill();
        
        //choose the right lines and update output
        numberOfOutputLines = 0;

        for (int i = 0; i<lines.length; i++) {
        	if (!lines[i].isDefined()) {
        		continue;
        	}
        	
        	if (!currentPartIsInRegion) {
        		lines[i].setUndefined();
        	} else {
        		//output[numberOfPoints+outputSize] = lines[outputSize];
        		numberOfOutputLines++;
        	}
        	currentPartIsInRegion = !currentPartIsInRegion;
        }
        
        output = new GeoElement[numberOfPoints+numberOfOutputLines];
        int index=0;
        for (index = 0; index<numberOfPoints; index++) {
        	output[index]=P[index];
        }
        for (int i = 0; i<lines.length; i++) {
        	if (lines[i].isDefined()) {
        		output[index] = lines[i];
        		index++;
        	}
        }
        
        GeoElement.setLabels(new String[] {null}, P);
        
        if (labelPrefixForLines!=null && labelPrefixForLines != "") {
        	for (int i = numberOfPoints; i<output.length; i++){
        		if (!output[i].labelSet)
        			output[i].setLabel(lines[i].getIndexLabel(labelPrefixForLines));
        	}
        } 
        
   

	}
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = c;
        input[1] = g;


       // output = lines;
        setDependencies(); // done by AlgoElement
    }    
    

	protected void setLabels(String[] labels) {
		if (P == null || P.length==0)
			GeoElement.setLabels(new String[] {null}, output);
		else {
			GeoElement.setLabels(labels, P);
			if (numberOfOutputLines!=0) {
				labelPrefixForLines = ((GeoElement)P[0]).getFreeLabel(P[0].getLabel().toLowerCase());
			}
			/*
        	for (int i = numberOfPoints; i<output.length; i++){
        		if (!output[i].labelSet)
        			output[i].setLabel(lines[i].getIndexLabel(labelPrefixForLines));
        	}*/
			GeoElement.setLabels(labelPrefixForLines,lines);
		}
	}
	private boolean inOpenInterval(double t, double a, double b) {
		return Kernel.isGreater(b, t) && Kernel.isGreater(t, a);
	}
}