package geogebra.kernel;

import java.util.ArrayList;

public class AlgoFirstLocus extends AlgoFirst {

	AlgoFirstLocus(Construction cons, String label, GeoLocus inputLocus,
			GeoNumeric n) {
		super(cons, label, inputLocus, n);


	}

    public String getClassName() {
        return "AlgoFirstLocus";
    }
    
    protected final void compute() {
    	
    	ArrayList<MyPoint> points = ((GeoLocus)inputList).getPoints();
    	
    	size = points.size();
    	int outsize = n == null ? 1 : (int)n.getDouble();
    	
    	if (!inputList.isDefined() ||  size == 0 || outsize < 0 || outsize > size) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
    	if (outsize == 0) return; // return empty list
    	
    	// avoid label creation
    	boolean oldSuppressLabels = cons.isSuppressLabelsActive();
    	cons.setSuppressLabelCreation(true);
    	
    	for (int i=0 ; i<outsize ; i++) {
    		MyPoint mp = points.get(i);
    		GeoPoint p = new GeoPoint(cons, null, mp.x, mp.y, 1.0);
    		outputList.add(p);
    	}
    	
    	cons.setSuppressLabelCreation(oldSuppressLabels);

   }

}
