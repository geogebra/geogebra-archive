package geogebra.kernel;

import geogebra.kernel.integration.GaussQuadIntegration;
import geogebra.kernel.roots.RealRootFunction;

/**
* @author  Victor Franco Espino
* @version 19-04-2007
*
* Calculate Curve Length between the points A and B: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
*/

public class AlgoLengthCurve2Points extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, B; //input
	private GeoCurveCartesian c, c1; //c1 is c'(x)
    private GeoNumeric length; //output
	private RealRootFunction lengthCurve; //is T = sqrt(a'(t)^2+b'(t)^2)
	private GaussQuadIntegration gauss;

    AlgoLengthCurve2Points(Construction cons, String label, GeoCurveCartesian c, GeoPoint A, GeoPoint B) {
        super(cons);
        this.A = A;
        this.B = B;
        this.c = c;
        length = new GeoNumeric(cons);

        //First derivative of curve f
        AlgoDerivative algo = new AlgoDerivative(cons, c, null);
        this.c1 = (GeoCurveCartesian) algo.getDerivative();

        lengthCurve = new LengthCurve();
		gauss = new GaussQuadIntegration(5);

        cons.removeFromConstructionList(algo);
        setInputOutput();
        compute();
        length.setLabel(label);
    }

    String getClassName() {
        return "AlgoLengthCurve2Points";
    }

    void setInputOutput(){
        input = new GeoElement[3];
        input[0] = c;
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
    	double a = c.getClosestParameter(A,c.getMinParameter());
    	double b = c.getClosestParameter(B,c.getMinParameter());

		if (a <= b)
			length.setValue(gauss.integrate(lengthCurve, a, b));
		else
			length.setValue(-gauss.integrate(lengthCurve, b, a));
			//length.setValue(gauss.integrate(lengthCurve, f.getMinParameter(),f.getMaxParameter() )
			//	 - gauss.integrate(lengthCurve, b, a));
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
