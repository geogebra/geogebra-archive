package geogebra;

public class Applet extends GeoGebraAppletBase {

	protected Application buildApplication(String[] args, boolean ua) {
		return new GeoGebraApplication(null, this, undoActive);
	}

}
