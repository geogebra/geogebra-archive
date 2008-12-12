package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoRay;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.main.Application;

public class AlgoRayPointVector3D extends AlgoElement3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint3D P; // input
    private GeoVector3D v; // input
    private GeoRay3D ray; // output       

    AlgoRayPointVector3D(
        Construction cons,
        String label,
        GeoPoint3D P,
        GeoVector3D v) {
        super(cons);
        this.P = P;
        this.v = v;
        ray = new GeoRay3D(cons, P);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        ray.setLabel(label);
    
        Application.debug("AlgoRayPointVector3D : constructor");

    }

    protected String getClassName() {
        return "AlgoRayPointVector3D";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;

        output = new GeoElement[1];
        output[0] = ray;
        setDependencies(); // done by AlgoElement
    }

    GeoRay3D getRay() {
        return ray;
    }
    GeoPoint3D getP() {
        return P;
    }
    GeoVector3D getv() {
        return v;
    }

    // calc the line g through P and Q    
    protected final void compute() {
    	Application.debug("AlgoRayPointVector3D : compute");
        GeoVec4D.lineThroughPointVector(P, v, ray);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("RayThroughAWithDirectionB",P.getLabel(),v.getLabel()));
        
        return sb.toString();
    }

}
