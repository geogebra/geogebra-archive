package geogebra;

public class GeoGebraApplication extends Application {

    public GeoGebraApplication(String[] args, GeoGebra frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public GeoGebraApplication(String[] args, GeoGebraAppletBase applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
}
