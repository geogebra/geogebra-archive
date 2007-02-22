package geogebra.kernel;

import geogebra.kernel.integration.GaussQuadIntegration;
import geogebra.kernel.roots.RealRootFunction;

/**
* @author  Victor Franco Espino
* @version 14-02-2007
*
* Calculate Curve Length between the parameters t0 and t1: integral from t0 
to t1 on T = sqrt(a'(t)^2+b'(t)^2)
*/

public class AlgoLengthCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoNumeric t0, t1; //input
	private GeoCurveCartesian f, f1; //f1 is f'(x)
    private GeoNumeric length; //output
    private GeoPoint A, B; //output: representative points of the parameters t0 and t1
	private RealRootFunction lengthCurve; //is T = sqrt(a'(t)^2+b'(t)^2)
	private GaussQuadIntegration gauss;

    AlgoLengthCurve(Construction cons, String label, GeoNumeric t0, 
GeoNumeric t1, GeoCurveCartesian f) {
        super(cons);
        this.t0 = t0;
        this.t1 = t1;
        this.f = f;
        length = new GeoNumeric(cons);
        A = new GeoPoint(cons);
        B = new GeoPoint(cons);

        //First derivative of curve f
        AlgoDerivative algo = new AlgoDerivative(cons, f, null);
        this.f1 = (GeoCurveCartesian) algo.getDerivative();

        lengthCurve = new LengthCurve();
		gauss = new GaussQuadIntegration(5);

        cons.removeFromConstructionList(algo);
        setInputOutput();
        compute();
        length.setLabel(label);
        A.setLabel(label);
        B.setLabel(label);
    }

    String getClassName() {
        return "AlgoLengthCurve";
    }

    void setInputOutput(){
        input = new GeoElement[3];
        input[0] = t0;
        input[1] = t1;
        input[2] = f;

        output = new GeoElement[3];
        output[0] = length;
        output[1] = A;
        output[2] = B;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    final void compute() {
    	double a = 
t0.getValue();//f.getClosestParameter(A,f.getMinParameter());
    	double b = 
t1.getValue();//f.getClosestParameter(B,f.getMinParameter());

		if (a <= b)
			length.setValue(gauss.integrate(lengthCurve, a, b));
		else
			length.setValue(-gauss.integrate(lengthCurve, b, a));
			//length.setValue(gauss.integrate(lengthCurve, f.getMinParameter(),f.getMaxParameter() )
			//	 - gauss.integrate(lengthCurve, b, a));

		//locate the points A and B according to the value of the parameters t0 and t1
		A.setCoords(f.evaluateCurve(a));
		B.setCoords(f.evaluateCurve(b));
    }

    /**
	 * T = sqrt(a'(t)^2+b'(t)^2)
	 */
	private class LengthCurve implements RealRootFunction {
		public double evaluate(double t) {
			double f1eval[] = new double[2];
	    	f1.evaluateCurve(t,f1eval);
	        return (Math.sqrt(f1eval[0]*f1eval[0] + f1eval[1]*f1eval[1]));
		}
	}
}
