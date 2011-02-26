package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoCylinder extends AlgoElement3D {
	
	
	private GeoQuadric3D cylinder;
	private GeoElement secondInput;
	private NumberValue r;
	

	/**
	 * @param c construction
	 */
	public AlgoCylinder(Construction c, GeoElement secondInput, NumberValue r) {		
		super(c);
		cylinder = new GeoQuadric3D(c);
		this.r = r;
		
		this.secondInput = secondInput;
		

	}
	
	
	
	public String getClassName() {
		return "AlgoCylinderInfinite";
	}
	


	
	
	/**
	 * 
	 * @return second input
	 */
	protected GeoElement getSecondInput(){
		return secondInput;
	}
	
	/**
	 * 
	 * @return radius
	 */
	protected GeoElement getRadius(){
		return (GeoElement) r;
	}	
	/**
	 * 
	 * @return direction of the axis
	 */
	protected abstract Coords getDirection();
	
	
	/**
	 * @return the cone
	 */
	public GeoQuadric3D getQuadric(){
		return cylinder;
	}

}
