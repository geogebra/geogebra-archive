package geogebra3D.euclidianForPlane;

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;


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
	
	private GeoPlaneND plane;

	/**
	 * 
	 * @param ec
	 * @param plane 
	 */
	public EuclidianViewForPlane(EuclidianController ec, GeoPlaneND plane) {
		super(ec, new boolean[]{ true, true }, true);
		
		this.plane = plane;
	}

	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos(){
		for (GeoElement geo : getKernel().getConstruction().getGeoElements()){
			geo.addView(this); //TODO replace with link with 3D view
			add(geo);
		}
	}
	
	
	
	

	public boolean contains(GeoElement geo){
		switch(geo.getGeoClassType()){
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
			return plane.isInRegion((GeoPointND) geo);
		default:
			return false;
		}
	}
	
	
	public void getInhomCoords(GeoPointND P, double[] ret){
		Coords coords = plane.getNormalProjection(P.getInhomCoordsInD(3))[1];
		for (int i=0; i<ret.length; i++)
    		ret[i]=coords.get(i+1);
	}
	
}
