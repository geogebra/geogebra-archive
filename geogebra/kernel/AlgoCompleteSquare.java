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
		compute();		
		lastDeg = 0;
		square.setLabel(label);
	}
	@Override
	protected void compute() {		
		double deg = f.getFunctionExpression().getDegree(f.getFunction().getFunctionVariables()[0]);
		int degInt = (int) deg;
		if(!Kernel.isEqual(deg, degInt) || degInt %2 == 1 || deg < 2){
			square.setUndefined();
			return;
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
		//px^2+qx+r; p+q+r=s;		
		double r = f.evaluate(0);
		double s = f.evaluate(1);
		//          p*2^d+q*(2^d/2)  => p*2^(d/2)+q  => p*(2^(d/2)-1)  => p
		double p = ((f.evaluate(2)-r)*Math.pow(2, -deg/2) - (s-r))  / (Math.pow(2, deg/2)-1);
		double q = s-p-r;		
		
		boolean isQuadratic = !f.isGeoFunctionConditional();
		double[] checkpoints = {Math.pow(10000,2/degInt),-Math.pow(10000,2/degInt),Math.PI,Math.E};
		for(int i=0;i<checkpoints.length;i++){
			double x=checkpoints[i];
			double dif = p*Math.pow(x,deg)+q*Math.pow(x,deg/2)+r- f.evaluate(x);
			if(!Kernel.isZero(dif*dif))				
				isQuadratic = false;			
		}
		if(!isQuadratic){
			square.setUndefined();
		}else{
			square.setDefined(true);
			a.set(p);
			h.set(-q/(2*p));
			k.set(r-q*q/(p*4));		
		}		
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
