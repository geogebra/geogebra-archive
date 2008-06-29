package geogebra3D;

import geogebra.GeoGebra;
import geogebra.GeoGebraAppletBase;

public class GeoGebraApplication3D extends Application3D {

    public GeoGebraApplication3D(String[] args, GeoGebra frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public GeoGebraApplication3D(String[] args, GeoGebraAppletBase applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
}
