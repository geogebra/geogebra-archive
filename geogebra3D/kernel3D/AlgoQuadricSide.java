package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public class AlgoQuadricSide extends AlgoQuadric {
	
	
	/**
	 * @param c construction
	 */
	public AlgoQuadricSide(Construction c, String label, GeoQuadric3DLimited inputQuadric) {		
		super(c,inputQuadric,null,new AlgoQuadricComputerSide());
		

		setInputOutput(new GeoElement[] {inputQuadric}, new GeoElement[] {getQuadric()});
		
		compute();
		
		getQuadric().setLabel(label);
	}
	
	
	private GeoQuadric3DLimited getInputQuadric(){
		return (GeoQuadric3DLimited) getSecondInput();
	}
	
	

	protected void compute() {
		
		//check origin
		if (!getInputQuadric().isDefined()){
			getQuadric().setUndefined();
			return;
		}
		
		//compute the quadric
		getQuadric().setDefined();
		getQuadric().setType(getInputQuadric().getType());
		getComputer().setQuadric(getQuadric(), getInputQuadric().getMidpoint3D(), getInputQuadric().getEigenvec3D(2), getInputQuadric().getHalfAxis(0));
		((GeoQuadric3DPart) getQuadric()).setLimits(getInputQuadric().getMin(), getInputQuadric().getMax());
	}
	



	protected Coords getDirection() {
		return null;
	}
	

	
	

}
