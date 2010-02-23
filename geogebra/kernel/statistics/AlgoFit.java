package geogebra.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import org.apache.commons.math.linear.*;

/**
 * AlgoFit
 * A general linear curvefit:
 * 		Fit[<List of Points>,<List of Functions>]
 * Example:
 *     f(x)=1, g(x)=x, h(x)=e^x
 *     L={A,B,...}
 *     c(x)=Fit[L,{f,g,h}]
 *     will give a least square curvefit:
 *     c(x)= a+b*x+c*e^x
 *     
 * Simple test procedure:
 * 		Make points A,B, ...
 * 		L={A,B,...}
 * 		f(x)=1, g(x)=x, h(x)=x^2,... =x^n
 * 		F={f,g,h,...}
 * 		right(x)=Regpoly[L,n]
 * 		fit(x)=Fit[L,F]
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-23
 */
public class AlgoFit extends AlgoElement {
	
	private static final boolean	DEBUG	=	true;		//false in distribution

	private static final long serialVersionUID = 1L;
	private GeoList pointlist; 			// input
	private GeoList functionlist;  		// output
	private GeoFunction fitfunction; 	// output
	
	//variables:
	private	int 			datasize		=	0;				//rows in M and Y
	private	int				functionsize	=	0;				//cols in M
	private	GeoFunction[]	functionarray	=	null;
	private	RealMatrix		M				=	null;
	private	RealMatrix		Y				=	null;
	private	RealMatrix		P				=	null;
	private FunctionVariable X				=	null;
	


	public AlgoFit(Construction cons, String label, GeoList pointlist,GeoList functionlist) {
		super(cons);

		//delete?: regMath = kernel.getRegressionMath();

		this.pointlist = pointlist;
		this.functionlist=functionlist;
		fitfunction = new GeoFunction(cons);
		setInputOutput();
		compute();
		fitfunction.setLabel(label);
	}// Constructor

