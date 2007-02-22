package geogebra.kernel;

import java.awt.Color;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Color of curvature for curve's: when you move the point of this curve
 * the color of point changue depending on the value of curvature
 */

public class AlgoColorCurvatureCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
	private GeoCurveCartesian f; // input
    private GeoNumeric K, slider; //output K
    private Color colorPoint;
    
    AlgoColorCurvatureCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f, GeoNumeric slider) {
        super(cons);
        this.A = A;
        this.f = f;
        this.slider = slider;//to adjust the color to the curve 
         
        //Catch value of the curvature
        AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,A, f);
		this.K = algo.getResult();
		A.setTrace(true);//Activate the trace of point to paint the curve
	
		cons.removeFromConstructionList(algo);
        setInputOutput();
        compute();
        
        if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}   
    }
 
    String getClassName() {
        return "AlgoColorCurvatureCurve";
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
    
    GeoNumeric getValue() {
        return K;
    }

    final void compute() {
    	double value = K.getValue(); 
    	double a = slider.getValue();
    	if (a<0) a=0;//No negative values for Color
    	
    	/**Color of curvature
    	 * 
    	 * Negative Curvature : Green
    	 * Positive Curvature : Red
    	 * Curvature near zero: Blue
    	*/
    	float red, green, alpha = (float) 0.5;

    	if (value >= Kernel.MIN_PRECISION){
			red = (float) (a*value/(a*value+1));
			green = 0;
    		colorPoint = new Color(red,green,1-red,alpha);
			A.setObjColor(colorPoint);
		}else if (value < 0){
			green =  (float) (a*value/(a*value-1));
			red = 0;
			colorPoint = new Color(red,green,1-green,alpha);
			A.setObjColor(colorPoint);
		}
    }   
}