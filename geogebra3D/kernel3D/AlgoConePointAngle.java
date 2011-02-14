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
public abstract class AlgoConePointAngle extends AlgoElement3D {
	
	
	private GeoQuadric3D cone;
	
	private GeoPointND origin;
	private GeoElement secondInput;
	private NumberValue angle;
	

	/**
	 * @param c construction
	 */
	public AlgoConePointAngle(Construction c, String label, GeoPointND origin, GeoElement secondInput, NumberValue angle) {		
		super(c);
		
		cone = new GeoQuadric3D(c);
		
		this.origin=origin;
		this.angle = angle;
		
		this.secondInput = secondInput;
		
		setInputOutput(new GeoElement[] {(GeoElement) origin,secondInput,(GeoElement) angle}, new GeoElement[] {getQuadric()});
		compute();
		
		getQuadric().setLabel(label);

	}
	
	
	
	public String getClassName() {
		return "AlgoCone";
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
	protected GeoElement getAngle(){
		return (GeoElement) angle;
	}	
	/**
	 * 
	 * @return direction of the axis
	 */
	protected abstract Coords getDirection();
	
	
	/**
	 * 
	 * @return origin point
	 */
	protected GeoPointND getOrigin(){
		return origin;
	}
	
	/**
	 * @return the cone
	 */
	public GeoQuadric3D getQuadric(){
		return cone;
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
		
		// check angle
		double a = ((NumberValue) getAngle()).getDouble();
		double c = Math.cos(a);
		double s = Math.sin(a);

		if (c<0 || s<0) {
			getQuadric().setUndefined();
			return;
		}else if (Kernel.isZero(c)){//TODO if c=0 then draws a plane
			getQuadric().setUndefined();
			return;
		}	
		else if (Kernel.isZero(s)){//TODO if s=0 then draws a line
			getQuadric().setUndefined();
			return;
		}

		double r=s/c;
		
		d.normalize();
		
		getQuadric().setDefined();
		
		getQuadric().setCone(origin.getInhomCoordsInD(3),d,r);
		
	}

}
