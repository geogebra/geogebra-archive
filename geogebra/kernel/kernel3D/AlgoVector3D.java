package geogebra.kernel.kernel3D;

import geogebra.kernel.AlgoVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoVectorInterface;


/**
 * Vector between two points P and Q.
 * Extends AlgoVector
 * 
 * @author  ggb3D
 */

public class AlgoVector3D extends AlgoVector {

	/** constructor
	 * @param cons
	 * @param label
	 * @param P
	 * @param Q
	 */
	public AlgoVector3D(Construction cons, String label, GeoPointInterface P, GeoPointInterface Q) {
		super(cons, label, P, Q);
	}


	protected GeoVectorInterface createNewVector(){

		return new GeoVector3D(cons);

	}


	protected GeoPointInterface newStartPoint(){

		return new GeoPoint3D((GeoPoint3D) getP());

	}


	

}
