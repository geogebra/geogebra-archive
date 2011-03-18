package geogebra.kernel;

public abstract class AlgoElementCAS extends AlgoElement {

	public AlgoElementCAS(Construction c) {
		super(c);
	}

	protected GeoElement geo;
	protected AlgoElement algoCAS;
	

	public void remove() {  
    	super.remove();   
    	geo.removeAlgorithm(algoCAS);
    }

}
