package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

import java.util.ArrayList;

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
	private GeoPoint3D[] inputPoints;
	
	/** lists describing the faces (for polyhedron of TYPE_NONE) */
	private GeoList faces = null;
	
	
	/** points generated as output (e.g. for prisms) */
	private GeoPoint3D[] outputPoints;
	
	
	private int type;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	private AlgoPolyhedron(Construction c){
		super(c);
		polyhedron = new GeoPolyhedron(c);
	}
	
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
		this(c);
		
		this.type = type;
		
		
		
		int numPoints;
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID://construct a pyramid with last point as apex
			this.inputPoints = a_points;
			outputPoints = new GeoPoint3D[0];
			numPoints = inputPoints.length;
			
			
			for (int i=0; i<numPoints-1; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
				polyhedron.addPointToCurrentFace(this.inputPoints[(i+1)%(numPoints-1)]);
				polyhedron.addPointToCurrentFace(this.inputPoints[numPoints-1]);//apex
				polyhedron.endCurrentFace();
			}
			
			polyhedron.startNewFace();
			for (int i=0; i<numPoints-1; i++)
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
			polyhedron.endCurrentFace();
			break;
			
			/*
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
			*/

		case GeoPolyhedron.TYPE_PRISM://construct a prism
			inputPoints = a_points;
			numPoints = a_points.length - 1;
			GeoPoint3D[] points = new GeoPoint3D[numPoints*2];
			outputPoints = new GeoPoint3D[numPoints-1];
			for(int i=0;i<numPoints+1;i++)
				points[i] = a_points[i];
			for(int i=0;i<numPoints-1;i++){
				outputPoints[i] = ((Kernel3D) kernel).Point3D(null, 0, 0, 0);
				points[numPoints+1+i] = outputPoints[i];
			}
			

			
			for (int i=0; i<numPoints; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(points[i]);
				polyhedron.addPointToCurrentFace(points[(i+1)%(numPoints)]);
				polyhedron.addPointToCurrentFace(points[numPoints + ((i+1)%(numPoints))]);
				polyhedron.addPointToCurrentFace(points[numPoints + i]);
				polyhedron.endCurrentFace();
			}
			
			polyhedron.startNewFace();
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.endCurrentFace();
			
			polyhedron.startNewFace();
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[numPoints+i]);
			polyhedron.endCurrentFace();
			
			break;
		}
		
		polyhedron.updateFaces();
		updateInputOutput();
		compute();

		
	}
	
	
	
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF TYPE NONE
	////////////////////////////////////////////

	
	/** creates a polyhedron regarding faces description
	 * @param c construction 
	 * @param label name
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, String label, GeoList faces) {
		this(c,faces);
		polyhedron.setLabel(label);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, GeoList faces) {
		this(c);
		this.type = GeoPolyhedron.TYPE_NONE;
		
		setFaces(faces);

		outputPoints = new GeoPoint3D[0];
		
		updateInputOutput();
		
	}
	
	
	/** send GeoList description of faces to polyhedron
	 * and update polyhedron polygons and segments
	 * @param faces
	 */
	private void setFaces(GeoList faces){
		
		this.faces = faces;
		
		for(int i=0;i<faces.size();i++){ 
			polyhedron.startNewFace();
			GeoList list = (GeoList) faces.get(i);
			for (int j=0;j<list.size();j++){
				GeoPoint3D point = (GeoPoint3D) list.get(j);
				polyhedron.addPointToCurrentFace(point);
	
			}
			polyhedron.endCurrentFace();
		}
		
		polyhedron.updateFaces();

		
	}
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	private void updateInputOutput(){
		
		//polyhedron = new GeoPolyhedron(c,this.points,faces);	
		
		GeoSegment3D[] segments = polyhedron.getSegments();
		GeoPolygon3D[] polygons = polyhedron.getFaces();
		
		GeoElement[] input,output;
		
		// input : points from 0 to outputPointsIndex, or list of faces
		if(this.faces == null){
			input = inputPoints;
		}else{
			input = new GeoElement[1];
			input[0] = this.faces;
		}
		
		// output : polyhedron, polygons, segments, and points from outputPointsIndex to end			
		output = new GeoElement[1+polygons.length+segments.length+outputPoints.length];	
		
		output[0] = polyhedron;
		for(int i=0; i<polygons.length; i++)
			output[1+i] = polygons[i];
		for(int i=0; i<segments.length; i++)
			output[1+polygons.length+i] = segments[i];
		for(int i=0; i<outputPoints.length; i++){
			output[1+polygons.length+segments.length+ i] = outputPoints[i];
		}

		
		setInputOutput(input, output);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	// overrides AlgoElement.doSetDependencies() to avoid every output.setParentAlgorithm(this)
	/**
	 * 
	 */
	
	
	protected void doSetDependencies() {
 
		
		for (int i=0;i<outputPoints.length;i++)
			outputPoints[i].setParentAlgorithm(this);
			
		
        polyhedron.setParentAlgorithm(this); 
       
        cons.addToAlgorithmList(this);  
    }
    

	
	
	protected void compute() {
		switch(type){
		case GeoPolyhedron.TYPE_PRISM:
			//translation from bottom to top
			Ggb3DVector v = inputPoints[inputPoints.length-1].getCoords().sub(inputPoints[0].getCoords());
			//Application.debug("v=\n"+v);
			//translate all output points
			for (int i=0;i<outputPoints.length;i++){
				outputPoints[i].setCoords(inputPoints[i+1].getCoords().add(v));
				outputPoints[i].updateCoords();
				outputPoints[i].updateCascade();
				//Application.debug("point["+i+"]="+points[i]);
			}
			break;
		case GeoPolyhedron.TYPE_NONE:
			
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
