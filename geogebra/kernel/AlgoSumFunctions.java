/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


import geogebra.Application;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;


public class AlgoSumFunctions  extends AlgoElement {

	private static final long serialVersionUID = 1L;

    protected String getClassName() {
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
    	
       	GeoFunction fun1 = (GeoFunction)geoList.get(0);
       	GeoFunction fun2 = (GeoFunction)geoList.get(1);

       	ExpressionNode left = fun1.getFunctionExpression();
       	ExpressionNode right = fun2.getFunctionExpression();           	
    	
    	ExpressionNode sum = new ExpressionNode(kernel, left, ExpressionNode.PLUS, right);
    	
    	Function f = new Function(sum, fun1.getFunction().getFunctionVariable());
    	//f.setExpression(n1);
    	//f.initFunction();
    	//f.set
    	
       	resultFun.setFunction(f);
       	resultFun.setDefined(true);

    
    
    /*
       	ExpressionNode left2 = new ExpressionNode(kernel,fun1.getFunction().getFunctionVariable());
       	ExpressionNode right2 = new ExpressionNode(kernel,fun2.getFunction().getFunctionVariable());
    	ExpressionNode n2 = new ExpressionNode(kernel, left2, ExpressionNode.PLUS, right2);
    	ExpressionValue ev = n2.evaluate();
    	
    	Application.debug(ev.getClass()+"");
    	
    	FunctionVariable fVar = new FunctionVariable((MyDouble)ev);
    	Function f = new Function(n1,fVar);*/
    }	
    


}