	protected String getClassName() {
		return "AlgoFit";
	}

	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1]=  functionlist;
		output = new GeoElement[1];
		output[0] = fitfunction;
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFit() {
		return fitfunction;
	}

	protected final void compute() {
		GeoElement 	geo1	=	null;
		GeoElement  geo2	=	null;
		datasize		=	pointlist.size();				//rows in M and Y
		functionsize	=	functionlist.size();			//cols in M
		functionarray	=	new GeoFunction[functionsize];
		M				=	new Array2DRowRealMatrix(datasize,functionsize);
		Y				=	new Array2DRowRealMatrix(datasize,1);
		P				=	new Array2DRowRealMatrix(functionsize,1);

		
		if (!pointlist.isDefined() 		||	//Lot of things can go wrong...	
			!functionlist.isDefined() 	||
			(functionsize>datasize)   	||
			(functionsize<1)			||
			(datasize<1)					//Perhaps a max restriction of functions and data?
			)								//Even if noone would try 500 datapoints and 100 functions...
		{
				fitfunction.setUndefined();
				return;
		} else {							//We are in business...
			//Best to also check:
			geo1=functionlist.get(0);
			geo2=pointlist.get(0);
			if(!geo1.isGeoFunction() || !geo2.isGeoPoint()){
				fitfunction.setUndefined();
				return;
			}//if wrong contents in lists
			try{
				makeMatrixes();					//Get functions, x and y from lists
				
				//Solve for parametermatrix P:
				DecompositionSolver solver=new QRDecompositionImpl(M).getSolver();
				P=solver.solve(Y);
				errorMsg("P:");
				mprint(P);
				
				//Make fitfunction
				X=new FunctionVariable(kernel);
				
				Function f=new Function(makeFunction(),X);
				//f.resolveVariables();				//?? not sure if this helps...

				/*
				Function f=new Function(
						((GeoFunction)functionlist.get(2)).getFunctionExpression(),
						((GeoFunction)functionlist.get(2)).getFunction().getFunctionVariable()
						);
				*/
				fitfunction.setFunction(f);
				fitfunction.setDefined(true);				
				
			}catch(Throwable t){
				fitfunction.setUndefined();
				errorMsg(t.getMessage());
				if(DEBUG){t.printStackTrace();}
			}//try-catch
		}//if	

	}//compute()
	
	//Get info from lists into matrixes and functionarray
	private final  void makeMatrixes() throws Exception{
		GeoElement	geo=null;
		GeoPoint	point=null;
		double		x,y;
		
		//Make array of functions:
		for(int i=0;i<functionsize;i++){
			geo=functionlist.get(i);
			if(!geo.isGeoFunction()){
				throw(new Exception("Not functions in function list..."));
			}//if not function
			functionarray[i]=(GeoFunction) functionlist.get(i);
		}//for all functions
		//Make matrixes with the right values: M*P=Y
		M=new Array2DRowRealMatrix(datasize,functionsize);
		Y=new Array2DRowRealMatrix(datasize,1);
		for(int r=0;r<datasize;r++){
			geo=pointlist.get(r);
			if(!geo.isGeoPoint()){
				throw(new Exception("Not points in function list..."));
			}//if not point
			point=(GeoPoint)geo;
			x=point.getX();
			y=point.getY();
			Y.setEntry(r,0,y);
			for(int c=0;c<functionsize;c++){
				M.setEntry(r,c,functionarray[c].evaluate(x));
			}//for columns (=functions)			
		}//for rows (=datapoints)
		mprint(M);
		mprint(Y);
		
		/* ---------------------------- test/example 
		double[][] test={
				{1,1,1},
				{1,2,4},
				{1,3,9},
				{1,4,16},
				{1,5,25}
		};//test[][]
		double[] y={1.0,3.0,5.0,4.0,2.0};
		M=new Array2DRowRealMatrix(test,false);
		Y=new Array2DRowRealMatrix(y);
		errorMsg("M:");
		aprint(M);
		errorMsg("Y:");
		aprint(Y);
		------------------------------------------ */
		
	}//makeMatrixes()
	
	// Making expression node for p1*f(x)+p2*g(x)+p3*h(x)+...
	private final ExpressionNode makeFunction(){
		MyDouble p=null;
		Function     f=null;
		GeoFunction gf=null;
		ExpressionValue expr=null;
		ExpressionNode  prod=null;
		ExpressionNode   node=null;
		ExpressionNode	funcnode=null;	//Functions need special treatment
		
		for(int i=0;i<functionsize;i++){
			p=new MyDouble(kernel,P.getEntry(i,0));		//prameter
			gf=(GeoFunction)functionlist.get(i);		//function	*** todo: checks
			f=gf.getFunction();
			//funcnode=f.getExpression();
			//funcnode=new ExpressionNode(kernel,f,ExpressionNode.FUNCTION,f.getFunctionVariable());  ?? I feel FUNCTION should be used...
			// but it did not help :-( And I don't quite understand how to use it... 
			funcnode=new ExpressionNode(kernel,f.getExpression());

			prod=new ExpressionNode(kernel,p,ExpressionNode.MULTIPLY,funcnode);
			if(i==0){	//first
				expr=prod;
			}else if(i==(functionsize-1)) {  //last
				node=new ExpressionNode(kernel,expr,ExpressionNode.PLUS,prod);
			}else{      
				expr=new ExpressionNode(kernel,expr,ExpressionNode.PLUS,prod);
			}//if not first time
		}//for all functions

		errorMsg("node: "+node.toString());
		
		return node;
		
	}//makeFunction()
	
	
	/// --- Debug --- ///
    private final static void errorMsg(String s){
    	geogebra.main.Application.debug(s);
    }//errorMsg(String)   
    
  // --- SNIP --- /// *** Comment out when finished ***
 	
    // Hook
    public final void test(){
    	
    }//test()
    
    public void mprint(RealMatrix m){
    	int rows=m.getRowDimension();
    	int cols=m.getColumnDimension();
    	for(int r=0;r<rows;r++){
    		for(int c=0;c<cols;c++){
    			System.out.print(m.getEntry(r,c)+"  ");
    		}//for c
    		System.out.println();
    	}//for r
    }//mprint()

 
 
  // --- SNIP --- /// 

}// class AlgoFit
