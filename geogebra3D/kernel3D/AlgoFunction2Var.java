package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author ggb3D
 *
 */
public class AlgoFunction2Var extends AlgoFunctionNVarND {
	
		
	/**
	 * 
	 */
	public AlgoFunction2Var(Construction cons, String label, 
			NumberValue zcoord, 
			GeoNumeric localVarU, NumberValue Ufrom, NumberValue Uto, 
			GeoNumeric localVarV, NumberValue Vfrom, NumberValue Vto 		
	)  {


		super(cons, label,
				new NumberValue[] {zcoord}, 
				new GeoNumeric[] {localVarU, localVarV}, 
				new NumberValue[] {Ufrom, Vfrom}, new NumberValue[] {Uto, Vto}		
		);
		
	}
	
	
	
	protected GeoElement createFunction(Construction cons, FunctionNVar[] fun){
		
		return new GeoFunctionNVar(cons,fun[0]);
	}
	
	
	
	public String getClassName() {
		return "AlgoFunction2Var";
	}

	protected void compute() {

		((GeoFunctionNVar) function).setInterval(
				new double[] {from[0].getDouble(), from[1].getDouble()}, 
				new double[] {to[0].getDouble(), to[1].getDouble()}				
		);
		
	}
	
	
	/**
	 * @return the function
	 */
	public GeoFunctionNVar getFunction(){
		return (GeoFunctionNVar) function;
	}

}
