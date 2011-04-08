package geogebra3D.gui.view.algebra;

import geogebra.gui.view.algebra.MyRenderer;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;


/**
 * Algebra cell renderer for 3D
 * @author mathieu
 *
 */
public class MyRenderer3D extends MyRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 */
	public MyRenderer3D(Application app) {
		super(app);
	}
	
	
	protected String getAlgebraDescriptionTextOrHTML(GeoElement geo){
		return geo.getAlgebraDescriptionTextOrHTML(app.getActiveEuclidianView());
	}


}
