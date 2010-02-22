/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

//
 
package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.GeneralPathClipped;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class AlgoPolygonOperation extends AlgoIntersectAbstract {

	private static final long serialVersionUID = 1L;
	private GeoPolygon inPoly0; //input
	private GeoPolygon inPoly1; //input
    private GeoPolygon poly; //output	
    
    private GeoPoint [] points;
    private NumberValue opType;
    private EuclidianView ev;
    
    AlgoPolygonOperation(Construction cons, String[] labels, GeoPolygon inPoly0, GeoPolygon inPoly1, NumberValue opType) {
        super(cons);      
        
        ev = cons.getApplication().getEuclidianView();   
        this.opType = opType;
        
        this.inPoly0 = inPoly0;
        this.inPoly1 = inPoly1;
        
        points = new GeoPoint[0];
        poly = new GeoPolygon(cons, points);

        setInputOutput();       
        compute();     
        poly.setLabel(labels[0]);
            
    }


    protected String getClassName() {
        return "AlgoPolygonOperation";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];       
	    input[0] = inPoly0;
	    input[1] = inPoly1;
	    input[2] = opType.toGeoElement();
		// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        cons.addToAlgorithmList(this);

        // setOutput(); done in compute

        // parent of output
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
              
    }

    
	private void setOutput() {
		if (points == null)
			return;
		output = new GeoElement[1];
		output[0] = poly;
	}
    
  
    GeoPolygon getPoly() {
        return poly;
    }

   
    private Area getArea(GeoPoint[] points) {
    	
    	double [] coords = new double[2]; 
    	GeneralPathClipped gp = new GeneralPathClipped(ev);
		
		// first point
		points[0].getInhomCoords(coords);		
        gp.moveTo(coords[0], coords[1]);   
       
        for (int i=1; i < points.length; i++) {
			points[i].getInhomCoords(coords);
			//ev.toScreenCoords(coords);			
        	gp.lineTo(coords[0], coords[1]);
        }

        return new Area(gp);	
	}
    
      
    
	protected final void compute() {

		// Get Area instances from each input polygon
		Area a1 = getArea(inPoly0.getPoints());
		Area a2 = getArea(inPoly1.getPoints());

		// Perform desired operation on the Areas
		switch ((int) opType.getDouble()) {
		case 1:
			a1.intersect(a2);
			break;
		case 2:
			a1.add(a2);
			break;
		case 3:
			a1.subtract(a2);
			break;
		}

		if (a1.isEmpty()) {
			poly.setUndefined();
			return;
		}

		// Iterate through the path of the newly operated Area
		// and recover the polygon vertices.
		ArrayList<Double> xcoord = new ArrayList<Double>();
		ArrayList<Double> ycoord = new ArrayList<Double>();
		double[] coords = new double[6];

		PathIterator it = a1.getPathIterator(null);

		int type = it.currentSegment(coords);
		xcoord.add(coords[0]);
		ycoord.add(coords[1]);
		it.next();

		// System.out.println( coords[0] + " , " + coords[1]);
		while (!it.isDone()) {
			type = it.currentSegment(coords);
			xcoord.add(coords[0]);
			ycoord.add(coords[1]);
			if (type == PathIterator.SEG_CLOSE) {
				break;
			}
			// System.out.println(type + ": " + coords[0] + " , " + coords[1]);
			it.next();

		}

		
		// Update the points array to the correct size
		int n = xcoord.size();
		int oldPointNumber = points.length;
		if (n != oldPointNumber) {
			updatePointsArray(n);
			poly.setPoints(points);
			setOutput();
		}

		// Set the points to the new polgon vertices
		for (int k = 0; k < n; k++) {
			points[k].setCoords(xcoord.get(k), ycoord.get(k), 1);

		}

		// Compute area of poly (this will set poly defined to true)
		poly.calcArea();

	}
    	
	
	
	/**
	 * Ensures that the pointList holds n points.
	 * 
	 * @param n
	 */
	private void updatePointsArray(int n) {
		GeoPoint[] oldPoints = points;
		int oldPointsLength = oldPoints == null ? 0 : oldPoints.length;

		// new points
		points = new GeoPoint[n];

		// reuse old points
		for (int i = 0; i < oldPointsLength; i++) {
			if (i < points.length) {
				// reuse old point
				points[i] = oldPoints[i];
			} else {
				removePoint(oldPoints[i]);
			}
		}

		// create new points if needed
		for (int i = oldPointsLength; i < points.length; i++) {
			GeoPoint newPoint = new GeoPoint(cons);
			newPoint.setCoords(0, 0, 1); // set defined
			newPoint.setParentAlgorithm(this);
			// newPoint.setPointSize(A.pointSize);
			// newPoint.setEuclidianVisible(A.isEuclidianVisible() ||
			// B.isEuclidianVisible());
			newPoint.setAuxiliaryObject(true);
			points[i] = newPoint;
		}
	}

	private void removePoint(GeoPoint oldPoint) {
		// remove dependent algorithms (e.g. segments) from update sets of
		// objects further up (e.g. polygon) the tree
		ArrayList algoList = oldPoint.getAlgorithmList();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = (AlgoElement) algoList.get(k);
			for (int j = 0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);
		}

		// remove old point
		oldPoint.setParentAlgorithm(null);

		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = (AlgoElement) algoList.get(k);
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPointsSegment
					&& ((AlgoJoinPointsSegment) algo).getPoly() == poly) {
			} else {
				algo.remove();
			}
		}

	}
   
  
}
