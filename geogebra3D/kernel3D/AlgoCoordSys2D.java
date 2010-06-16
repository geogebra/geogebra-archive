package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * Algo creating a 2D coord sys linked to 3D points.
 * <p>
 * The dependencies can be set or not (for helper algo).
 * 
 * 
 * @author ggb3D
 *
 */
public abstract class AlgoCoordSys2D extends AlgoElement3D {

	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;
	
	
	/** 3D points */
	private GeoPoint3D[] points;
	
	
	
	/** says if the vector Vx has to be parallel to xOy plane */
	private boolean vxParallelToXoy;
	
	/**
	 * create a 2D coord sys joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 */
	public AlgoCoordSys2D(Construction c, String label, GeoPoint3D[] points) {
		this(c,points);
		((GeoElement) cs).setLabel(label);
	}
	
	/**
	 * create a 2D coord sys joining points.
	 * @param c construction
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 */
	public AlgoCoordSys2D(Construction c, GeoPoint3D[] points) {		
		this(c,points,false,true);
	}
	
	/**
	 * create a 2D coord sys joining points.
	 * @param c construction
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 * @param vxParallelToXoy says if the vector Vx has to be parallel to xOy plane
	 * @param setDependencies says if the dependencies have to be set
	 */
	public AlgoCoordSys2D(Construction c, GeoPoint3D[] points, 
			boolean vxParallelToXoy, boolean setDependencies) {
		super(c);
		
		createCoordSys(c);
		this.points = points;
		this.vxParallelToXoy = vxParallelToXoy;

		
		GeoElement[] out = new GeoElement[1];		
		out[0]= (GeoElement) cs;
		
		
		//set input and output		
		setInputOutput(points, out, setDependencies);
		
		
		
		
	}
	
	
	/**
	 * create the coord sys
	 * @param c construction
	 */
	abstract protected void createCoordSys(Construction c);
	
	protected void compute() {
				
		GgbCoordSys coordsys = cs.getCoordSys();
		
		//recompute the coord sys
		coordsys.resetCoordSys();
		int i;
		for(i=0;(!coordsys.isMadeCoordSys())&&(i<points.length);i++)
			coordsys.addPoint(points[i].getCoords());
		
		if (coordsys.makeOrthoMatrix(true)){

			// check if other points lie on the coord sys
			for(;(i<points.length)&&(coordsys.isDefined());i++){
				//project the point on the coord sys
				GgbVector[] project=points[i].getCoords().projectPlane(coordsys.getMatrixOrthonormal());

				//check if the vertex lies on the coord sys
				if(!Kernel.isEqual(project[1].get(3), 0, Kernel.STANDARD_PRECISION))
					coordsys.setUndefined();

			}
			
			if (coordsys.isDefined())
				coordsys.makeEquationVector();
		}
		
	}

	
	/**
	 * return the cs
	 * @return the cs
	 */
	public GeoCoordSys2D getCoordSys() {		
		return cs;
	}

	
	
	
	
}
