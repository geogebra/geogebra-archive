package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D {

	/** the polyhedron created */
	protected GeoPolyhedron polyhedron;
	
	/** all points of the polyhedron (needed for the faces description) */
	private GeoPoint3D[] points;
	
	/** points used for input */
	//private GeoPoint3D[] inputPoints;
	
	/** points generated as output (e.g. for prisms) */
	//private GeoPoint3D[] outputPoints;
	
	/** in points, some are inputs, others are output (since outputPointsIndex) */
	private int outputPointsIndex;
	
	private int type;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param label name
	 * @param points vertices
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, String label, GeoPoint3D[] points, int type) {
		this(c,points,type);
		polyhedron.setLabel(label);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param a_points vertices
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, GeoPoint3D[] a_points, int type) {
		super(c);
		
		this.type = type;
		
		
		int[][] faces = null;
		int numPoints;
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID://construct a pyramid with last point as apex
			this.points = a_points;
			outputPointsIndex = a_points.length;
			numPoints = points.length;
			faces = new int[numPoints][];
			for (int i=0; i<numPoints-1; i++){
				faces[i]=new int[3];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints-1);
				faces[i][2]=numPoints-1;//apex
			}
			faces[numPoints-1]=new int[numPoints-1];
			for (int i=0; i<numPoints-1; i++)
				faces[numPoints-1][i]=i;
			break;
			
		case GeoPolyhedron.TYPE_PSEUDO_PRISM://construct a "pseudo-prismatic" polyhedron
			this.points = a_points;
			outputPointsIndex = a_points.length;
			numPoints = points.length /2;
			faces = new int[numPoints+2][];
			for (int i=0; i<numPoints; i++){
				faces[i]=new int[4];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints);
				faces[i][2]=numPoints + ((i+1)%(numPoints));
				faces[i][3]=numPoints + i;
			}
			faces[numPoints]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints][i]=numPoints-i-1;
			faces[numPoints+1]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints+1][i]=numPoints+i;
			break;

		case GeoPolyhedron.TYPE_PRISM://construct a prism
			numPoints = a_points.length - 1;
			this.points = new GeoPoint3D[numPoints*2];
			outputPointsIndex = a_points.length;
			for(int i=0;i<numPoints+1;i++)
				points[i] = a_points[i];
			for(int i=numPoints+1;i<numPoints*2;i++)
				points[i] = ((Kernel3D) kernel).Point3D(null, 0, 0, 0);
			compute();

			faces = new int[numPoints+2][];
			for (int i=0; i<numPoints; i++){
				faces[i]=new int[4];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints);
				faces[i][2]=numPoints + ((i+1)%(numPoints));
				faces[i][3]=numPoints + i;
			}
			faces[numPoints]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints][i]=i;
			faces[numPoints+1]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints+1][i]=numPoints+i;
			break;
		}
		
		end(c, faces);
		

		
	}
	
	
	
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF TYPE NONE
	////////////////////////////////////////////

	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param label name
	 * @param points vertices
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, String label, GeoPoint3D[] points, int[][] faces) {
		this(c,points,faces);
		polyhedron.setLabel(label);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param points vertices
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, GeoPoint3D[] points, int[][] faces) {
		super(c);
		this.type = GeoPolyhedron.TYPE_NONE;
		end(c,faces);
		
	}
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	private void end(Construction c, int[][] faces){
		
		polyhedron = new GeoPolyhedron(c,this.points,faces);	
		
		GeoSegment3D[] segments = polyhedron.getSegments();
		GeoPolygon3D[] polygons = polyhedron.getFaces();
		
		// input : points from 0 to outputPointsIndex
		GeoElement[] input = new GeoElement[outputPointsIndex];	
		for(int i=0; i<outputPointsIndex; i++)
			input[i] = points[i];
		
		// output : polyhedron, polygons, segments, and points from outputPointsIndex to end			
		GeoElement[] output = new GeoElement[1+polygons.length+segments.length+(points.length-outputPointsIndex)];	
		
		output[0] = polyhedron;
		for(int i=0; i<polygons.length; i++)
			output[1+i] = polygons[i];
		for(int i=0; i<segments.length; i++)
			output[1+polygons.length+i] = segments[i];
		for(int i=0; i<points.length-outputPointsIndex; i++){
			output[1+polygons.length+segments.length+ i] = points[outputPointsIndex+i];
		}

		
		setInputOutput(input, output);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	// overrides AlgoElement.doSetDependencies() to avoid every output.setParentAlgorithm(this)
	/**
	 * 
	 */
	
	
	protected void doSetDependencies() {
 
		
		for (int i=outputPointsIndex;i<points.length;i++)
			points[i].setParentAlgorithm(this);
			
		
        polyhedron.setParentAlgorithm(this); 
       
        cons.addToAlgorithmList(this);  
    }
    

	
	
	protected void compute() {
		switch(type){
		case GeoPolyhedron.TYPE_PRISM:
			//translation from bottom to top
			Ggb3DVector v = points[outputPointsIndex-1].getCoords().sub(points[0].getCoords());
			//Application.debug("v=\n"+v);
			//translate all output points
			for (int i=outputPointsIndex;i<points.length;i++){
				points[i].setCoords(points[i-outputPointsIndex+1].getCoords().add(v));
				points[i].updateCoords();
				points[i].updateCascade();
				//Application.debug("point["+i+"]="+points[i]);
			}
			break;
		default:
		}

	}



	protected String getClassName() {
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			return "AlgoPyramid";
		case GeoPolyhedron.TYPE_PRISM:
			return "AlgoPrism";
		default:
			return "AlgoPolyhedron";
		}
	}
	

}
