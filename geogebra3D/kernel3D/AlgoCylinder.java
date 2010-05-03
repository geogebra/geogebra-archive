package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author ggb3D
 *
 */
public class AlgoCylinder extends AlgoElement3D {
	
	
	private GeoQuadric3D cylinder;
	private GeoPoint3D origin;
	private GeoVector3D direction;
	private NumberValue r;
	
	/**
	 * create a cone, with label.
	 * @param c construction
	 * @param label 
	 */
	public AlgoCylinder(Construction c, String label, GeoPoint3D origin, GeoVector3D direction, NumberValue r) {
		this(c,origin, direction, r);
		cylinder.setLabel(label);
	}
	/**
	 * create a cone
	 * @param c construction
	 */
	public AlgoCylinder(Construction c, GeoPoint3D origin, GeoVector3D direction, NumberValue r) {		
		super(c);
		cylinder = new GeoQuadric3D(c);
		this.origin = origin;
		this.direction = direction;
		this.r = r;
		
		setInputOutput(new GeoElement[] {origin,direction,(GeoElement) r}, new GeoElement[] {cylinder});
		compute();
	}
	
	
	
	protected String getClassName() {
		return "AlgoCylinder";
	}

	protected void compute() {

		cylinder.setCylinder(origin,direction,r.getDouble());
		
	}
	
	
	/**
	 * @return the cone
	 */
	public GeoQuadric3D getQuadric(){
		return cylinder;
	}

}
