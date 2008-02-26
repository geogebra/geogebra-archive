package geogebra;

public class GeoGebraApplication extends Application {

    public GeoGebraApplication(String[] args, GeoGebra frame, boolean undoActive) {
        super(args, frame, undoActive);
    }

    public GeoGebraApplication(String[] args, GeoGebraApplet applet, boolean undoActive) {
    	super(args, applet, undoActive);
    }
}
