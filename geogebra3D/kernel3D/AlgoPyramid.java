package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPyramid extends AlgoPolyhedron {


	
	/** creates a pyramid regarding vertices (last one as apex)
	 * @param c construction 
	 * @param label name
	 * @param points vertices
	 */
	public AlgoPyramid(Construction c, String label, GeoPoint3D[] points) {
		this(c,points);
		polyhedron.setLabel(label);
	}
	
	/** creates a polyhedron regarding vertices (last one as apex)
	 * @param c construction 
	 * @param points vertices
	 */
	public AlgoPyramid(Construction c, GeoPoint3D[] points) {
		super(c,points,null);
		
	}
	
	



	protected String getClassName() {
		return "AlgoPyramid";
	}
	

}
