package geogebra.kernel;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Calculate Curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoCurvatureCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
	private GeoCurveCartesian f, f1, f2;// f = f(x), f1 is f'(x), f2 is f''(x)
    private GeoNumeric K; //output
    
    AlgoCurvatureCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    AlgoCurvatureCurve(Construction cons, GeoPoint A, GeoCurveCartesian f) {
        super(cons);
        this.f = f;
        this.A = A;
        K = new GeoNumeric(cons);
       
        //First derivative of curve f
        AlgoDerivative algo = new AlgoDerivative(cons, f);
		this.f1 = (GeoCurveCartesian) algo.getDerivative();
		
		//Second derivative of curve f
		algo = new AlgoDerivative(cons, f1);
		this.f2 = (GeoCurveCartesian) algo.getDerivative();
		
        cons.removeFromConstructionList(algo);
        setInputOutput();
        compute();
    }
 
    String getClassName() {
        return "AlgoCurvatureCurve";
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
    	double f1eval[] = new double[2];
    	double f2eval[] = new double[2];
    	double t, t3, evals, tvalue;
    	
    	tvalue = f.getClosestParameter(A,f.getMinParameter());
    	f1.evaluateCurve(tvalue,f1eval);
        t = Math.sqrt(f1eval[0]*f1eval[0] + f1eval[1]*f1eval[1]);
        t3 = t * t * t;
        f2.evaluateCurve(tvalue,f2eval);
        evals = f1eval[0]*f2eval[1] - f2eval[0]*f1eval[1];
        
        K.setValue( evals / t3 );
    }   
}