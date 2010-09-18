package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;

public class AlgoConvexHull extends AlgoHull{

	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoConvexHull";
    }

}
