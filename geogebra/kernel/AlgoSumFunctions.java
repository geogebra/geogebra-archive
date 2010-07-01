/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;


public class AlgoSumFunctions  extends AlgoElement {

	private static final long serialVersionUID = 1L;

    public String getClassName() {
        return "AlgoSumFunctions";
    }

	private GeoList geoList; //input
    public GeoNumeric Truncate; //input	
    public GeoFunction resultFun;
    
   
    public AlgoSumFunctions(Construction cons, String label, GeoList geoList) {
        this(cons, label, geoList, null);
    }

    public AlgoSumFunctions(Construction cons, String label, GeoList geoList, GeoNumeric Truncate) {
    	super(cons);
        this.geoList = geoList;
        this.Truncate=Truncate;
        
        resultFun = new GeoFunction(cons);

        setInputOutput();
        compute();
        resultFun.setLabel(label);
    }

    protected void setInputOutput(){
    	if (Truncate == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	}
    	else {
    		 input = new GeoElement[2];
             input[0] = geoList;
             input[1] = Truncate;
    	}

        output = new GeoElement[1];
        output[0] = resultFun;
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return resultFun;
    }
    

    protected final void compute() {
    	//Sum[{x^2,x^3}]
    	
    	int n = Truncate == null ? geoList.size() : (int)Truncate.getDouble();
    	
    	if (n == 0 || n > geoList.size()) {
    		resultFun.setUndefined();
    		return;
    	}
    	else if (n == 1)
    	{
    		if (!geoList.get(0).isGeoFunction()) {
        		resultFun.setUndefined();
        		return;
        	}
    		
           	GeoFunction fun1 = (GeoFunction)geoList.get(0);

        	FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
        	FunctionVariable x =  new FunctionVariable(kernel);

        	ExpressionNode left = fun1.getFunctionExpression().getCopy(fun1.getKernel());
        	
        	Function f = new Function(left.replace(x1,x),x);
        	
           	resultFun.setFunction(f);
           	resultFun.setDefined(true);
    		return;
    	}

		if (!geoList.get(0).isGeoFunction() || !geoList.get(1).isGeoFunction()) {
    		resultFun.setUndefined();
    		return;
    	}
		
		// try needed for Sum[Sequence[If[x < i, i x], i, 1, 3]] at the moment
		try {
	    	// add first two:
	    	resultFun = GeoFunction.add(resultFun,(GeoFunction)geoList.get(0), (GeoFunction)geoList.get(1));
	    	
	    	if (n == 2) return;
	    	
	    	for (int i = 2 ; i < n ; i++) {  	
	    		
	    		if (!geoList.get(i).isGeoFunction()) {
	        		resultFun.setUndefined();
	        		return;
	        	}
	    		resultFun = GeoFunction.add(resultFun,resultFun, (GeoFunction)geoList.get(i));
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
    		resultFun.setUndefined();
    		return;			
		}
    	
    	/*
    	// this works:
       	GeoFunction fun1 = (GeoFunction)geoList.get(0);
       	GeoFunction fun2 = (GeoFunction)geoList.get(1);

    	FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
    	FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
    	FunctionVariable x =  new FunctionVariable(kernel);
    	

    	ExpressionNode left = fun1.getFunctionExpression().getCopy(fun1.getKernel());
       	ExpressionNode right = fun2.getFunctionExpression().getCopy(fun2.getKernel());    
       	
    	ExpressionNode sum = new ExpressionNode(kernel, left.replace(x1,x), ExpressionNode.PLUS, right.replace(x2,x));
    	
    	Function f = new Function(sum,x);//, fun1.getFunction().getFunctionVariable());
    	//f.setExpression(n1);
    	//f.initFunction();
    	//f.set
    	
       	resultFun.setFunction(f);
       	resultFun.setDefined(true);*/

    
    
    /*
       	//ExpressionNode left2 = new ExpressionNode(kernel, left, ExpressionNode.FUNCTION, x);
       	//ExpressionNode right2 = new ExpressionNode(kernel, right, ExpressionNode.FUNCTION, x);
       	
    	
    	//ExpressionNode sum = new ExpressionNode(kernel, left, ExpressionNode.PLUS, right.replace(x2,x));
       	ExpressionNode left2 = new ExpressionNode(kernel,fun1.getFunction().getFunctionVariable());
       	ExpressionNode right2 = new ExpressionNode(kernel,fun2.getFunction().getFunctionVariable());
    	ExpressionNode n2 = new ExpressionNode(kernel, left2, ExpressionNode.PLUS, right2);
    	ExpressionValue ev = n2.evaluate();
    	
    	Application.debug(ev.getClass()+"");
    	
    	FunctionVariable fVar = new FunctionVariable((MyDouble)ev);
    	Function f = new Function(n1,fVar);*/
    }	
    


}
