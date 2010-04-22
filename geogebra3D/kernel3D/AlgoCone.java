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
	private NumberValue r;
	
	/**
	 * create a cone, with label.
	 * @param c construction
	 * @param label 
	 */
	public AlgoCone(Construction c, String label, GeoPoint3D origin, GeoVector3D direction, NumberValue r) {
		this(c,origin, direction, r);
		cone.setLabel(label);
	}
	/**
	 * create a cone
	 * @param c construction
	 */
	public AlgoCone(Construction c, GeoPoint3D origin, GeoVector3D direction, NumberValue r) {		
		super(c);
		cone = new GeoQuadric3D(c);
		this.origin = origin;
		this.direction = direction;
		this.r = r;
		
		setInputOutput(new GeoElement[] {origin,direction,(GeoElement) r}, new GeoElement[] {cone});
		compute();
	}
	
	
	
	protected String getClassName() {
		return "AlgoCone";
	}

	protected void compute() {

		cone.setCone(origin,direction,r.getDouble());
		
	}
	
	
	/**
	 * @return the cone
	 */
	public GeoQuadric3D getCone(){
		return cone;
	}

}
