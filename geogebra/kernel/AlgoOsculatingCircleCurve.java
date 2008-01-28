package geogebra.kernel;


/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Osculating Circle of a curve f in point A: center = A + (radius)^2 * v
 * 											  radius = 1/abs(k(x)), k(x)=curvature of f
 *                                            v = curvature vector of f in point A
 */

public class AlgoOsculatingCircleCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, R;// input A
    private GeoCurveCartesian f;// input
    private GeoVector v;//curvature vector of f in point A 
    private GeoNumeric curv;//curvature of f in point A
    private GeoConic circle; // output
    
    AlgoOsculatingCircleCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f) {
        super(cons);
        this.A = A;
        this.f = f;
        
        R = new GeoPoint(cons);//R is the center of the circle
        circle = new GeoConic(cons);

        //Catch curvature and curvature vector
        AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,A,f);
        AlgoCurvatureVectorCurve cv = new AlgoCurvatureVectorCurve(cons,A,f);
        curv = algo.getResult();
        v = cv.getVector();
 
    	cons.removeFromConstructionList(algo);
		cons.removeFromConstructionList(cv);
		setInputOutput();
        compute();
        circle.setLabel(label);
    }
 
    protected String getClassName() {
        return "AlgoOsculatingCircleCurve";
    }

    // for AlgoElement
    void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
        
        output = new GeoElement[1];
        output[0] = circle;  
        setDependencies(); // done by AlgoElement
    }
    
    //Return the resultant circle
    GeoConic getCircle() {
    	return circle;
    }

    final void compute() {
    	double radius = 1/Math.abs(curv.getValue());
    	double r2 = radius*radius;
    	double x = r2 * v.x;
    	double y = r2 * v.y;
    	
    	R.setCoords(A.inhomX + x, A.inhomY + y, 1.0);
    	circle.setCircle(R, A);	
    }
}