package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author ggb3D
 *
 */
public class AlgoFunction2Var extends AlgoElement3D {
	
	
	private GeoFunction2Var function;
	private NumberValue type;
	private NumberValue coeff;
	private NumberValue startU, endU, startV, endV;
	
	/**
	 * create a cone, with label.
	 * @param c construction
	 * @param label 
	 */
	public AlgoFunction2Var(Construction c, String label, 
			NumberValue type, NumberValue coeff,
			NumberValue startU, NumberValue endU,
			NumberValue startV, NumberValue endV	) {


		super(c);
		
		function = new GeoFunction2Var(c);
		this.type = type;
		this.coeff = coeff;
		this.startU = startU;
		this.endU = endU;
		this.startV = startV;
		this.endV = endV;
		
		
		setInputOutput(
				new GeoElement[] {(GeoElement) type, (GeoElement) coeff, 
						(GeoElement) startU, (GeoElement) endU,
						(GeoElement) startV, (GeoElement) endV}, 
				new GeoElement[] {function});
		compute();
		
		function.setLabel(label);
	}
	
	
	
	protected String getClassName() {
		return "AlgoFunction2Var";
	}

	protected void compute() {

		function.set((int) type.getDouble(),coeff.getDouble());
		
		function.setInterval(startU.getDouble(), endU.getDouble(), startV.getDouble(), endV.getDouble());
		
	}
	
	
	/**
	 * @return the function
	 */
	public GeoFunction2Var getFunction(){
		return function;
	}

}
