package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D {

	/** the polyhedron created */
	private GeoPolyhedron polyhedron;
	
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

		polyhedron = new GeoPolyhedron(c,points,faces);
		
		setInputOutput(points, new GeoElement[] {polyhedron});
		
	}
	
	
	

	@Override
	protected void compute() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
