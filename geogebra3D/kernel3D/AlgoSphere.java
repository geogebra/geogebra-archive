package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

public class AlgoSphere extends AlgoElement3D {
	
	private GeoQuadric quadric;
	private GeoPoint3D center;
	private GeoNumeric radius;

	public AlgoSphere(Construction c, String label, GeoPoint3D center, GeoNumeric radius) {
		this(c,center,radius);
		quadric.setLabel(label);
	}
	
	
	public AlgoSphere(Construction c, GeoPoint3D center, GeoNumeric radius) {
		super(c);
		
		quadric = new GeoQuadric(c);		
		this.center = center;
		this.radius = radius;
		
		setInputOutput(new GeoElement[] {center,radius}, new GeoElement[] {quadric}); 
		
	}


	protected void compute() {
		quadric.setSphere(center, radius.getDouble());
	}



	protected String getClassName() {
		return "AlgoSphere";
	}
	
	
	public GeoQuadric getQuadric(){
		return quadric;
	}
	

}
