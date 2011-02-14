package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * @author ggb3D
 *
 */
public class AlgoCylinderAxisRadius extends AlgoCylinder {
	
	
	private GeoLineND axis;
	

	/**
	 * @param c construction
	 */
	public AlgoCylinderAxisRadius(Construction c, String label, GeoLineND axis, NumberValue r) {		
		super(c,(GeoElement) axis,r);
		
		this.axis=axis;
		
		setInputOutput(new GeoElement[] {(GeoElement) axis,(GeoElement) r}, new GeoElement[] {getQuadric()});
		compute();
		
		getQuadric().setLabel(label);
	}
	
	
	

	protected void compute() {
		

		if (!((GeoElement) axis).isDefined()){
			getQuadric().setUndefined();
			return;
		}
		 
		
		Coords o = axis.getPointInD(3, 0);
		Coords d = axis.getPointInD(3, 1).sub(o);
		
		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			getQuadric().setUndefined();
			return;
		}
		
		d.normalize();
		
		getQuadric().setDefined();
		
		getQuadric().setCylinder(o,d,((NumberValue) getRadius()).getDouble());
		
	}
	
	

	protected Coords getDirection(){
		return axis.getPointInD(3, 1).sub(axis.getPointInD(3, 0));
	}

	final public String toString() {
		return app.getPlain("CylinderWithAxisARadiusB",((GeoElement) axis).getLabel(),getRadius().getLabel());

	}
	

	
	

}
