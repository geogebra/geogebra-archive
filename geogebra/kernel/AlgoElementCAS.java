package geogebra.kernel;

import geogebra.main.Application;

public abstract class AlgoElementCAS extends AlgoElement {

	public AlgoElementCAS(Construction c) {
		super(c);
	}

	protected GeoElement geo;
	protected AlgoElement algoCAS;
	

	public void remove() {  
    	super.remove();  
    	if (geo != null)
    		geo.removeAlgorithm(algoCAS);
    }

}
