package geogebra.kernel;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Calculate Curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
 */

public class AlgoCurvature extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
	private GeoFunction f, f1, f2; // f = f(x), f1 is f'(x), f2 is f''(x)
    private GeoNumeric K; //output
    
    AlgoCurvature(Construction cons, String label, GeoPoint A, GeoFunction f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    AlgoCurvature(Construction cons, GeoPoint A, GeoFunction f) {
        super(cons);
        this.f = f;
        this.A = A;
        K = new GeoNumeric(cons);
        
        //First derivative of function f
        AlgoDerivative algo = new AlgoDerivative(cons, f);
        cons.removeFromConstructionList(algo);
		this.f1 = (GeoFunction) algo.getDerivative();
		
		//Second derivative of function f
		algo = new AlgoDerivative(cons, f1);
		cons.removeFromConstructionList(algo);
		this.f2 = (GeoFunction) algo.getDerivative();
				
        setInputOutput();
        compute();
    }
 
    String getClassName() {
        return "AlgoCurvature";
    }

    // for AlgoElement
    void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
       
        output = new GeoElement[1];
        output[0] = K;
        setDependencies(); // done by AlgoElement
    }
    
    GeoNumeric getResult() {
        return K;
    }

    final void compute() {
    	try {    	
    		double f1eval = f1.evaluate(A.inhomX);
    		double t = Math.sqrt(1 + f1eval * f1eval);
    		double t3 = t * t * t;

    		K.setValue( f2.evaluate(A.inhomX) / t3 );
    	} 
    	catch (Exception e) {
    		// in case something went wrong, e.g. derivatives not defined
    		K.setUndefined();
    	}
    }   
}