package tutor;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.GeoGebraAppletBase;
import geogebra.kernel.GeoElement;

public class TeacherApplication extends Application {

    public TeacherApplication(String[] args, GeoGebra frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public TeacherApplication(String[] args, GeoGebraAppletBase applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
    
    public void deleteAllGeoElements() {
    	// delete all
    	Object [] geos = getKernel().getConstruction().getGeoSetConstructionOrder().toArray();
    	if (geos.length == 0) return;
    	
    	//if (isSaved() || saveCurrentFile()) {
	    	for (int i=0; i < geos.length; i++) {
	    		GeoElement geo = (GeoElement) geos[i];
	    		if (geo.isLabelSet()) 
	    			geo.remove();
	    	}
	    	
	    	getKernel().initUndoInfo();
    		setCurrentFile(null);
    		setMoveMode();
    	//}
    }
    
}
