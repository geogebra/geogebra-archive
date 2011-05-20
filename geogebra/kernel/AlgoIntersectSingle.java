package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 * Single intersection point 
 */
public class AlgoIntersectSingle extends AlgoIntersect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// input
	private AlgoIntersect algo;
	private int index; // index of point in algo	
	
	// output
	private GeoPoint point;
	
	private GeoPoint [] parentOutput;

	// intersection point is index-th intersection point of algo
	AlgoIntersectSingle(String label, AlgoIntersect algo, int index) {
		super(algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo			
		
		// check index
		if (index < 0) 
			index = 0;
		else 
			this.index = index;
		
		point = new GeoPoint(algo.cons);								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		point.setLabel(label);		
	}
	
    protected boolean showUndefinedPointsInAlgebraView() {
    	return true;
    }
	
	public String getClassName() {
		return "AlgoIntersectSingle";
	}
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
	
	// for AlgoElement
	public void setInputOutput() {
		input = new GeoElement[3];
		input[0] = algo.input[0];
		input[1] = algo.input[1];

		//	dummy value to store the index of the intersection point
		// index + 1 is used here to let numbering start at 1
		input[2] = new GeoNumeric(cons, index+1); 
		
		output = new GeoPoint[1];
		output[0] = point;
	                   
		setDependencies(); // done by AlgoElement
	}
	
	public GeoPoint getPoint() {
		return point;
	}
	
	protected GeoPoint [] getIntersectionPoints() {
		return (GeoPoint []) output;
	}
		
	protected GeoPoint[] getLastDefinedIntersectionPoints() {	
		return null;
	}

    public boolean isNearToAlgorithm() {
    	return true;
    }
    
	protected final void initForNearToRelationship() {				
		parentOutput = algo.getIntersectionPoints();					
		
		// tell parent algorithm about the loaded position;
		// this is needed for initing the intersection algo with
		// the intersection point stored in XML files
		algo.initForNearToRelationship();
		algo.setIntersectionPoint(index, point);
		algo.compute();
	}

	protected void compute() {
		parentOutput = algo.getIntersectionPoints();
		
		if (input[0].isDefined() && input[1].isDefined() && index < parentOutput.length) {	
			// 	get coordinates from helper algorithm
			point.setCoords(parentOutput[index]);
		} else {
			point.setUndefined();
		}
	}   
	
	public void remove() {
		super.remove();
		algo.removeUser(); // this algorithm was a user of algo
	}



}
