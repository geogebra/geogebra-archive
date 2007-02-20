package geogebra.kernel;


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
		else if (index >= algo.output.length)
			index = algo.output.length - 1;
		else 
			this.index = index;
		
		point = new GeoPoint(algo.cons);								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		point.setLabel(label);		
	}
	
    boolean showUndefinedPointsInAlgebraView() {
    	return true;
    }
	
	String getClassName() {
		return "AlgoIntersectSingle";
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
	
	GeoPoint [] getIntersectionPoints() {
		return (GeoPoint []) output;
	}
		
	GeoPoint[] getLastDefinedIntersectionPoints() {	
		return null;
	}

    public boolean isNearToAlgorithm() {
    	return true;
    }
    
	final void initForNearToRelationship() {				
		parentOutput = algo.getIntersectionPoints();					
		
		// tell parent algorithm about the loaded position;
		// this is needed for initing the intersection algo with
		// the intersection point stored in XML files
		algo.initForNearToRelationship();
		algo.setIntersectionPoint(index, point);
		algo.compute();
	}

	void compute() {		
		// get coordinates from helper algorithm
		point.setCoords(parentOutput[index]);						
	}   
	
	public void remove() {
		super.remove();
		algo.removeUser(); // this algorithm was a user of algo
	}



}
