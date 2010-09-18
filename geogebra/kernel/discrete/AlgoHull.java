/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.discrete;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.MyPoint;

import java.util.ArrayList;
import java.util.Collection;

import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.VoronoiAlgorithm;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.RepresentationInterface;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import signalprocesser.voronoi.statusstructure.VLinkedNode;


/**
 * Mode of a list. Adapted from AlgoMode
 * @author Michael Borcherds
 * @version 
 */

public class AlgoHull extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoNumeric percentage; // input
    private GeoLocus locus; // output   
    private ArrayList<MyPoint> al;
    private ArrayList<VPoint> vl;
    private int size;

    public AlgoHull(Construction cons, String label, GeoList inputList, GeoNumeric percentage) {
        super(cons);
        this.inputList = inputList;
        this.percentage=percentage;
               
        locus = new GeoLocus(cons);

        setInputOutput();
        compute();
        locus.setLabel(label);
    }

    public String getClassName() {
        return "AlgoHull";
    }

    protected void setInputOutput(){
        input = new GeoElement[percentage == null ? 1 : 2];
        input[0] = inputList;
        if (percentage != null) input[1] = percentage;

        output = new GeoElement[1];
        output[0] = locus;
        setDependencies(); // done by AlgoElement
    }

    public GeoLocus getResult() {
        return locus;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
   	
		TriangulationRepresentation.CalcCutOff calccutoff = new TriangulationRepresentation.CalcCutOff() {
            public int calculateCutOff(TriangulationRepresentation rep) {
                // Get variables
                double percent = (percentage == null || !percentage.isDefined()) ? 1 : percentage.getDouble();
                
                if (percent < 0) percent = 0;
                else if (percent > 1) percent = 1;
                
                double min = rep.getMinLength();
                double max = rep.getMaxLength();
                
                // Calculate normalised length based off percentage
                int val = (int)( percent * (max-min) + min );
                
                // Return value
                //updateLengthSlider(rep, val);
                //updateNormalisedLengthSlider(rep, val);
                return val;
            }
        };
        
        AbstractRepresentation representation;
        //vl = RepresentationFactory.convertPointsToBoundaryProblemPoints(vl);
        representation = RepresentationFactory.createTriangulationRepresentation();

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointInterface p = (GeoPointInterface)geo;
				p.getInhomCoords(inhom);
				//vl.add(new VPoint(inhom[0], inhom[1]));
				vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}

        //vl = RepresentationFactory.convertPointsToSimpleTriangulationPoints(vl);
       
        ///TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
        //BoundaryProblemRepresentation boundaryRep = (BoundaryProblemRepresentation) representation
        //trianglarrep.setCalcCutOff(calccutoff);
       // ArrayList<VPoint> edge = trianglarrep.getPointsFormingOutterBoundary();
        //trianglarrep.paint((Graphics2D) kernel.getApplication().getEuclidianView().getGraphics());

        
        
        vl = RepresentationFactory.convertPointsToTriangulationPoints(vl);
        representation = RepresentationFactory.createTriangulationRepresentation();
        //((TriangulationRepresentation)representation).setDetermineClustersMode();
        
        TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
        trianglarrep.setCalcCutOff(calccutoff);
        
        TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
        representationwrapper.innerrepresentation = representation;
        
        VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);
        
         ArrayList<VPoint> edge = ((TriangulationRepresentation)representation).getPointsFormingOutterBoundary();
        
        VHalfEdge outeredge = ((TriangulationRepresentation)representation).findOuterEdge();
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
       for (int i = 0 ; i < edge.size() ; i++) {
        	VPoint p = edge.get(i);
            al.add(new MyPoint(p.x, p.y, i != 0));

        }
        

		//cons.setSuppressLabelCreation(oldState);
		locus.setPoints(al);
		locus.setDefined(true);

        
        //outputList.setDefined(true);
       
    }
    
    public class TestRepresentationWrapper implements RepresentationInterface {
        
        /* ***************************************************** */
        // Variables
        
        private final ArrayList<VPoint> circleevents = new ArrayList<VPoint>();
        
        private RepresentationInterface innerrepresentation = null;
        
        /* ***************************************************** */
        // Data/Representation Interface Method
        
        // Executed before the algorithm begins to process (can be used to
        //   initialise any data structures required)
        public void beginAlgorithm(Collection<VPoint> points) {
            // Reset the triangle array list
            circleevents.clear();
            
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.beginAlgorithm(points);
            }
        }
        
        // Called to record that a vertex has been found
        public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 ) {
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.siteEvent(n1, n2, n3);
            }
        }
        public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y ) {
            // Add the circle event
            circleevents.add( new VPoint(circle_x, circle_y) );
            
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
            }
        }
        
        // Called when the algorithm has finished processing
        public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode) {
            // Call the inner representation
            if ( innerrepresentation!=null ) {
                innerrepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
            }
        }
        
        /* ***************************************************** */
    }

    

}
