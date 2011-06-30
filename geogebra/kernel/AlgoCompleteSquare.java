package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;

public class AlgoCompleteSquare extends AlgoElement {
	
	private GeoFunction f,square;
	private FunctionVariable fv;
	private MyDouble a,h,k; //a(x-h)^2+k
	private int lastDeg;
	private AlgoCoefficients algoCoef;
	public AlgoCompleteSquare(Construction cons,String label,GeoFunction f){
		super(cons);
		this.f=f;
		a = new MyDouble(kernel);
		h = new MyDouble(kernel);
		k = new MyDouble(kernel);
		
		fv = new FunctionVariable(kernel);	
		ExpressionNode squareE = new ExpressionNode(kernel,fv,ExpressionNode.MINUS,h)
					.power(new MyDouble(kernel,2)).multiply(a).plus(k);
		Function squareF = new Function(squareE,fv);
		squareF.initFunction();
		square = new GeoFunction(cons);		
		setInputOutput();
		square.setFunction(squareF);
		algoCoef = new AlgoCoefficients(cons,f);
		algoCoef.remove();
		compute();		
		lastDeg = 0;
		square.setLabel(label);
		
	}
	@Override
	protected void compute() {		
		algoCoef.compute();
		GeoList coefs = algoCoef.getResult();
		int degInt = algoCoef.getResult().size()-1;
		
		if(degInt %2 == 1 || degInt < 2 || f.isGeoFunctionConditional()){
			square.setUndefined();
			return;
		}
		
		for(int i=1;i<degInt;i++){
			if(2*i != degInt && !Kernel.isZero(((GeoNumeric)coefs.get(i)).getDouble())){
				square.setUndefined();
				return;
			}
		}
		
		if(lastDeg != degInt){
			ExpressionNode squareE;
			if(degInt == 2)
			 squareE = new ExpressionNode(kernel,fv,ExpressionNode.MINUS,h)
			.power(new MyDouble(kernel,2)).multiply(a).plus(k);
			else
			squareE = new ExpressionNode(kernel,
						new ExpressionNode(kernel,fv,ExpressionNode.POWER,new MyDouble(kernel,degInt/2))
					,ExpressionNode.MINUS,h)
					.power(new MyDouble(kernel,2)).multiply(a).plus(k);
			square.getFunction().setExpression(squareE);
		}
		lastDeg = degInt;
		fv.setVarString(f.getVarString());
		//px^2+qx+r		
		double r = ((GeoNumeric)coefs.get(0)).getDouble();
		double q = ((GeoNumeric)coefs.get(degInt/2)).getDouble();
		double p = ((GeoNumeric)coefs.get(degInt)).getDouble();
		//if one is undefined, others are as well
		square.setDefined(!Double.isNaN(r));
		a.set(p);
		h.set(-q/(2*p));
		k.set(r-q*q/(p*4));		
				
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0]=f;
		setOutputLength(1);
		setOutput(0,square);
		setDependencies();

	}
	public GeoFunction getResult(){
		return square;
	}

	@Override
	public String getClassName() {
		return "AlgoCompleteSquare";
	}

}
