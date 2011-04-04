package geogebra3D.euclidianForPlane;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.kernel3D.GeoPlane3D;


/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoCoordSys2D plane;

	/**
	 * 
	 * @param ec
	 * @param plane 
	 */
	public EuclidianViewForPlane(EuclidianController ec, GeoCoordSys2D plane) {
		super(ec, new boolean[]{ true, true }, true);
		
		this.plane = plane;
	}
	
	
	public boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D();
	}
	
	public void attachView() {
		kernel.attach(this);
	}


	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos(){

		kernel.notifyAddAll(this);
	}
	
	
	
	
	
	
	
	public Coords getInhomCoordsForView(Coords coords){
		return coords.projectPlane(getPlaneMatrix())[1];
	}
	
	public CoordMatrix getPlaneMatrix(){
		return plane.getCoordSys().getDrawingMatrix();
	}
	
}
