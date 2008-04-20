package geogebra;

public class GeoGebraApplet extends GeoGebraAppletBase {

	protected Application buildApplication(String[] args, boolean ua) {
		return new GeoGebraApplication(args, this, ua);
	}

}
