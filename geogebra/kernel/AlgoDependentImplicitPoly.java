package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;

public class AlgoDependentImplicitPoly extends AlgoElement {


	private static final long serialVersionUID = 1L;
	private Equation equation;
	private ExpressionValue[][] coeff;  // input
	private GeoImplicitPoly implicitPoly;     // output 
    
    
	protected AlgoDependentImplicitPoly(Construction c,String label, Equation equ) {
		super(c, false);
		equation=equ;
		Polynomial lhs = equ.getNormalForm();
		coeff=lhs.getCoeff();
		for (int i=0;i<coeff.length;i++){
			for (int j=0;j<coeff[i].length;j++){
				if (coeff[i][j]!=null){
					// find constant parts of input and evaluate them right now
	    			if (coeff[i][j].isConstant()){
	    				coeff[i][j]=coeff[i][j].evaluate();
	    			}
	    			
	    			// check that coefficient is a number: this may throw an exception
	                ExpressionValue eval = coeff[i][j].evaluate();
	                ((NumberValue) eval).getDouble(); 
				}
    		}
    	}
    	c.addToConstructionList(this, false);
    	implicitPoly=new GeoImplicitPoly(c);
    	setInputOutput(); // for AlgoElement    
    	// compute value of dependent number        
    	compute(); 

    	implicitPoly.setLabel(label);
    }
	
	@Override
	protected void compute() {
		implicitPoly.setCoeff(coeff);
	}

	@Override
	protected void setInputOutput() {
		input = equation.getGeoElementVariables();  
		setOutputLength(1);        
        setOutput(0,implicitPoly);        
        setDependencies(); // done by AlgoElement
	}

	@Override
    public String getClassName() {
		return "AlgoDependentImplicitPoly";
	}
	
	public GeoImplicitPoly getImplicitPoly(){
		return implicitPoly;
	}
	
	public final String toString() {
        return equation.toString();
    }
	
	public final String toRealString() {
        return equation.toRealString();
    }
	

}
