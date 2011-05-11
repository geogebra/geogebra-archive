package geogebra3D.gui;


import geogebra.gui.InputDialogCircleRadius;
import geogebra.gui.InputHandler;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;

/**
 * 
 *
 */
public class InputDialogCirclePointDirectionRadius extends InputDialogCircleRadius{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoPointND geoPoint;
	
	private GeoDirectionND forAxis;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param forAxis 
	 * @param kernel
	 */
	public InputDialogCirclePointDirectionRadius(Application app, String title, InputHandler handler, GeoPointND point, GeoDirectionND forAxis, Kernel kernel) {
		super(app, title, handler, kernel);
		
		geoPoint = point;
		this.forAxis = forAxis;

	}

	protected GeoElement createCircle(NumberValue num){

		return kernel.getManager3D().Circle3D(
				null,
				geoPoint,
				num,
				forAxis);

	}

}
