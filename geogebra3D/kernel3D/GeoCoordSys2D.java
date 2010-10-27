package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPointND;

/** Simple interface for elements that have a 2D coord sys
 * @author matthieu
 *
 */
public interface GeoCoordSys2D extends GeoCoordSys, Region3D {
	
	
	/*
	
	public GeoCoordSys2D(Construction c) {
		super(c);
	}
	
	
	public GgbMatrix4x4 getDrawingMatrix(){
		return getCoordSys().getMatrixOrthonormal();
	}
	
	public GgbVector getViewDirection(){
		
		return getCoordSys().getNormal();
	}
	
	
	/////////////////////////////////////
	//
	// REGION3D INTERFACE
	//
	/////////////////////////////////////
	
	


	public GgbVector[] getNormalProjection(GgbVector coords) {
		return coords.projectPlane(getCoordSys().getMatrixOrthonormal());
	}

	public GgbVector[] getProjection(GgbVector coords,
			GgbVector willingDirection) {
		return coords.projectPlaneThruV(getCoordSys().getMatrixOrthonormal(),willingDirection);
	}

	public boolean isInRegion(GeoPointInterface P) {
		GgbVector planeCoords = getNormalProjection(((GeoPoint3D) P).getCoords())[1];
		return Kernel.isEqual(planeCoords.get(3),0,Kernel.STANDARD_PRECISION);
	}

	public void pointChangedForRegion(GeoPointInterface P) {
		
		P.updateCoords2D();
		P.updateCoordsFrom2D(false);
		
		
	}

	public void regionChanged(GeoPointInterface P) {
		pointChangedForRegion(P);
		
	}
	
	public GgbVector getPoint(double x2d, double y2d){
		return getCoordSys().getPoint(x2d,y2d);
	}
	
	
	*/

}
