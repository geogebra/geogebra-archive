package geogebra3D.gui.view.algebra;

import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.algebra.MyRenderer;
import geogebra.main.Application;


/**
 * Algebra view for 3D : change display regarding graphic view selected
 * 
 * @author mathieu
 *
 */
public class AlgebraView3D extends AlgebraView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param algCtrl
	 */
	public AlgebraView3D(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
/*
	protected MyRenderer newMyRenderer(Application app){
		return new MyRenderer3D(app);
	}
	*/

}
