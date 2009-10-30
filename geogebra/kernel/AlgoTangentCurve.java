package geogebra.kernel;

/**
 * @author Victor Franco Espino
 * version 11-02-2007
 * 
 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
 */

public class AlgoTangentCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoCurveCartesian f, df; // input f
    private GeoLine tangent; // output  
    private GeoPoint T;
    private boolean pointOnCurve;

    AlgoTangentCurve(Construction cons,String label,GeoPoint P,GeoCurveCartesian f) {
        super(cons);
        this.P = P;
        this.f = f;
        tangent = new GeoLine(cons);
        
        // check if P is defined as a point of the curve's graph
        pointOnCurve = false;
        if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
        	AlgoPointOnPath algo = (AlgoPointOnPath) P.getParentAlgorithm();
        	pointOnCurve = algo.getPath() == f;
        }        
        
        if (pointOnCurve)
        	T = P;
        else
        	T = new GeoPoint(cons);
        tangent.setStartPoint(T);

        //First derivative of curve f
        AlgoDerivative algo = new AlgoDerivative(cons, f);
		this.df = (GeoCurveCartesian) algo.getDerivative();
		
		cons.removeFromConstructionList(algo);
        setInputOutput(); // for AlgoElement                
        compute();
        tangent.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoTangentCurve";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = f;

        output = new GeoElement[1];
        output[0] = tangent;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getTangent() {
        return tangent;
    }
    GeoCurveCartesian getCurve() {
        return f;
    }
    GeoPoint getPoint() {
        return P;
    }
    GeoPoint getTangentPoint() {
        return T;
    }

    protected final void compute() {
        if (!(f.isDefined() && P.isDefined())) {
            tangent.setUndefined();
            return;
        }

        // first derivative
        if (df == null || !df.isDefined()) {
            tangent.setUndefined();
            return;
        }

        // calc the tangent;
        double feval[] = new double[2];
        double dfeval[] = new double[2];        
        
        double tvalue = f.getClosestParameter(P,f.getMinParameter());
        f.evaluateCurve(tvalue, feval);
        df.evaluateCurve(tvalue, dfeval);
        tangent.setCoords(-dfeval[1],dfeval[0],feval[0]*dfeval[1]-dfeval[0]*feval[1]);
        
        if (!pointOnCurve)
        	T.setCoords(feval[0], feval[1], 1.0);
    }
}