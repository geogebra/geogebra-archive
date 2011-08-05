package geogebra.kernel;

import geogebra.main.Application;

/**
 * @author  Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 * Calculate Curvature for function:
 */

public class AlgoCurvature extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
	private GeoFunction f;
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
    
    public AlgoCurvature(Construction cons, GeoPoint A, GeoFunction f) {
        super(cons);
        this.f = f;
        this.A = A;
        K = new GeoNumeric(cons);              
				
        setInputOutput();
        compute();
    }
 
    public String getClassName() {
        return "AlgoCurvature";
    }

    // for AlgoElement
    protected void setInputOutput(){
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

    protected final void compute() {
    	if (f.isDefined())
    		K.setValue( f.evaluateCurvature(A.inhomX) );
    	else     	
    		K.setUndefined();    	
    }   
    
	public void remove() {  
    	super.remove();  
    }

}