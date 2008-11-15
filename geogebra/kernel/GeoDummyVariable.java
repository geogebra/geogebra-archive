package geogebra.kernel;

/**
 * Dummy GeoElement to be used for symbolic variable resolving
 * for the GeoGebra CAS.
 * 
 * @see kernel.setResolveUnkownVarsAsDummyGeos();
 * @author Markus Hohenwarter
 */
public class GeoDummyVariable extends GeoNumeric {

	private String varName;
	
	public GeoDummyVariable(Construction c, String varName) {
		super(c);	
		this.varName = varName;
	}
	
	public String toString() {
		return varName;
	}
	
	public String toValueString() {
		return varName;	
	}		

}
