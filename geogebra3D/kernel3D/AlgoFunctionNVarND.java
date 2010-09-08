package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author ggb3D
 *
 */
public class AlgoFunctionNVarND extends AlgoElement3D {

	
	/** input function */
	protected GeoElement inputFunction; 

	/** output function */
	protected GeoElement function; 
	
	//private NumberValue[] coords; // input : expression for each coord x, y, z, ...
	/** input : "from" values for each var */
	protected NumberValue[] from;  
	/** input : "to" values for each var */
	protected NumberValue[] to;
    //private GeoNumeric[] localVar;     // input : variables u, v, ...
	
	
	private AlgoFunctionNVarND(Construction cons,NumberValue[] from, NumberValue[] to){
		super(cons);
		
		//this.coords = coords;
    	this.from = from;
    	this.to = to;
    	//this.localVar = localVar;
	}
			
	/**
	 * Construct a function
	 * 
	 * @param cons 
	 * @param label 
	 * @param coords description of the function
	 * @param localVar var of the function
	 * @param from "from" values for each var
	 * @param to "to" values for each var
	 * 
	 */
	public AlgoFunctionNVarND(Construction cons, String label, 
			NumberValue[] coords,  
			GeoNumeric[] localVar, NumberValue[] from, NumberValue[] to)  {


		this(cons,from,to);
    	
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
			/* TODO
			fun[i] = new FunctionNVar(exp[i], funVar);
			fun[i].initFunction();
			 */	
			for (int j=0;j<localVar.length; j++)
				exp[i].replace(localVar[j], funVar[j]);
			fun[i] = new FunctionNVar(exp[i], funVar);
		}
        
		// create the function
		function = new GeoFunctionNVar(cons, fun[0]);
		
		//end of construction
		end(label,coords,localVar);
	}
	
	
	/**
	 * Construct a function
	 * 
	 * @param cons 
	 * @param label 
	 * @param f (x,y) function
	 * @param from "from" values for each var
	 * @param to "to" values for each var
	 * 
	 */
	public AlgoFunctionNVarND(Construction cons, String label, 
			GeoFunctionNVar f,  
			NumberValue[] from, NumberValue[] to)  {


		this(cons,from,to);
 		
		inputFunction = f;
		function = inputFunction.copy();
		
		//end of construction
		end(label,null,null);
	}
		
	private void end(String label, NumberValue[] coords, GeoNumeric[] localVar){
       
		int inputLength = from.length+to.length;
		if (coords!=null)
			inputLength+=coords.length;
		else
			inputLength+=1; //for the function
		if (localVar!=null)
			inputLength+=localVar.length;
		GeoElement[] input = new GeoElement[inputLength];
		
		int index = 0;
		
		if(coords!=null)
			for (int i=0;i<coords.length;i++){
				input[index]=(GeoElement) coords[i];
				index++;
			}
		else{
			input[index]=inputFunction;
			index++;
		}
		
		for (int i=0;i<from.length;i++){
			if (localVar!=null){
				input[index]=(GeoElement) localVar[i];
				index++;
			}
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

	/**
	 * @return the function
	 */
	public GeoFunctionNVar getFunction(){
		return (GeoFunctionNVar) function;
	}
	
	

	protected void compute() {

		if (inputFunction!=null)
			function.set(inputFunction);
		
		((GeoFunctionNVar) function).setInterval(
				getDouble(from), 
				getDouble(to)			
		);
		
	}
	
	private double[] getDouble(NumberValue[] values){
		double[] ret = new double[values.length];
		for (int i=0; i<values.length; i++)
			ret[i]=values[i].getDouble();
		return ret;
	}

	public String getClassName() {
		
		return "AlgoFunctionInterval";
	}
	
	

}
