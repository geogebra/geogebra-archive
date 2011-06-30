package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;

public class AlgoCompleteSquare extends AlgoElement {
	
	private GeoFunction f,square;
	private FunctionVariable fv;
	private MyDouble a,h,k; //a(x-h)^2+k
	
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
		square.setLabel(label);
	}
	@Override
	protected void compute() {		
		fv.setVarString(f.getVarString());
		//px^2+qx+r; p+q+r=s;
		double r = f.evaluate(0);
		double s = f.evaluate(1);		
		double p = 0.5*(s+f.evaluate(-1))-r;
		double q = s-p-r;
		boolean isQuadratic = !f.isGeoFunctionConditional();
		double[] checkpoints = {1000,-1000,Math.PI,Math.E};
		for(int i=0;i<checkpoints.length;i++){
			double x=checkpoints[i];
			if(!Kernel.isEqual(p*x*x+q*x+r, f.evaluate(x)))
				isQuadratic = false;
		}
		if(!isQuadratic){
			square.setUndefined();
		}else{
			square.setDefined(true);
			a.set(p);
			h.set(-q/2*p);
			k.set(r-q*q/p/4);		
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
