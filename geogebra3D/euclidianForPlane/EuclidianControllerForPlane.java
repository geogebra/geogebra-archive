package geogebra3D.euclidianForPlane;

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * Controler for 2D view created from a plane
 * @author matthieu
 *
 */
public class EuclidianControllerForPlane extends EuclidianController {

	public EuclidianControllerForPlane(Kernel kernel) {
		super(kernel);
	}
	
	
	private Coords getCoordsFromView(double x, double y){
		return ((EuclidianViewForPlane) view).getCoordsFromView(new Coords(x,y,0,1));
	}
	
	
	protected void movePoint(boolean repaint) {
		
		Coords coords = getCoordsFromView(xRW,yRW);
		
		//Application.debug("xRW, yRW= "+xRW+", "+yRW+"\n3D coords:\n"+coords);
		
		movedGeoPoint.setCoords(coords, true);
		((GeoElement) movedGeoPoint).updateCascade();
		
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}
	
	
	
	protected GeoPointND createNewPoint(boolean forPreviewable){
	
		Coords coords = getCoordsFromView(xRW,yRW);
		
		GeoPointND ret = kernel.getManager3D().Point3DIn(null, ((EuclidianView) view).getPlaneContaining(), coords.getX(), coords.getY(), coords.getZ());
		return ret;
	}
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path){
		Coords coords = getCoordsFromView(xRW,yRW);
		return createNewPoint(forPreviewable, path, coords.getX(), coords.getY(), coords.getZ());
	}
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region){
		Coords coords = getCoordsFromView(xRW,yRW);
		return createNewPoint(forPreviewable, region, coords.getX(), coords.getY(), coords.getZ());
	}
	


	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1){
		return createCircle2ForPoints3D(p0, p1);
	}


}
