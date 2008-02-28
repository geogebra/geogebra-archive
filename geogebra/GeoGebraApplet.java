package geogebra;

public class GeoGebraApplet extends GeoGebraAppletBase {

	protected GeoGebraApplicationBase buildApplication(String[] args, boolean ua) {
		return new GeoGebraApplication(null, this, undoActive);
	}

}
