package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.main.Application;
import geogebra.main.MyError;

public class AlgoDependentImplicitPoly extends AlgoElement {


	private static final long serialVersionUID = 1L;
	private Equation equation;
	private ExpressionValue[][] coeff;  // input
	private GeoElement geoElement;     // output (will be a implicitPoly, line or conic)
	private FunctionNVar[] dependentFromFunctions;
    
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
    	if (equ.isForcedLine()){
    		geoElement=new GeoLine(c);
    	}else if (equ.isForcedConic()){
    		geoElement=new GeoConic(c);
    	}else if (equ.isForcedImplicitPoly()){
    		geoElement=new GeoImplicitPoly(c);
    	}else{
	    	switch (equ.degree()) {
				// linear equation -> LINE   
				case 1 :
					geoElement=new GeoLine(c);
					break;
				// quadratic equation -> CONIC                                  
				case 2 :
					geoElement=new GeoConic(c);
					break;
				default :
					geoElement=new GeoImplicitPoly(c);
	    	}
    	}
    	setInputOutput(); // for AlgoElement    
    	
    	compute(true); 

    	geoElement.setLabel(label);
    }
	
	@Override
	protected void compute(){
		compute(false);
	}
	
	protected void replaceGeoElement(GeoElement newElem){
		String label=geoElement.getLabel();
		geoElement.doRemove();
		geoElement=newElem;
		setInputOutput();
		geoElement.setLabel(label);
	}
	
	
	protected void compute(boolean first) {
		if (!first){
			try{
				if (equation.isFunctionDependent()){
					boolean functionChanged=false;
					for (int i=0;i<dependentFromFunctions.length;i++){
						if (dependentFromFunctions[i]!=null){
							if (!(input[i] instanceof FunctionalNVar)){
								functionChanged=true;
								break;
							}else{
								if (((FunctionalNVar)input[i]).getFunction()!=dependentFromFunctions[i]){
									functionChanged=true;
									break;
								}
							}
						}
					}
					if (functionChanged){
						equation.initEquation();
						coeff=equation.getNormalForm().getCoeff();
					}
				}
			}catch(MyError e){
				geoElement.setUndefined();
				return;
			}
		}
		switch (equation.degree()) {
			// linear equation -> LINE   
			case 1 :
				if (geoElement instanceof GeoLine){
					setLine();
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoLine(getConstruction()));
						setLine();
					}
				}
				break;
			// quadratic equation -> CONIC                                  
			case 2 :
				if (geoElement instanceof GeoConic){
					setConic();
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoConic(getConstruction()));
						setConic();
					}
				}
				break;
			default :
				if (geoElement instanceof GeoImplicitPoly){
					((GeoImplicitPoly)geoElement).setDefined();
					((GeoImplicitPoly)geoElement).setCoeff(coeff);
				}else{
					if (geoElement.hasChildren())
						geoElement.setUndefined();
					else{
						replaceGeoElement(new GeoImplicitPoly(getConstruction()));
						((GeoImplicitPoly)geoElement).setDefined();
						((GeoImplicitPoly)geoElement).setCoeff(coeff);
					}
				}
		}
	}
	
	protected void setLine(){
		ExpressionValue[] expr=new ExpressionValue[3];
		expr[2]=expr[1]=expr[0]=null;
		if (coeff.length>0){
			if (coeff[0].length>0){
				expr[2]=coeff[0][0];
				if (coeff[0].length>1){
					expr[1]=coeff[0][1];
				}
			}
			if (coeff.length>1){
				if (coeff[1].length>0){
					expr[0]=coeff[1][0];
				}
			}
		}
		double[] dCoeff=new double[expr.length];
		for (int i=0;i<expr.length;i++){
			if (expr[i]!=null){
				dCoeff[i]=((NumberValue)expr[i].evaluate()).getDouble();
			}else{
				dCoeff[i]=0;
			}
		}
		((GeoLine)geoElement).setCoords(dCoeff[0], dCoeff[1], dCoeff[2]);
	}
	
	protected void setConic(){
		ExpressionValue[] expr=new ExpressionValue[6];
		for (int i=0;i<6;i++){
			expr[i]=null;
		}
		if (coeff.length>0){
			if (coeff[0].length>0){
				expr[5]=coeff[0][0];
				if (coeff[0].length>1){
					expr[4]=coeff[0][1];
					if (coeff[0].length>2){
						expr[2]=coeff[0][2];
					}
				}
			}
			if (coeff.length>1){
				if (coeff[1].length>0){
					expr[3]=coeff[1][0];
					if (coeff[1].length>1){
						expr[1]=coeff[1][1];
					}
				}
				if (coeff.length>2){
					if (coeff[2].length>0){
						expr[0]=coeff[2][0];
					}
				}
			}
		}
		double[] dCoeff=new double[expr.length];
		for (int i=0;i<expr.length;i++){
			if (expr[i]!=null){
				dCoeff[i]=((NumberValue)expr[i].evaluate()).getDouble();
			}else{
				dCoeff[i]=0;
			}
		}
		((GeoConic)geoElement).setDefined();
		((GeoConic)geoElement).setCoeffs(dCoeff);
	}

	@Override
	protected void setInputOutput() {
		if (input==null){
			input = equation.getGeoElementVariables();
			dependentFromFunctions=new FunctionNVar[input.length];
			for (int i=0;i<input.length;i++){
				if (input[i] instanceof FunctionalNVar){
					dependentFromFunctions[i]=((FunctionalNVar)input[i]).getFunction();
				}else{
					dependentFromFunctions[i]=null;
				}
			}
		}
		if (getOutputLength()==0)
			setOutputLength(1);        
        setOutput(0,geoElement);        
        setDependencies(); // done by AlgoElement
	}

	@Override
    public String getClassName() {
		return "AlgoDependentImplicitPoly";
	}
	
	public GeoElement getGeo(){
		return geoElement;
//		if (type==GeoElement.GEO_CLASS_IMPLICIT_POLY)
//			return (GeoImplicitPoly)geoElement;
//		else
//			return null;
	}
	
	public final String toString() {
        return equation.toString();
    }
	
	public final String toRealString() {
        return equation.toRealString();
    }

	@Override
	protected String toExpString() {
		return geoElement.getLabel()+": "+equation.toString();
	}
	

}
