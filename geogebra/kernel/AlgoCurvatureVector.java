package geogebra.kernel;


/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Calculate Curvature Vector for function: c(x) = (1/T^4)*(-f'*f'',f''), T = sqrt(1+(f')^2)
 */

public class AlgoCurvatureVector extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
    private GeoFunction f, f1, f2; // f = f(x), f1 is f'(x), f2 is f''(x)
    private GeoVector v; // output

    AlgoCurvatureVector(Construction cons, String label, GeoPoint A, GeoFunction f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    v.setLabel(label);
    	}else{
    		// if we don't have a label we could try c
    	    v.setLabel("c"); 
    	}  
    }
    
    AlgoCurvatureVector(Construction cons, GeoPoint A, GeoFunction f) {
        super(cons);
        this.A = A;
        this.f = f;
        
        // create new vector
        v = new GeoVector(cons);
        try {     
            v.setStartPoint(A);  
        } catch (CircularDefinitionException e) {}                         
        
        //First derivative of function f
        AlgoCasDerivative algo = new AlgoCasDerivative(cons, f);
        cons.removeFromConstructionList(algo);
		this.f1 = (GeoFunction) algo.getResult();
		
		//Second derivative of function f
		algo = new AlgoCasDerivative(cons, f1);
		cons.removeFromConstructionList(algo);
		this.f2 = (GeoFunction) algo.getResult();
        		
		setInputOutput();
        compute();
    }
 
    public String getClassName() {
        return "AlgoCurvatureVector";
    }

    // for AlgoElement
    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
        
        output = new GeoElement[1];
        output[0] = v;
        setDependencies(); // done by AlgoElement
    }
    
    //Return the resultant vector
    GeoVector getVector() {
    	return v;
    }

    protected final void compute() {
    	try {
	       double f1eval = f1.evaluate(A.inhomX);
	       double f2eval = f2.evaluate(A.inhomX);
	       double t = Math.sqrt(1 + f1eval * f1eval);
	       double t4 = t*t*t*t;
	       
	       double x = A.inhomX - (f1eval * f2eval) / t4;
	       double y = A.inhomY + f2eval / t4;
	       	     
           v.x = x - A.inhomX;
           v.y = y - A.inhomY;             
           v.z = 0.0;	       	      
	    } 
		catch (Exception e) {
			// in case something went wrong, e.g. derivatives not defined
			v.setUndefined();
		}
    }
}