package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author ggb3D
 *
 */
public class AlgoCone extends AlgoElement3D {
	
	
	private GeoQuadric3D cone;
	private GeoPoint3D origin;
	private GeoVector3D direction;
	private NumberValue angle;
	
	/**
	 * create a cone, with label.
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param direction 
	 * @param angle 
	 */
	public AlgoCone(Construction c, String label, GeoPoint3D origin, GeoVector3D direction, NumberValue angle) {
		this(c,origin, direction, angle);
		cone.setLabel(label);
	}
	/**
	 * create a cone
	 * @param c construction
	 */
	public AlgoCone(Construction c, GeoPoint3D origin, GeoVector3D direction, NumberValue angle) {		
		super(c);
		cone = new GeoQuadric3D(c);
		this.origin = origin;
		this.direction = direction;
		this.angle = angle;
		
		setInputOutput(new GeoElement[] {origin,direction,(GeoElement) angle}, new GeoElement[] {cone});
		compute();
	}
	
	
	
	public String getClassName() {
		return "AlgoCone";
	}

	protected void compute() {

		cone.setCone(origin,direction,angle.getDouble());
		
	}
	
	
	/**
	 * @return the cone
	 */
	public GeoQuadric3D getQuadric(){
		return cone;
	}

}
