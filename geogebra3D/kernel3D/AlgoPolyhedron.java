package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D {

	/** the polyhedron created */
	protected GeoPolyhedron polyhedron;
	
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
		
		if (faces == null){ //construct a pyramid with last point as apex
			int numPoints = points.length;
			faces = new int[numPoints][];
			for (int i=0; i<numPoints-1; i++){
				faces[i]=new int[3];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints-1);
				faces[i][2]=numPoints-1;//apex
				//Application.debug("faces = "+i+","+((i+1)%(numPoints-1))+","+(numPoints-1));
			}
			faces[numPoints-1]=new int[numPoints-1];
			for (int i=0; i<numPoints-1; i++)
				faces[numPoints-1][i]=i;
		}
		
		

		polyhedron = new GeoPolyhedron(c,points,faces);	
		
		setInputOutput(points, new GeoElement[] {polyhedron});
		
	}
	

	
	
	

	@Override
	protected void compute() {
		// TODO Auto-generated method stub

	}



	protected String getClassName() {
		return "AlgoPolyhedron";
	}
	

}
