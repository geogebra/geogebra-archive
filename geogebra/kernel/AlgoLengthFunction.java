package geogebra.kernel;

import geogebra.kernel.integration.GaussQuadIntegration;
import geogebra.kernel.roots.RealRootFunction;

/**
 * @author  Victor Franco Espino
 * @version 19-04-2007
 * 
 * Calculate Function Length between the numbers A and B: integral from A to B on T = sqrt(1+(f')^2)
 */

public class AlgoLengthFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoNumeric A, B; //input
	private GeoFunction f, f1; //f1 is f'(x)
    private GeoNumeric length; //output
	private RealRootFunction lengthFunction; //is T = sqrt(1+(f')^2)
	private GaussQuadIntegration gauss;
    	
	AlgoLengthFunction(Construction cons, String label, GeoFunction f, GeoNumeric A, GeoNumeric B){
    	this(cons, f, A, B);
        length.setLabel(label); 	
    }
		
	AlgoLengthFunction(Construction cons, GeoFunction f, GeoNumeric A, GeoNumeric B) {
        super(cons);
        this.A = A;
        this.B = B;
        this.f = f;
        length = new GeoNumeric(cons);
        
        //First derivative of function f
        AlgoDerivative algo = new AlgoDerivative(cons, f, null);
        this.f1 = (GeoFunction) algo.getDerivative();
        
    	lengthFunction = new LengthFunction();
		gauss = new GaussQuadIntegration(5);
        
        cons.removeFromConstructionList(algo);
        setInputOutput();
        compute(); 
	}
	 
    String getClassName() {
        return "AlgoLengthFunction";
    }

    void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f;
        input[1] = A;
        input[2] = B;
        
        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }
    
    GeoNumeric getLength() {
        return length;
    }

    final void compute() {
    	double a = A.value;
    	double b = B.value;
    	
		if (a <= b)
			length.setValue(gauss.integrate(lengthFunction, a, b));
		else
			//length.setValue(-gauss.integrate(lengthFunction, b, a));
			length.setValue(gauss.integrate(lengthFunction, b, a));
    }
    
    /**
	 * T = sqrt( 1 + f'(x)^2) 
	 */
	private class LengthFunction implements RealRootFunction {
		public double evaluate(double t) {
			double p = f1.evaluate(t);
			return Math.sqrt(1 + p*p);
		}
	}
}