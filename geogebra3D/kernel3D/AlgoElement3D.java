package geogebra3D.kernel3D;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

abstract public class AlgoElement3D extends AlgoElement{

	public AlgoElement3D(Construction c) {
		super(c);
	}
	
	
	
	protected void setInputOutput(GeoElement[] a_input, GeoElement[] a_output) {
		
		input = a_input;
		output = a_output;
		setInputOutput();
	
	}
	
	
	protected void setInputOutput() {
		         
        setDependencies();
        compute();
       
	}

}
