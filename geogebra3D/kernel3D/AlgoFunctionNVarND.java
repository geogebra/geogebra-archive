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
public abstract class AlgoFunctionNVarND extends AlgoElement3D {
	
	
	protected GeoElement function; //output function
	
	private NumberValue[] coords; // input : expression for each coord x, y, z, ...
	protected NumberValue[] from;  // input : "from" and "to" values for each var 

	protected NumberValue[] to;
    private GeoNumeric[] localVar;     // input : variables u, v, ...
	
	/**
	 * 
	 */
	public AlgoFunctionNVarND(Construction cons, String label, 
			NumberValue[] coords,  
			GeoNumeric[] localVar, NumberValue[] from, NumberValue[] to)  {


		super(cons);
		
    	this.coords = coords;
    	this.from = from;
    	this.to = to;
    	this.localVar = localVar;
    	
    	
    	// we need to create Function objects for the coord NumberValues,
    	// so let's get the expressions of xcoord and ycoord and replace
    	// the localVar by a functionVar
    	FunctionVariable[] funVar = new FunctionVariable[localVar.length];
    	for (int i=0;i<localVar.length; i++){
    		funVar[i] = new FunctionVariable(kernel);
    		funVar[i].setVarString(localVar[i].getLabel());
    	}
		
		ExpressionNode[] exp = new ExpressionNode[coords.length];
		FunctionNVar[] fun = new FunctionNVar[coords.length];

		for (int i=0;i<coords.length;i++){
			exp[i]= kernel.convertNumberValueToExpressionNode(coords[i]);
			for (int j=0;j<localVar.length; j++)
				exp[i].replace(localVar[j], funVar[j]);
			fun[i] = new FunctionNVar(exp[i], funVar);
		}
        
		// create the function
		function = createFunction(cons, fun);
       
		GeoElement[] input = new GeoElement[coords.length+localVar.length+from.length+to.length];
		int index = 0;
		for (int i=0;i<coords.length;i++){
			input[index]=(GeoElement) coords[i];
			index++;
		}
		for (int i=0;i<localVar.length;i++){
			input[index]=(GeoElement) localVar[i];
			index++;
			input[index]=(GeoElement) from[i];
			index++;
			input[index]=(GeoElement) to[i];
			index++;		
		}
		
		
		setInputOutput(
				input, 
				new GeoElement[] {function});
		
		compute();      
		function.setLabel(label);
	}

	protected abstract GeoElement createFunction(Construction cons, FunctionNVar[] fun);
	
	
	
	
	
	
	

}
