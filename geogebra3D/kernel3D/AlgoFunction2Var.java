package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
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
		
		return new GeoFunction2Var(cons,fun);
	}
	
	
	
	protected String getClassName() {
		return "AlgoFunction2Var";
	}

	protected void compute() {

		((GeoFunction2Var) function).setInterval(from[0].getDouble(), to[0].getDouble(), from[1].getDouble(), to[1].getDouble());
		
	}
	
	
	/**
	 * @return the function
	 */
	public GeoFunction2Var getFunction(){
		return (GeoFunction2Var) function;
	}

}
