package geogebra.kernel;

import java.util.ArrayList;

public abstract class AlgoTransformation extends AlgoElement {

	public AlgoTransformation(Construction c) {
        super(c);  	
	}
	
	public ArrayList getMoveableInputPoints() {
		return null;
	}
	 
}
