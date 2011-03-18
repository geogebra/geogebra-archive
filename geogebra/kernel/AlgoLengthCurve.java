package geogebra.kernel;

import geogebra.kernel.roots.RealRootFunction;

/**
* @author  Victor Franco Espino
* @version 19-04-2007
*
* Calculate Curve Length between the parameters t0 and t1: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
*/

public class AlgoLengthCurve extends AlgoElementCAS {

	private static final long serialVersionUID = 1L;
	private GeoNumeric t0, t1; //input
	private GeoCurveCartesian c, c1; //c1 is c'(x)
    private GeoNumeric length; //output
	private RealRootFunction lengthCurve; //is T = sqrt(a'(t)^2+b'(t)^2)	

    AlgoLengthCurve(Construction cons, String label, GeoCurveCartesian c, GeoNumeric t0, GeoNumeric t1) {
        super(cons);
        this.t0 = t0;
        this.t1 = t1;
        this.c = c;
        length = new GeoNumeric(cons);

        //First derivative of curve f
        algoCAS = new AlgoCasDerivative(cons, c);
        this.c1 = (GeoCurveCartesian) ((AlgoCasDerivative)algoCAS).getResult();

        lengthCurve = new LengthCurve();		

        cons.removeFromConstructionList(algoCAS);
        geo = c;
        
        setInputOutput();
        compute();
        length.setLabel(label);
    }

    public String getClassName() {
        return "AlgoLengthCurve";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = c;
        input[1] = t0;
        input[2] = t1;

        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    protected final void compute() {
    	double a = t0.getValue();
    	double b = t1.getValue();

    	double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(lengthCurve, a, b));
		length.setValue(lenVal);		
    }

    /**
	 * T = sqrt(a'(t)^2+b'(t)^2)
	 */
	private class LengthCurve implements RealRootFunction {
		public double evaluate(double t) {
			double f1eval[] = new double[2];
	    	c1.evaluateCurve(t,f1eval);
	        return (Math.sqrt(f1eval[0]*f1eval[0] + f1eval[1]*f1eval[1]));
		}
	}
}
