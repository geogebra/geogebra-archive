package geogebra3D.euclidianForPlane;

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoPoint3D;

/**
 * Controler for 2D view created from a plane
 * @author matthieu
 *
 */
public class EuclidianControllerForPlane extends EuclidianController {

	public EuclidianControllerForPlane(Kernel kernel) {
		super(kernel);
	}
	
	
	
	protected void movePoint(boolean repaint) {
		
		Coords coords = ((EuclidianViewForPlane) view).getCoordsFromView(new Coords(xRW,yRW,0,1));
		
		//Application.debug("xRW, yRW= "+xRW+", "+yRW+"\n3D coords:\n"+coords);
		
		movedGeoPoint.setCoords(coords, true);
		((GeoElement) movedGeoPoint).updateCascade();
		
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}

}
