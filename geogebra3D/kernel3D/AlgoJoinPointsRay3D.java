package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;


public class AlgoJoinPointsRay3D extends AlgoElement3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint3D P, Q;  // input
    private GeoRay3D  ray;     // output       
        
    /** Creates new AlgoJoinPoints */
    AlgoJoinPointsRay3D(Construction cons, String label, GeoPoint3D P, GeoPoint3D Q) {
        super(cons);
        Application.debug("AlgoJoinPointsRay3D : constructor");
        
        this.P = P;
        this.Q = Q;                
        ray = new GeoRay3D(cons, P); 
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        ray.setLabel(label);
    }   
    
    protected String getClassName() {
        return "AlgoJoinPointsRay3D";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
        
        output = new GeoElement[1];        
        output[0] = ray;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoRay3D getRay3D() { return ray; }
    GeoPoint3D getP() { return P; }
    GeoPoint3D getQ() { return Q; }
    
    // calc the line g through P and Q    
    protected final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        Application.debug("AlgoJoinPointsRay3D : compute()");

        //GeoVec4D.lineThroughPointVector(P, Q, ray);
        ray.setCoord(P,Q);
    }   
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        sb.append(app.getPlain("RayThroughAB",P.getLabel(),Q.getLabel()));

        /*
        sb.append(app.getPlain("RayThrough"));
        sb.append(' ');
        sb.append(P.getLabel());
        sb.append(", ");
        sb.append(Q.getLabel());*/
        
        return sb.toString();
    }
}
