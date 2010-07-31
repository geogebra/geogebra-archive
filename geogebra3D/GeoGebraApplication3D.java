package geogebra3D;

import geogebra.CommandLineArguments;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.AppletImplementation;

public class GeoGebraApplication3D extends Application3D {

    public GeoGebraApplication3D(CommandLineArguments args, GeoGebraFrame frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public GeoGebraApplication3D(CommandLineArguments args, AppletImplementation applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
}
