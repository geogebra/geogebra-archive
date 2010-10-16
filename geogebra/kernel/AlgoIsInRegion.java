/**
 * 
 */
package geogebra.kernel;


/**
 * Determine whether point is in region.
 * @author kondr
 * 
 */
public class AlgoIsInRegion extends AlgoElement {

	private GeoPointInterface pi;
	private Region region;
	private GeoBoolean result;

	/**
	 * Creates new algo 
	 * @param c
	 * @param label
	 * @param pi
	 * @param region
	 */
	public AlgoIsInRegion(Construction c, String label, GeoPointInterface pi,
			Region region) {
		super(c);
		this.pi = pi;
		this.region = region;
		result = new GeoBoolean(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void compute() {		
		result.setValue(region.isInRegion(pi));
	}

	@Override
	protected void setInputOutput() {
		setOutputLength(1);
		setOutput(0, result);
		input = new GeoElement[2];
		input[0] = (GeoPoint) pi;
		input[1] = (GeoElement) region;
		setDependencies();
	}

	/** 
	 * Returns true iff point is in region.
	 * @return true iff point is in region
	 */
	public GeoBoolean getResult() {
		return result;
	}

	@Override
	public String getClassName() {
		return "AlgoIsInRegion";
	}

}
