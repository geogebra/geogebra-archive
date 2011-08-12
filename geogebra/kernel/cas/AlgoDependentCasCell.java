package geogebra.kernel.cas;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.Function;

/**
 * Algorithm to take care of GeoCasCells and possibly 
 * other GeoElements in the construction. This algorithm updates 
 * a given output GeoCasCell (e.g. m := c + 3) and possibly a 
 * twin GeoElement object (e.g. GeoNumeric m = c + 3 when c is defined). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentCasCell extends AlgoElement {

	/**
	 * Creates a new algorithm to handle updates of the given cell.
	 * 
	 * @param outputCasCell the output cell that this algorithm should update.
	 */
	public AlgoDependentCasCell(GeoCasCell outputCasCell) {
		super(outputCasCell.getConstruction());
	}
	
	@Override
	protected void setInputOutput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
