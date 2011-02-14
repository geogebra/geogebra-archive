package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoCylinderPointRadius extends AlgoCylinder {
	
	
	private GeoPointND origin;
	

	/**
	 * @param c construction
	 */
	public AlgoCylinderPointRadius(Construction c, String label, GeoPointND origin, GeoElement secondInput, NumberValue r) {		
		super(c,secondInput,r);
		
		this.origin=origin;
		
		setInputOutput(new GeoElement[] {(GeoElement) origin,secondInput,(GeoElement) r}, new GeoElement[] {getQuadric()});
		compute();
		
		getQuadric().setLabel(label);
	}
	
	
	

	protected void compute() {
		
		//check origin
		if (!((GeoElement) origin).isDefined() || origin.isInfinite()){
			getQuadric().setUndefined();
			return;
		}
		
		//check direction
		Coords d = getDirection();
		
		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getQuadric().setUndefined();
			return;
		}
		
		// check radius
		double r = ((NumberValue) getRadius()).getDouble();
		if (Kernel.isZero(r)) {
			r = 0;
		}else if (r < 0) {
			getQuadric().setUndefined();
			return;
		}		
		
		d.normalize();
		
		getQuadric().setDefined();

		getQuadric().setCylinder(origin.getInhomCoordsInD(3),getDirection(),r);
		
	}
	
	/**
	 * 
	 * @return origin point
	 */
	protected GeoPointND getOrigin(){
		return origin;
	}
	

	
	

}
